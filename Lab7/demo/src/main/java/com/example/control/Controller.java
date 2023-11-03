package com.example.control;

import java.util.List;

import com.example.db_controller.DatabaseManager;
import com.example.db_controller.IDGenerator;
import com.example.objects.Author;
import com.example.objects.Book;
import com.example.xml_parser.MyParser;

public class Controller {
    DatabaseManager dbManager = new DatabaseManager();
    MyParser parser = new MyParser("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml",
            "D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Schema.xsd", dbManager);
    InputManager manager = new InputManager();

    public void start() {
        dbManager.destroyDB();
        dbManager.initDB();
        parser.parseSAX();
        dbManager.destroyDB();
    }

    private void mainLoop() {
        help();
        String input;
        boolean working;
        while(true){
            input = manager.getString("Enter command : ");
            switch (input) {
                case "od":
                    System.out.println("\n Now Working with Database \n");
                    helpActions();
                    working = true;
                    while (working) {
                        input = manager.getString("Enter command : ");
                        switch (input) {
                            case "aa":
                                
                                break;
                        
                            default:
                                break;
                        }
                    }
                    break;
                case "os":

                    break;
                case "e":
                    return;
                case "h":
                    help();
                    break;
                default:
                    System.out.println("Invalid Command!");
                    break;
            }
        }
    }

    private void help(){
        System.out.println("\n e - exit program;\n" + 
                " od - open DB;\n" + 
                " ox - open XML;\n" +
                " h - help;");
    }

    private void helpActions() {
        System.out.println("\n sa - show authors;\n" + 
                " sb - show books;\n" +
                " ga - get author;\n" +
                " gb - get book:\n" +
                " gap - get authors by param;\n" +
                " gbp - get books by param;\n" + 
                " aa - add author;\n" + 
                " ab - add book;\n" +
                " ua - update author;\n" +
                " ub - update book;\n" +
                " da - delete author;\n" +
                " db - delete book;\n" + 
                " e - exit current environment;\n" +
                " h - help;");
    }

    private Author createAuthor(IDGenerator idGenerator){
        Author author = new Author(idGenerator.generateId());

        return author;
    }

    private Book createBook(IDGenerator idGenerator){
        Book book = new Book(idGenerator.generateId());

        return book;
    }
}
