package com.example.ConsoleManagement;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

//import com.example.DataManager.IDGenerator;

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

    public String getOption(List<String> options, String prompt) {
        while (true) {
            System.out.println(prompt);
            System.out.println("Available options : \n");
            System.out.println(options);
            String option = getLine();
            if (options.contains(option)) {
                return option;
            } else {
                System.out.println(option + " does not exist!");
            }
        }
    }

    public LocalDate getDate(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                String input = getLine();
                LocalDate date = LocalDate.parse(input);
                if (date.isAfter(LocalDate.now())) {
                    Exception e = new Exception("This date is from the Future! Enter a valid date");
                    throw e;
                }
                return date;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public int getInt(String prompt, int lowerBound, int upperBound) {
        while (true) {
            System.out.println(prompt);
            try {
                String input = getLine();
                int res = Integer.parseInt(input);
                if (res < lowerBound || res > upperBound) {
                    NumberFormatException e = new NumberFormatException(
                            "Value must be in range [" + lowerBound + ", " + upperBound + "]!");
                    throw e;
                }
                return res;
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
