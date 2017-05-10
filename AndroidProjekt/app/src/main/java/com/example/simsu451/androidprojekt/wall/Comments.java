package com.example.simsu451.androidprojekt.wall;


import java.util.List;

/**
 * Created by simsu451 on 28/04/17.
 */

public class Comments {
    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComments(List<Comment> comments) {
        this.comments.addAll(comments);
    }


}
