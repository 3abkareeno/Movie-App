package com.example.android.data;

/**
 * Created by Dr. Osama on 10/18/2016.
 */

public class Trailer {
    private String name;
    private String link;

    public Trailer(String name, String link) {
        this.name = name;
        this.link = "https://www.youtube.com/watch?v=" + link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = "https://www.youtube.com/watch?v=" + link;
    }
}
