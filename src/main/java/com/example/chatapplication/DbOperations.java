package com.example.chatapplication;

import java.sql.*;

public class DbOperations {

    private static Connection connection;
    private static String UserTableName;
    private static String ChatBackupTableName;

    public static void getConnection() throws SQLException {
        if (connection == null)
        {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatApplication", "root", "root" );
        }

    }

    public static void createUserTable(String users) throws SQLException {
        getConnection();
        Statement statement = connection.createStatement();
        UserTableName = users;
        statement.execute("CREATE TABLE " + users + " (id INT primary key auto_increment ,  name VARCHAR(30), joining_date date)");

    }

    public static void createChatTable(String chat_backup) throws SQLException {
        getConnection();
        Statement statement = connection.createStatement();
        ChatBackupTableName = chat_backup;
        statement.execute("CREATE TABLE " + chat_backup + " (msg_id INT primary key , name VARCHAR (30), msg VARCHAR (200))");
    }

    public static void addUserInDb(String user_name) throws SQLException {
        getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(("INSERT INTO " + UserTableName + " VALUES (null,?,?)"));
        preparedStatement.setString(1,user_name);
        preparedStatement.setDate(2,new Date(System.currentTimeMillis()));
        preparedStatement.executeUpdate();
    }

    public static void saveMessage(String id, String user_name, String message) throws SQLException {
        getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(("INSERT INTO " + ChatBackupTableName + " VALUES (?,?,?)"));
        preparedStatement.setString(1,id);
        preparedStatement.setString(2,user_name);
        preparedStatement.setString(3,message);
        preparedStatement.execute();
    }
}
