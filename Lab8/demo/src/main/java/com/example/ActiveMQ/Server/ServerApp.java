package com.example.ActiveMQ.Server;

import javax.jms.JMSException;

public class ServerApp {
    public static void main(String[] args){
        Server server = new Server();
        try {
            server.listenForClients();
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
