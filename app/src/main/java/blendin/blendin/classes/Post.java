/*
* Data structure for storing and passing the data of a post.
*/

package blendin.blendin.classes;

import java.io.Serializable;
import java.lang.System;

public class Post implements Serializable {

    private String id; //generated upon creation
    private String authorID;
    private String category;
    private String title;
    private String content;
    private long timestamp; //generated upon creation
    private double latitude;
    private double longitude;
    private int commentCount;

    // Empty constructor required for importing Firebase database data into custom class objects
    @SuppressWarnings("unused")
    public Post() {

    }

    public Post(String id, String authorID, String category, String title, String content, double latitude, double longitude) {
        this.id = id;
        this.authorID = authorID;
        this.category = category;
        this.title = title;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
        this.commentCount = 0;
    }

    // Public getter methods required for importing Firebase database data into custom class objects

    public String getId() {
        return id;
    }
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
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    @SuppressWarnings("WeakerAccess")
    public int getCommentCount() {
        return commentCount;
    }

}
