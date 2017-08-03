package blendin.blendin.classes;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Post {
    public String id; //generated upon creation
    public User author;
    public String category;
    public String title;
    public String content;
    public long timestamp; //generated upon creation
    public Location location; //generated upon creation
    public List<Comment> comments; //generated upon creation, added to

    public Post(User author, String category, String title, String details) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.content = details;

        //this.id = ; // TODO: generate id
        //this.timestamp = ; // TODO: find timestamp
        //this.location = ; // TODO: find location

        this.comments = new ArrayList<>();
    }
}
