/*
* Data structure for storing and passing the data of a comment.
*/

package blendin.blendin.classes;

import java.io.Serializable;

public class Comment implements Serializable {

    //public String id; //generated upon creation
    public String authorID;
    public String content;
    public long timestamp; //generated upon creation
    //public Location location; //generated upon creation
    public double latitude;
    public double longitude;

    public Comment() {

    }

    public Comment(String authorID, String content) {
        this.authorID = authorID;
        this.content = content;

        //this.id = "12"; // TODO: generate id in backend
        this.timestamp = 1501794232000L; // TODO: generate time in backend

        /*this.location = new Location(""); // TODO: find location when creating comment
        this.location.setLatitude(56.949d);
        this.location.setLongitude(24.106d); */
        latitude = 56.949d;
        longitude = 24.106d;
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
    public double getlatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

}
