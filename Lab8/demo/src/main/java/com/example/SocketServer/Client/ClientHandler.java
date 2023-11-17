package com.example.SocketServer.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.SocketServer.Server.ServerHandler;
import com.example.jsonParser.MyJsonParser;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ServerHandler serverHandler;

    public ClientHandler(Socket socket, ServerHandler serverHandler) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.serverHandler = serverHandler;
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

    private void genBookID() {
        serverHandler.writeLock(serverHandler.getBookLock());
        writer.println(serverHandler.dbManager.getBookGenerator().generateId());
        serverHandler.writeUnlock(serverHandler.getBookLock());
    }

    private void genAuthorID() {
        serverHandler.writeLock(serverHandler.getAuthorLock());
        writer.println(serverHandler.dbManager.getAuthorGenerator().generateId());
        serverHandler.writeUnlock(serverHandler.getAuthorLock());
    }

    private void showAuthors() {
        List<Author> authors;
        serverHandler.readLock(serverHandler.getDBLock());
        authors = serverHandler.dbManager.getAuthors();
        serverHandler.readUnlock(serverHandler.getDBLock());
        writer.println(MyJsonParser.toJsonAuthors(authors));
    }

    private void showBooks() {
        List<Book> books;
        serverHandler.readLock(serverHandler.getDBLock());
        books = serverHandler.dbManager.getBooks();
        serverHandler.readUnlock(serverHandler.getDBLock());
        writer.println(MyJsonParser.toJsonBooks(books));
    }

    private void sendAuthorIds() {
        List<String> IDs;
        serverHandler.readLock(serverHandler.getAuthorLock());
        IDs = serverHandler.dbManager.getAuthorGenerator().getIDs();
        serverHandler.readUnlock(serverHandler.getAuthorLock());
        writer.println(MyJsonParser.toJsonIDs(IDs));
    }

    private void sendBookIds() {
        List<String> IDs;
        serverHandler.readLock(serverHandler.getBookLock());
        IDs = serverHandler.dbManager.getBookGenerator().getIDs();
        serverHandler.readUnlock(serverHandler.getBookLock());
        writer.println(MyJsonParser.toJsonIDs(IDs));
    }

    private void getBook() throws IOException {
        sendBookIds();
        String temp = reader.readLine();
        if (serverHandler.dbManager.getBookGenerator().exists(temp)) {
            serverHandler.readLock(serverHandler.getDBLock());
            Book book = serverHandler.dbManager.getBook(temp);
            serverHandler.readUnlock(serverHandler.getDBLock());
            writer.println(MyJsonParser.toJsonBook(book));
        } else {
            writer.println("[]");
        }
    }

    private void getAuthor() throws IOException {
        sendAuthorIds();
        String temp = reader.readLine();
        if (serverHandler.dbManager.getAuthorGenerator().exists(temp)) {
            serverHandler.readLock(serverHandler.getDBLock());
            Author author = serverHandler.dbManager.getAuthor(temp, true);
            serverHandler.readUnlock(serverHandler.getDBLock());
            writer.println(MyJsonParser.toJsonAuthor(author));
        } else {
            writer.println("[]");
        }
    }

    private void addAuthor() throws IOException {
        genAuthorID();
        String temp;
        while ((temp = reader.readLine()).equals("bi")) {
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

    private void addBook() throws IOException {
        sendAuthorIds();
        String temp = reader.readLine();
        serverHandler.writeLock(serverHandler.getAuthorLock());
        boolean checker = serverHandler.dbManager.getAuthorGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getAuthorLock());
        if (checker) {
            writer.println(temp);
            genBookID();
            Book book = MyJsonParser.parseBook(reader.readLine());
            serverHandler.writeLock(serverHandler.getAuthorLock());
            serverHandler.writeLock(serverHandler.getBookLock());
            serverHandler.writeLock(serverHandler.getDBLock());
            serverHandler.dbManager.addBook(book);
            serverHandler.dbManager.getAuthorGenerator().releaseId(book.getAuthor());
            serverHandler.writeUnlock(serverHandler.getDBLock());
            serverHandler.writeUnlock(serverHandler.getBookLock());
            serverHandler.writeUnlock(serverHandler.getAuthorLock());
        } else {
            writer.println("");
        }
    }

    private void deleteBook() throws IOException {
        sendBookIds();
        String temp = reader.readLine();
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

    private void deleteAuthor() throws IOException {
        sendAuthorIds();
        String temp = reader.readLine();
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

    private void updateAuthor() throws IOException {
        sendAuthorIds();
        String temp = reader.readLine();
        serverHandler.writeLock(serverHandler.getAuthorLock());
        boolean checker = serverHandler.dbManager.getAuthorGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getAuthorLock());
        if (checker) {
            serverHandler.readLock(serverHandler.getDBLock());
            Author author = serverHandler.dbManager.getAuthor(temp, true);
            serverHandler.readUnlock(serverHandler.getDBLock());
            writer.println(MyJsonParser.toJsonAuthor(author));
            author = MyJsonParser.parseAuthor(reader.readLine());
            serverHandler.writeLock(serverHandler.getDBLock());
            serverHandler.writeLock(serverHandler.getAuthorLock());
            serverHandler.dbManager.updateAuthor(author);
            serverHandler.dbManager.getAuthorGenerator().releaseId(author.getId());
            serverHandler.writeUnlock(serverHandler.getAuthorLock());
            serverHandler.writeUnlock(serverHandler.getDBLock());
        } else {
            writer.println("");
        }
    }

    private void updateBook() throws IOException {
        sendBookIds();
        String temp = reader.readLine();
        serverHandler.writeLock(serverHandler.getBookLock());
        boolean checker = serverHandler.dbManager.getBookGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getBookLock());
        if (checker) {
            serverHandler.readLock(serverHandler.getDBLock());
            Book book = serverHandler.dbManager.getBook(temp);
            serverHandler.readUnlock(serverHandler.getDBLock());
            writer.println(MyJsonParser.toJsonBook(book));
            book = MyJsonParser.parseBook(reader.readLine());
            serverHandler.writeLock(serverHandler.getDBLock());
            serverHandler.writeLock(serverHandler.getBookLock());
            serverHandler.dbManager.updateBook(book);
            serverHandler.dbManager.getBookGenerator().releaseId(book.getId());
            serverHandler.writeUnlock(serverHandler.getBookLock());
            serverHandler.writeUnlock(serverHandler.getDBLock());
        } else {
            writer.println("");
        }
    }

    private void changeAuthor() throws IOException {
        sendBookIds();
        String temp = reader.readLine();
        serverHandler.writeLock(serverHandler.getBookLock());
        boolean checker = serverHandler.dbManager.getBookGenerator().reserveId(temp);
        serverHandler.writeUnlock(serverHandler.getBookLock());
        if (checker) {
            serverHandler.readLock(serverHandler.getDBLock());
            Book book = serverHandler.dbManager.getBook(temp);
            serverHandler.readUnlock(serverHandler.getDBLock());
            writer.println(MyJsonParser.toJsonBook(book));
            sendAuthorIds();
            temp = reader.readLine();
            serverHandler.writeLock(serverHandler.getAuthorLock());
            checker = serverHandler.dbManager.getAuthorGenerator().reserveId(temp);
            serverHandler.writeUnlock(serverHandler.getAuthorLock());
            if (checker) {
                writer.println(temp);
                book = MyJsonParser.parseBook(reader.readLine());
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
                writer.println("");
            }
        } else {
            writer.println("");
        }
    }

    private void getAuthorsByParams() throws IOException {
        switch (reader.readLine()) {
            case "n":
                int min = Integer.parseInt(reader.readLine());
                int max = Integer.parseInt(reader.readLine());
                serverHandler.readLock(serverHandler.getDBLock());
                List<Author> authors = serverHandler.dbManager.getAuthors(min, max);
                serverHandler.readUnlock(serverHandler.getDBLock());
                writer.println(MyJsonParser.toJsonAuthors(authors));
                break;
            case "c":
                String temp = reader.readLine();
                serverHandler.readLock(serverHandler.getDBLock());
                authors = serverHandler.dbManager.getAuthors(temp);
                serverHandler.readUnlock(serverHandler.getDBLock());
                writer.println(MyJsonParser.toJsonAuthors(authors));
                break;
            default:
                writer.println("");
                return;
        }
    }

    private void getBooksByParams() throws IOException {
        switch (reader.readLine()) {
            case "p":
                double min = Double.parseDouble(reader.readLine());
                double max = Double.parseDouble(reader.readLine());
                serverHandler.readLock(serverHandler.getDBLock());
                List<Book> books = serverHandler.dbManager.getBooks(min, max);
                serverHandler.readUnlock(serverHandler.getDBLock());
                writer.println(MyJsonParser.toJsonBooks(books));
                break;
            case "n":
                String temp = reader.readLine();
                serverHandler.readLock(serverHandler.getDBLock());
                books = serverHandler.dbManager.getBooks(temp);
                serverHandler.readUnlock(serverHandler.getDBLock());
                writer.println(MyJsonParser.toJsonBooks(books));
                break;
            case "g":
                temp = reader.readLine();
                serverHandler.readLock(serverHandler.getDBLock());
                books = serverHandler.dbManager.getBooks(Book.Genre.valueOf(temp));
                serverHandler.readUnlock(serverHandler.getDBLock());
                writer.println(MyJsonParser.toJsonBooks(books));
                break;
            default:
                writer.println("");
                return;
        }
    }

    private void getBooksOfAuthor() throws IOException {
        sendAuthorIds();
        String tmp = reader.readLine();
        serverHandler.readLock(serverHandler.getAuthorLock());
        boolean checker = serverHandler.dbManager.getAuthorGenerator().exists(tmp);
        serverHandler.readUnlock(serverHandler.getAuthorLock());
        if (checker) {
            serverHandler.readLock(serverHandler.getDBLock());
            List<Book> books = serverHandler.dbManager.getBooksOfAuthor(tmp);
            serverHandler.readUnlock(serverHandler.getDBLock());
            writer.println(MyJsonParser.toJsonBooks(books));
        } else {
            writer.println("");
        }
    }

    private void uploadFromXml() throws IOException{
        serverHandler.loadToDB(MyJsonParser.parseAuthors(reader.readLine()));
    }

    private void loadToXml(){
        serverHandler.readLock(serverHandler.getDBLock());
        writer.println(MyJsonParser.toJsonAuthors(serverHandler.dbManager.getAuthors()));
        serverHandler.readUnlock(serverHandler.getDBLock());
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected() && !socket.isClosed()) {
                String input = reader.readLine();
                writer.flush();
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
                        closeClient();
                        return;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeClient();
        }
    }

}
