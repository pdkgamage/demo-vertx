package com.demo.vertx;

import com.demo.vertx.verticle.DBVerticle;
import com.demo.vertx.verticle.HelloVerticle;
import com.demo.vertx.verticle.HttpServerVerticle;
import io.vertx.core.Vertx;

import java.util.logging.Logger;

public class Application {
    static Logger log = Logger.getLogger(Application.class.getName());
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new HttpServerVerticle());
        vertx.deployVerticle(new HelloVerticle());
        vertx.deployVerticle(new DBVerticle());
        log.info("Verticle deployed successfully");
    }
}
