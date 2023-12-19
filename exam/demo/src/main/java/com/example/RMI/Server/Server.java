package com.example.RMI.Server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;


public class Server {

    private final int port = 1234;

    public void initAndListen(){
        try{
            RemoteDataManager dbManager = new RemoteDataManager();
            //UnicastRemoteObject.unexportObject(dbManager, true);
            RemoteDataManagerInterface mgrInterface = (RemoteDataManagerInterface) UnicastRemoteObject.exportObject(dbManager, 0);

            LocateRegistry.createRegistry(port);

            Naming.rebind("rmi://localhost:1234/DB", mgrInterface);

            System.out.println("SERVER ONLINE : awaiting clients` requests");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}