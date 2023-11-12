package com.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientApp {
    public static void main(String[] args){
        String host = "localhost";
        final int portId = 1234;
        Client client = new Client(host, portId);
        client.run();
    }
}
