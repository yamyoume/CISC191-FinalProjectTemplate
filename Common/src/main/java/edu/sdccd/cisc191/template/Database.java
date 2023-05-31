package edu.sdccd.cisc191.template;

import java.sql.Connection;
import java.sql.DriverManager;
public class Database {
    public static String url = "jdbc:mysql://185.212.71.102:3306/u984662218_java";
    public static String user = "u984662218_java";
    public static String password = "J&gsJ81c";
    public static Connection getConnection() throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}