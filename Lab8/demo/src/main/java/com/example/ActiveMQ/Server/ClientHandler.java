package com.example.ActiveMQ.Server;

import java.io.IOException;
import java.util.List;

import javax.jms.*;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.jsonParser.MyJsonParser;

public class ClientHandler implements Runnable {

    private MessageProducer producer;
    private MessageConsumer consumer;
    private Session mySession;
    private Queue myQueue;
    private Server serverHandler;

    public ClientHandler(Session session, int ID, Server server) {
        this.mySession = session;
        this.serverHandler = server;
        try {
            myQueue = session.createQueue("Client" + ID);
            this.producer = session.createProducer(myQueue);
            this.consumer = session.createConsumer(myQueue);
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private void genBookID() throws JMSException {
        serverHandler.writeLock(serverHandler.getBookLock());
        producer.send(mySession.createTextMessage(serverHandler.dbManager.getBookGenerator().generateId()));
        serverHandler.writeUnlock(serverHandler.getBookLock());
    }

    private void genAuthorID() throws JMSException {
        serverHandler.writeLock(serverHandler.getAuthorLock());
        producer.send(mySession.createTextMessage(serverHandler.dbManager.getAuthorGenerator().generateId()));
        serverHandler.writeUnlock(serverHandler.getAuthorLock());
    }

    private void showAuthors() throws JMSException {
        List<Author> authors;
        serverHandler.readLock(serverHandler.getDBLock());
        authors = serverHandler.dbManager.getAuthors();
        serverHandler.readUnlock(serverHandler.getDBLock());
        producer.send(mySession.createTextMessage(MyJsonParser.toJsonAuthors(authors)));
    }

    private void showBooks() throws JMSException {
        List<Book> books;
        serverHandler.readLock(serverHandler.getDBLock());
        books = serverHandler.dbManager.getBooks();
        serverHandler.readUnlock(serverHandler.getDBLock());
        producer.send(mySession.createTextMessage(MyJsonParser.toJsonBooks(books)));
    }

    private void sendAuthorIds() throws JMSException {
        List<String> IDs;
        serverHandler.readLock(serverHandler.getAuthorLock());
        IDs = serverHandler.dbManager.getAuthorGenerator().getIDs();
        serverHandler.readUnlock(serverHandler.getAuthorLock());
        producer.send(mySession.createTextMessage(MyJsonParser.toJsonIDs(IDs)));
    }

    private void sendBookIds() throws JMSException {
        List<String> IDs;
        serverHandler.readLock(serverHandler.getBookLock());
        IDs = serverHandler.dbManager.getBookGenerator().getIDs();
        serverHandler.readUnlock(serverHandler.getBookLock());
        producer.send(mySession.createTextMessage(MyJsonParser.toJsonIDs(IDs)));
    }

    private void getBook() throws IOException, JMSException {
        sendBookIds();
        String temp = ((TextMessage) consumer.receive()).getText();
        if (serverHandler.dbManager.getBookGenerator().exists(temp)) {
            serverHandler.readLock(serverHandler.getDBLock());
            Book book = serverHandler.dbManager.getBook(temp);
            serverHandler.readUnlock(serverHandler.getDBLock());
            producer.send(mySession.createTextMessage(MyJsonParser.toJsonBook(book)));
        } else {
            producer.send(mySession.createTextMessage("[]"));
        }
    }

    private void getAuthor() throws IOException, JMSException {
        sendAuthorIds();
        String temp = ((TextMessage) consumer.receive()).getText();
        if (serverHandler.dbManager.getAuthorGenerator().exists(temp)) {
            serverHandler.readLock(serverHandler.getDBLock());
            Author author = serverHandler.dbManager.getAuthor(temp, true);
            serverHandler.readUnlock(serverHandler.getDBLock());
            producer.send(mySession.createTextMessage(MyJsonParser.toJsonAuthor(author)));
        } else {
            producer.send(mySession.createTextMessage("[]"));
        }
    }

    private void addAuthor() throws IOException, JMSException {
        genAuthorID();
        String temp;
        while ((temp = ((TextMessage) consumer.receive()).getText()).equals("bi")) {
            genBookID();
        }
        serverHandler.writeLock(serverHandler.getAuthorLock());
        serverHandler.writeLock(serverHandler.getBookLock());
        serverHandler.writeLock(serverHandler.getDBLock());
        serverHandler.dbManager.addAuthor(MyJsonParser.parseAuthor(temp));
        serverHandler.writeUnlock(serverHandler.getDBLock());
        serverHandler.writeUnlock(serverHandler.getBookLock());
        serverHandler.writeUnlock(serverHandler.getAuthorLock());
    }

    private void addBook() throws IOException, JMSException {
        sendAuthorIds();
        String temp = ((TextMessage) consumer.receive()).getText();
        serverHandler.writeLock(serverHandler.getAuthorLock());
        boolean checker = serverHandler.dbManager.getAuthorGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getAuthorLock());
        if (checker) {
            producer.send(mySession.createTextMessage(temp));
            genBookID();
            Book book = MyJsonParser.parseBook(((TextMessage) consumer.receive()).getText());
            serverHandler.writeLock(serverHandler.getAuthorLock());
            serverHandler.writeLock(serverHandler.getBookLock());
            serverHandler.writeLock(serverHandler.getDBLock());
            serverHandler.dbManager.addBook(book);
            serverHandler.dbManager.getAuthorGenerator().releaseId(book.getAuthor());
            serverHandler.writeUnlock(serverHandler.getDBLock());
            serverHandler.writeUnlock(serverHandler.getBookLock());
            serverHandler.writeUnlock(serverHandler.getAuthorLock());
        } else {
            producer.send(mySession.createTextMessage(""));
        }
    }

    private void deleteBook() throws IOException, JMSException {
        sendBookIds();
        String temp = ((TextMessage) consumer.receive()).getText();
        serverHandler.writeLock(serverHandler.getBookLock());
        boolean checker = serverHandler.dbManager.getBookGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getBookLock());
        if (checker) {
            serverHandler.writeLock(serverHandler.getAuthorLock());
            serverHandler.writeLock(serverHandler.getBookLock());
            serverHandler.writeLock(serverHandler.getDBLock());
            serverHandler.dbManager.deleteBook(temp);
            serverHandler.writeUnlock(serverHandler.getDBLock());
            serverHandler.writeUnlock(serverHandler.getBookLock());
            serverHandler.writeUnlock(serverHandler.getAuthorLock());
        }
    }

