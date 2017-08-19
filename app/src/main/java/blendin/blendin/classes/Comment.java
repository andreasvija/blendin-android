/*
* Data structure for storing and passing the data of a comment.
*/

package blendin.blendin.classes;

import java.io.Serializable;

public class Comment implements Serializable {

    public String id; //generated upon creation
    public String authorID;
    public String content;
    public long timestamp; //generated upon creation
    //public Location location; //generated upon creation
    public double latitude;
    public double longitude;

    // Empty constructor required for importing Firebase database data into custom class objects
    public Comment() {

    }

    public Comment(String authorID, String content) {
        this.authorID = authorID;
        this.content = content;
        
        this.timestamp = System.currentTimeMillis();
        /*this.location = new Location(""); // TODO: find location when creating comment
        this.location.setLatitude(56.949d);
        this.location.setLongitude(24.106d); */
        this.latitude = 56.949d;
        this.longitude = 24.106d;
    }

    // Getter methods required for importing Firebase database data into custom class objects
    public String getAuthorID() {
        return authorID;
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

}
