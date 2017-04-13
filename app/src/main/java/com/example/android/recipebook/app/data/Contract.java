package com.example.android.recipebook.app.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import com.example.android.recipebook.app.BuildConfig;


/**
 * Created by Fatma on 21-Oct-16.
 */

public class Contract {
    public static final String URL_SEARCH = "http://food2fork.com/api/search?key=" + BuildConfig.RECIPE_API_KEY + "&q=";
    public static final String URL_GET = "http://food2fork.com/api/get?key="  + BuildConfig.RECIPE_API_KEY + "&rId=";
    public static final String URL_SORT = "http://food2fork.com/api/search?key=" + BuildConfig.RECIPE_API_KEY + "&sort=";

    /**************************************/

    public static final String CONTENT_AUTHORITY = "com.example.android.recipebook.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public  static final String PATH_FAV = "favourites";
    public  static final String PATH_REC = "recipes";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }


    public static final class FavouriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_FAV).build();

        public static final String TABLE_NAME = "favourites";
        public static final String _ID = "fav_recipe_id";
        public static final String NAME = "fav_recipe_name";
        public static final String PUBLISHER = "publisher";
        public static final String SOURCE_URL = "source_url";
        public static final String IMAGE_URL = "image_url";


        public static Uri buildFavouriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class RecipeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_REC).build();

        public static final String TABLE_NAME = "recipes";
        public static final String _ID = "recipe_id";
        public static final String NAME = "recipe_name";

        public static Uri buildRecipeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
