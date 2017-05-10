package com.example.simsu451.androidprojekt.wall;


import java.util.List;
import java.util.Collections;


/**
 * Created by simsu451 on 28/04/17.
 */

public class Posts {
    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        Collections.sort(this.posts, new PostComparator());
    }

    public void addPosts(List<Post> posts) {
        this.posts.addAll(posts);
        Collections.sort(this.posts, new PostComparator());
    }

    public int getLatest() {
        if (posts.size() == 0) return -1;
        return posts.get(0).getId();
    }

    public int getOldest() {
        if (posts.size() == 0) return -1;
        return posts.get(posts.size() - 1).getId();
    }


}
