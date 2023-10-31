package com.example.db_controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final String db_url = "jdbc:sqlite:library.db";

    private Connection connect() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(db_url);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return con;
    }
}
