package com.example.psmart.ui.login;

public class User {

    private final int id;
    private static String name;
    private static String email;


    public User(int id, String email, String name) {
        this.id = id;
        User.name = name;
        User.email = email;

    }

    public int getId() {
        return id;
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }
}

