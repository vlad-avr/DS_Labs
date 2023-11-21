package com.example.ActiveMQ.Server;

import java.util.List;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.example.Entities.Author;
import com.example.dbManager.DatabaseManager;
import com.example.dbManager.MyParser;

public class Server {

    public DatabaseManager dbManager = new DatabaseManager();
    private MyParser parser = new MyParser();

    public Server(){
        dbManager.initDB();
        loadData();
        try {
            // Create a connection factory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            // Create a connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a topic
            Queue destination = session.createQueue("myQueue");

            // Create a producer
            MessageProducer producer = session.createProducer(destination);

            // Create a message
            TextMessage message = session.createTextMessage("Hello, Clients!");

            // Send the message to the topic
            producer.send(message);

            System.out.println("Message sent to the topic");

            // Close resources
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
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

}
