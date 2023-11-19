package com.example.Entities;

import java.io.Serializable;

//import com.example.db_controller.IDGenerator;

public class Book implements Serializable{

    public static enum Genre {
        horror,
        scifi,
        comedy,
        drama,
        adventure,
        biography
    }

    private String id;
    private String name;
    private Double price;
    private Genre genre;
    private String authorId;
    // private IDGenerator authorIds;

    public Book(String id, String name, Double price, Genre genre, String authorId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.genre = genre;
        this.authorId = authorId;
    }

    //SPECIFICALLY FOR JSON
    public Book(){}

    public Book(String id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public String getGenre() {
        return this.genre.toString();
    }

    public String getAuthor() {
        return this.authorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setAuthor(String Id) {
        this.authorId = Id;
    }

    public String getId(){
        return this.id;
    }

    public String toString(){
        return "\n ID : " + this.id + "\n Name : " + this.name + "\n Price : " + this.price + " $ \n Genre : " + this.genre;
    }

    // public String encode(){
    //     return this.id + " " + this.name + " " + this.price + " " + this.genre + " " + this.authorId;
    // }
}
