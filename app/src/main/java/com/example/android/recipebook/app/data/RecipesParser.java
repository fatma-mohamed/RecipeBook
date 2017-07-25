package com.example.android.recipebook.app.data;

import android.util.Log;

import com.example.android.recipebook.app.interfaces.DataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Fatma on 19-Nov-16.
 */

public class RecipesParser implements DataParser {
    @Override
    public ArrayList<Recipe> parse(JSONObject recipesJson) throws JSONException {
        final String RECIPES_LIST = "recipes";
        final String SIZE = "count";

        final String PUBLISHER = "publisher";
        final String TITLE = "title";
        final String SOURCE_URL = "source_url";
        final String IMAGE_URL = "image_url";
        final String ID = "recipe_id";


        JSONArray recipesArray = recipesJson.getJSONArray(RECIPES_LIST);

        int arraySize = recipesArray.length();
        ArrayList<Recipe> results = new ArrayList<Recipe>();
        for(int i=0;i<arraySize;++i)
        {
            String publisher;
            String title;
            String source_url;
            String image_url;
            String id;

            JSONObject recipe = recipesArray.getJSONObject(i);
            publisher = recipe.getString(PUBLISHER);
            title = recipe.getString(TITLE);
            source_url = recipe.getString(SOURCE_URL);
            image_url = recipe.getString(IMAGE_URL);
            id = recipe.getString(ID);

            Recipe newRecipe = new Recipe(publisher,title,source_url,image_url,id);
            results.add(newRecipe);
        }

        for(Recipe m:results)
            Log.v("DATA",m.toString());
        return results;
    }
}
