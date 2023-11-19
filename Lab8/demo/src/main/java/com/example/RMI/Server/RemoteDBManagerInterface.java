package com.example.RMI.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.Entities.Book.Genre;

public interface RemoteDBManagerInterface extends Remote{
    public List<Author> getAuthors() throws RemoteException;
    public List<Author> getAuthors(int min, int max) throws RemoteException;
    public List<Author> getAuthors(String toContain) throws RemoteException;
    
    public List<Book> getBooks() throws RemoteException;
    public List<Book> getBooks(String toContain) throws RemoteException;
    public List<Book> getBooks(double min, double max) throws RemoteException;
    public List<Book> getBooks(Genre genre) throws RemoteException;
    public List<Book> getBooksOfAuthor(String authorId) throws RemoteException;

    public List<String> getAuthorIds() throws RemoteException;
    public List<String> getBookIds() throws RemoteException;
    public String generateAuthorId() throws RemoteException;
    public String generateBookId() throws RemoteException;
}
