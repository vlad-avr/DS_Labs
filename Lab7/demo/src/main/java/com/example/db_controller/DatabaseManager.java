package com.example.db_controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.objects.Author;
import com.example.objects.Book;

public class DatabaseManager {
    private final String db_url = "jdbc:sqlite:D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\db\\library.db";
    IDGenerator booksIdGenerator = new IDGenerator("B");
    IDGenerator authorsIdGenerator = new IDGenerator("A");

    public IDGenerator getBookGenerator() {
        return this.booksIdGenerator;
    }

    public IDGenerator getAuthorGenerator() {
        return this.authorsIdGenerator;
    }

    public void initDB() {
        createDatabase();
        createAuthorsTable();
        createBooksTable();
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

    private void createDatabase() {
        try (Connection conn = DriverManager.getConnection(db_url)) {
            if (conn == null) {
                System.out.println("\n UNABLE TO ESTABLISH CONNECTION!\n");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createBooksTable() {
        String sql = "CREATE TABLE IF NOT EXISTS books (\n"
                + "     id text PRIMARY KEY, \n"
                + "     name text NOT NULL, \n"
                + "     price real NOT NULL, \n"
                + "     genre text NOT NULL, \n"
                + "     author_id text NOT NULL \n"
                + ");";

        try (Connection con = DriverManager.getConnection(db_url); Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createAuthorsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS authors (\n"
                + "     id text PRIMARY KEY, \n"
                + "     firstname text NOT NULL, \n"
                + "     lastname text NOT NULL \n"
                + ");";

        try (Connection con = DriverManager.getConnection(db_url); Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addAuthor(Author author) {
        String sql = "INSERT INTO authors (id, firstname, lastname) VALUES (?, ?, ?)";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, author.getId());
            statement.setString(2, author.getFirstName());
            statement.setString(3, author.getLastName());
            statement.executeUpdate();
            List<Book> books = author.getBooks();
            for (Book book : books) {
                addBook(book);
            }
            // authorsIdGenerator.addId(author.getId());
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (id, name, price, genre, author_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, book.getId());
            statement.setString(2, book.getName());
            statement.setDouble(3, book.getPrice());
            statement.setString(4, book.getGenre());
            statement.setString(5, book.getAuthor());
            statement.executeUpdate();
            // booksIdGenerator.addId(book.getId());
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void updateAuthor(Author author) {
        String str = "UPDATE authors SET firstname = ?, lastname = ? WHERE id = ?";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(str)) {
            statement.setString(1, author.getFirstName());
            statement.setString(2, author.getLastName());
            statement.setString(3, author.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void updateBook(Book book) {
        String str = "UPDATE books SET name = ?, price = ?, genre = ?, author_id = ? WHERE id = ?";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(str)) {
            statement.setString(1, book.getName());
            statement.setDouble(2, book.getPrice());
            statement.setString(3, book.getGenre());
            statement.setString(4, book.getAuthor());
            statement.setString(5, book.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public Author getAuthor(String ID, boolean load_books) {
        String authorSql = "SELECT *\nFROM authors WHERE id = ?";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(authorSql)) {
            statement.setString(1, ID);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                Author author = new Author(res.getString("firstname"), res.getString("lastname"),
                        res.getString("id"));
                if (load_books) {
                    String bookSql = "SELECT *\nFROM books WHERE author_id = ?";
                    try (PreparedStatement statement2 = con.prepareStatement(bookSql)) {
                        statement2.setString(1, ID);
                        ResultSet f_res = statement2.executeQuery();
                        while (f_res.next()) {
                            Book book = new Book(f_res.getString("id"), f_res.getString("name"),
                                    f_res.getDouble("price"), Book.Genre.valueOf(f_res.getString("genre")),
                                    f_res.getString("author_id"));
                            if (book != null) {
                                author.addBook(book);
                            }
                        }
                    } catch (SQLException exception) {
                        System.out.println(exception.getMessage());
                    }
                }
                return author;
            } else {
                SQLException exception = new SQLException("Author with ID " + ID + " does not exist!");
                throw exception;
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    public List<Author> getAuthors() {
        List<Author> authors = new ArrayList<>();
        for (String ID : authorsIdGenerator.getIDs()) {
            Author author = getAuthor(ID, true);
            if (author != null) {
                authors.add(author);
            }
        }
        return authors;
    }

    public List<Author> getAuthors(int minBookCount, int maxBookCount) {
        List<Author> authors = new ArrayList<>();
        for (String ID : authorsIdGenerator.getIDs()) {
            Author author = getAuthor(ID, true);
            if (author != null && author.getBooks().size() >= minBookCount
                    && author.getBooks().size() <= maxBookCount) {
                authors.add(author);
            }
        }
        return authors;
    }

    public List<Author> getAuthors(String toContain) {
        List<Author> authors = new ArrayList<>();
        for (String ID : authorsIdGenerator.getIDs()) {
            Author author = getAuthor(ID, true);
            if (author != null
                    && (author.getFirstName().contains(toContain) || author.getLastName().contains(toContain))) {
                authors.add(author);
            }
        }
        return authors;
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        for (String ID : booksIdGenerator.getIDs()) {
            Book book = getBook(ID);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getBooks(double minPrice, double maxPrice) {
        List<Book> books = new ArrayList<>();
        for (String ID : booksIdGenerator.getIDs()) {
            Book book = getBook(ID);
            if (book != null && book.getPrice() >= minPrice && book.getPrice() <= maxPrice) {
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getBooks(String toContain) {
        List<Book> books = new ArrayList<>();
        for (String ID : booksIdGenerator.getIDs()) {
            Book book = getBook(ID);
            if (book != null && book.getName().contains(toContain)) {
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getBooks(Book.Genre genre) {
        List<Book> books = new ArrayList<>();
        for (String ID : booksIdGenerator.getIDs()) {
            Book book = getBook(ID);
            if (book != null && Book.Genre.valueOf(book.getGenre()) == genre) {
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> getBooksOfAuthor(String ID) {
        String bookSql = "SELECT *\nFROM books WHERE author_id = ?";
        List<Book> books = new ArrayList<>();
        try (Connection con = connect(); PreparedStatement statement2 = con.prepareStatement(bookSql)) {
            statement2.setString(1, ID);
            ResultSet f_res = statement2.executeQuery();
            while (f_res.next()) {
                Book book = new Book(f_res.getString("id"), f_res.getString("name"),
                        f_res.getDouble("price"), Book.Genre.valueOf(f_res.getString("genre")),
                        f_res.getString("author_id"));
                if (book != null) {
                    books.add(book);
                }
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return books;
    }

    public Book getBook(String ID) {
        String bookSql = "SELECT *\nFROM books WHERE id = ?";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(bookSql)) {
            statement.setString(1, ID);
            ResultSet f_res = statement.executeQuery();
            Book book = new Book(f_res.getString("id"), f_res.getString("name"),
                    f_res.getDouble("price"), Book.Genre.valueOf(f_res.getString("genre")),
                    f_res.getString("author_id"));
            return book;
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    public void deleteAuthor(String ID) {
        Author author = getAuthor(ID, true);
        String authorSql = "DELETE FROM authors WHERE id = ?";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(authorSql)) {
            statement.setString(1, ID);
            String bookSql = "DELETE FROM books WHERE author_id = ?";
            try (PreparedStatement statement2 = con.prepareStatement(bookSql)) {
                statement2.setString(1, ID);
                statement2.execute();
                for (Book book : author.getBooks()) {
                    booksIdGenerator.removeId(book.getId());
                }
            } catch (SQLException exception) {
                System.out.println(exception.getMessage());
            }
            statement.executeUpdate();
            authorsIdGenerator.removeId(ID);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void deleteBook(String ID) {
        String bookSql = "DELETE FROM books WHERE id = ?";
        try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(bookSql)) {
            statement.setString(1, ID);
            statement.executeUpdate();
            booksIdGenerator.removeId(ID);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

}
