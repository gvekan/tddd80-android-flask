package com.example.simsu451.androidprojekt.wall;

import java.util.Comparator;

/**
 * The PostComparator compares posts according to their index
 */

class PostComparator implements Comparator<Post> {

    @Override
    public int compare(Post post1, Post post2) {
        if (post1.getId() > post2.getId()) return -1;
        else if (post1.getId() < post2.getId()) return 1;
        return 0;
    }

}
