package com.example.SocketServer.Client;

public class ClientApp {
    public static void main(String[] args){
        String host = "localhost";
        final int portId = 1234;
        Client client = new Client(host, portId);
        client.run();
    }
}
