/*
* Data structure for storing and passing the data of a post or comment's author.
*/

package blendin.blendin.classes;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String id;
    private String name;
    private String photoURL;
    private String location;
    private List<String> languages;

    // Empty constructor required for importing Firebase database data into custom class objects
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public String getPhotoURL() {
        return photoURL;
    }
    public String getLocation() {
        return location;
    }
    @SuppressWarnings("unused")
    public List<String> getLanguages() {
        return languages;
    }
}
