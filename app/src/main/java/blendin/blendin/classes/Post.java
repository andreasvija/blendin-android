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
    public String authorID;
    public String category;
    public String title;
    public String content;
    public long timestamp; //generated upon creation
    //public Location location; //generated upon creation
    public double latitude;
    public double longitude;
    public int commentCount;
    //public ArrayList<Comment> comments; //generated upon creation, added to

    // Empty constructor required for importing Firebase database data into custom class objects
    public Post() {

    }

    public Post(String authorID, String category, String title, String content /*, ArrayList<Comment> comments*/) {
        this.authorID = authorID;
        this.category = category;
        this.title = title;
        this.content = content;

        this.timestamp = System.currentTimeMillis();

        /*this.location = new Location(""); // TODO: find location when creating post
        this.location.setLatitude(56.949d);
        this.location.setLongitude(24.106d); */
        this.latitude = 56.949d;
        this.longitude = 24.106d;
        this.commentCount = 0;

        //this.comments = comments;
    }

    /*public int getCommentCount() {
        return comments.size();
    }*/

    // Getter methods required for importing Firebase database data into custom class objects
    public String getAuthorID() {
        return authorID;
    }
    public String getCategory() {
        return category;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public double getlatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public int getCommentCount() {
        return commentCount;
    }
}
