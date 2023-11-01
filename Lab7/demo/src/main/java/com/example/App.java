package com.example;

import com.example.control.Controller;

public final class App {
    private App() {
    }
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.start();
    }
}
