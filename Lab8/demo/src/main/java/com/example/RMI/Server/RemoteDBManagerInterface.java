package com.example.RMI.Server;

import java.rmi.Remote;
import java.util.List;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.example.Entities.Book.Genre;

public interface RemoteDBManagerInterface extends Remote{
    public List<Author> getAuthors();
    public List<Author> getAuthors(int min, int max);
    public List<Author> getAuthors(String toContain);
    
    public List<Book> getBooks();
    public List<Book> getBooks(String toContain);
    public List<Book> getBooks(float min, float max);
    public List<Book> getBooks(Genre genre);
}
