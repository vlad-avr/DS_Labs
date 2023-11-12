package com.example.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import com.example.control.InputManager;
import com.example.objects.Author;
import com.example.objects.Book;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private InputManager manager = new InputManager();

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeClient();
        }
    }

    private void closeClient() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {

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

    private void mainLoop() {
        help();
        String input;
        boolean working;
        while (true) {
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
                        switch (input) {
                            case "sa":
                                // authors = dbManager.getAuthors();
                                // for (Author author : authors) {
                                //     System.out.println(author.toString());
                                // }
                                break;
                            case "sb":
                                // books = dbManager.getBooks();
                                // for (Book book : books) {
                                //     System.out.println(book.toString());
                                // }
                                break;
                            case "aa":
                                // dbManager.addAuthor(
                                //         createAuthor(dbManager.getAuthorGenerator(), dbManager.getBookGenerator()));
                                break;
                            case "ab":
                                // dbManager.addBook(
                                //         createBook(dbManager.getBookGenerator(), dbManager.getAuthorGenerator()));
                                break;
                            case "ua":
                                // dbManager.updateAuthor(updateAuthor(dbManager.getAuthor(
                                //         manager.getID(dbManager.getAuthorGenerator(), "Enter author ID : "), false)));
                                break;
                            case "ub":
                                // dbManager.updateBook(updateBook(
                                //         dbManager.getBook(
                                //                 manager.getID(dbManager.getBookGenerator(), "Enter book ID : ")),
                                //         dbManager.getAuthorGenerator()));
                                break;
                            case "gap":
                                // authors = getAuthorsByParamsDB();
                                // if (authors != null) {
                                //     for (Author author : authors) {
                                //         System.out.println(author.toString());
                                //     }
                                // }
                                break;
                            case "gbp":
                                // books = getBooksByParamsDB();
                                // if (books != null) {
                                //     for (Book book : books) {
                                //         System.out.println(book.toString());
                                //     }
                                // }
                                break;
                            case "da":
                                // dbManager.deleteAuthor(
                                //         manager.getID(dbManager.getAuthorGenerator(), "Enter author id : "));
                                break;
                            case "db":
                                // dbManager.deleteBook(manager.getID(dbManager.getBookGenerator(), "Enter book id : "));
                                break;
                            case "ga":
                                // System.out.println(dbManager
                                //         .getAuthor(manager.getID(dbManager.getAuthorGenerator(), "Enter author id : "),
                                //                 true)
                                //         .toString());
                                break;
                            case "gb":
                                // System.out.println(dbManager
                                //         .getBook(manager.getID(dbManager.getBookGenerator(), "Enter author id : "))
                                //         .toString());
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
                //     System.out.println("\n Now Working with Database \n");
                //     System.out.println("\n Open XML file as a data source: \n");
                //     parser.getXml(manager.getString("Enter xml file path : "));
                //     if (parser.isOk()) {
                //         System.out.println("\n File opened successfully!\n");
                //         helpActions();
                //         working = true;
                //         while (working) {
                //             input = manager.getString("Enter command : ");
                //             List<Author> authors;
                //             List<Book> books;
                //             switch (input) {
                //                 case "sa":
                //                     authors = parser.getAuthors();
                //                     for (Author author : authors) {
                //                         System.out.println(author.toString());
                //                     }
                //                     break;
                //                 case "sb":
                //                     books = parser.getBooks();
                //                     for (Book book : books) {
                //                         System.out.println(book.toString());
                //                     }
                //                     break;
                //                 case "aa":
                //                     parser.addAuthor(createAuthor(parser.getAuthorGenerator(),
                //                             parser.getBookGenerator()));
                //                     break;
                //                 case "ab":
                //                     parser.addBook(
                //                             createBook(parser.getBookGenerator(), parser.getAuthorGenerator()));
                //                     break;
                //                 case "ua":
                //                     parser.updateAuthor(updateAuthor(parser.getAuthor(
                //                             manager.getID(parser.getAuthorGenerator(), "Enter author ID : "))));
                //                     break;
                //                 case "ub":
                //                     parser.updateBook(updateBook(
                //                             parser.getBook(
                //                                     manager.getID(parser.getBookGenerator(), "Enter book ID : ")),
                //                             parser.getAuthorGenerator()), true);
                //                     break;
                //                 case "gap":
                //                     authors = getAuthorsByParamsXML();
                //                     if (authors != null) {
                //                         for (Author author : authors) {
                //                             System.out.println(author.toString());
                //                         }
                //                     }
                //                     break;
                //                 case "gbp":
                //                     books = getBooksByParamsXML();
                //                     if (books != null) {
                //                         for (Book book : books) {
                //                             System.out.println(book.toString());
                //                         }
                //                     }
                //                     break;
                //                 case "da":
                //                     parser.deleteAuthor(
                //                             manager.getID(parser.getAuthorGenerator(), "Enter author id : "));
                //                     break;
                //                 case "db":
                //                     parser.deleteBook(
                //                             manager.getID(parser.getBookGenerator(), "Enter book id : "));
                //                     break;
                //                 case "ga":
                //                     System.out.println(parser
                //                             .getAuthor(
                //                                     manager.getID(parser.getAuthorGenerator(),
                //                                             "Enter author id : "))
                //                             .toString());
                //                     break;
                //                 case "gb":
                //                     System.out.println(parser
                //                             .getBook(manager.getID(parser.getBookGenerator(), "Enter author id : "))
                //                             .toString());
                //                     break;
                //                 case "h":
                //                     helpActions();
                //                     break;
                //                 case "e":
                //                     System.out.println("\nYou stopped working with XML\n");
                //                     working = false;
                //                     break;
                //                 default:
                //                     System.out.println("Invalid command!");
                //                     break;
                //             }
                //         }
                //     } else {
                //         System.out.println(
                //                 "\n Unble to open XML file (file not found or file structure wasn`t validated by XSD\n");
                //     }
                //     break;
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
}
