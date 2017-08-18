/*
* Data structure for storing and passing the data of a post or comment's author.
*/

package blendin.blendin.classes;

import java.io.Serializable;
import java.net.URL;

public class User implements Serializable {

    //public String id;
    public String name;
    public String photoURL;

    // Empty constructor required for importing Firebase database data into custom class objects
    public User() {

    }

    public User(/*String id,*/ String name, String photoURL) {
        //TODO: download id, name and photoURL from fb
        //this.id = id;
        this.name = name;
        this.photoURL = photoURL;
    }

    // Getter methods required for importing Firebase database data into custom class objects
    public String getName() {
        return name;
    }
    public String getPhotoURL() {
        return photoURL;
    }
}
