package com.example.simsu451.androidprojekt.wall;

import java.util.Comparator;

/**
 * Created by gusan092 on 10/05/17.
 */

public class CommentComparator implements Comparator<Comment> {

    @Override
    public int compare(Comment lhs, Comment rhs) {
        if (lhs.getIndex() > rhs.getIndex()) return -1;
        else if (lhs.getIndex() < rhs.getIndex()) return 1;
        return 0;
    }
}
