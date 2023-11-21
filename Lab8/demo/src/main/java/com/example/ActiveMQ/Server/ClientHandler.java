package com.example.ActiveMQ.Server;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

public class ClientHandler implements Runnable{

    private MessageProducer producer;
    private MessageConsumer consumer;

    @Override
    public void run() {
        boolean working = true;
    }
    
}
