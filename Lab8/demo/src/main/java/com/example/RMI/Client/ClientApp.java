package com.example.RMI.Client;

import java.rmi.RemoteException;

public class ClientApp {
    
    public static void main(String[] args){
        Client client = new Client();
        try {
            client.mainLoop();
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }
}
