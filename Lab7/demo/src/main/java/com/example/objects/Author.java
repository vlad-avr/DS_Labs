package com.example.objects;

import java.util.ArrayList;
import java.util.List;

public class Author {
    private String firstName;
    private String lastName;
    private String ID;
    private List<String> books = new ArrayList<>();

    public Author(String firstName, String lastName, String ID){
        this.firstName = firstName;
        this.lastName = lastName;
        this.ID = ID;
    }

    public Author(){}

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void addBook(String bookId){
        if(!books.contains(bookId)){
            books.add(bookId);
        }else{
            System.out.println("\nAuthor " + ID + "  is ALREADY the author of book : " + bookId);
        }
    }

    public void removeBook(String bookId){
        if(books.contains(bookId)){
            books.remove(bookId);
        }else{
            System.out.println("\nAuthor " + ID + " is NOT the author of book : " + bookId);
        }
    }

    public List<String> getBooks(){
        return this.books;
    }

	public String getId() {
		return this.ID;
	}
}
