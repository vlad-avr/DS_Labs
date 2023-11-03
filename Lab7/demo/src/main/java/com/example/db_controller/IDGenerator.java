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
                if(id1.length() < id2.length()){
                    return -1;
                }else if(id1.length() > id2.length()){
                    return 1;
                }else{
                    if(getValue(id1) < getValue(id2)){
                        return -1;
                    }else if(getValue(id1) > getValue(id2)){
                        return 1;
                    }
                }
                return 0;
            }
        });
    }

    private int getValue(String ID){
        try{
            return Integer.parseInt(ID.substring(ID.lastIndexOf('-')+1, ID.length()));
        }catch(NumberFormatException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public String generateId(){
        int Id = 1;
        if(idRecord.size() > 0){
            if(Id < getValue(idRecord.get(0))){
                addId(idFormat + Id);
                return idFormat + Id;
            }
            for(int i = 1; i < idRecord.size(); i++){
                if(getValue(idRecord.get(i)) - getValue(idRecord.get(i-1)) > 1){
                    addId(idFormat + (getValue(idRecord.get(i-1)) + 1));
                    return idFormat + (getValue(idRecord.get(i-1)) + 1);
                }else{
                    Id = getValue(idRecord.get(i));
                }
            }
        }
        Id++;
        addId(idFormat + Id);
        return idFormat + Id;
    }

    public void addId(String Id){
        if (!idRecord.contains(Id)) {
            idRecord.add(Id);
            sort();
        } else {
            System.out.println("\nThis ID already exists!");
        }
    }

    public void removeId(String Id) {
        if (idRecord.contains(Id)) {
            idRecord.remove(Id);
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
