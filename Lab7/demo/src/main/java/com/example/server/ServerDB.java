package com.example.server;

import com.example.db_controller.DatabaseManager;
import com.example.xml_parser.MyParser;

public class ServerDB {
    private DatabaseManager dbManager;
    private MyParser parser;
    
    public ServerDB(){
        dbManager = new DatabaseManager();
        parser = new MyParser("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Schema.xsd", dbManager);
    }
}
