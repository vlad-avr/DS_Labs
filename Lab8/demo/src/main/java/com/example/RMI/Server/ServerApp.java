package com.example.RMI.Server;

public class ServerApp {
    
    public static void main(String[] args){
        Server server = new Server();
        server.initAndListen();
    }
}
