package com.example.RMI.Client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.InputManager.InputManager;
import com.example.RMI.Server.RemoteDBManagerInterface;

public class Client {
    private RemoteDBManagerInterface dbRemote;
    private InputManager inputManager = new InputManager();
    public Client(){
        try{
            dbRemote = (RemoteDBManagerInterface) Naming.lookup("rmi://localhost:1234/DB");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showAuthors(List<Author> authors){
        if(authors == null){
            return;
        }
        for(Author author : authors){
            System.out.println(author.toString());
        }
    }

    private void showBooks(List<Book> books){
        if(books == null){
            return;
        }
        for(Book book : books){
            System.out.println(book.toString());
        }
    }

    private void getAuthors() throws RemoteException{
        showAuthors(dbRemote.getAuthors());
    }

    private void getBooks() throws RemoteException{
        showBooks(dbRemote.getBooks());
    }

    private void getAuthorsByParams() throws RemoteException{
        System.out.println("\n You are in author loading menu \n");
        String input;
        System.out
                .println("\n n - find by number of books;\n c - find authors whose names contain certian string;");
        input = inputManager.getString("Enter Command");
        switch (input) {
            case "n":
                showAuthors(dbRemote.getAuthors(inputManager.getInt("Enter min number of books : "), inputManager.getInt("Enter max number of books : ")));
                return;
            case "c":
                showAuthors(dbRemote.getAuthors(inputManager.getString("Enter the string : ")));
                return;
            default:
                System.out.println("Invalid command!");
                return;
        }
    }

    private void getBooksByParams() throws RemoteException{
        System.out.println("\n You are in book loading menu \n");
        String input;
        System.out
                .println(
                        "\n p - find by price;\n n - find books which names contain certian string;\n g - find books of certain genre;");
        input = inputManager.getString("Enter Command");
        switch (input) {
            case "n":
                showBooks(dbRemote.getBooks(inputManager.getString("Enter the string : ")));
                return;
            case "g":
                showBooks(dbRemote.getBooks(inputManager.getGenre("Enter the genre : ")));
                return;
            case "p":
                showBooks(dbRemote.getBooks(inputManager.getDouble("Enter min price : "), inputManager.getDouble("Enter max price : ")));
                return;
            default:
                System.out.println("Invalid command!");
                return;

        }
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
                " ca - change author;\n" +
                " gba - get books of certain author;\n" +
                " da - delete author;\n" +
                " db - delete book;\n" +
                " lx - load data from DB to XML;\n" +
                " ux - upload data from XML to DB;\n" +  
                " e - exit current environment;\n" +
                " h - help;");
    }

    public void mainLoop() throws RemoteException{
        String input;
        helpActions();
        while (true) {
            input = inputManager.getString(" Enter command : ");
            switch (input) {
                case "sa":
                    getAuthors();
                    break;
                case "sb":
                    getBooks();
                    break;
                case "gap":
                    getAuthorsByParams();
                    break;
                case "gbp":
                    getBooksByParams();
                    break;
                case "h":
                    helpActions();
                    break;
                case "e":
                    System.out.println("You stopped working with current environment!");
                    return;
                default:
                    System.out.println("Invalid command!");
            }
        }
    }
}
