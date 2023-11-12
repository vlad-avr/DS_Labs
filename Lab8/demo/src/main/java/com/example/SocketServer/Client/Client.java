package com.example.SocketServer.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.InputManager.InputManager;
import com.example.jsonParser.MyJsonParser;

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

    private Author createAuthor(String ID) {
        System.out.println("\n You are in author creation menu\n");
        Author author = new Author(ID);
        System.out.println("\n New author`s ID is " + author.getId());
        author.setFirstName(manager.getString("Enter firstname : "));
        author.setLastName(manager.getString("Enter last name : "));
        while (manager.getBool("Do you want to add a book for this author ('+' for yes and '-' for no?")) {
            // author.addBook(createBook(bookGenerator, author.getId()));
        }
        return author;
    }

    private void sendAuthorsRequest() {
        System.out.println("\n You are in author loading menu \n");
        String input;
        while (true) {
            System.out
                    .println("\n n - find by number of books;\n c - find authors whose names contain certian string;");
            input = manager.getString("Enter Command");
            switch (input) {
                case "n":
                    out.println("n");
                    out.println(manager.getInt("Enter min number of books : "));
                    out.println(manager.getInt("Enter max number of books : "));
                    return;
                case "c":
                    out.println("c");
                    out.println(manager.getString("Enter the string : "));
                    return;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
    }

    private void sendBooksRequest() {
        System.out.println("\n You are in book loading menu \n");
        String input;
        while (true) {
            System.out
                    .println(
                            "\n p - find by price;\n n - find books which names contain certian string;\n g - find books of certain genre;\n a - find books of certain author");
            input = manager.getString("Enter Command");
            switch (input) {
                case "n":
                    out.println(input);
                    out.println(manager.getString("Enter the string : "));
                    return;
                case "g":
                    out.println(input);
                    out.println(manager.getGenre("Enter the genre : "));
                    return;
                case "p":
                    out.println(input);
                    out.println(manager.getDouble("Enter min price : "));
                    out.println(manager.getDouble("Enter max price : "));
                    return;
                case "a":
                    out.println(input);
                    try {
                        out.println(manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter author id : "));
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    return;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
    }

    private void mainLoop(PrintWriter out, BufferedReader in) throws IOException {
        String input;
        input = manager.getString("Enter command : ");
        List<Author> authors;
        List<Book> books;
        Author authorTmp;
        Book bookTmp;
        String ID;
        switch (input) {
            case "sa":
                out.println("sa");
                authors = MyJsonParser.parseAuthors(in.readLine());
                if (authors == null) {
                    break;
                }
                for (Author author : authors) {
                    System.out.println(author.toString());
                }
                break;
            case "sb":
                out.println("sb");
                books = MyJsonParser.parseBooks(in.readLine());
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
                String tmp = in.readLine();
                System.out.println(tmp);
                authors = MyJsonParser.parseAuthors(tmp);
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
                books = MyJsonParser.parseBooks(in.readLine());
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
                ID = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter author ID : ");
                out.println(ID);
                authorTmp = MyJsonParser.parseAuthor(in.readLine());
                if (authorTmp != null) {
                    System.out.println(authorTmp.toString());
                }
                break;
            case "gb":
                out.println(input);
                ID = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter book ID : ");
                out.println(ID);
                bookTmp = MyJsonParser.parseBook(in.readLine());
                if (bookTmp != null) {
                    System.out.println(bookTmp.toString());
                }
                break;
            case "h":
                helpActions();
                break;
            case "e":
                System.out.println("\nYou stopped working with DB\n");
                closeClient();
                break;
            default:
                System.out.println("Invalid command!");
                break;
        }

    }
}
