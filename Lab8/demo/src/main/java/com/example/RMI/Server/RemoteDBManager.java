package com.example.RMI.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.Entities.Book.Genre;
import com.example.dbManager.DatabaseManager;

public class RemoteDBManager extends UnicastRemoteObject implements RemoteDBManagerInterface{

    private DatabaseManager manager;
    private ReadWriteLock bookLock = new ReentrantReadWriteLock();
    private ReadWriteLock authorLock = new ReentrantReadWriteLock();
    private ReadWriteLock dbLock = new ReentrantReadWriteLock();

    protected RemoteDBManager() throws RemoteException {}

    public RemoteDBManager(DatabaseManager manager) throws RemoteException{
        this.manager = manager;
        manager.initDB();
    }

    @Override
    public List<Author> getAuthors() {
        dbLock.readLock().lock();
        List<Author> authors = manager.getAuthors();
        dbLock.readLock().unlock();
        return authors;
    }

    @Override
    public List<Author> getAuthors(int min, int max) {
        dbLock.readLock().lock();
        List<Author> authors = manager.getAuthors(min, max);
        dbLock.readLock().unlock();
        return authors;
    }

    @Override
    public List<Author> getAuthors(String toContain) {
        dbLock.readLock().lock();
        List<Author> authors = manager.getAuthors(toContain);
        dbLock.readLock().unlock();
        return authors;
    }

    @Override
    public List<Book> getBooks() {
        dbLock.readLock().lock();
        List<Book> books = manager.getBooks();
        dbLock.readLock().unlock();
        return books;
    }

    @Override
    public List<Book> getBooks(String toContain) {
        dbLock.readLock().lock();
        List<Book> books = manager.getBooks(toContain);
        dbLock.readLock().unlock();
        return books;
    }

    @Override
    public List<Book> getBooks(float min, float max) {
        dbLock.readLock().lock();
        List<Book> books = manager.getBooks(min, max);
        dbLock.readLock().unlock();
        return books;
    }

    @Override
    public List<Book> getBooks(Genre genre) {
        dbLock.readLock().lock();
        List<Book> books = manager.getBooks(genre);
        dbLock.readLock().unlock();
        return books;
    }
    
}
