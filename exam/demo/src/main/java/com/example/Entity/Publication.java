package com.example.Entity;

import java.io.Serializable;

public class Publication implements Serializable{
    private String ID;
    private String author;
    private String publishingDate;
    private int numberOfPages;
    private String name;

    public Publication() {
    }

    public Publication(String ID) {
        this.ID = ID;
    }

    public Publication(String ID, String author, String name, String publishingDate, int numberOfPages) {
        this.ID = ID;
        this.author = author;
        this.name = name;
        this.publishingDate = publishingDate.toString();
        this.numberOfPages = numberOfPages;
    }

    public void setID(String val) {
        ID = val;
    }

    public void setAuthor(String val) {
        author = val;
    }

    public void setPublishingDate(String date) {
        publishingDate = date;
    }

    public void setNumberOfPages(int num){
        numberOfPages = num;
    }

    public void setName(String val) {
        name = val;
    }

    public String getID() {
        return ID;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishingDate() {
        return publishingDate;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "Student " + ID + " : \n" + " Name : " + name + "\n Author :  " + author
                + "\n Publication Date : " + publishingDate.toString()
                + "\n Number of Pages : " + numberOfPages;
    }
}
