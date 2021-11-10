package com.sandeep.shopping.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao {

    private static final Logger logger = Logger.getLogger(UserDao.class.getName());

    public User getDetails(String userName){
        User user = new User();

        try{
            Connection connection = H2DatabaseConnection.getConnectionToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where userName =?");
            preparedStatement.setString(1, userName);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setUserName(resultSet.getString("username"));
                user.setAge(resultSet.getInt("age"));
                user.setGender(resultSet.getString("gender"));
            }



        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Couldn 't not execute query", exception);
        }

        return user;
    }
}
