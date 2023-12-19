package com.example.Socket.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

import com.example.Socket.Server.Server;
import com.example.Entity.Publication;
import com.example.JsonParser.MyJsonParser;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server serverHandler;

    public ClientHandler(Socket socket, Server serverHandler) {
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

    private void genId() {
        writer.println(serverHandler.dbManager.getIdGenerator().generateId());
    }

    private void showPublications() {
        List<Publication> pubs;
        pubs = serverHandler.dbManager.getPublications();
        writer.println(MyJsonParser.toJsonPublications(pubs));
    }

    private void sendIds() {
        List<String> IDs;
        IDs = serverHandler.dbManager.getIdGenerator().getIDs();
        writer.println(MyJsonParser.toJsonIDs(IDs));
    }

    private void getPublication() throws IOException {
        sendIds();
        String temp = reader.readLine();
        if (serverHandler.dbManager.getIdGenerator().exists(temp)) {
            Publication p = serverHandler.dbManager.getPublication(temp);
            writer.println(MyJsonParser.toJsonPublication(p));
        } else {
            writer.println("[]");
        }
    }

    private void addPublication() throws IOException {
        genId();
        String temp = reader.readLine();
        serverHandler.dbManager.addPublication(MyJsonParser.parsePublication(temp));
    }

    private void deletePublication() throws IOException {
        sendIds();
        String temp = reader.readLine();
        boolean checker = serverHandler.dbManager.getIdGenerator().reserveId(temp);
        if (checker) {
            serverHandler.dbManager.deletePublication(temp);
        }
    }

    private void updatePublication() throws IOException {
        sendIds();
        String temp = reader.readLine();
        boolean checker = serverHandler.dbManager.getIdGenerator().reserveId(temp);
        if (checker) {
            Publication p = serverHandler.dbManager.getPublication(temp);
            writer.println(MyJsonParser.toJsonPublication(p));
            p = MyJsonParser.parsePublication(reader.readLine());
            serverHandler.dbManager.updatePublication(p);
            serverHandler.dbManager.getIdGenerator().releaseId(p.getID());
        } else {
            writer.println("");
        }
    }

    private void getPublicationsByParams() throws IOException {
        List<Publication> pubs;
        switch (reader.readLine()) {
            case "gg":
                String temp = reader.readLine();
                pubs = serverHandler.dbManager.getPublicationsByName(temp);
                writer.println(MyJsonParser.toJsonPublications(pubs));
                break;
            case "gd":
                LocalDate localDate = LocalDate.parse(reader.readLine());
                pubs = serverHandler.dbManager.getPublicationsByDate(localDate);
                writer.println(MyJsonParser.toJsonPublications(pubs));
                break;
            default:
                writer.println("");
                return;
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected() && !socket.isClosed()) {
                String input = reader.readLine();
                writer.flush();
                switch (input) {
                    case "sa":
                        showPublications();
                        break;
                    case "gap":
                        getPublicationsByParams();
                        break;
                    case "ga":
                        getPublication();
                        break;
                    case "aa":
                        addPublication();
                        break;
                    case "da":
                        deletePublication();
                        break;
                    case "ua":
                        updatePublication();
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