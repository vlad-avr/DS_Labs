package com.example.ActiveMQ.Client;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Client {
    public Client(){
        try {
            // Create a connection factory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:8161");

            // Create a connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a destination (queue)
            Destination destination = session.createQueue("myQueue");

            // Create a consumer
            MessageConsumer consumer = session.createConsumer(destination);

            // Receive the message
            Message message = consumer.receive();

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Message received by client: " + textMessage.getText());
            } else {
                System.out.println("Unexpected message type received");
            }

            // Close resources
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
