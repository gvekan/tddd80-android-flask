package com.example.simsu451.androidprojekt.wall;

/**
 * Created by simsu451 on 28/04/17.
 */

public class Post {
    private int id;
    private String name;
    private String text;
    private int likes;
    private boolean liking;
    private int comments;
    private boolean commentsShowing = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLiking() {
        return liking;
    }

    public void setLiking(boolean liking) {
        this.liking = liking;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public boolean isCommentsShowing() {
        return commentsShowing;
    }

    public void setCommentsShowing(boolean commentsShowing) {
        this.commentsShowing = commentsShowing;
    }
}
