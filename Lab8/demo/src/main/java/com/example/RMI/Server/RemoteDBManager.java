package com.example.RMI.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.dbManager.DatabaseManager;

public class RemoteDBManager extends UnicastRemoteObject implements RemoteDBManagerInterface{

    private DatabaseManager manager;
    private ReadWriteLock bookLock = new ReentrantReadWriteLock();
    private ReadWriteLock authorLock = new ReentrantReadWriteLock();
    private ReadWriteLock dbLock = new ReentrantReadWriteLock();

    protected RemoteDBManager() throws RemoteException {}

    public RemoteDBManager(DatabaseManager manager) throws RemoteException{
        this.manager = manager;
    }
    
}
