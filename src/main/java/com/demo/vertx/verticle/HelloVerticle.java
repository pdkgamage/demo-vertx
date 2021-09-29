package com.demo.vertx.verticle;

import com.demo.vertx.util.EventBusAddress;
import io.vertx.core.AbstractVerticle;

import java.util.UUID;

public class HelloVerticle extends AbstractVerticle {

    String randomNumber = UUID.randomUUID().toString();

    public void start() {

        vertx.eventBus().consumer(EventBusAddress.HELLO_NAME_ADDRESS, msg -> {
            String name = (String)msg.body();
            msg.reply(String.format("Hello %s %s", name, randomNumber));
        });
    }
}
