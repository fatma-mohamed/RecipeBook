package com.example.android.recipebook.app.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fatma on 19-Nov-16.
 */

public interface DataParser {
     <T> T parse(JSONObject recipesJson) throws JSONException;
}
