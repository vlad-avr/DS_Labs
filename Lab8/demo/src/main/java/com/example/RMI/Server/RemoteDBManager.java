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
import com.example.dbManager.MyParser;

public class RemoteDBManager extends UnicastRemoteObject implements RemoteDBManagerInterface{

    private DatabaseManager manager;
    private MyParser parser = new MyParser();
    private ReadWriteLock bookLock = new ReentrantReadWriteLock();
    private ReadWriteLock authorLock = new ReentrantReadWriteLock();
    private ReadWriteLock dbLock = new ReentrantReadWriteLock();

    protected RemoteDBManager() throws RemoteException {}

    public RemoteDBManager(DatabaseManager manager) throws RemoteException{
        this.manager = manager;
        manager.initDB();
        loadData();
    }

    private void loadData() {
        List<Author> authors = parser
                .parseSAX("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml");
        if (authors == null) {
            return;
        }
        for (Author author : authors) {
            manager.addAuthor(author);
        }
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
    public List<Book> getBooks(double min, double max) {
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

    @Override
    public List<String> getAuthorIds() throws RemoteException {
        authorLock.readLock().lock();
        List<String> Ids = manager.getAuthorGenerator().getIDs();
        authorLock.readLock().unlock();
        return Ids;
    }

    @Override
    public List<String> getBookIds() throws RemoteException {
        bookLock.readLock().lock();
        List<String> Ids = manager.getBookGenerator().getIDs();
        bookLock.readLock().unlock();
        return Ids;
    }

    @Override
    public String generateAuthorId() throws RemoteException {
        authorLock.writeLock().lock();
        String Id = manager.getAuthorGenerator().generateId();
        authorLock.writeLock().unlock();
        return Id;
    }

    @Override
    public String generateBookId() throws RemoteException {
        bookLock.writeLock().lock();
        String Id = manager.getBookGenerator().generateId();
        bookLock.writeLock().unlock();
        return Id;
    }

    @Override
    public List<Book> getBooksOfAuthor(String authorId) throws RemoteException {
        dbLock.readLock().lock();
        List<Book> books = manager.getBooksOfAuthor(authorId);
        dbLock.readLock().unlock();
        return books;
    }

    @Override
    public boolean reserveAuthor(String Id) throws RemoteException {
        authorLock.writeLock().lock();
        boolean res = manager.getAuthorGenerator().reserveId(Id);
        authorLock.writeLock().unlock();
        return res;
    }

    @Override
    public boolean reserveBook(String Id) throws RemoteException {
        bookLock.writeLock().lock();
        boolean res = manager.getBookGenerator().reserveId(Id);
        bookLock.writeLock().unlock();
        return res;
    }

    @Override
    public void addAuthor(Author author) throws RemoteException {
        authorLock.writeLock().lock();
        bookLock.writeLock().lock();
        dbLock.writeLock().lock();
        manager.addAuthor(author);
        dbLock.writeLock().unlock();
        bookLock.writeLock().unlock();
        authorLock.writeLock().unlock();
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        bookLock.writeLock().lock();
        dbLock.writeLock().lock();
        manager.addBook(book);
        dbLock.writeLock().unlock();
        bookLock.writeLock().unlock();
    }

    @Override
    public Author getAuthor(String Id) throws RemoteException {
        dbLock.readLock().lock();
        Author author = manager.getAuthor(Id, true);
        dbLock.readLock().unlock();
        return author;
    }

    @Override
    public Book getBook(String Id) throws RemoteException {
        bookLock.readLock().lock();
        Book book = manager.getBook(Id);
        bookLock.readLock().unlock();
        return book;
    }

    @Override
    public void updateAuthor(Author author) throws RemoteException {
        dbLock.writeLock().lock();
        manager.updateAuthor(author);
        dbLock.writeLock().unlock();
        authorLock.writeLock().lock();
        manager.getAuthorGenerator().releaseId(author.getId());
        authorLock.writeLock().unlock();
    }

    @Override
    public void updateBook(Book book) throws RemoteException {
        dbLock.writeLock().lock();
        manager.updateBook(book);
        dbLock.writeLock().unlock();
        bookLock.writeLock().lock();
        manager.getBookGenerator().releaseId(book.getId());
        bookLock.writeLock().unlock();
    }

    @Override
    public void updateBookAuthor(Book book) throws RemoteException {
        dbLock.writeLock().lock();
        manager.updateBook(book);
        dbLock.writeLock().unlock();
        authorLock.writeLock().lock();
        manager.getAuthorGenerator().releaseId(book.getAuthor());
        authorLock.writeLock().unlock();
        bookLock.writeLock().lock();
        manager.getBookGenerator().releaseId(book.getId());
        bookLock.writeLock().unlock();
    }

    @Override
    public void deleteAuthor(String Id) throws RemoteException {
        authorLock.writeLock().lock();
        bookLock.writeLock().lock();
        dbLock.writeLock().lock();
        manager.deleteAuthor(Id);
        dbLock.writeLock().unlock();
        bookLock.writeLock().unlock();
        authorLock.writeLock().unlock();
    }

    @Override
    public void deleteBook(String Id) throws RemoteException {
        bookLock.writeLock().lock();
        dbLock.writeLock().lock();
        manager.deleteBook(Id);
        dbLock.writeLock().unlock();
        bookLock.writeLock().unlock();
    }

    @Override
    public void uploadFromXML(List<Author> authors) throws RemoteException {
        for (Author author : authors) {
            dbLock.writeLock().lock();
            authorLock.writeLock().lock();
            bookLock.writeLock().lock();
            if (manager.getAuthorGenerator().exists(author.getId())) {
                manager.updateAuthor(author, true);
            } else {
                manager.addAuthor(author, true);
            }
            bookLock.writeLock().unlock();
            authorLock.writeLock().unlock();
            dbLock.writeLock().unlock();
        }
    }

}
