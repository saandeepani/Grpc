package com.sandeep.shopping.server;

import com.sandeep.shopping.service.OrderServiceImpl;
import com.sandeep.shopping.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServer {

    private static final Logger logger = Logger.getLogger(UserServer.class.getName());
    private Server server;

    public void startServer(){
        int port = 50051;
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new UserServiceImpl())
                    .addService(new OrderServiceImpl())
                    .build()
                    .start();
            logger.info("Server started on port 50051");

            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    logger.info("Clean server shutdown in case JVM was shutdown");
                    try {
                        UserServer.this.stopServer();
                    } catch (InterruptedException exception) {
                        logger.log(Level.SEVERE, "Server shutdown interrupted", exception);
                    }
                }
            });
        } catch (IOException exception) {
           logger.log(Level.SEVERE, "Server did not start", exception);
        }
    }

    public void stopServer() throws InterruptedException {
        if(server!=null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if(server!=null){
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        UserServer userServer = new UserServer();
        userServer.startServer();
        userServer.blockUntilShutdown();

    }

    	//To make executable jar you need to do following steps
		//Changes in pom.xml
		//Add a plugin to create a fat jar
		//Add the main method class
        //Add the resource folder to jar
}
