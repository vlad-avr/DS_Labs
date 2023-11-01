package com.example.db_controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IDGenerator {
    // ID Format is a String that goes before the numbers, for example "ID-1" where
    // "ID" is ID Format
    private final String idFormat;
    private List<String> idRecord = new ArrayList<>();

    public IDGenerator(String idFormat) {
        this.idFormat = idFormat + "-";
    }

    public void sort() {
        Collections.sort(idRecord, new Comparator<String>() {
            @Override
            public int compare(String id1, String id2) {
                return id1.compareTo(id2);
            }
        });
    }

    private int getValue(String ID){
        try{
            return Integer.parseInt(ID.substring(ID.lastIndexOf('-'), ID.length()));
        }catch(NumberFormatException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public String generateId(){
        int Id = 0;
        if(idRecord.size() > 0){
            if(Id < getValue(idRecord.get(0))){
                addId(idFormat + Id);
                return idFormat + Id;
            }
            for(int i = 1; i < idRecord.size(); i++){
                if(getValue(idRecord.get(i)) - getValue(idRecord.get(i-1)) > 1){
                    addId(i, idFormat + getValue(idRecord.get(i-1)) + 1);
                    return idFormat + getValue(idRecord.get(i-1)) + 1;
                }else{
                    Id = getValue(idRecord.get(i));
                }
            }
        }
        Id++;
        addId(idFormat + Id);
        return idFormat + Id;
    }

    public void addId(int index, String Id){
        if (!idRecord.contains(Id)) {
            idRecord.add(index, Id);
        } else {
            System.out.println("\nThis ID already exists!");
        }
    }

    public void addId(String Id) {
        addId(idRecord.size(), Id);
    }

    public void removeId(String Id) {
        if (idRecord.contains(Id)) {
            idRecord.add(Id);
        } else {
            System.out.println("\nThis ID does not exist!");
        }
    }

    public boolean exists(String Id) {
        if (idRecord.contains(Id)) {
            return true;
        }
        return false;
    }

    public List<String> getIDs(){
        return this.idRecord;
    }
}
