package com.telerikacademy.socialalien.repositories;

import com.telerikacademy.socialalien.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.sql.*;


@PropertySource("classpath:application.properties")
public class UserRepositoryJdbcImpl{

    private String dbUrl, dbUsername, dbPassword;

    @Autowired
    public UserRepositoryJdbcImpl(Environment env) {
        dbUrl = env.getProperty("database.url");
        dbUsername = env.getProperty("database.username");
        dbPassword = env.getProperty("database.password");
    }

    public User getById(int id) {
        try {
            String query = "select username from alien_network.users where id = ?";
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(id));
            ResultSet result = statement.executeQuery();
            result.next();
            return new User(result.getString("username"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public User getById(int id) {
//        String query = "select username from alien_network.users where id = ?";
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(query)
//        ) {
//            statement.setString(1, String.valueOf(id));
//
//            try (
//                    ResultSet result = statement.executeQuery()
//            ) {
//                return new User(result.getString("username"));
//            }
//
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//    }
}
