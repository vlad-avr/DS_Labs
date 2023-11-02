package com.example.objects;

import java.util.ArrayList;
import java.util.List;

public class Author {
    private String firstName;
    private String lastName;
    private String ID;
    private List<Book> books = new ArrayList<>();

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

    public void addBook(Book book){
        books.add(book);
    }

    public void removeBook(String bookId){
        for(Book book : books){
            if(book.getId().equals(bookId)){
                books.remove(book);
                return;
            }
        }
        System.out.println("\nBook with id " + bookId + " not found!");
    }

    public List<Book> getBooks(){
        return this.books;
    }

	public String getId() {
		return this.ID;
	}

    public String toString(){
        String res = "\nID : " + this.ID + "\nName : " + this.lastName + " " + this.firstName + "\nBooks :";
        for(Book book : books){
            res += book.toString();
        }
        return res;
    }
}
