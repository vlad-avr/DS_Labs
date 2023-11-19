package com.example.RMI.Client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.InputManager.InputManager;
import com.example.RMI.Server.RemoteDBManagerInterface;
import com.example.dbManager.MyParser;

public class Client {
    private RemoteDBManagerInterface dbRemote;
    private InputManager inputManager = new InputManager();
    private MyParser parser = new MyParser();

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

    private void getAuthor() throws RemoteException{
        Author author = dbRemote.getAuthor(inputManager.getID(dbRemote.getAuthorIds(), "Enter author Id : "));
        if(author != null){
            System.out.println(author.toString());
            return;
        }
        System.out.println("Author with this Id no longer exists!");
    }

    private void getBook() throws RemoteException{
        Book book = dbRemote.getBook(inputManager.getID(dbRemote.getBookIds(), "Enter book Id : "));
        if(book != null){
            System.out.println(book.toString());
            return;
        }
        System.out.println("Book with this Id no longer exists!");
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

    private Author createAuthor(String Id) throws RemoteException{
        System.out.println("\n You are in author creation menu\n");
        Author author = new Author(Id);
        System.out.println("\n New author`s ID is " + author.getId());
        author.setFirstName(inputManager.getString("Enter firstname : "));
        author.setLastName(inputManager.getString("Enter last name : "));
        while (inputManager.getBool("Do you want to add a book for this author ('+' for yes and '-' for no)?")) {
            author.addBook(createBook(dbRemote.generateBookId(), author.getId()));
        }
        return author;
    }

    private Book createBook(String Id, String authorId){
        System.out.println("\n You are in book creation menu\n");
        Book book = new Book(Id);
        book.setAuthor(authorId);
        System.out.println("\n New book`s ID is " + book.getId() + " and its author is " + book.getAuthor());
        book.setName(inputManager.getString("Enter name : "));
        book.setPrice(inputManager.getDouble("Enter price : "));
        book.setGenre(inputManager.getGenre("Enter genre : "));
        return book;
    }

    private Author modifyAuthor(Author author){
        System.out.println("\n You are in author modification menu\n");
        System.out.println("Current state : \n" + author);
        while (inputManager.getBool("Do you want change something? ")) {
            System.out.println(" f - change firstname;\n l - change lastname;");
            String input = inputManager.getString("Enter command : ");
            switch (input) {
                case "f":
                    author.setFirstName(inputManager.getString("Enter firstname : "));
                    break;
                case "l":
                    author.setLastName(inputManager.getString("Enter lastname : "));
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
        return author;
    }

    private Book modifyBook(Book book){
        System.out.println("\n You are in book modification menu\n");
        System.out.println("Current state : \n" + book);
        while (inputManager.getBool("Do you want change something? ")) {
            System.out.println(" n - change name;\n p - change price;\n g - change genre;");
            String input = inputManager.getString("Enter command : ");
            switch (input) {
                case "n":
                    book.setName(inputManager.getString("Enter name : "));
                    break;
                case "p":
                    book.setPrice(inputManager.getDouble("Enter price : "));
                    break;
                case "g":
                    book.setGenre(inputManager.getGenre("Enter genre"));
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
        return book;
    }

    private void updateAuthor() throws RemoteException{
        String Id = inputManager.getID(dbRemote.getAuthorIds(), "Enter author Id : ");
        boolean reserved = dbRemote.reserveAuthor(Id);
        if(reserved){
            Author author = modifyAuthor(dbRemote.getAuthor(Id));
            dbRemote.updateAuthor(author);
            return;
        }
        System.out.println("Unable to acquire lock on author with Id " + Id);
    }

    private void updateBook() throws RemoteException{
        String Id = inputManager.getID(dbRemote.getBookIds(), "Enter book Id : ");
        boolean reserved = dbRemote.reserveBook(Id);
        if(reserved){
            Book book = modifyBook(dbRemote.getBook(Id));
            dbRemote.updateBook(book);
            return;
        }
        System.out.println("Unable to acquire lock on book with Id " + Id);
    }

    private void changeAuthor() throws RemoteException{
        String Id = inputManager.getID(dbRemote.getBookIds(), "Enter book Id : ");
        boolean reserved = dbRemote.reserveBook(Id);
        if(reserved){
            Book book = dbRemote.getBook(Id);
            String newAuthor = inputManager.getID(dbRemote.getAuthorIds(), "Enter new author Id : ");
            boolean authorReserved = dbRemote.reserveAuthor(newAuthor);
            if(authorReserved){
                book.setAuthor(newAuthor);
                dbRemote.updateBookAuthor(book);
                return;
            }
            System.out.println("Unable to acquire lock on author with id " + newAuthor);
            return;
        }
        System.out.println("Unable to acquire lock on book with id " + Id);
    }

    private void addAuthor() throws RemoteException{
        dbRemote.addAuthor(createAuthor(dbRemote.generateAuthorId()));
    }

    private void addBook() throws RemoteException{
        String authorId = inputManager.getID(dbRemote.getAuthorIds(), "Enter author Id : ");
        dbRemote.addBook(createBook(dbRemote.generateBookId() , authorId));
    }

    private void getBooksOfAuthor() throws RemoteException{
        String id = inputManager.getID(dbRemote.getAuthorIds(), "Enter author id : ");
        showBooks(dbRemote.getBooksOfAuthor(id));
    }

    private void deleteAuthor() throws RemoteException{
        String Id = inputManager.getID(dbRemote.getAuthorIds(), "Enter author Id : ");
        boolean reserved = dbRemote.reserveAuthor(Id);
        if(reserved){
            dbRemote.deleteAuthor(Id);
            return;
        }
        System.out.println("Unable to acquire lock on author with id " + Id);
    }

    private void deleteBook() throws RemoteException{
        String Id = inputManager.getID(dbRemote.getBookIds(), "Enter book Id : ");
        boolean reserved = dbRemote.reserveBook(Id);
        if(reserved){
            dbRemote.deleteBook(Id);
            return;
        }
        System.out.println("Unable to acquire lock on book with id " + Id);
    }

    private void loadToXML() throws RemoteException{
        //D:\\Java\\DS_Labs\\Lab8\\demo\\src\\main\\java\\com\\example\\XMLs\\Example.xml
        String path = inputManager.getString("Enter path to XML file : ");
        if(!parser.tryOpen(path)){
            System.out.println(" Unable to open file");
            return;
        }
        List<Author> authors = dbRemote.getAuthors();
        parser.writeXML(path, authors);
    }

    private void uploadFromXML() throws RemoteException{
        String path = inputManager.getString("Enter path to XML file : ");
        if(!parser.tryOpen(path)){
            System.out.println(" Unable to open file");
            return;
        }
        List<Author> authors = parser.parseSAX(path);
        dbRemote.uploadFromXML(authors);
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
                case "gba":
                    getBooksOfAuthor();
                    break;
                case "ga":
                    getAuthor();
                    break;
                case "gb":
                    getBook();
                    break;
                case "aa":
                    addAuthor();
                    break;
                case "ab":
                    addBook();
                    break;
                case "ua":
                    updateAuthor();
                    break;
                case "ub":
                    updateBook();
                    break;
                case "ca":
                    changeAuthor();
                    break;
                case "da":
                    deleteAuthor();
                    break;
                case "db":
                    deleteBook();
                    break;
                case "lx":
                    loadToXML();
                    break;
                case "ux":
                    uploadFromXML();
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
