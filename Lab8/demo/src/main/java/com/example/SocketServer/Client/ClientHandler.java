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

    @Override
    public void run() {
        try {
            while (socket.isConnected() && !socket.isClosed()) {
                String input = reader.readLine();
                writer.flush();
                List<Author> authors;
                List<Book> books;
                List<String> IDs;
                Author author;
                Book book;
                String temp = "";
                switch (input) {
                    case "sa":
                        serverHandler.readLock(serverHandler.getDBLock());
                        authors = serverHandler.dbManager.getAuthors();
                        serverHandler.readUnlock(serverHandler.getDBLock());
                        writer.println(MyJsonParser.toJsonAuthors(authors));
                        break;
                    case "sb":
                        serverHandler.readLock(serverHandler.getDBLock());
                        books = serverHandler.dbManager.getBooks();
                        serverHandler.readUnlock(serverHandler.getDBLock());
                        writer.println(MyJsonParser.toJsonBooks(books));
                        break;
                    // case "gap":
                    //     switch (reader.readLine()) {
                    //         case "n":
                    //             int min = Integer.parseInt(reader.readLine());
                    //             int max = Integer.parseInt(reader.readLine());
                    //             serverHandler.readLock(serverHandler.getDBLock());
                    //             authors = serverHandler.dbManager.getAuthors(min, max);
                    //             serverHandler.readUnlock(serverHandler.getDBLock());
                    //             writer.println(MyJsonParser.toJsonAuthors(authors));
                    //             break;
                    //         case "c":
                    //             temp = reader.readLine();
                    //             serverHandler.readLock(serverHandler.getDBLock());
                    //             authors = serverHandler.dbManager.getAuthors(temp);
                    //             serverHandler.readUnlock(serverHandler.getDBLock());
                    //             writer.println(MyJsonParser.toJsonAuthors(authors));
                    //             break;
                    //         default:
                    //             System.out.println("UKNOWN");
                    //             break;
                    //     }
                    // case "gbp":
                    //     switch (reader.readLine()) {
                    //         case "n":
                    //             temp = reader.readLine();
                    //             serverHandler.readLock(serverHandler.getDBLock());
                    //             books = serverHandler.dbManager.getBooks(temp);
                    //             serverHandler.readUnlock(serverHandler.getDBLock());
                    //             writer.println(MyJsonParser.toJsonBooks(books));
                    //             break;
                    //         case "g":
                    //             temp = reader.readLine();
                    //             serverHandler.readLock(serverHandler.getDBLock());
                    //             books = serverHandler.dbManager.getBooks(Book.Genre.valueOf(temp));
                    //             serverHandler.readUnlock(serverHandler.getDBLock());
                    //             System.out.println(books.get(0).toString());
                    //             writer.println(MyJsonParser.toJsonBooks(books));
                    //             break;
                    //         case "p":
                    //             double min = Double.parseDouble(reader.readLine());
                    //             double max = Double.parseDouble(reader.readLine());
                    //             serverHandler.readLock(serverHandler.getDBLock());
                    //             books = serverHandler.dbManager.getBooks(min, max);
                    //             serverHandler.readUnlock(serverHandler.getDBLock());
                    //             writer.println(MyJsonParser.toJsonBooks(books));
                    //             break;
                    //         case "a":
                    //             serverHandler.readLock(serverHandler.getAuthorLock());
                    //             IDs = serverHandler.dbManager.getAuthorGenerator().getIDs();
                    //             serverHandler.readUnlock(serverHandler.getAuthorLock()); 
                    //             writer.println(MyJsonParser.toJsonIDs(IDs));
                    //             temp = reader.readLine();
                    //             System.out.println(temp);
                    //             if(serverHandler.dbManager.getAuthorGenerator().exists(temp)){
                    //                 serverHandler.readLock(serverHandler.getDBLock());
                    //                 books = serverHandler.dbManager.getBooksOfAuthor(temp);
                    //                 serverHandler.readUnlock(serverHandler.getDBLock());
                    //                 writer.println(MyJsonParser.toJsonBooks(books));
                    //             }else{
                    //                 writer.println("");
                    //             }
                    //             break;
                    //         default:
                    //             break;
                    //     }
                    case "ga":
                        serverHandler.readLock(serverHandler.getAuthorLock());
                        IDs = serverHandler.dbManager.getAuthorGenerator().getIDs();
                        serverHandler.readUnlock(serverHandler.getAuthorLock());
                        writer.println(MyJsonParser.toJsonIDs(IDs));
                        temp = reader.readLine();
                        if (serverHandler.dbManager.getAuthorGenerator().exists(temp)) {
                            serverHandler.readLock(serverHandler.getDBLock());
                            author = serverHandler.dbManager.getAuthor(temp, true);
                            serverHandler.readUnlock(serverHandler.getDBLock());
                            writer.println(MyJsonParser.toJsonAuthor(author));
                        } else {
                            writer.println("[]");
                        }
                        break;
                    case "gb":
                        serverHandler.readLock(serverHandler.getBookLock());
                        IDs = serverHandler.dbManager.getBookGenerator().getIDs();
                        serverHandler.readUnlock(serverHandler.getBookLock());
                        writer.println(MyJsonParser.toJsonIDs(IDs));
                        temp = reader.readLine();
                        if (serverHandler.dbManager.getBookGenerator().exists(temp)) {
                            serverHandler.readLock(serverHandler.getDBLock());
                            book = serverHandler.dbManager.getBook(temp);
                            serverHandler.readUnlock(serverHandler.getDBLock());
                            writer.println(MyJsonParser.toJsonBook(book));
                        } else {
                            writer.println("[]");
                        }
                        break;
                    case "ai":
                        serverHandler.writeLock(serverHandler.getAuthorLock());
                        writer.println(serverHandler.dbManager.getAuthorGenerator().generateId());
                        serverHandler.writeUnlock(serverHandler.getAuthorLock());
                        break;
                    case "bi":
                        serverHandler.writeLock(serverHandler.getBookLock());
                        writer.println(serverHandler.dbManager.getBookGenerator().generateId());
                        serverHandler.writeUnlock(serverHandler.getBookLock());
                        break;
                    case "aa":
                        serverHandler.writeLock(serverHandler.getAuthorLock());
                        serverHandler.writeLock(serverHandler.getBookLock());
                        serverHandler.writeLock(serverHandler.getDBLock());
                        serverHandler.dbManager.addAuthor(MyJsonParser.parseAuthor(reader.readLine()));
                        serverHandler.writeUnlock(serverHandler.getDBLock());
                        serverHandler.writeUnlock(serverHandler.getBookLock());
                        serverHandler.writeUnlock(serverHandler.getAuthorLock());
                        break;
                    case "rai":
                        serverHandler.readLock(serverHandler.getAuthorLock());
                        IDs = serverHandler.dbManager.getAuthorGenerator().getIDs();
                        serverHandler.readUnlock(serverHandler.getAuthorLock());
                        writer.println(MyJsonParser.toJsonIDs(IDs));
                        temp = reader.readLine();
                        if(serverHandler.dbManager.getAuthorGenerator().exists(temp)){
                            serverHandler.writeLock(serverHandler.getAuthorLock());
                            serverHandler.dbManager.getAuthorGenerator().reserveId(temp);
                            serverHandler.writeUnlock(serverHandler.getAuthorLock());
                        }
                        break;
                    case "rbi":
                        serverHandler.readLock(serverHandler.getBookLock());
                        IDs = serverHandler.dbManager.getBookGenerator().getIDs();
                        serverHandler.readUnlock(serverHandler.getBookLock());
                        writer.println(MyJsonParser.toJsonIDs(IDs));
                        temp = reader.readLine();
                        if(serverHandler.dbManager.getBookGenerator().exists(temp)){
                            serverHandler.writeLock(serverHandler.getBookLock());
                            serverHandler.dbManager.getBookGenerator().reserveId(temp);
                            serverHandler.writeUnlock(serverHandler.getBookLock());
                        }
                        break;
                    case "ab":
                        serverHandler.writeLock(serverHandler.getAuthorLock());
                        serverHandler.writeLock(serverHandler.getBookLock());
                        serverHandler.writeLock(serverHandler.getDBLock());
                        book = MyJsonParser.parseBook(reader.readLine());
                        serverHandler.dbManager.addBook(book);
                        serverHandler.dbManager.getAuthorGenerator().releaseId(book.getAuthor());
                        serverHandler.writeUnlock(serverHandler.getDBLock());
                        serverHandler.writeUnlock(serverHandler.getBookLock());
                        serverHandler.writeUnlock(serverHandler.getAuthorLock());
                        break;
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
