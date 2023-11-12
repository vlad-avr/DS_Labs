package com.example.InputManager;

import java.util.List;
import java.util.Scanner;

import com.example.Entities.Book;
import com.example.dbManager.IDGenerator;


public class InputManager {
    private Scanner scanner = new Scanner(System.in);

    private String getLine() {
        String input;
        do {
            input = scanner.nextLine();
        } while (input == null || input.isEmpty());
        return input;
    }

    public String getString(String prompt) {
        System.out.println(prompt);
        return getLine();
    }

    public boolean getBool(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = getLine();
            if (input.equals("+")) {
                return true;
            } else if (input.equals("-")) {
                return false;
            } else {
                System.out.println("Invalid input: enter '+' for yes or '-' for no!");
            }
        }
    }

    public String getID(List<String> IDs, String prompt){
        System.out.println("Available IDs : \n");
        System.out.println(IDs);
        while (true) {
            System.out.println(prompt);
            String id = getLine();
            if(IDs.contains(id)){
                return id;
            }else{
                System.out.println(id + " does not exist!");
            }            
        }
    }

    public String getID(IDGenerator idGenerator, String prompt){
        System.out.println("Available IDs : \n");
        System.out.println(idGenerator.getIDs());
        while (true) {
            System.out.println(prompt);
            String id = getLine();
            if(idGenerator.exists(id)){
                return id;
            }else{
                System.out.println(id + " does not exist!");
            }            
        }
    }

    public double getDouble(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                String input = getLine();
                double res = Double.parseDouble(input);
                if (res < 0) {
                    NumberFormatException e = new NumberFormatException("Value must be greater than 0!");
                    throw e;
                }
                return res;
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public int getInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                String input = getLine();
                int res = Integer.parseInt(input);
                if (res < 0) {
                    NumberFormatException e = new NumberFormatException("Value must be greater than 0!");
                    throw e;
                }
                return res;
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public Book.Genre getGenre(String prompt) {
        System.out.println("Available genres : ");
        for(Book.Genre genre : Book.Genre.values()){
            System.out.println(genre.toString());
        }
        while (true) {
            System.out.println(prompt);
            try {
                String input = getLine();
                Book.Genre genre = Book.Genre.valueOf(input);
                return genre;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
