package com.sandeep.shopping.service;

import com.sandeep.shopping.client.OrderClient;
import com.sandeep.shopping.db.User;
import com.sandeep.shopping.db.UserDao;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    UserDao userDao = new UserDao();

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {

        User user = userDao.getDetails(request.getUserName());

        UserResponse.Builder userResponseBuilder = UserResponse.newBuilder()
                .setId(user.getId())
                .setUserName(user.getUserName())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(Gender.valueOf(user.getGender()));

        List<Order> orders = getOrders(userResponseBuilder);

        userResponseBuilder.setNoOfOrders(orders.size());

        UserResponse userResponse = userResponseBuilder.build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    private List<Order> getOrders(UserResponse.Builder userResponseBuilder) {
        //get Orders by invoking the order client
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                                            .usePlaintext().build();
        OrderClient orderClinet = new OrderClient(channel);
        List<Order> orders = orderClinet.getOrders(userResponseBuilder.getId());
        // you must shut channel if you have no communication any more

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Channel not closed Properly");
        }
        return orders;
    }
}
