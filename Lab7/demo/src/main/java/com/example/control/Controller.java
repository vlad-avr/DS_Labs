package com.example.control;

import com.example.db_controller.DatabaseManager;
import com.example.xml_parser.MyParser;

public class Controller {
    DatabaseManager dbManager = new DatabaseManager();
    MyParser parser = new MyParser("D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Data.xml", dbManager);

    public void start(){
        dbManager.initDB();
        parser.parseSAX();
    }
}
