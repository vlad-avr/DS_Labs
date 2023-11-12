package com.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.example.control.InputManager;
import com.example.objects.Author;
import com.example.objects.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {
    private InputManager manager = new InputManager();
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String host, int portId) {
        try {
            clientSocket = new Socket(host, portId);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void closeClient() {
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        try {
            while (clientSocket.isConnected() && !clientSocket.isClosed()) {
                mainLoop(out, in);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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

    private void sendAuthorsRequest() {
        System.out.println("\n You are in author loading menu \n");
        String input;
        while (manager.getBool("Do you want to load anything?")) {
            System.out
                    .println("\n n - find by number of books;\n c - find authors whose names contain certian string;");
            input = manager.getString("Enter Command");
            switch (input) {
                case "n":
                    out.println("n");
                    out.println(manager.getInt("Enter min number of books : "));
                    out.println(manager.getInt("Enter max number of books : "));
                    break;
                case "c":
                    out.println("c");
                    out.println(manager.getString("Enter the string : "));
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
    }

    private void sendBooksRequest() {
        System.out.println("\n You are in book loading menu \n");
        String input;
        while (manager.getBool("Do you want to load anything?")) {
            System.out
                    .println(
                            "\n p - find by price;\n n - find books which names contain certian string;\n g - find books of certain genre;\n a - find books of certain author");
            input = manager.getString("Enter Command");
            switch (input) {
                case "n":
                    out.println(input);
                    out.println(manager.getString("Enter the string : "));
                    break;
                case "g":
                    out.println(input);
                    out.println(manager.getGenre("Enter the genre : "));
                    break;
                case "p":
                    out.println(input);
                    out.println(manager.getDouble("Enter min price : "));
                    out.println(manager.getDouble("Enter max price : "));
                    break;
                // case "a":
                // out.println(input);
                // return dbManager
                // .getBooksOfAuthor(manager.getID(dbManager.getAuthorGenerator(), "Enter author
                // id : "));
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
    }

    private List<Author> parseAuthors(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Author>> ref = new TypeReference<List<Author>>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Author parseAuthor(String json) {
        if (json == "") {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Author> ref = new TypeReference<Author>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Book parseBook(String json) {
        if (json == "") {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Book> ref = new TypeReference<Book>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private List<String> parseIds(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<String>> ref = new TypeReference<List<String>>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private List<Book> parseBooks(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Book>> ref = new TypeReference<List<Book>>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void mainLoop(PrintWriter out, BufferedReader in) throws IOException {
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
                    List<Author> authors;
                    List<Book> books;
                    Author authorTmp;
                    Book bookTmp;
                    String ID;
                    switch (input) {
                        case "sa":
                            out.println("sa");
                            authors = parseAuthors(in.readLine());
                            if (authors == null) {
                                break;
                            }
                            for (Author author : authors) {
                                System.out.println(author.toString());
                            }
                            break;
                        case "sb":
                            out.println("sb");
                            books = parseBooks(in.readLine());
                            if (books == null) {
                                break;
                            }
                            for (Book book : books) {
                                System.out.println(book.toString());
                            }
                            break;
                        case "aa":
                            out.println(input);
                            ID = in.readLine();
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
                            out.println("gap");
                            sendAuthorsRequest();
                            authors = parseAuthors(in.readLine());
                            if (authors == null) {
                                break;
                            }
                            for (Author author : authors) {
                                System.out.println(author.toString());
                            }
                            break;
                        case "gbp":
                            out.println(input);
                            sendBooksRequest();
                            books = parseBooks(in.readLine());
                            if (books == null) {
                                break;
                            }
                            for (Book book : books) {
                                System.out.println(book.toString());
                            }
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
                            out.println(input);
                            ID = manager.getID(parseIds(in.readLine()), "Enter author ID : ");
                            out.println(ID);
                            authorTmp = parseAuthor(in.readLine());
                            if (authorTmp != null) {
                                System.out.println(authorTmp.toString());
                            }
                            break;
                        case "gb":
                            out.println(input);
                            ID = manager.getID(parseIds(in.readLine()), "Enter book ID : ");
                            out.println(ID);
                            bookTmp = parseBook(in.readLine());
                            if (bookTmp != null) {
                                System.out.println(bookTmp.toString());
                            }
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
            case "e":
                closeClient();
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
