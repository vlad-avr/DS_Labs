package com.example.RMI.Server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import com.example.dbManager.DatabaseManager;

public class Server {

    private final int port = 1234;

    public Server(){
        initAndListen();
    }

    private void initAndListen(){
        try{
            RemoteDBManager dbManager = new RemoteDBManager(new DatabaseManager());
            RemoteDBManagerInterface mgrInterface = (RemoteDBManagerInterface) UnicastRemoteObject.exportObject(dbManager, 0);

            LocateRegistry.createRegistry(port);

            Naming.rebind("DB", mgrInterface);

            System.out.println("SERVER ONLINE : awaiting clients` requests");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
