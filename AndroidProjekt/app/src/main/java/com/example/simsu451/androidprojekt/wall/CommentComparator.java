package com.example.simsu451.androidprojekt.wall;

import java.util.Comparator;

/**
 * The CommentsComparator sorts comments according to their index
 */

class CommentComparator implements Comparator<Comment> {

    @Override
    public int compare(Comment comment1, Comment comment2) {
        if (comment1.getIndex() < comment2.getIndex()) return -1;
        else if (comment1.getIndex() > comment2.getIndex()) return 1;
        return 0;
    }
}
