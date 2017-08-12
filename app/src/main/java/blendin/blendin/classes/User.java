/*
* Data structure for storing and passing the data of a post or comment's author.
*/

package blendin.blendin.classes;

public class User {
    public String id;
    public String name;
    public String photoURL;

    public User(String id, String name, String photoURL) { //TODO: download name and photoURL from fb
        this.id = id;
        this.name = name;
        this.photoURL = photoURL;
    }
}
