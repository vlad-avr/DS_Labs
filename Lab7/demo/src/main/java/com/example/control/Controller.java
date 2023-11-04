package com.example.control;

import java.util.List;

import com.example.db_controller.DatabaseManager;
import com.example.db_controller.IDGenerator;
import com.example.objects.Author;
import com.example.objects.Book;
import com.example.xml_parser.MyParser;

//"D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml"
//"D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Dynamic.xml"

public class Controller {
    DatabaseManager dbManager = new DatabaseManager();
    MyParser parser = new MyParser("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Schema.xsd",
            dbManager);
    InputManager manager = new InputManager();

    public void start() {
        dbManager.destroyDB();
        dbManager.initDB();
        parser.parseSAX("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml");
        mainLoop();
        dbManager.destroyDB();
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
                                authors = dbManager.getAuthors();
                                for (Author author : authors) {
                                    System.out.println(author.toString());
                                }
                                break;
                            case "sb":
                                books = dbManager.getBooks();
                                for (Book book : books) {
                                    System.out.println(book.toString());
                                }
                                break;
                            case "aa":
                                dbManager.addAuthor(
                                        createAuthor(dbManager.getAuthorsGenerator(), dbManager.getBooksGenerator()));
                                break;
                            case "ab":
                                dbManager.addBook(
                                        createBook(dbManager.getBooksGenerator(), dbManager.getAuthorsGenerator()));
                                break;
                            case "ua":
                                dbManager.updateAuthor(updateAuthor(dbManager.getAuthor(
                                        manager.getID(dbManager.getAuthorsGenerator(), "Enter author ID : "), false)));
                                break;
                            case "ub":
                                dbManager.updateBook(updateBook(
                                        dbManager.getBook(
                                                manager.getID(dbManager.getBooksGenerator(), "Enter book ID : ")),
                                        dbManager.getAuthorsGenerator()));
                                break;
                            case "gap":
                                authors = getAuthorsByParams();
                                if (authors != null) {
                                    for (Author author : authors) {
                                        System.out.println(author.toString());
                                    }
                                }
                                break;
                            case "gbp":
                                books = getBooksByParams();
                                if (books != null) {
                                    for (Book book : books) {
                                        System.out.println(book.toString());
                                    }
                                }
                                break;
                            case "da":
                                dbManager.deleteAuthor(
                                        manager.getID(dbManager.getAuthorsGenerator(), "Enter author id : "));
                                break;
                            case "db":
                                dbManager.deleteBook(manager.getID(dbManager.getBooksGenerator(), "Enter book id : "));
                                break;
                            case "ga":
                                System.out.println(dbManager
                                        .getAuthor(manager.getID(dbManager.getAuthorsGenerator(), "Enter author id : "),
                                                true)
                                        .toString());
                                break;
                            case "gb":
                                System.out.println(dbManager
                                        .getBook(manager.getID(dbManager.getBooksGenerator(), "Enter author id : "))
                                        .toString());
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
                case "ox":
                    System.out.println("\n Now Working with Database \n");
                    System.out.println("\n Open XML file as a data source: \n");
                    parser.getXml(manager.getString("Enter xml file path : "));
                    if (parser.isOk()) {
                        System.out.println("\n File opened successfully!\n");
                        helpActions();
                        working = true;
                        while (working) {
                            input = manager.getString("Enter command : ");
                            List<Author> authors;
                            List<Book> books;
                            switch (input) {
                                case "sa":
                                    authors = parser.getAuthors();
                                    for (Author author : authors) {
                                        System.out.println(author.toString());
                                    }
                                    break;
                                case "sb":
                                    books = parser.getBooks();
                                    for (Book book : books) {
                                        System.out.println(book.toString());
                                    }
                                    break;
                                case "aa":
                                    parser.addAuthor(createAuthor(parser.getAuthorGenerator(),
                                            parser.getBookGenerator()));
                                    break;
                                case "ab":
                                    parser.addBook(
                                            createBook(parser.getBookGenerator(), parser.getAuthorGenerator()));
                                    break;
                                case "ua":
                                    parser.updateAuthor(updateAuthor(parser.getAuthor(
                                            manager.getID(parser.getAuthorGenerator(), "Enter author ID : "))));
                                    break;
                                case "ub":
                                    parser.updateBook(updateBook(
                                            parser.getBook(
                                                    manager.getID(parser.getBookGenerator(), "Enter book ID : ")),
                                            parser.getAuthorGenerator()), true);
                                    break;
                                case "gap":
                                    // authors = getAuthorsByParams();
                                    // if (authors != null) {
                                    // for (Author author : authors) {
                                    // System.out.println(author.toString());
                                    // }
                                    // }
                                    break;
                                case "gbp":
                                    // books = getBooksByParams();
                                    // if (books != null) {
                                    // for (Book book : books) {
                                    // System.out.println(book.toString());
                                    // }
                                    // }
                                    break;
                                case "da":
                                    parser.deleteAuthor(
                                            manager.getID(parser.getAuthorGenerator(), "Enter author id : "));
                                    break;
                                case "db":
                                    parser.deleteBook(
                                            manager.getID(parser.getBookGenerator(), "Enter book id : "));
                                    break;
                                case "ga":
                                    System.out.println(parser
                                            .getAuthor(
                                                    manager.getID(parser.getAuthorGenerator(),
                                                            "Enter author id : "))
                                            .toString());
                                    break;
                                case "gb":
                                    System.out.println(parser
                                            .getBook(manager.getID(parser.getBookGenerator(), "Enter author id : "))
                                            .toString());
                                    break;
                                case "h":
                                    helpActions();
                                    break;
                                case "e":
                                    System.out.println("\nYou stopped working with XML\n");
                                    working = false;
                                    break;
                                default:
                                    System.out.println("Invalid command!");
                                    break;
                            }
                        }
                    } else {
                        System.out.println(
                                "\n Unble to open XML file (file not found or file structure wasn`t validated by XSD\n");
                    }
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

    private List<Author> getAuthorsByParams() {
        System.out.println("\n You are in author loading menu \n");
        String input;
        while (manager.getBool("Do you want to load anything?")) {
            System.out
                    .println("\n n - find by number of books;\n c - find authors whose names contain certian string;");
            input = manager.getString("Enter Command");
            switch (input) {
                case "n":
                    return dbManager.getAuthors(manager.getInt("Enter min number of books : "),
                            manager.getInt("Enter max number of books : "));
                case "c":
                    return dbManager.getAuthors(manager.getString("Enter the string : "));
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
        return null;
    }

    private List<Book> getBooksByParams() {
        System.out.println("\n You are in book loading menu \n");
        String input;
        while (manager.getBool("Do you want to load anything?")) {
            System.out
                    .println(
                            "\n p - find by price;\n n - find books which names contain certian string;\n g - find books of certain genre;\n a - find books of certain author");
            input = manager.getString("Enter Command");
            switch (input) {
                case "n":
                    return dbManager.getBooks(manager.getString("Enter the string : "));
                case "g":
                    return dbManager.getBooks(manager.getGenre("Enter the genre : "));
                case "p":
                    return dbManager.getBooks(manager.getDouble("Enter min price : "),
                            manager.getDouble("Enter max price : "));
                case "a":
                    return dbManager
                            .getBooksOfAuthor(manager.getID(dbManager.getAuthorsGenerator(), "Enter author id : "));
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
        return null;
    }

    private Author createAuthor(IDGenerator idGenerator, IDGenerator bookGenerator) {
        System.out.println("\n You are in author creation menu\n");
        Author author = new Author(idGenerator.generateId());
        System.out.println("\n New author`s ID is " + author.getId());
        author.setFirstName(manager.getString("Enter firstname : "));
        author.setLastName(manager.getString("Enter last name : "));
        while (manager.getBool("Do you want to add a book for this author ('+' for yes and '-' for no?")) {
            author.addBook(createBook(bookGenerator, author.getId()));
        }
        return author;
    }

    private Book createBook(IDGenerator idGenerator, IDGenerator authorGenerator) {
        return createBook(idGenerator, manager.getID(authorGenerator, "Enter author ID : "));
    }

    private Book createBook(IDGenerator idGenerator, String authorID) {
        System.out.println("\n You are in book creation menu\n");
        Book book = new Book(idGenerator.generateId());
        book.setAuthor(authorID);
        System.out.println("\n New author`s ID is " + book.getId() + " and its author is " + book.getAuthor());
        book.setName(manager.getString("Enter name : "));
        book.setPrice(manager.getDouble("Enter price : "));
        book.setGenre(manager.getGenre("Enter genre : "));
        return book;
    }

    private Author updateAuthor(Author author) {
        System.out.println("\n You are in author modification menu\n");
        while (manager.getBool("Do you want change something? ")) {
            System.out.println(" f - change firstname;\n l - change lastname;");
            String input = manager.getString("Enter command : ");
            switch (input) {
                case "f":
                    author.setFirstName(manager.getString("Enter firstname : "));
                    break;
                case "l":
                    author.setLastName(manager.getString("Enter lastname : "));
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
        return author;
    }

    private Book updateBook(Book book, IDGenerator authorGenerator) {
        System.out.println("\n You are in book modification menu\n");
        while (manager.getBool("Do you want change something? ")) {
            System.out.println(" a - change author;\n n - change name;\n p - change price;\n g - change genre;");
            String input = manager.getString("Enter command : ");
            switch (input) {
                case "a":
                    book.setAuthor(manager.getID(authorGenerator, "Enter author ID : "));
                    break;
                case "n":
                    book.setName(manager.getString("Enter namr : "));
                    break;
                case "p":
                    book.setPrice(manager.getDouble("Enter price : "));
                    break;
                case "g":
                    book.setGenre(manager.getGenre("Enter genre"));
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
        return book;
    }
}
