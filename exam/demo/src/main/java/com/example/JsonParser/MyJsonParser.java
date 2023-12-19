package com.example.JsonParser;

import java.util.List;

import com.example.Entity.Publication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyJsonParser {

    public static List<Publication> parsePublications(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Publication>> ref = new TypeReference<List<Publication>>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Publication parsePublication(String json) {
        if (json == "") {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Publication> ref = new TypeReference<Publication>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    public static List<String> parseIds(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<String>> ref = new TypeReference<List<String>>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String toJsonPublication(Publication pub) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(pub);
        } catch (JsonProcessingException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "";
        }
    }


    public static String toJsonPublications(List<Publication> pubs) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(pubs);
        } catch (JsonProcessingException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "";
        }
    }

    public static String toJsonIDs(List<String> IDs) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(IDs);
        } catch (JsonProcessingException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "";
        }
    }
}