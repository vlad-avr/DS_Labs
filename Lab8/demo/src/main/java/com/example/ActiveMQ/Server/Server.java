package com.example.ActiveMQ.Server;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.example.Entities.Author;
import com.example.dbManager.DatabaseManager;
import com.example.dbManager.MyParser;

public class Server {

    public DatabaseManager dbManager = new DatabaseManager();
    private MyParser parser = new MyParser();
    private ReadWriteLock dbLock = new ReentrantReadWriteLock();
    private ReadWriteLock bookLock = new ReentrantReadWriteLock();
    private ReadWriteLock authorLock = new ReentrantReadWriteLock();
    private Connection connection;
    //private Session session;

    public Server() {
        dbManager.initDB();
        loadData();
    }

    private void loadData() {
        List<Author> authors = parser
                .parseSAX("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml");
        if (authors == null) {
            return;
        }
        for (Author author : authors) {
            dbManager.addAuthor(author);
        }
    }

    public void loadToDB(List<Author> authors) {
        for (Author author : authors) {
            writeLock(dbLock);
            writeLock(authorLock);
            writeLock(bookLock);
            if (dbManager.getAuthorGenerator().exists(author.getId())) {
                dbManager.updateAuthor(author, true);
            } else {
                dbManager.addAuthor(author, true);
            }
            writeUnlock(bookLock);
            writeUnlock(authorLock);
            writeUnlock(dbLock);
        }
    }

    public ReadWriteLock getDBLock() {
        return this.dbLock;
    }

    public ReadWriteLock getBookLock() {
        return this.bookLock;
    }

    public ReadWriteLock getAuthorLock() {
        return this.authorLock;
    }

    public void writeLock(ReadWriteLock lock) {
        lock.writeLock().lock();
    }

    public void writeUnlock(ReadWriteLock lock) {
        lock.writeLock().unlock();
    }

    public void readLock(ReadWriteLock lock) {
        lock.readLock().lock();
    }

    public void readUnlock(ReadWriteLock lock) {
        lock.readLock().unlock();
    }

    public void listenForClients() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue destination = session.createQueue("main");
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);
        int clientCounter = 0;
        boolean working = true;
        while(working){
            String res = ((TextMessage)consumer.receive()).getText();
            if(res.equals("e")){
                working = false;
            }
            if(res.equals("c")){
                producer.send(session.createTextMessage(String.valueOf(clientCounter)));
                Thread thr = new Thread(new ClientHandler(session, clientCounter, this));
                thr.start();
                System.out.println("New Client " + clientCounter + " has joined!");
                clientCounter++;
                
            }
        }
        consumer.close();
        producer.close();
        session.close();
        connection.close();
    }

}
