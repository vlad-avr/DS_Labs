package com.example.Socket.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.example.ConsoleManagement.InputManager;
import com.example.DataManager.PublicationFactory;
import com.example.Entity.Publication;
import com.example.JsonParser.MyJsonParser;

public class Client {
    private InputManager inputManager = new InputManager();
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean working = true;

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
        System.out.println(
                " a - add Publication,\n d - delete Publication,\n u - update Publication,\n g - get Publication by id,\n ga - get all Publications,\n gap - get Publication by params,\n h - help,\n e - exit\n ");
    }


    private void sendPublicationsRequest() {
        System.out.println("\n You are in Publication loading menu \n");
        String input;
        System.out
        .println(
                "\n gg - find by name;\n gd - find by date;\n");
        input = inputManager.getString("Enter Command");
        switch (input) {
            case "gg":
                out.println("gg");
                out.println(inputManager.getString("Enter group name : "));
                return;
            case "gd":
                out.println("gd");
                out.println(inputManager.getDate("Enter date : "));
                return;
            default:
                System.out.println("Invalid command!");
                return;
        }
    }


    private void showPublications() throws IOException {
        out.println("sa");
        List<Publication> pubs = MyJsonParser.parsePublications(in.readLine());
        if (pubs == null) {
            return;
        }
        PublicationFactory.sort(pubs);
        for (Publication student : pubs) {
            System.out.println(student.toString());
        }
    }

    private void addPublication() throws IOException {
        out.println("aa");
        Publication p = PublicationFactory.makePublication(inputManager, in.readLine());
        out.println(MyJsonParser.toJsonPublication(p));
    }

    private void deletePublication() throws IOException {
        out.println("da");
        String ID = inputManager.getOption(MyJsonParser.parseIds(in.readLine()), " Enter Publication ID : ");
        out.println(ID);
    }

    private void getPublication() throws IOException {
        out.println("ga");
        String ID = inputManager.getOption(MyJsonParser.parseIds(in.readLine()), "Enter Publication ID : ");
        out.println(ID);
        Publication p = MyJsonParser.parsePublication(in.readLine());
        if (p != null) {
            System.out.println(p.toString());
        }
    }


    private void updatePublication() throws IOException {
        out.println("ua");
        String ID = inputManager.getOption(MyJsonParser.parseIds(in.readLine()), "Enter Publication Id : ");
        out.println(ID);
        String temp = in.readLine();
        if (temp != "") {
            Publication p = MyJsonParser.parsePublication(temp);
            out.println(MyJsonParser.toJsonPublication(PublicationFactory.updateStudent(inputManager, p)));
        } else {
            System.out.println("This id is already reserved by other client!");
        }
    }

    private void getPublicationByParams() throws IOException {
        out.println("gap");
        sendPublicationsRequest();
        String tmp = in.readLine();
        if (tmp.equals("")) {
            return;
        }
        List<Publication> pubs = MyJsonParser.parsePublications(tmp);
        PublicationFactory.sort(pubs);
        for (Publication student : pubs) {
            System.out.println(student.toString());
        }
    }


    private void mainLoop(PrintWriter out, BufferedReader in) throws IOException {
        String input;
        input = inputManager.getString("Enter command : ");
        switch (input) {
            case "ga":
                showPublications();
                break;
            case "a":
                addPublication();
                break;
            case "u":
                updatePublication();
                break;
            case "gap":
                getPublicationByParams();
                break;
            case "d":
                deletePublication();
                break;
            case "g":
                getPublication();
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