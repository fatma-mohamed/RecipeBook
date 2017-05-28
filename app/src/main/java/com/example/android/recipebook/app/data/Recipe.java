package com.example.android.recipebook.app.data;


import java.util.ArrayList;

/**
 * Created by Fatma on 08-Jan-17.
 */

public class Recipe {
    private String _ID = null;
    private String publisher;
    private String title;
    private String sourceURL;
    private String imageURL;
    private Double time;
    private Integer numServings;
    private String ingredients;
    private String directions;

    public Recipe(String _publisher, String _title,  String source_url, String image_url,  String id)
    {
        _ID = id;
        title = _title;
        publisher = _publisher;
        sourceURL = source_url;
        imageURL = image_url;
        time = null;
        numServings = null;
        ingredients = null;
        directions = null;
    }

    public Recipe(String id, String _title,  Double t, Integer n, String i, String d)
    {
        _ID = id;
        title = _title;
        publisher = null;
        sourceURL = null;
        imageURL = null;
        time = t;
        numServings = n;
        ingredients = i;
        directions = d;
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

    public String getDirections() {
        return directions;
    }

    public String getIngredients() {
        return ingredients;
    }

    public Integer getNumServings() {
        return numServings;
    }

}
