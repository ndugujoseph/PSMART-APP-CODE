package com.example.psmart.ui.login;

public class Account {
    private final int id;
    private final String name;
    private final String gender;
    private final String email;
    private final String contact;
    private final String image;

    public Account(int id, String name, String gender, String email, String contact,String image) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.contact = contact;
        this.image = image;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }
    public String getImage() {
        return image;
    }
}