    private void deleteAuthor() throws IOException, JMSException {
        sendAuthorIds();
        String temp = ((TextMessage) consumer.receive()).getText();
        serverHandler.writeLock(serverHandler.getAuthorLock());
        boolean checker = serverHandler.dbManager.getAuthorGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getAuthorLock());
        if (checker) {
            serverHandler.writeLock(serverHandler.getAuthorLock());
            serverHandler.writeLock(serverHandler.getBookLock());
            serverHandler.writeLock(serverHandler.getDBLock());
            serverHandler.dbManager.deleteAuthor(temp);
            serverHandler.writeUnlock(serverHandler.getDBLock());
            serverHandler.writeUnlock(serverHandler.getBookLock());
            serverHandler.writeUnlock(serverHandler.getAuthorLock());
        }
    }

    private void updateAuthor() throws IOException, JMSException {
        sendAuthorIds();
        String temp = ((TextMessage) consumer.receive()).getText();
        serverHandler.writeLock(serverHandler.getAuthorLock());
        boolean checker = serverHandler.dbManager.getAuthorGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getAuthorLock());
        if (checker) {
            serverHandler.readLock(serverHandler.getDBLock());
            Author author = serverHandler.dbManager.getAuthor(temp, true);
            serverHandler.readUnlock(serverHandler.getDBLock());
            producer.send(mySession.createTextMessage(MyJsonParser.toJsonAuthor(author)));
            author = MyJsonParser.parseAuthor(((TextMessage) consumer.receive()).getText());
            serverHandler.writeLock(serverHandler.getDBLock());
            serverHandler.writeLock(serverHandler.getAuthorLock());
            serverHandler.dbManager.updateAuthor(author);
            serverHandler.dbManager.getAuthorGenerator().releaseId(author.getId());
            serverHandler.writeUnlock(serverHandler.getAuthorLock());
            serverHandler.writeUnlock(serverHandler.getDBLock());
        } else {
            producer.send(mySession.createTextMessage(""));
        }
    }

    private void updateBook() throws IOException, JMSException {
        sendBookIds();
        String temp = ((TextMessage) consumer.receive()).getText();
        serverHandler.writeLock(serverHandler.getBookLock());
        boolean checker = serverHandler.dbManager.getBookGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getBookLock());
        if (checker) {
            serverHandler.readLock(serverHandler.getDBLock());
            Book book = serverHandler.dbManager.getBook(temp);
            serverHandler.readUnlock(serverHandler.getDBLock());
            producer.send(mySession.createTextMessage(MyJsonParser.toJsonBook(book)));
            book = MyJsonParser.parseBook(((TextMessage) consumer.receive()).getText());
            serverHandler.writeLock(serverHandler.getDBLock());
            serverHandler.writeLock(serverHandler.getBookLock());
            serverHandler.dbManager.updateBook(book);
            serverHandler.dbManager.getBookGenerator().releaseId(book.getId());
            serverHandler.writeUnlock(serverHandler.getBookLock());
            serverHandler.writeUnlock(serverHandler.getDBLock());
        } else {
            producer.send(mySession.createTextMessage(""));
        }
    }

    private void changeAuthor() throws IOException, JMSException {
        sendBookIds();
        String temp = ((TextMessage) consumer.receive()).getText();
        serverHandler.writeLock(serverHandler.getBookLock());
        boolean checker = serverHandler.dbManager.getBookGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getBookLock());
        if (checker) {
            serverHandler.readLock(serverHandler.getDBLock());
            Book book = serverHandler.dbManager.getBook(temp);
            serverHandler.readUnlock(serverHandler.getDBLock());
            producer.send(mySession.createTextMessage(MyJsonParser.toJsonBook(book)));
            sendAuthorIds();
            temp = ((TextMessage) consumer.receive()).getText();
            serverHandler.writeLock(serverHandler.getAuthorLock());
            checker = serverHandler.dbManager.getAuthorGenerator().reserveId(temp);
            serverHandler.writeUnlock(serverHandler.getAuthorLock());
            if (checker) {
                producer.send(mySession.createTextMessage(temp));
                book = MyJsonParser.parseBook(((TextMessage) consumer.receive()).getText());
                serverHandler.writeLock(serverHandler.getDBLock());
                serverHandler.writeLock(serverHandler.getAuthorLock());
                serverHandler.writeLock(serverHandler.getBookLock());
                serverHandler.dbManager.updateBook(book);
                serverHandler.dbManager.getBookGenerator().releaseId(book.getId());
                serverHandler.dbManager.getAuthorGenerator().releaseId(book.getAuthor());
                serverHandler.writeUnlock(serverHandler.getBookLock());
                serverHandler.writeUnlock(serverHandler.getAuthorLock());
                serverHandler.writeUnlock(serverHandler.getDBLock());
            } else {
                producer.send(mySession.createTextMessage(""));
            }
        } else {
            producer.send(mySession.createTextMessage(""));
        }
    }

    private void getAuthorsByParams() throws IOException, JMSException {
        switch (((TextMessage) consumer.receive()).getText()) {
            case "n":
                int min = Integer.parseInt(((TextMessage) consumer.receive()).getText());
                int max = Integer.parseInt(((TextMessage) consumer.receive()).getText());
                serverHandler.readLock(serverHandler.getDBLock());
                List<Author> authors = serverHandler.dbManager.getAuthors(min, max);
                serverHandler.readUnlock(serverHandler.getDBLock());
                producer.send(mySession.createTextMessage(MyJsonParser.toJsonAuthors(authors)));
                break;
            case "c":
                String temp = ((TextMessage) consumer.receive()).getText();
                serverHandler.readLock(serverHandler.getDBLock());
                authors = serverHandler.dbManager.getAuthors(temp);
                serverHandler.readUnlock(serverHandler.getDBLock());
                producer.send(mySession.createTextMessage(MyJsonParser.toJsonAuthors(authors)));
                break;
            default:
                producer.send(mySession.createTextMessage(""));
                return;
        }
    }

    private void getBooksByParams() throws IOException, JMSException {
        switch (((TextMessage) consumer.receive()).getText()) {
            case "p":
                double min = Double.parseDouble(((TextMessage) consumer.receive()).getText());
                double max = Double.parseDouble(((TextMessage) consumer.receive()).getText());
                serverHandler.readLock(serverHandler.getDBLock());
                List<Book> books = serverHandler.dbManager.getBooks(min, max);
                serverHandler.readUnlock(serverHandler.getDBLock());
                producer.send(mySession.createTextMessage(MyJsonParser.toJsonBooks(books)));
                break;
            case "n":
                String temp = ((TextMessage) consumer.receive()).getText();
                serverHandler.readLock(serverHandler.getDBLock());
                books = serverHandler.dbManager.getBooks(temp);
                serverHandler.readUnlock(serverHandler.getDBLock());
                producer.send(mySession.createTextMessage(MyJsonParser.toJsonBooks(books)));
                break;
            case "g":
                temp = ((TextMessage) consumer.receive()).getText();
                serverHandler.readLock(serverHandler.getDBLock());
                books = serverHandler.dbManager.getBooks(Book.Genre.valueOf(temp));
                serverHandler.readUnlock(serverHandler.getDBLock());
                producer.send(mySession.createTextMessage(MyJsonParser.toJsonBooks(books)));
                break;
            default:
                producer.send(mySession.createTextMessage(""));
                return;
        }
    }

    private void getBooksOfAuthor() throws IOException, JMSException {
        sendAuthorIds();
        String tmp = ((TextMessage) consumer.receive()).getText();
        serverHandler.readLock(serverHandler.getAuthorLock());
        boolean checker = serverHandler.dbManager.getAuthorGenerator().exists(tmp);
        serverHandler.readUnlock(serverHandler.getAuthorLock());
        if (checker) {
            serverHandler.readLock(serverHandler.getDBLock());
            List<Book> books = serverHandler.dbManager.getBooksOfAuthor(tmp);
            serverHandler.readUnlock(serverHandler.getDBLock());
            producer.send(mySession.createTextMessage(MyJsonParser.toJsonBooks(books)));
        } else {
            producer.send(mySession.createTextMessage(""));
        }
    }

    private void uploadFromXml() throws IOException, JMSException {
        serverHandler.loadToDB(MyJsonParser.parseAuthors(((TextMessage) consumer.receive()).getText()));
    }

    private void loadToXml() throws JMSException {
        serverHandler.readLock(serverHandler.getDBLock());
        producer.send(mySession.createTextMessage(MyJsonParser.toJsonAuthors(serverHandler.dbManager.getAuthors())));
        serverHandler.readUnlock(serverHandler.getDBLock());
    }

    private void work() throws JMSException, IOException {
        boolean working = true;
        while (working) {
            String input = ((TextMessage) consumer.receive()).getText();
            switch (input) {
                case "sa":
                    showAuthors();
                    break;
                case "sb":
                    showBooks();
                    break;
                case "gap":
                    getAuthorsByParams();
                    break;
                case "gbp":
                    getBooksByParams();
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
                case "da":
                    deleteAuthor();
                    break;
                case "db":
                    deleteBook();
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
                case "gba":
                    getBooksOfAuthor();
                    break;
                case "lx":
                    loadToXml();
                    break;
                case "ux":
                    uploadFromXml();
                    break;
                case "e":
                    working = false;
                    return;
                default:
                    break;
            }
        }
    }

    @Override
    public void run() {
        try {
            work();
            producer.close();
            consumer.close();
            mySession.close();
        } catch (JMSException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
