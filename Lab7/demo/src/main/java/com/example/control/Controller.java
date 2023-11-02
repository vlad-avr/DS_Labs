package com.example.control;

import java.util.List;

import com.example.db_controller.DatabaseManager;
import com.example.objects.Author;
import com.example.objects.Book;
import com.example.xml_parser.MyParser;

public class Controller {
    DatabaseManager dbManager = new DatabaseManager();
    MyParser parser = new MyParser("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml", "D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Schema.xsd",dbManager);

    public void start(){
        dbManager.destroyDB();
        dbManager.initDB();
        parser.parseSAX();
        System.out.println("\n ENTRY DATA : \n");
        List<Author> authors = dbManager.getAuthors();
        List<Book> books = dbManager.getBooks();
        for(Author author : authors){
            System.out.println(author.toString());
        }
        for(Book book : books){
            System.out.println(book.toString());
        }
        System.out.println("\n TESTING GETTERS : \n");
        System.out.println("\n FROM DB:\n");
        Author author = dbManager.getAuthor("A-1", true);
        System.out.println(author.toString());
        Book book = dbManager.getBook("B-1");
        System.out.println(book.toString());
        System.out.println("\n FROM XML:\n");
        author = parser.getAuthor("A-1", parser.getXml());
        System.out.println(author.toString());
        book = parser.getBook("B-1", parser.getXml());
        System.out.println(book.toString());
        dbManager.destroyDB();
    }
}
