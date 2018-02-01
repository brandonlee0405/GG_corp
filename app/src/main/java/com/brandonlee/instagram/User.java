package com.brandonlee.instagram;

/**
 * Created by adria on 2/1/2018.
 */

public class User {
    String userID;
    String username;
    String fullname;
    String theme;

    public User(){

    }

    public User(String userID, String username, String fullname, String theme) {
        this.userID = userID;
        this.username = username;
        this.fullname = fullname;
        this.theme = theme;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getTheme() {
        return theme;
    }


}
