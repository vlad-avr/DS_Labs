package com.example.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.example.control.InputManager;
import com.example.db_controller.DatabaseManager;
import com.example.objects.Author;
import com.example.objects.Book;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private DatabaseManager dbManager;

    public ClientHandler(Socket socket, DatabaseManager dbManager) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.dbManager = dbManager;
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
        // help();
        // while (socket.isConnected() && !socket.isClosed()) {
        //     mainLoop();
        // }
        try {
            String testString = reader.readLine();
            System.out.println("Client says : " + testString);
            writer.println("Сука юзер, сам iдi нахуй!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeClient();
        }
    }

    
}
