package com.example.RMI.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

import com.example.Entity.Publication;

public interface RemoteDataManagerInterface extends Remote{
    public List<Publication> getPublications() throws RemoteException;
    public List<Publication> getPublicationsByDate(LocalDate date) throws RemoteException;
    public List<Publication> getPublicationsByName(String group) throws RemoteException;
    public Publication getPublication(String Id) throws RemoteException;

    public void addPublication(Publication student) throws RemoteException;
    public void updatePublication(Publication updStudent) throws RemoteException;
    public void deletePublication(String Id) throws RemoteException;

    public List<String> getIds() throws RemoteException;
    public String generateId() throws RemoteException;
    public boolean reserveId(String Id) throws RemoteException;
}