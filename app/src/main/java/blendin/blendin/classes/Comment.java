/*
* Data structure for storing and passing the data of a comment.
*/

package blendin.blendin.classes;

import java.io.Serializable;

public class Comment implements Serializable {

    private String id; //generated upon creation
    private String authorID;
    private String content;
    private long timestamp; //generated upon creation
    private double latitude; //generated upon creation
    private double longitude; //generated upon creation

    // Empty constructor required for importing Firebase database data into custom class objects
    public Comment() {

    }

    public Comment(String authorID, String content, double latitude, double longitude) {
        this.authorID = authorID;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter methods required for importing Firebase database data into custom class objects

    public String getId() {
        return id;
    }
    public String getAuthorID() {
        return authorID;
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

    public void setId(String id) {
        this.id = id;
    }
}
