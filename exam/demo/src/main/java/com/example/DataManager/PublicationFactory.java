package com.example.DataManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.ConsoleManagement.InputManager;
import com.example.Entity.Publication;

public class PublicationFactory {

    public final static List<String> faculties = new ArrayList<>(Arrays.asList("FCSC", "FIT"));

    public static Publication makePublication(InputManager inputManager, String ID, List<String> sources) {
        System.out.println("\nYou are in 'Make your Publication!' menu, now make your student:\n");
        Publication publication = new Publication(ID);
        publication.setAuthor(inputManager.getString("Enter author: "));
        publication.setName(inputManager.getString("Enter name: "));
        publication.setPublishingDate(inputManager.getDate("Enter publishing date: ").toString());
        publication.setNumberOfPages(inputManager.getInt("Enter number of pages: ", 0, Integer.MAX_VALUE));
        while(sources != null &&  inputManager.getBool("Add source? (+/-): ")){
            publication.addSource(inputManager.getOption(sources, "Enter source: "));
        }
        return publication;
    }

    public static Publication updateStudent(InputManager inputManager, Publication student) {
        if (student == null) {
            return null;
        }
        System.out.println("\nYou are in 'Update your Student!' menu, now modify your student:\n");
        String input;
        while (inputManager.getBool("Do you want to change something? (+/-)")) {
            input = inputManager.getString(
                    "Enter what to change ( a - author,\n n - name,\n d - date,\n p - number of pages): ");
            switch (input) {
                case "a":
                    student.setAuthor(inputManager.getString("Enter author: "));
                    break;
                case "n":
                    student.setName(inputManager.getString("Enter name: "));
                    break;
                case "d":
                    student.setPublishingDate(inputManager.getDate("Enter publishing date: ").toString());
                    break;
                case "p":
                    student.setNumberOfPages(inputManager.getInt("Enter number of pages: ", 0, Integer.MAX_VALUE));
                    break;
                default:
                    System.out.println("Invalid Command!");
                    break;
            }
        }
        return student;
    }

    public static List<Publication> initSampleData() {
        List<Publication> publications = new ArrayList<>();
        publications.add(new Publication("S-1", "Author2", "Book2", LocalDate.of(2002, 9, 10).toString(), 100));
        publications.add(new Publication("S-2", "Author3", "Book3", LocalDate.of(2003, 9, 10).toString(), 101));
        publications.add(new Publication("S-3", "Author4", "Book4", LocalDate.of(2004, 9, 10).toString(), 102));
        publications.add(new Publication("S-4", "Author5", "Book5", LocalDate.of(2005, 9, 10).toString(), 103));
        publications.add(new Publication("S-5", "Author6", "Book6", LocalDate.of(2006, 9, 10).toString(), 104));
        publications.add(new Publication("S-6", "Author7", "Book7", LocalDate.of(2007, 9, 10).toString(), 105));
        publications.add(new Publication("S-7", "Author8", "Book8", LocalDate.of(2008, 9, 10).toString(), 106));
        publications.add(new Publication("S-8", "Author9", "Book9", LocalDate.of(2009, 9, 10).toString(), 107));
        publications.add(new Publication("S-9", "Author10", "Book10", LocalDate.of(2010, 9, 10).toString(), 108));
        return publications;
    }

    public static void sort(List<Publication> list) {
        Collections.sort(list, new Comparator<Publication>() {

            @Override
            public int compare(Publication o1, Publication o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
    }

}
