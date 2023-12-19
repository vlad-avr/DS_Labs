package com.example.DataManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.Entity.Publication;

public class DataManager {

    private List<Publication> publications;
    private IDGenerator idGenerator;

    public DataManager(){
        publications = new ArrayList<>();
        idGenerator = new IDGenerator("S");
    }

    public DataManager(String IdSample){
        publications = new ArrayList<>();
        idGenerator = new IDGenerator(IdSample);
    }

    public DataManager(List<Publication> dummyData, String IdSample){
        this.publications = new ArrayList<>();
        idGenerator = new IDGenerator(IdSample);
        for(Publication student : dummyData){
            addPublication(student);
        }
    }

    public IDGenerator getIdGenerator(){
        return this.idGenerator;
    }

    public List<Publication> getPublications(){
        return publications;
    }

    public List<Publication> getPublicationsByDate(LocalDate date){
        List<Publication> result = new ArrayList<>();
        for(Publication student : publications){
            if(LocalDate.parse(student.getPublishingDate()).isAfter(date)){
                result.add(student);
            }
        }
        return result;
    }

    public List<Publication> getPublicationsByName(String str){
        List<Publication> result = new ArrayList<>();
        for(Publication student : publications){
            if(student.getName().contains(str)){
                result.add(student);
            }
        }
        return result;
    }

    public Publication getPublication(String Id){
        for(Publication publication : publications){
            if(publication.getID().equals(Id)){
                return publication;
            }
        }
        return null;
    }

    public void addPublication(Publication publication){
        publications.add(publication);
        idGenerator.addId(publication.getID());
    }

    public void deletePublication(String Id){
        int deleteInd = 0;
        for(; deleteInd < publications.size(); deleteInd++){
            if(publications.get(deleteInd).getID().equals(Id)){
                break;
            }
        }
        if(deleteInd != publications.size()){
            publications.remove(deleteInd);
            idGenerator.removeId(Id);
        }
    }

    public void updatePublication(Publication upd){
        int ind = 0;
        for(;ind < publications.size(); ind++){
            if(publications.get(ind).getID().equals(upd.getID())){
                break;
            }
        }
        publications.set(ind, upd);
    }
    
}
