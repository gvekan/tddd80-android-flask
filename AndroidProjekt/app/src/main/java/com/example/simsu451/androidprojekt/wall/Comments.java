package com.example.simsu451.androidprojekt.wall;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Java bean for a list of <Comment>
 */

class Comments {
    private ArrayList<Comment> comments;

    ArrayList<Comment> getComments() {
        return comments;
    }

    void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    void addComments(ArrayList<Comment> comments) {
        this.comments.addAll(comments);
        Collections.sort(this.comments, new CommentComparator());
    }

    int getLatest() {
        int size = comments.size();
        if (size == 0) return -1;
        return comments.get(size - 1).getIndex();
    }

    int getOldest() {
        if (comments.size() == 0) return -1;
        return comments.get(0).getIndex();
    }
}
