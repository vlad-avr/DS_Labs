package com.example.Socket.Server;

public class ServerApp {
    public static void main(String[] args){
        System.out.println("SERVER ONLINE");
        Server server = new Server();
        server.run();
    }
}
