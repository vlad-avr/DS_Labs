package com.example.SocketServer.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.Entities.Author;
import com.example.SocketServer.Client.ClientHandler;
import com.example.dbManager.DatabaseManager;
import com.example.dbManager.MyParser;

public class ServerHandler {
    // private ServerDB serverDB;
    public DatabaseManager dbManager;
    private MyParser parser;
    private ServerSocket serverSocket;
    private final int portId = 1234;

    private ReadWriteLock dbLock = new ReentrantReadWriteLock();
    private ReadWriteLock bookLock = new ReentrantReadWriteLock();
    private ReadWriteLock authorLock = new ReentrantReadWriteLock();

    public ServerHandler() {
        try {
            this.serverSocket = new ServerSocket(portId);
            dbManager = new DatabaseManager();
            dbManager.initDB();
            parser = new MyParser();
            loadData();
            listenForClients();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeServer();
        }
    }

    public void loadToDB(List<Author> authors) {
        for (Author author : authors) {
            writeLock(dbLock);
            writeLock(authorLock);
            writeLock(bookLock);
            if (dbManager.getAuthorGenerator().exists(author.getId())) {
                dbManager.updateAuthor(author, true);
            } else {
                dbManager.addAuthor(author, true);
            }
            writeUnlock(bookLock);
            writeUnlock(authorLock);
            writeUnlock(dbLock);
        }
    }

    private void loadData() {
        List<Author> authors = parser
                .parseSAX("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml");
        if (authors == null) {
            return;
        }
        for (Author author : authors) {
            dbManager.addAuthor(author);
        }
    }

    public ReadWriteLock getDBLock() {
        return this.dbLock;
    }

    public ReadWriteLock getBookLock() {
        return this.bookLock;
    }

    public ReadWriteLock getAuthorLock() {
        return this.authorLock;
    }

    public void writeLock(ReadWriteLock lock) {
        lock.writeLock().lock();
    }

    public void writeUnlock(ReadWriteLock lock) {
        lock.writeLock().unlock();
    }

    public void readLock(ReadWriteLock lock) {
        lock.readLock().lock();
    }

    public void readUnlock(ReadWriteLock lock) {
        lock.readLock().unlock();
    }

    public void run() {
        listenForClients();
    }

    private void listenForClients() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("\n A new Client has connected!\n");
                ClientHandler handler = new ClientHandler(socket, this);

                Thread thr = new Thread(handler);
                thr.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                closeServer();
            }
        }
    }

    private void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
