/*
* Data structure for storing and passing the data of a post.
*/

package blendin.blendin.classes;

import android.location.Location;

import java.io.Serializable;
import java.lang.System;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {

    public String id; //generated upon creation
    public User author;
    public String category;
    public String title;
    public String content;
    public long timestamp; //generated upon creation
    //public Location location; //generated upon creation
    public double latitude;
    public double longitude;
    public ArrayList<Comment> comments; //generated upon creation, added to

    public Post(User author, String category, String title, String content, ArrayList<Comment> comments) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.content = content;

        this.id = "12"; // TODO: generate id in backend
        this.timestamp = 1501794232000L; // TODO: generate time in backend

        /*this.location = new Location(""); // TODO: find location when creating post
        this.location.setLatitude(56.949d);
        this.location.setLongitude(24.106d); */
        latitude = 56.949d;
        longitude = 24.106d;

        this.comments = comments;
    }

    public int getCommentCount() {
        return comments.size();
    }

}
