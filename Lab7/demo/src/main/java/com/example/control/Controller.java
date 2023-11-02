package com.example.control;

import java.util.List;

import com.example.db_controller.DatabaseManager;
import com.example.objects.Author;
import com.example.objects.Book;
import com.example.objects.Book.Genre;
import com.example.xml_parser.MyParser;

public class Controller {
    DatabaseManager dbManager = new DatabaseManager();
    MyParser parser = new MyParser("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml",
            "D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Schema.xsd", dbManager);

    public void start() {
        dbManager.destroyDB();
        dbManager.initDB();
        parser.parseSAX();
        // GENERAL
        System.out.println("\n ENTRY DATA : \n");
        List<Author> authors = dbManager.getAuthors();
        List<Book> books = dbManager.getBooks();
        for (Author author : authors) {
            System.out.println(author.toString());
        }
        for (Book book : books) {
            System.out.println(book.toString());
        }
        // GETTERS
        System.out.println("\n TESTING GETTERS : \n");
        System.out.println("\n FROM DB:\n");
        System.out.println("\n Loading Author : \n");
        Author author = dbManager.getAuthor("A-1", true);
        System.out.println(author.toString());

        System.out.println("\n Loading Book : \n");
        Book book = dbManager.getBook("B-1");
        System.out.println(book.toString());

        System.out.println("\n FROM XML:\n");
        System.out.println("\n Loading Author : \n");
        author = parser.getAuthor("A-1", parser.getXml());
        System.out.println(author.toString());

        System.out.println("\n Loading Book : \n");
        book = parser.getBook("B-1", parser.getXml());
        System.out.println(book.toString());
        // UPDATERS
        System.out.println("\n TESTING UPDATERS : \n");
        System.out.println("\n TESTING GETTERS : \n");
        System.out.println("\n FROM DB:\n");
        System.out.println("\n Updating Author : \n");
        System.out.println("\n Before : \n");
        author = dbManager.getAuthor("A-2", true);
        System.out.println(author.toString());
        author.setFirstName("Govi");
        // Book newBook = new Book(dbManager.getBooksGenerator().generateId());
        // newBook.setAuthor("A-2");
        // newBook.setGenre(Genre.drama);
        // newBook.setName("Cthulu and Dagon");
        // newBook.setPrice(69.69);
        // author.addBook(newBook);
        // author.removeBook(author.getBooks().get(0).getId());
        dbManager.updateAuthor(author);
        author = dbManager.getAuthor("A-2", true);
        System.out.println("\n After : \n");
        System.out.println(author.toString());

        System.out.println("\n Updating Book : \n");
        System.out.println("\n Before : \n");
        book = dbManager.getBook("B-1");
        System.out.println(book.toString());
        book.setPrice(1000.1);
        dbManager.updateBook(book);
        book = dbManager.getBook("B-1");
        System.out.println("\n After : \n");
        System.out.println(book.toString());

        System.out.println("\n FROM XML:\n");
        System.out.println("\n Updating Author : \n");
        System.out.println("\n Before : \n");
        author = parser.getAuthor("A-2", parser.getXml());
        System.out.println(author.toString());
        author.setFirstName("Govi");
        // newBook = new Book(dbManager.getBooksGenerator().generateId());
        // newBook.setAuthor("A-2");
        // newBook.setGenre(Genre.drama);
        // newBook.setName("Cthulu and Dagon");
        // newBook.setPrice(69.69);
        // author.addBook(newBook);
        // author.removeBook(author.getBooks().get(0).getId());
        parser.updateAuthor(author, parser.getXml());
        author = parser.getAuthor("A-2", parser.getXml());
        System.out.println("\n After : \n");
        System.out.println(author.toString());

        System.out.println("\n Updating Book : \n");
        System.out.println("\n Before : \n");
        book = parser.getBook("B-1", parser.getXml());
        System.out.println(book.toString());
        book.setPrice(1000.1);
        parser.updateBook(book, parser.getXml(), true);
        book = parser.getBook("B-1", parser.getXml());
        System.out.println("\n After : \n");
        System.out.println(book.toString());



        dbManager.destroyDB();
    }
}
