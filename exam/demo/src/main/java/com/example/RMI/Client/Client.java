package com.example.RMI.Client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;


import com.example.ConsoleManagement.InputManager;
import com.example.DataManager.PublicationFactory;
import com.example.Entity.Publication;
import com.example.RMI.Server.RemoteDataManagerInterface;

public class Client {
    private RemoteDataManagerInterface dbRemote;
    private InputManager inputManager = new InputManager();

    public Client() {
        try {
            dbRemote = (RemoteDataManagerInterface) Naming.lookup("rmi://localhost:1234/DB");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void showPublications(List<Publication> publications) {
        if (publications == null) {
            return;
        }
        PublicationFactory.sort(publications);
        for (Publication publication : publications) {
            System.out.println(publication.toString());
        }
    }

    private void getPublications() throws RemoteException {
        showPublications(dbRemote.getPublications());
    }

    private void getPublication() throws RemoteException {
        Publication p = dbRemote.getPublication(inputManager.getOption(dbRemote.getIds(), "Enter Publication Id : "));
        if (p != null) {
            System.out.println(p.toString());
            return;
        }
        System.out.println("Publication with this Id no longer exists!");
    }

    private void getPublicationsByParams() throws RemoteException {
        System.out.println("\n You are in Publication loading menu \n");
        String input;
        System.out
                .println(
                        "\n gg - find by name;\n gd - find by date;\n");
        input = inputManager.getString("Enter Command");
        switch (input) {
            case "gg":
                showPublications(dbRemote.getPublicationsByName(inputManager.getString("Enter string to contain : ")));
                return;
            case "gd":
                showPublications(dbRemote.getPublicationsByDate(inputManager.getDate("Enter date : ")));
                return;
            default:
                System.out.println("Invalid command!");
                return;
        }
    }

    private void updatePublication() throws RemoteException {
        String Id = inputManager.getOption(dbRemote.getIds(), "Enter student Id : ");
        boolean reserved = dbRemote.reserveId(Id);
        if (reserved) {
            Publication p = PublicationFactory.updateStudent(inputManager, dbRemote.getPublication(Id));
            if(p != null){
                dbRemote.updatePublication(p);
            }
            return;
        }
        System.out.println("Unable to acquire lock on Publication with Id " + Id);
    }

    private void addPublication() throws RemoteException {
        dbRemote.addPublication(PublicationFactory.makePublication(inputManager, dbRemote.generateId()));
    }

    private void deletePublication() throws RemoteException {
        String Id = inputManager.getOption(dbRemote.getIds(), "Enter Publication Id : ");
        boolean reserved = dbRemote.reserveId(Id);
        if (reserved) {
            dbRemote.deletePublication(Id);
            return;
        }
        System.out.println("Unable to acquire lock on Publication with id " + Id);
    }

    private void helpActions() {
        System.out.println(
                " a - add Publication,\n d - delete Publication,\n u - update Publication,\n g - get Publication by id,\n ga - get all Publications,\n gap - get Publication by params,\n h - help,\n e - exit\n ");
    }

    public void mainLoop() throws RemoteException {
        String input;
        helpActions();
        while (true) {
            input = inputManager.getString(" Enter command : ");
            switch (input) {
                case "ga":
                    getPublications();
                    break;
                case "gap":
                    getPublicationsByParams();
                    break;
                case "g":
                    getPublication();
                    break;
                case "a":
                    addPublication();
                    break;
                case "u":
                    updatePublication();
                    break;
                case "d":
                    deletePublication();
                    break;
                case "h":
                    helpActions();
                    break;
                case "e":
                    System.out.println("You stopped working with current environment!");
                    return;
                default:
                    System.out.println("Invalid command!");
            }
        }
    }
}