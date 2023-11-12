package com.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.example.control.InputManager;

public class Client {
    private InputManager manager = new InputManager();
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String host, int portId){
         try {
            clientSocket = new Socket(host, portId);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run(){
        mainLoop(out, in);
    }

    private void help() {
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

    private void mainLoop(PrintWriter out, BufferedReader in) {
        String input;
        boolean working;
        input = manager.getString("Enter command : ");
        switch (input) {
            case "od":
                System.out.println("\n Now Working with Database \n");
                helpActions();
                working = true;
                while (working) {
                    input = manager.getString("Enter command : ");
                    // List<Author> authors;
                    // List<Book> books;
                    switch (input) {
                        case "sa":
                            // authors = dbManager.getAuthors();
                            // for (Author author : authors) {
                            // System.out.println(author.toString());
                            // }
                            break;
                        case "sb":
                            // books = dbManager.getBooks();
                            // for (Book book : books) {
                            // System.out.println(book.toString());
                            // }
                            break;
                        case "aa":
                            // dbManager.addAuthor(
                            // createAuthor(dbManager.getAuthorGenerator(), dbManager.getBookGenerator()));
                            break;
                        case "ab":
                            // dbManager.addBook(
                            // createBook(dbManager.getBookGenerator(), dbManager.getAuthorGenerator()));
                            break;
                        case "ua":
                            // dbManager.updateAuthor(updateAuthor(dbManager.getAuthor(
                            // manager.getID(dbManager.getAuthorGenerator(), "Enter author ID : "),
                            // false)));
                            break;
                        case "ub":
                            // dbManager.updateBook(updateBook(
                            // dbManager.getBook(
                            // manager.getID(dbManager.getBookGenerator(), "Enter book ID : ")),
                            // dbManager.getAuthorGenerator()));
                            break;
                        case "gap":
                            // authors = getAuthorsByParamsDB();
                            // if (authors != null) {
                            // for (Author author : authors) {
                            // System.out.println(author.toString());
                            // }
                            // }
                            break;
                        case "gbp":
                            // books = getBooksByParamsDB();
                            // if (books != null) {
                            // for (Book book : books) {
                            // System.out.println(book.toString());
                            // }
                            // }
                            break;
                        case "da":
                            // dbManager.deleteAuthor(
                            // manager.getID(dbManager.getAuthorGenerator(), "Enter author id : "));
                            break;
                        case "db":
                            // dbManager.deleteBook(manager.getID(dbManager.getBookGenerator(), "Enter book
                            // id : "));
                            break;
                        case "ga":
                            // System.out.println(dbManager
                            // .getAuthor(manager.getID(dbManager.getAuthorGenerator(), "Enter author id :
                            // "),
                            // true)
                            // .toString());
                            break;
                        case "gb":
                            // System.out.println(dbManager
                            // .getBook(manager.getID(dbManager.getBookGenerator(), "Enter author id : "))
                            // .toString());
                            break;
                        case "h":
                            helpActions();
                            break;
                        case "e":
                            System.out.println("\nYou stopped working with DB\n");
                            working = false;
                            break;
                        default:
                            System.out.println("Invalid command!");
                            break;
                    }
                }
                break;
            // case "ox":
            // System.out.println("\n Now Working with Database \n");
            // System.out.println("\n Open XML file as a data source: \n");
            // parser.getXml(manager.getString("Enter xml file path : "));
            // if (parser.isOk()) {
            // System.out.println("\n File opened successfully!\n");
            // helpActions();
            // working = true;
            // while (working) {
            // input = manager.getString("Enter command : ");
            // List<Author> authors;
            // List<Book> books;
            // switch (input) {
            // case "sa":
            // authors = parser.getAuthors();
            // for (Author author : authors) {
            // System.out.println(author.toString());
            // }
            // break;
            // case "sb":
            // books = parser.getBooks();
            // for (Book book : books) {
            // System.out.println(book.toString());
            // }
            // break;
            // case "aa":
            // parser.addAuthor(createAuthor(parser.getAuthorGenerator(),
            // parser.getBookGenerator()));
            // break;
            // case "ab":
            // parser.addBook(
            // createBook(parser.getBookGenerator(), parser.getAuthorGenerator()));
            // break;
            // case "ua":
            // parser.updateAuthor(updateAuthor(parser.getAuthor(
            // manager.getID(parser.getAuthorGenerator(), "Enter author ID : "))));
            // break;
            // case "ub":
            // parser.updateBook(updateBook(
            // parser.getBook(
            // manager.getID(parser.getBookGenerator(), "Enter book ID : ")),
            // parser.getAuthorGenerator()), true);
            // break;
            // case "gap":
            // authors = getAuthorsByParamsXML();
            // if (authors != null) {
            // for (Author author : authors) {
            // System.out.println(author.toString());
            // }
            // }
            // break;
            // case "gbp":
            // books = getBooksByParamsXML();
            // if (books != null) {
            // for (Book book : books) {
            // System.out.println(book.toString());
            // }
            // }
            // break;
            // case "da":
            // parser.deleteAuthor(
            // manager.getID(parser.getAuthorGenerator(), "Enter author id : "));
            // break;
            // case "db":
            // parser.deleteBook(
            // manager.getID(parser.getBookGenerator(), "Enter book id : "));
            // break;
            // case "ga":
            // System.out.println(parser
            // .getAuthor(
            // manager.getID(parser.getAuthorGenerator(),
            // "Enter author id : "))
            // .toString());
            // break;
            // case "gb":
            // System.out.println(parser
            // .getBook(manager.getID(parser.getBookGenerator(), "Enter author id : "))
            // .toString());
            // break;
            // case "h":
            // helpActions();
            // break;
            // case "e":
            // System.out.println("\nYou stopped working with XML\n");
            // working = false;
            // break;
            // default:
            // System.out.println("Invalid command!");
            // break;
            // }
            // }
            // } else {
            // System.out.println(
            // "\n Unble to open XML file (file not found or file structure wasn`t validated
            // by XSD\n");
            // }
            // break;
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
