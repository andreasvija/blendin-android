/*
* Data structure for storing and passing the data of a post.
*/

package blendin.blendin.classes;

import java.io.Serializable;
import java.lang.System;

public class Post implements Serializable {

    public String id; //generated upon creation
    public String authorID;
    public String category;
    public String title;
    public String content;
    public long timestamp; //generated upon creation
    public double latitude;
    public double longitude;
    public int commentCount;

    // Empty constructor required for importing Firebase database data into custom class objects
    public Post() {

    }

    public Post(String authorID, String category, String title, String content, double latitude, double longitude) {
        this.authorID = authorID;
        this.category = category;
        this.title = title;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
        this.commentCount = 0;
    }

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
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public int getCommentCount() {
        return commentCount;
    }
}
