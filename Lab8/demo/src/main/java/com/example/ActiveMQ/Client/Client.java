package com.example.ActiveMQ.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.InputManager.InputManager;
import com.example.jsonParser.MyJsonParser;

public class Client {

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private MessageProducer producer;
    private InputManager manager = new InputManager();
    private boolean working = true;

    public Client() {
        try {
            // Create a connection factory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("main");
            MessageConsumer consumer = session.createConsumer(destination);
            MessageProducer producer = session.createProducer(destination);
            producer.send(session.createTextMessage("c"));
            // Receive the message
            Message message = consumer.receive();

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                run("Client" + textMessage.getText());
            } else {
                System.out.println("Unexpected message type received");
            }
            producer.close();
            consumer.close();
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void run(String qName) {
        try {
            Queue q = session.createQueue(qName);
            producer = session.createProducer(q);
            consumer = session.createConsumer(q);
            helpActions();
            while (working) {
                mainLoop();
            }
        } catch (IOException | JMSException e) {
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
                " ca - change author;\n" +
                " gba - get books of certain author;\n" +
                " da - delete author;\n" +
                " db - delete book;\n" +
                " lx - load data from DB to XML;\n" +
                " ux - upload data from XML to DB;\n" +
                " e - exit current environment;\n" +
                " h - help;");
    }

    private Author createAuthor(String Id) throws JMSException {
        System.out.println("\n You are in author creation menu\n");
        Author author = new Author(Id);
        System.out.println("\n New author`s ID is " + author.getId());
        author.setFirstName(manager.getString("Enter firstname : "));
        author.setLastName(manager.getString("Enter last name : "));
        while (manager.getBool("Do you want to add a book for this author ('+' for yes and '-' for no)?")) {
            producer.send(session.createTextMessage("bi"));
            author.addBook(createBook(((TextMessage) consumer.receive()).getText(), author.getId()));
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

    private void sendAuthorsRequest() throws JMSException {
        System.out.println("\n You are in author loading menu \n");
        String input;
        System.out
                .println("\n n - find by number of books;\n c - find authors whose names contain certian string;");
        input = manager.getString("Enter Command");
        switch (input) {
            case "n":
                producer.send(session.createTextMessage("n"));
                producer.send(session.createTextMessage(String.valueOf(manager.getInt("Enter min number of books : "))));
                producer.send(session.createTextMessage(String.valueOf(manager.getInt("Enter max number of books : "))));
                return;
            case "c":
                producer.send(session.createTextMessage("c"));
                producer.send(session.createTextMessage(manager.getString("Enter the string : ")));
                return;
            default:
                System.out.println("Invalid command!");
                producer.send(session.createTextMessage(""));
                return;
        }
    }

    private void sendBooksRequest() throws JMSException {
        System.out.println("\n You are in book loading menu \n");
        String input;
        System.out
                .println(
                        "\n p - find by price;\n n - find books which names contain certian string;\n g - find books of certain genre;");
        input = manager.getString("Enter Command");
        switch (input) {
            case "n":
                producer.send(session.createTextMessage(input));
                producer.send(session.createTextMessage(manager.getString("Enter the string : ")));
                return;
            case "g":
                producer.send(session.createTextMessage(input));
                producer.send(session.createTextMessage(String.valueOf(manager.getGenre("Enter the genre : "))));
                return;
            case "p":
                producer.send(session.createTextMessage(input));
                producer.send(session.createTextMessage(String.valueOf(manager.getDouble("Enter min price : "))));
                producer.send(session.createTextMessage(String.valueOf(manager.getDouble("Enter max price : "))));
                return;
            default:
                System.out.println("Invalid command!");
                producer.send(session.createTextMessage(""));
                return;

        }
    }

    private void showAuthors() throws IOException, JMSException {
        producer.send(session.createTextMessage("sa"));
        List<Author> authors = MyJsonParser.parseAuthors(((TextMessage) consumer.receive()).getText());
        if (authors == null) {
            return;
        }
        for (Author author : authors) {
            System.out.println(author.toString());
        }
    }

    private void showBooks() throws IOException, JMSException {
        producer.send(session.createTextMessage("sb"));
        List<Book> books = MyJsonParser.parseBooks(((TextMessage) consumer.receive()).getText());
        if (books == null) {
            return;
        }
        for (Book book : books) {
            System.out.println(book.toString());
        }
    }

    private void addAuthor() throws IOException, JMSException {
        producer.send(session.createTextMessage("aa"));
        Author authorTmp = createAuthor(((TextMessage) consumer.receive()).getText());
        producer.send(session.createTextMessage(MyJsonParser.toJsonAuthor(authorTmp)));
    }

    private void addBook() throws IOException, JMSException {
        producer.send(session.createTextMessage("ab"));
        String authorId = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                " Enter author ID : ");
        producer.send(session.createTextMessage(authorId));
        authorId = ((TextMessage) consumer.receive()).getText();
        if (authorId != "") {
            String ID = ((TextMessage) consumer.receive()).getText();
            Book bookTmp = createBook(ID, authorId);
            producer.send(session.createTextMessage(MyJsonParser.toJsonBook(bookTmp)));
        } else {
            System.out.println("This id is already reserved by other client!");
        }
    }

    private void deleteAuthor() throws IOException, JMSException {
        producer.send(session.createTextMessage("da"));
        String ID = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                " Enter author ID : ");
        producer.send(session.createTextMessage(ID));
    }

    private void deleteBook() throws IOException, JMSException {
        producer.send(session.createTextMessage("db"));
        String ID = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                " Enter book ID : ");
        producer.send(session.createTextMessage(ID));
    }

