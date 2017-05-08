package com.example.simsu451.androidprojekt;


import java.util.List;

/**
 * Created by simsu451 on 28/04/17.
 */

public class Posts {
    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        //sort
        this.posts = posts;
    }


}
