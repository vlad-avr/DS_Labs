package com.example.db_controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.objects.Author;
import com.example.objects.Book;

public class DatabaseManager {
    private final String db_url = "jdbc:sqlite:library.db";

    public void initDB() {
        create_database();
        create_authors_table();
        create_books_table();
    }

    public void destroyDB() {
        String sql1 = "DROP TABLE IF EXISTS authors";
        String sql2 = "DROP TABLE IF EXISTS books";
        try (Connection con = DriverManager.getConnection(db_url); Statement st = con.createStatement()) {
            st.execute(sql1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try (Connection con = DriverManager.getConnection(db_url); Statement st = con.createStatement()) {
            st.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connect() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(db_url);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return con;
    }

    private void create_database() {
        try (Connection conn = DriverManager.getConnection(db_url)) {
            if (conn == null) {
                System.out.println("\n UNABLE TO ESTABLISH CONNECTION!\n");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void create_books_table() {
        String sql = "CREATE TABLE IF NOT EXISTS books (\n"
                + "     id text NOT NULL, \n"
                + "     name text NOT NULL, \n"
                + "     price real NOT NULL, \n"
                + "     genre text NOT NULL, \n"
                + "     author_id text NOT NULL, \n"
                + ");";

        try (Connection con = DriverManager.getConnection(db_url); Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void create_authors_table() {
        String sql = "CREATE TABLE IF NOT EXISTS authors (\n"
                + "     id text NOT NULL, \n"
                + "     firstname text NOT NULL, \n"
                + "     lastname text NOT NULL, \n"
                + ");";

        try (Connection con = DriverManager.getConnection(db_url); Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addAuthor(Author author) {
        String sql = "INSERT INTO authors (id, firstname, lastname) VALUES (" + author.getId() + ", "
                + author.getFirstName() + ", " + author.getLastName() + ")";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (id, name, price, genre, author_id) VALUES (" + book.getId() + ", "
                + book.getName() + ", " + book.getPrice() + ", " + book.getGenre() + ", " + book.getAuthor() + ")";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

}