    private void getAuthor() throws IOException {
        out.println("ga");
        String ID = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                "Enter author ID : ");
        out.println(ID);
        Author authorTmp = MyJsonParser.parseAuthor(((TextMessage) consumer.receive()).getText());
        if (authorTmp != null) {
            System.out.println(authorTmp.toString());
        }
    }

    private void getBook() throws IOException {
        out.println("gb");
        String ID = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                "Enter book ID : ");
        out.println(ID);
        Book bookTmp = MyJsonParser.parseBook(((TextMessage) consumer.receive()).getText());
        if (bookTmp != null) {
            System.out.println(bookTmp.toString());
        }
    }

    private void updateAuthor() throws IOException {
        out.println("ua");
        String ID = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                "Enter author Id : ");
        out.println(ID);
        String temp = ((TextMessage) consumer.receive()).getText();
        if (temp != "") {
            Author authorTmp = MyJsonParser.parseAuthor(temp);
            out.println(MyJsonParser.toJsonAuthor(modifyAuthor(authorTmp)));
        } else {
            System.out.println("This id is already reserved by other client!");
        }
    }

    private void updateBook() throws IOException {
        out.println("ub");
        String ID = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                "Enter book Id : ");
        out.println(ID);
        String temp = ((TextMessage) consumer.receive()).getText();
        if (temp != "") {
            Book bookTmp = MyJsonParser.parseBook(temp);
            out.println(MyJsonParser.toJsonBook(modifyBook(bookTmp)));
        } else {
            System.out.println("This id is already reserved by other client!");
        }
    }

    private void changeAuthor() throws IOException {
        out.println("ca");
        String ID = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                "Enter book Id : ");
        out.println(ID);
        String temp = ((TextMessage) consumer.receive()).getText();
        if (temp != "") {
            Book bookTmp = MyJsonParser.parseBook(temp);
            String newAuthorId = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                    "Enter author Id : ");
            out.println(newAuthorId);
            newAuthorId = ((TextMessage) consumer.receive()).getText();
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
        String tmp = ((TextMessage) consumer.receive()).getText();
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
        String tmp = ((TextMessage) consumer.receive()).getText();
        if (tmp.equals("")) {
            return;
        }
        List<Book> books = MyJsonParser.parseBooks(tmp);
        for (Book book : books) {
            System.out.println(book.toString());
        }
    }

    private void getBooksOfAuthor() throws IOException {
        out.println("gba");
        String tmp = manager.getID(MyJsonParser.parseIds(((TextMessage) consumer.receive()).getText()),
                "Enter author id : ");
        out.println(tmp);
        tmp = ((TextMessage) consumer.receive()).getText();
        if (tmp.equals("")) {
            System.out.println("Unable to reach the author ot his books (either deleted or corrupted data)");
            return;
        }
        List<Book> books = MyJsonParser.parseBooks(tmp);
        for (Book book : books) {
            System.out.println(book.toString());
        }
    }

    private void loadToXml() throws IOException {
        // D:\\Java\\DS_Labs\\Lab8\\demo\\src\\main\\java\\com\\example\\XMLs\\Example.xml
        String path = manager.getString("Enter path to XML file : ");
        if (!parser.tryOpen(path)) {
            System.out.println(" Unable to open file");
            return;
        }
        out.println("lx");
        List<Author> authors = MyJsonParser.parseAuthors(((TextMessage) consumer.receive()).getText());
        parser.writeXML(path, authors);
    }

    private void uploadFromXml() {
        String path = manager.getString("Enter path to XML file : ");
        if (!parser.tryOpen(path)) {
            System.out.println(" Unable to open file");
            return;
        }
        out.println("ux");
        List<Author> authors = parser.parseSAX(path);
        out.println(MyJsonParser.toJsonAuthors(authors));
    }

    private void mainLoop() throws IOException {
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
