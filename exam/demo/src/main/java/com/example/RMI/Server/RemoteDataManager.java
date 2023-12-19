package com.example.RMI.Server;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

import com.example.DataManager.DataManager;
import com.example.DataManager.PublicationFactory;
import com.example.Entity.Publication;

public class RemoteDataManager implements RemoteDataManagerInterface{

    private DataManager manager;

    protected RemoteDataManager() throws RemoteException {
        manager = new DataManager(PublicationFactory.initSampleData(), "S");
    }

    @Override
    public List<Publication> getPublications() throws RemoteException {
        return manager.getPublications();
    }

    @Override
    public List<Publication> getPublicationsByDate(LocalDate date) throws RemoteException {
        return manager.getPublicationsByDate(date);
    }

    @Override
    public List<Publication> getPublicationsByName(String group) throws RemoteException {
        return manager.getPublicationsByName(group);
    }

    @Override
    public Publication getPublication(String Id) throws RemoteException {
        return manager.getPublication(Id);
    }

    @Override
    public void addPublication(Publication student) throws RemoteException {
        manager.addPublication(student);
    }

    @Override
    public void updatePublication(Publication updStudent) throws RemoteException {
        manager.updatePublication(updStudent);
        manager.getIdGenerator().releaseId(updStudent.getID());
    }

    @Override
    public void deletePublication(String Id) throws RemoteException {
        manager.deletePublication(Id);
    }

    @Override
    public List<String> getIds() throws RemoteException {
        return manager.getIdGenerator().getIDs();
    }

    @Override
    public String generateId() throws RemoteException {
        return manager.getIdGenerator().generateId();
    }

    @Override
    public boolean reserveId(String Id) throws RemoteException {
        return manager.getIdGenerator().reserveId(Id);
    }
    
}
