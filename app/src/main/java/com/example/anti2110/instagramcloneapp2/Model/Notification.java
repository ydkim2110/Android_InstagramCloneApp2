package com.example.anti2110.instagramcloneapp2.Model;

/**
 * Created by anti2110 on 2018-11-23
 */
public class Notification {

    private String user_id;
    private String comment;
    private String post_id;
    private boolean is_post;

    public Notification() {
    }

    public Notification(String user_id, String comment, String post_id, boolean is_post) {
        this.user_id = user_id;
        this.comment = comment;
        this.post_id = post_id;
        this.is_post = is_post;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public boolean isIs_post() {
        return is_post;
    }

    public void setIs_post(boolean is_post) {
        this.is_post = is_post;
    }
}
