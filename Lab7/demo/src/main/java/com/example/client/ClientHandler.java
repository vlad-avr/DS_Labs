package com.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.JSONArray;

import com.example.db_controller.DatabaseManager;
import com.example.objects.Author;
import com.example.objects.Book;
import com.example.server.ServerHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {

    private ReadWriteLock lock = new ReentrantReadWriteLock();
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
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();
        try {
            while (socket.isConnected() && !socket.isClosed()) {
                String input = reader.readLine();
                List<Author> authors;
                List<Book> books;
                String temp = "";
                switch (input) {
                    case "sa":
                        readLock.lock();
                        authors = serverHandler.dbManager.getAuthors();
                        readLock.unlock();
                        writer.println(toJsonAuthors(authors));
                        break;
                    case "sb":
                        readLock.lock();
                        books = serverHandler.dbManager.getBooks();
                        readLock.unlock();
                        writer.println(toJsonBooks(books));
                        break;
                    case "gap":
                        switch (reader.readLine()) {
                            case "n":
                                int min = Integer.parseInt(reader.readLine());
                                int max = Integer.parseInt(reader.readLine());
                                readLock.lock();
                                authors = serverHandler.dbManager.getAuthors(min, max);
                                readLock.unlock();
                                writer.println(toJsonAuthors(authors));
                                break;
                            case "c":
                                temp = reader.readLine();
                                readLock.lock();
                                authors = serverHandler.dbManager.getAuthors(temp);
                                readLock.unlock();
                                writer.println(toJsonAuthors(authors));
                                break;
                            default:
                                break;
                        }
                    case "gbp":
                        switch (reader.readLine()) {
                            case "n":
                                temp = reader.readLine();
                                readLock.lock();
                                books = serverHandler.dbManager.getBooks(temp);
                                readLock.unlock();
                                writer.println(toJsonBooks(books));
                                break;
                            case "g":
                                temp = reader.readLine();
                                readLock.lock();
                                books = serverHandler.dbManager.getBooks(Book.Genre.valueOf(temp));
                                readLock.unlock();
                                writer.println(toJsonBooks(books));
                                break;
                            case "p":
                                double min = Double.parseDouble(reader.readLine());
                                double max = Double.parseDouble(reader.readLine());
                                readLock.lock();
                                books = serverHandler.dbManager.getBooks(min, max);
                                readLock.unlock();
                                writer.println(toJsonBooks(books));
                                break;
                            case "a":
                                writer.println("");
                                break;
                            default:
                                break;
                        }
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeClient();
        }
    }

    private String toJsonAuthors(List<Author> authors) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(authors);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    private String toJsonBooks(List<Book> books) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(books);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

}
