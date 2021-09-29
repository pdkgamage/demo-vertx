package com.demo.vertx.verticle;

import com.demo.vertx.util.EventBusAddress;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpServerVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.get("/demo/hello/:name").handler(this::helloName);
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    // just response with query param
    /*void helloName(RoutingContext ctx) {
        //curl  http://localhost:8080/demo/hello/HelloVertex
        String name = ctx.pathParam("name");
        ctx.request().response().end(String.format("Hello  %s" ,name));
    }*/

    // pass request to some other verticle and response
    /*void helloName(RoutingContext ctx) {
        String name = ctx.pathParam("name");
        vertx.eventBus().request(EventBusAddress.HELLO_NAME_ADDRESS,name, reply -> {
            ctx.request().response().end((String)reply.result().body());
        });
    }*/

    // get the response from other verticle which read the data from database table
    void helloName(RoutingContext ctx) {
        String name = ctx.pathParam("name");
        vertx.eventBus().request(EventBusAddress.NAME_RETRIEVE_ADDRESS,name, reply -> {
            if(reply.succeeded()) {
                ctx.request().response().end((String) reply.result().body());
            } else {
                ctx.request().response().end(reply.cause().getMessage());
            }
        });
    }
}
