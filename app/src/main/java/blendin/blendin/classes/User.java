/*
* Data structure for storing and passing the data of a post or comment's author.
*/

package blendin.blendin.classes;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

public class User implements Serializable {

    public String id;
    public String name;
    public String photoURL;
    public String location;
    public List<String> languages;

    // Empty constructor required for importing Firebase database data into custom class objects
    public User() {

    }

    public User(String id, String name, String photoURL, String location, List<String> languages) {
        this.id = id;
        this.name = name;
        this.photoURL = photoURL;
        this.location = location;
        this.languages = languages;
    }

    // Getter methods required for importing Firebase database data into custom class objects
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPhotoURL() {
        return photoURL;
    }
    public String getLocation() {
        return location;
    }
    public List<String> getLanguages() {
        return languages;
    }
}
