package com.example.RMI.Client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import com.example.Entities.Author;
import com.example.InputManager.InputManager;
import com.example.RMI.Server.RemoteDBManagerInterface;

public class Client {
    private RemoteDBManagerInterface dbRemote;
    private InputManager inputManager = new InputManager();
    public Client(){
        try{
            dbRemote = (RemoteDBManagerInterface) Naming.lookup("rmi://localhost:1234/DB");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showAuthors(List<Author> authors){
        for(Author author : authors){
            System.out.println(author.toString());
        }
    }

    public void mainLoop() throws RemoteException{
        String input;
        while (true) {
            input = inputManager.getString(" Enter command : ");
            switch (input) {
                case "sa":
                    showAuthors(dbRemote.getAuthors());
                    break;
                default:
                    break;
            }
        }
    }
}
