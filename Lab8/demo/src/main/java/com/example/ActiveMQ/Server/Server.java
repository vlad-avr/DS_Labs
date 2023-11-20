package com.example.ActiveMQ.Server;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Server {
    public Server(){
        try {
            // Create a connection factory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:8161");

            // Create a connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a topic
            Topic topic = session.createTopic("myTopic");

            // Create a producer
            MessageProducer producer = session.createProducer(topic);

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
}
