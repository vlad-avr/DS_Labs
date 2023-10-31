package com.example.db_controller;

import java.util.ArrayList;
import java.util.List;

public class IDGenerator {
    private List<String> idRecord = new ArrayList<>();

    public void addId(String Id){
        if(!idRecord.contains(Id)){
            idRecord.add(Id);
        }else{
            System.out.println("\nThis ID already exists!");
        }
    }

    public boolean exists(String Id){
        if(idRecord.contains(Id)){
            return true;
        }
        return false;
    }
}
