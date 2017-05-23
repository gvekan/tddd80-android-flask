package com.example.simsu451.androidprojekt.wall;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Java bean for a list of <Post>
 */

class Posts {
    private ArrayList<Post> posts;

    ArrayList<Post> getPosts() {
        return posts;
    }

    void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    void addPosts(ArrayList<Post> posts) {
        this.posts.addAll(posts);
        Collections.sort(this.posts, new PostComparator());
    }

    int getLatest() {
        if (posts.size() == 0) return -1;
        return posts.get(0).getId();
    }

    int getOldest() {
        if (posts.size() == 0) return -1;
        return posts.get(posts.size() - 1).getId();
    }


}
