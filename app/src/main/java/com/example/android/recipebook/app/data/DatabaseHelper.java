package com.example.android.recipebook.app.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Fatma on 25-Nov-16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context mContext;
    private static final int DATABASE_VERSION = 1;
    private static String LOG_TAG = DatabaseHelper.class.getSimpleName();

    static final String DATABASE_NAME = "recipebook.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + Contract.FavouriteEntry.TABLE_NAME + " (" +
                Contract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.FavouriteEntry.NAME + " STRING NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITES_TABLE);

        final String SQL_CREATE_RECIPES_TABLE = "CREATE TABLE " + Contract.RecipeEntry.TABLE_NAME + " (" +
                Contract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
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

    public ArrayList<String> getFavourites()
    {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.FavouriteEntry.CONTENT_URI,
                new String[]{Contract.FavouriteEntry.NAME},
                null,
                null,
                null);
        ArrayList<String> arr = new ArrayList<>();
        if(!cursor.moveToFirst())
            return arr;
        do
        {
            arr.add(cursor.getString(cursor.getColumnIndex(Contract.FavouriteEntry.NAME)));
        }while (cursor.moveToNext());
        return arr;
    }

    public boolean exists(String movieName)
    {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.FavouriteEntry.CONTENT_URI,
                new String[]{Contract.FavouriteEntry._ID},
                Contract.FavouriteEntry.NAME + " = ?",
                new String[]{movieName},
                null);
        if(cursor.moveToFirst())
            return true;
        else
            return false;
    }

    public long addFavourite(String movieName)
    {
        long favId = -1;
        // First, check if movie is already in fav.
        Cursor cursor = mContext.getContentResolver().query(
                Contract.FavouriteEntry.CONTENT_URI,
                new String[]{Contract.FavouriteEntry._ID},
                Contract.FavouriteEntry.NAME + " = ?",
                new String[]{movieName},
                null);

        Log.d(LOG_TAG,"Cursor = " + cursor.moveToFirst());
        if(!cursor.moveToFirst())
        {
            Log.d(LOG_TAG,"Movie"+ movieName +" not in fav");
            ContentValues values = new ContentValues();
            values.put(Contract.FavouriteEntry.NAME,movieName);

            Uri insertedUri = mContext.getContentResolver().insert(
                    Contract.FavouriteEntry.CONTENT_URI,
                    values
            );
            favId = ContentUris.parseId(insertedUri);
            Log.d(LOG_TAG,"Movie inserted" + favId);
        }

        cursor.close();
        return favId;
    }

    public boolean deleteFavourite(String movieName)
    {
        int deletedRows = 0;
        // First, check if movie is already in fav.
        Cursor cursor = mContext.getContentResolver().query(
                Contract.FavouriteEntry.CONTENT_URI,
                new String[]{Contract.FavouriteEntry._ID},
                Contract.FavouriteEntry.NAME + " = ?",
                new String[]{movieName},
                null);

        Log.d(LOG_TAG,"Cursor = " + cursor.moveToFirst());
        if(cursor.moveToFirst())
        {
            Log.d(LOG_TAG,"Movie in fav");
            ContentValues values = new ContentValues();
            values.put(Contract.FavouriteEntry.NAME,movieName);

            deletedRows = mContext.getContentResolver().delete(
                    Contract.FavouriteEntry.CONTENT_URI,
                    Contract.FavouriteEntry.NAME + " = ?",
                    new String[]{movieName}
            );
            Log.d(LOG_TAG,"Movie deleted" + deletedRows);
        }

        cursor.close();
        if(deletedRows>0)return true;
        else
            return false;
    }


}
