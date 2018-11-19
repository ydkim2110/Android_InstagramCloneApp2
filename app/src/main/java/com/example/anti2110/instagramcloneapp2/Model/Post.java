package com.example.anti2110.instagramcloneapp2.Model;

/**
 * Created by anti2110 on 2018-11-19
 */
public class Post {

    private String post_id;
    private String post_image;
    private String description;
    private String publisher;

    public Post() {
    }

    public Post(String post_id, String post_image, String description, String publisher) {
        this.post_id = post_id;
        this.post_image = post_image;
        this.description = description;
        this.publisher = publisher;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
