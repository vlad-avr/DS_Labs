package com.example.control;

import java.util.List;

import com.example.db_controller.DatabaseManager;
import com.example.objects.Author;
import com.example.objects.Book;
import com.example.xml_parser.MyParser;

public class Controller {
    DatabaseManager dbManager = new DatabaseManager();
    MyParser parser = new MyParser("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml", dbManager);

    public void start(){
        dbManager.initDB();
        parser.parseSAX();
        List<Author> authors = dbManager.getAuthors();
        for(Author author : authors){
            System.out.println(author.toString());
        }
        List<Book> books = dbManager.getBooks();
        for(Book book : books){
            System.out.println(book.toString());
        }
        dbManager.destroyDB();
    }
}
