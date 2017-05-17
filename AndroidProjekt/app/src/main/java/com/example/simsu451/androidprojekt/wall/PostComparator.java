package com.example.simsu451.androidprojekt.wall;

import java.util.Comparator;

/**
 * Created by gusan092 on 10/05/17.
 */

public class PostComparator implements Comparator<Post> {

    @Override
    public int compare(Post lhs, Post rhs) {
        if (lhs.getId() > rhs.getId()) return -1;
        else if (lhs.getId() < rhs.getId()) return 1;
        return 0;
    }

}
