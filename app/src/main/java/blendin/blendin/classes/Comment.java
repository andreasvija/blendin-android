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
    @SuppressWarnings("unused")
    public Comment() {

    }

    public Comment(String id, String authorID, String content, double latitude, double longitude) {
        this.id = id;
        this.authorID = authorID;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter methods required for importing Firebase database data into custom class objects

    @SuppressWarnings("WeakerAccess")
    public String getId() {
        return id;
    }
    @SuppressWarnings("WeakerAccess")
    public String getAuthorID() {
        return authorID;
    }
    public String getContent() {
        return content;
    }
    @SuppressWarnings("WeakerAccess")
    public long getTimestamp() {
        return timestamp;
    }
    @SuppressWarnings("WeakerAccess")
    public double getLatitude() {
        return latitude;
    }
    @SuppressWarnings("WeakerAccess")
    public double getLongitude() {
        return longitude;
    }

}
