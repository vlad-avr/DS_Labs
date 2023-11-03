package com.example.control;

import java.util.Scanner;

import com.example.objects.Book;

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

    public double getDouble(String prompt) {
        System.out.println(prompt);
        while (true) {
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

    public Book.Genre getGenre(String prompt){
        System.out.println(prompt);
        while (true) {
            try{
                String input = getLine();
                Book.Genre genre = Book.Genre.valueOf(input);
                return genre;
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
