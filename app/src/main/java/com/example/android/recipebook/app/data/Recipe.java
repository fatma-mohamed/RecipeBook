package com.example.android.recipebook.app.data;

import static android.R.attr.id;

/**
 * Created by Fatma on 08-Jan-17.
 */

public class Recipe {
    private String _ID;
    private String publisher;
    private String title;
    private String sourceURL;
    private String imageURL;
    private Double time;
    private Integer numServings;

    public Recipe(String _publisher, String _title,  String source_url, String image_url,  String id)
    {
        _ID = id;
        title = _title;
        publisher = _publisher;
        sourceURL = source_url;
        imageURL = image_url;
        time = null;
        numServings = null;
    }

    public String get_ID() {
        return _ID;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getTitle() {
        return title;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Double getTime() {
        return time;
    }

    public Integer getNumServings() {
        return numServings;
    }


//ingredients
    //directions

}
