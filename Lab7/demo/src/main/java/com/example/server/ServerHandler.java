package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.example.client.ClientHandler;
import com.example.db_controller.DatabaseManager;
// import com.example.xml_parser.MyParser;
import com.example.xml_parser.MyParser;

public class ServerHandler {
    // private ServerDB serverDB;
    private DatabaseManager dbManager;
    private MyParser parser;
    private ServerSocket serverSocket;
    private final int portId = 1234;

    public ServerHandler() {
        try {
            this.serverSocket = new ServerSocket(portId);
            dbManager = new DatabaseManager();
            parser = new MyParser("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Schema.xsd",
                    dbManager);
            parser.parseSAX("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml");
            listenForClients();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeServer();
        }
    }

    public void run() {
        listenForClients();
    }

    private void listenForClients() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("\n A new Client has connected!\n");
                ClientHandler handler = new ClientHandler(socket);

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
