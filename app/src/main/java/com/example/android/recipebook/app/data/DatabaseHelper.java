package com.example.android.recipebook.app.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import static com.example.android.recipebook.app.data.Contract.FavouriteEntry.IMAGE_URL;
import static com.example.android.recipebook.app.data.Contract.FavouriteEntry.PUBLISHER;

/**
 * Created by Fatma on 25-Nov-16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context mContext;
    private static final int DATABASE_VERSION = 5;
    private static String LOG_TAG = DatabaseHelper.class.getSimpleName();

    static final String DATABASE_NAME = "recipebook.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + Contract.FavouriteEntry.TABLE_NAME + " (" +
                Contract.FavouriteEntry._ID + " STRING PRIMARY KEY ," +
                Contract.FavouriteEntry.NAME + " STRING NOT NULL,"+
                Contract.FavouriteEntry.PUBLISHER + " STRING NOT NULL,"+
                Contract.FavouriteEntry.SOURCE_URL + " STRING NOT NULL,"+
                Contract.FavouriteEntry.IMAGE_URL + " STRING NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITES_TABLE);

        final String SQL_CREATE_RECIPES_TABLE = "CREATE TABLE " + Contract.RecipeEntry.TABLE_NAME + " (" +
                Contract.RecipeEntry._ID + " STRING PRIMARY KEY," +
                Contract.RecipeEntry.NAME + " STRING NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.FavouriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.RecipeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<Recipe> getFavourites()
    {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.FavouriteEntry.CONTENT_URI,
                new String[]{Contract.FavouriteEntry._ID,
                        Contract.FavouriteEntry.NAME,
                Contract.FavouriteEntry.PUBLISHER,
                Contract.FavouriteEntry.SOURCE_URL,
                Contract.FavouriteEntry.IMAGE_URL},
                null,
                null,
                null);
        ArrayList<Recipe> arr = new ArrayList<>();
        Recipe recipe;
        if(null == cursor | cursor.getCount()==0)
            return arr;
        while (cursor.moveToNext())
        {
            String _ID = cursor.getString(cursor.getColumnIndex(Contract.FavouriteEntry._ID)),
              NAME = cursor.getString(cursor.getColumnIndex(Contract.FavouriteEntry.NAME)),
                    PUBLISHER = cursor.getString(cursor.getColumnIndex(Contract.FavouriteEntry.PUBLISHER)),
                    SOURCE_URL = cursor.getString(cursor.getColumnIndex(Contract.FavouriteEntry.SOURCE_URL)),
                    IMAGE_URL = cursor.getString(cursor.getColumnIndex(Contract.FavouriteEntry.IMAGE_URL));
            recipe = new Recipe(PUBLISHER, NAME, SOURCE_URL, IMAGE_URL, _ID);
            arr.add(recipe);
        }
        return arr;
    }

    public boolean exists(String recipename)
    {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.FavouriteEntry.CONTENT_URI,
                new String[]{Contract.FavouriteEntry._ID},
                Contract.FavouriteEntry.NAME + " = ?",
                new String[]{recipename},
                null);
        if(null == cursor | cursor.getCount()==0)
            return false;
        else
            return true;
    }

    public long addFavourite(Recipe recipe)
    {
        long favId = -1;
        // First, check if recipe is already in fav.
        Cursor cursor = mContext.getContentResolver().query(
                Contract.FavouriteEntry.CONTENT_URI,
                new String[]{Contract.FavouriteEntry._ID},
                Contract.FavouriteEntry.NAME + " = ?",
                new String[]{recipe.getTitle()},
                null);

        if(null == cursor | cursor.getCount()==0)
        {
            Log.d(LOG_TAG,"Recipe"+ recipe.getTitle() +" not in fav");
            ContentValues values = new ContentValues();
            values.put(Contract.FavouriteEntry._ID,recipe.get_ID());
            values.put(Contract.FavouriteEntry.NAME,recipe.getTitle());
            values.put(Contract.FavouriteEntry.PUBLISHER,recipe.getPublisher());
            values.put(Contract.FavouriteEntry.SOURCE_URL,recipe.getSourceURL());
            values.put(Contract.FavouriteEntry.IMAGE_URL,recipe.getImageURL());

            Uri insertedUri = mContext.getContentResolver().insert(
                    Contract.FavouriteEntry.CONTENT_URI,
                    values
            );
            favId = ContentUris.parseId(insertedUri);
            Log.d(LOG_TAG,"Recipe inserted" + favId);
        }

        cursor.close();
        return favId;
    }

    public boolean deleteFavourite(String recipename)
    {
        int deletedRows = 0;
        // First, check if recipe is already in fav.
        Cursor cursor = mContext.getContentResolver().query(
                Contract.FavouriteEntry.CONTENT_URI,
                new String[]{Contract.FavouriteEntry._ID},
                Contract.FavouriteEntry.NAME + " = ?",
                new String[]{recipename},
                null);

        if(null == cursor | cursor.getCount()==0)
            cursor.close();
        else
        {
            Log.d(LOG_TAG,"Recipe in fav");

            deletedRows = mContext.getContentResolver().delete(
                    Contract.FavouriteEntry.CONTENT_URI,
                    Contract.FavouriteEntry.NAME + " = ?",
                    new String[]{recipename}
            );
            Log.d(LOG_TAG,"Movie deleted" + deletedRows);
        }

        if(deletedRows>0)
            return true;
        else
            return false;
    }


}
