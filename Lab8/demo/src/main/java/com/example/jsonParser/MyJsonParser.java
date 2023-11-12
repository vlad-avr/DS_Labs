package com.example.jsonParser;

import java.util.List;

import com.example.Entities.Author;
import com.example.Entities.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyJsonParser {
    public static List<Author> parseAuthors(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Author>> ref = new TypeReference<List<Author>>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Author parseAuthor(String json) {
        if (json == "") {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Author> ref = new TypeReference<Author>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Book parseBook(String json) {
        if (json == "") {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Book> ref = new TypeReference<Book>() {
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

    public static List<Book> parseBooks(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Book>> ref = new TypeReference<List<Book>>() {
        };
        try {
            return mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String toJsonAuthor(Author author) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(author);
        } catch (JsonProcessingException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "";
        }
    }

    public static String toJsonBook(Book book) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(book);
        } catch (JsonProcessingException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "";
        }
    }

    public static String toJsonAuthors(List<Author> authors) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(authors);
        } catch (JsonProcessingException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "";
        }
    }

    public static String toJsonBooks(List<Book> books) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(books);
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
