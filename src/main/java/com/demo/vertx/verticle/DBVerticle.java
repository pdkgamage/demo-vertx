package com.demo.vertx.verticle;

import com.demo.vertx.util.EventBusAddress;
import com.demo.vertx.util.SQLConstants;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBVerticle extends AbstractVerticle {
    private static final Logger logger = LogManager.getLogger(DBVerticle.class);
    private JDBCPool jdbcPool;

    @Override
    public void start() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("url", "jdbc:mysql://localhost:3306/vertxDemo");
        jsonObject.put("driver_class", "com.mysql.cj.jdbc.Driver");
        jsonObject.put("max_pool_size", 30);
        jsonObject.put("user", "root");
        jsonObject.put("password", "root");
        jdbcPool = JDBCPool.pool(
                vertx,jsonObject
        );

        vertx.eventBus().consumer(EventBusAddress.NAME_RETRIEVE_ADDRESS,
                message -> {
                    getVisitorDetails(message.body().toString())
                            .onSuccess(success -> {
                                message.reply(success);
                            }).onFailure(throwable -> {
                                logger.error("Error while getting data from database");
                                message.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "Error while getting data from database");
                            });
                });

    }

    public Future<String> getVisitorDetails(String name) {
        Promise<String> promise = Promise.promise();
        jdbcPool.preparedQuery(SQLConstants.GET_VISITOR_DETAILS)
                .execute(Tuple.of(name))
                .onFailure(throwable -> {
                    logger.error("Failure: {} ", throwable.getCause().getMessage());
                    promise.fail(throwable.getCause().getMessage());
                })
                .onSuccess(rows -> {
                    if (rows.size() > 0) {
                        rows.forEach(row -> {
                            promise.complete(row.getString("lastname"));
                        });
                    } else {
                        promise.fail("No valid records available");
                    }
                });
        return promise.future();
    }


}
