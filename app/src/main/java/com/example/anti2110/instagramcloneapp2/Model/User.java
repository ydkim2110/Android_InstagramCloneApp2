package com.example.anti2110.instagramcloneapp2.Model;

/**
 * Created by anti2110 on 2018-11-18
 */
public class User {

    private String id;
    private String username;
    private String fullname;
    private String imageUrl;
    private String bio;

    public User() {
    }

    public User(String id, String usename, String fullname, String imageUrl, String bio) {
        this.id = id;
        this.username = usename;
        this.fullname = fullname;
        this.imageUrl = imageUrl;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
