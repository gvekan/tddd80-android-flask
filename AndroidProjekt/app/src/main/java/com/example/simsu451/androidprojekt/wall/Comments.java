package com.example.simsu451.androidprojekt.wall;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by simsu451 on 28/04/17.
 */

public class Comments {
    private ArrayList<Comment> comments;

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComments(ArrayList<Comment> comments) {
        this.comments.addAll(comments);
        Collections.sort(this.comments, new CommentComparator());
    }

    public int getLatest() {
        int size = comments.size();
        if (size == 0) return -1;
        return comments.get(size - 1).getIndex();
    }

    public int getOldest() {
        if (comments.size() == 0) return -1;
        return comments.get(0).getIndex();
    }
}
