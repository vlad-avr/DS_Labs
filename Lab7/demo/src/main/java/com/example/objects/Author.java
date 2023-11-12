package com.example.objects;

import java.util.ArrayList;
import java.util.List;

public class Author {
    private String firstName;
    private String lastName;
    private String id;
    private List<Book> books = new ArrayList<>();

    public Author(String firstName, String lastName, String ID){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = ID;
    }

    //SPECIFICALLY FOR JSON
    public Author(){}

    // public Author(String authorString, boolean andBooks){
    //     StringTokenizer st = new StringTokenizer(authorString);
    //     this.ID = st.nextToken();
    //     this.firstName = st.nextToken();
    //     this.lastName = st.nextToken();
    //     int N = Integer.parseInt(st.nextToken());
    //     for(int i = 0; i < N; i++){
    //         books.add(new Book(st.nextToken("\n"), true));
    //     }
    // }

    public Author(String ID){
        this.id = ID;
    }


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
		return this.id;
	}

    public String toString(){
        String res = "\nID : " + this.id + "\nName : " + this.lastName + " " + this.firstName + "\nBooks :";
        for(Book book : books){
            res += book.toString();
        }
        return res;
    }

    // public String encode(){
    //     String encoded = this.ID + " " + this.firstName + " " + this.lastName + " " + this.books.size();
    //     for(Book book : books){
    //         encoded += " " + book.encode() + "\n";
    //     }
    //     return encoded;
    // }
}
