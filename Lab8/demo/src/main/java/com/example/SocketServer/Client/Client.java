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
import com.example.dbManager.MyParser;
import com.example.jsonParser.MyJsonParser;

public class Client {
    private InputManager manager = new InputManager();
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean working = true;
    private MyParser parser = new MyParser();

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
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        try {
            helpActions();
            while (clientSocket.isConnected() && !clientSocket.isClosed() && working) {
                mainLoop(out, in);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        closeClient();
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

    private Author createAuthor(String Id) {
        System.out.println("\n You are in author creation menu\n");
        Author author = new Author(Id);
        System.out.println("\n New author`s ID is " + author.getId());
        author.setFirstName(manager.getString("Enter firstname : "));
        author.setLastName(manager.getString("Enter last name : "));
        while (manager.getBool("Do you want to add a book for this author ('+' for yes and '-' for no)?")) {
            out.println("bi");
            try {
                author.addBook(createBook(in.readLine(), author.getId()));
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Which means that you can`t add a book");
            }
        }
        return author;
    }

    private Book createBook(String Id, String authorId) {
        System.out.println("\n You are in book creation menu\n");
        Book book = new Book(Id);
        book.setAuthor(authorId);
        System.out.println("\n New book`s ID is " + book.getId() + " and its author is " + book.getAuthor());
        book.setName(manager.getString("Enter name : "));
        book.setPrice(manager.getDouble("Enter price : "));
        book.setGenre(manager.getGenre("Enter genre : "));
        return book;
    }

    private Book modifyBook(Book book) {
        System.out.println("\n You are in book modification menu\n");
        System.out.println("Current state : \n" + book);
        while (manager.getBool("Do you want change something? ")) {
            System.out.println(" n - change name;\n p - change price;\n g - change genre;");
            String input = manager.getString("Enter command : ");
            switch (input) {
                case "n":
                    book.setName(manager.getString("Enter name : "));
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

    private Author modifyAuthor(Author author) {
        System.out.println("\n You are in author modification menu\n");
        System.out.println("Current state : \n" + author);
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

    private void sendAuthorsRequest() {
        System.out.println("\n You are in author loading menu \n");
        String input;
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
                out.println("");
                return;
        }
    }

    private void sendBooksRequest() {
        System.out.println("\n You are in book loading menu \n");
        String input;
        System.out
                .println(
                        "\n p - find by price;\n n - find books which names contain certian string;\n g - find books of certain genre;");
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
            default:
                System.out.println("Invalid command!");
                out.println("");
                return;

        }
    }

    private void showAuthors() throws IOException {
        out.println("sa");
        List<Author> authors = MyJsonParser.parseAuthors(in.readLine());
        if (authors == null) {
            return;
        }
        for (Author author : authors) {
            System.out.println(author.toString());
        }
    }

    private void showBooks() throws IOException {
        out.println("sb");
        List<Book> books = MyJsonParser.parseBooks(in.readLine());
        if (books == null) {
            return;
        }
        for (Book book : books) {
            System.out.println(book.toString());
        }
    }

    private void addAuthor() throws IOException {
        out.println("aa");
        Author authorTmp = createAuthor(in.readLine());
        out.println(MyJsonParser.toJsonAuthor(authorTmp));
    }

    private void addBook() throws IOException {
        out.println("ab");
        String authorId = manager.getID(MyJsonParser.parseIds(in.readLine()), " Enter author ID : ");
        out.println(authorId);
        authorId = in.readLine();
        if (authorId != "") {
            String ID = in.readLine();
            Book bookTmp = createBook(ID, authorId);
            out.println(MyJsonParser.toJsonBook(bookTmp));
        } else {
            System.out.println("This id is already reserved by other client!");
        }
    }

    private void deleteAuthor() throws IOException {
        out.println("da");
        String ID = manager.getID(MyJsonParser.parseIds(in.readLine()), " Enter author ID : ");
        out.println(ID);
    }

    private void deleteBook() throws IOException {
        out.println("db");
        String ID = manager.getID(MyJsonParser.parseIds(in.readLine()), " Enter book ID : ");
        out.println(ID);
    }

    private void getAuthor() throws IOException {
        out.println("ga");
        String ID = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter author ID : ");
        out.println(ID);
        Author authorTmp = MyJsonParser.parseAuthor(in.readLine());
        if (authorTmp != null) {
            System.out.println(authorTmp.toString());
        }
    }

    private void getBook() throws IOException {
        out.println("gb");
        String ID = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter book ID : ");
        out.println(ID);
        Book bookTmp = MyJsonParser.parseBook(in.readLine());
        if (bookTmp != null) {
            System.out.println(bookTmp.toString());
        }
    }

    private void updateAuthor() throws IOException {
        out.println("ua");
        String ID = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter author Id : ");
        out.println(ID);
        String temp = in.readLine();
        if (temp != "") {
            Author authorTmp = MyJsonParser.parseAuthor(temp);
            out.println(MyJsonParser.toJsonAuthor(modifyAuthor(authorTmp)));
        } else {
            System.out.println("This id is already reserved by other client!");
        }
    }

    private void updateBook() throws IOException {
        out.println("ub");
        String ID = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter book Id : ");
        out.println(ID);
        String temp = in.readLine();
        if (temp != "") {
            Book bookTmp = MyJsonParser.parseBook(temp);
            out.println(MyJsonParser.toJsonBook(modifyBook(bookTmp)));
        } else {
            System.out.println("This id is already reserved by other client!");
        }
    }

    private void changeAuthor() throws IOException {
        out.println("ca");
        String ID = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter book Id : ");
        out.println(ID);
        String temp = in.readLine();
        if (temp != "") {
            Book bookTmp = MyJsonParser.parseBook(temp);
            String newAuthorId = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter author Id : ");
            out.println(newAuthorId);
            newAuthorId = in.readLine();
            if (newAuthorId != "") {
                bookTmp.setAuthor(newAuthorId);
                out.println(MyJsonParser.toJsonBook(bookTmp));
            } else {
                System.out.println("The author is already reserved by other client!");
            }
        } else {
            System.out.println("This id is already reserved by other client!");
        }
    }

    private void getAuthorsByParams() throws IOException {
        out.println("gap");
        sendAuthorsRequest();
        String tmp = in.readLine();
        if (tmp.equals("")) {
            return;
        }
        List<Author> authors = MyJsonParser.parseAuthors(tmp);
        for (Author author : authors) {
            System.out.println(author.toString());
        }
    }

    private void getBooksByParams() throws IOException {
        out.println("gbp");
        sendBooksRequest();
        String tmp = in.readLine();
        if (tmp.equals("")) {
            return;
        }
        List<Book> books = MyJsonParser.parseBooks(tmp);
        for (Book book : books) {
            System.out.println(book.toString());
        }
    }

    private void getBooksOfAuthor() throws IOException{
        out.println("gba");
        String tmp = manager.getID(MyJsonParser.parseIds(in.readLine()), "Enter author id : ");
        out.println(tmp);
        tmp = in.readLine();
        if(tmp.equals("")){
            System.out.println("Unable to reach the author ot his books (either deleted or corrupted data)");
            return;
        }
        List<Book> books = MyJsonParser.parseBooks(tmp);
        for(Book book : books){
            System.out.println(book.toString());
        }
    }

    private void loadToXml() throws IOException{
        //D:\\Java\\DS_Labs\\Lab8\\demo\\src\\main\\java\\com\\example\\XMLs\\Example.xml
        String path = manager.getString("Enter path to XML file : ");
        if(!parser.tryOpen(path)){
            System.out.println(" Unable to open file");
            return;
        }
        out.println("lx");
        List<Author> authors = MyJsonParser.parseAuthors(in.readLine());
        parser.writeXML(path, authors);
    }

    private void uploadFromXml(){
        String path = manager.getString("Enter path to XML file : ");
        if(!parser.tryOpen(path)){
            System.out.println(" Unable to open file");
            return;
        }
        out.println("ux");
        List<Author> authors = parser.parseSAX(path);
        out.println(MyJsonParser.toJsonAuthors(authors));
    }

    private void mainLoop(PrintWriter out, BufferedReader in) throws IOException {
        String input;
        input = manager.getString("Enter command : ");
        switch (input) {
            case "sa":
                showAuthors();
                break;
            case "sb":
                showBooks();
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
            case "gap":
                getAuthorsByParams();
                break;
            case "gbp":
                getBooksByParams();
                break;
            case "gba":
                getBooksOfAuthor();
                break;
            case "da":
                deleteAuthor();
                break;
            case "db":
                deleteBook();
                break;
            case "ga":
                getAuthor();
                break;
            case "gb":
                getBook();
                break;
            case "lx":
                loadToXml();
                break;
            case "ux":
                uploadFromXml();
                break;
            case "h":
                helpActions();
                break;
            case "e":
                System.out.println("\nYou stopped working with DB\n");
                out.println("e");
                working = false;
                return;
            default:
                System.out.println("Invalid command!");
                break;
        }

    }
}
