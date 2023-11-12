package com.example.server;

public class Server {
    
    public static void main(String[] args){
        System.out.println("SERVER ONLINE");
        ServerHandler server = new ServerHandler();
        server.run();
    }

}
