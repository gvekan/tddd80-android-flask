package com.example.simsu451.androidprojekt.wall;

/**
 * Java bean for a post
 */

class Post {
    private int id;
    private String name;
    private String text;
    private int likes;
    private boolean liking;
    private int comments;
    private boolean commentsShowing = false;
    private String city;

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

    int getLikes() {
        return likes;
    }

    void setLikes(int likes) {
        this.likes = likes;
    }

    boolean isLiking() {
        return liking;
    }

    void setLiking(boolean liking) {
        this.liking = liking;
    }

    int getComments() {
        return comments;
    }

    void setComments(int comments) {
        this.comments = comments;
    }

    boolean isCommentsShowing() {
        return commentsShowing;
    }

    void setCommentsShowing(boolean commentsShowing) {
        this.commentsShowing = commentsShowing;
    }

    String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
