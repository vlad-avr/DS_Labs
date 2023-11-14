package com.example.dbManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IDGenerator {
    // ID Format is a String that goes before the numbers, for example "ID-1" where
    // "ID" is ID Format
    private final String idFormat;
    private List<String> idRecord = new ArrayList<>();
    private List<String> temporalCollector = new ArrayList<>();
    private List<String> reservedIds = new ArrayList<>();

    public IDGenerator(String idFormat) {
        this.idFormat = idFormat + "-";
    }

    public boolean idIsValid(String Id){
        if(getValue(Id) != -1 && Id.substring(0, Id.indexOf("-") + 1).equals(idFormat)){
            return true;
            
        }
        return false;
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
            return -1;
        }
    }

    public String generateId(){
        int Id = 1;
        if(idRecord.size() > 0){
            if(Id < getValue(idRecord.get(0)) && !temporalCollector.contains(idFormat + Id)){
                //addId(idFormat + Id);
                temporalCollector.add(idFormat + Id);
                return idFormat + Id;
            }
            for(int i = 1; i < idRecord.size(); i++){
                if(getValue(idRecord.get(i)) - getValue(idRecord.get(i-1)) > 1 && !temporalCollector.contains(idFormat + (getValue(idRecord.get(i-1)) + 1))){
                    //addId(idFormat + (getValue(idRecord.get(i-1)) + 1));
                    temporalCollector.add(idFormat + (getValue(idRecord.get(i-1)) + 1));
                    return idFormat + (getValue(idRecord.get(i-1)) + 1);
                }else{
                    Id = getValue(idRecord.get(i));
                }
            }
        }
        Id++;
        //addId(idFormat + Id);
        while (temporalCollector.contains(idFormat + Id)) {
            Id++;
        }
        temporalCollector.add(idFormat + Id);
        return idFormat + Id;
    }

    public void addId(String Id){
        if (!idRecord.contains(Id)) {
            idRecord.add(Id);
            if(temporalCollector.contains(Id)){
                temporalCollector.remove(Id);
            }
            sort();
        } else {
            System.out.println("\nThis ID already exists!");
        }
    }

    public boolean reserveId(String Id){
        if(reservedIds.contains(Id)){
            System.out.println(Id + " is already reserved by other client!");
            return false;
        }else{
            if(idRecord.contains(Id)){
                reservedIds.add(Id);
                return true;
            }else{
                System.out.println("This Id does not exist!");
                return false;
            }
        }
    }

    // public boolean isReserved(String Id){
    //     if(reservedIds.contains(Id)){
    //         return true;
    //     }
    //     return false;
    // }

    public void releaseId(String Id){
        if(reservedIds.contains(Id)){
            reservedIds.remove(Id);
        }
    }

    public void removeId(String Id) {
        if (idRecord.contains(Id)) {
            idRecord.remove(Id);
            if(reservedIds.contains(Id)){
                reservedIds.remove(Id);
            }
        } else {
            System.out.println("\nThis ID does not exist or it is reserved by other client!");
        }
    }

    public boolean exists(String Id) {
        if (idRecord.contains(Id) || temporalCollector.contains(Id)) {
            return true;
        }
        return false;
    }

    public List<String> getIDs(){
        return this.idRecord;
    }
}
