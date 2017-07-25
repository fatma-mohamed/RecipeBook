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

import static com.example.android.recipebook.app.data.Contract.BookmarkedEntry.SOURCE_URL;

/**
 * Created by Fatma on 25-Nov-16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context mContext;
    private static final int DATABASE_VERSION = 11;
    private static String LOG_TAG = DatabaseHelper.class.getSimpleName();

    static final String DATABASE_NAME = "recipebook.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_BOOKMARKED_TABLE = "CREATE TABLE " + Contract.BookmarkedEntry.TABLE_NAME + " (" +
                Contract.BookmarkedEntry._ID + " STRING PRIMARY KEY, " +
                Contract.BookmarkedEntry.NAME + " STRING NOT NULL, "+
                Contract.BookmarkedEntry.PUBLISHER + " STRING NOT NULL,"+
                Contract.BookmarkedEntry.SOURCE_URL + " STRING NOT NULL,"+
                Contract.BookmarkedEntry.IMAGE_URL + " STRING NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_BOOKMARKED_TABLE);

        final String SQL_CREATE_RECIPES_TABLE = "CREATE TABLE " + Contract.RecipeEntry.TABLE_NAME + " (" +
                Contract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.RecipeEntry.NAME + " STRING NOT NULL, "+
                Contract.RecipeEntry.PREPARE_TIME + " STRING, "+
                Contract.RecipeEntry.NUMBER_OF_SERVINGS + " INTEGER, "+
                Contract.RecipeEntry.INGREDIENTS + " STRING  NOT NULL, "+
                Contract.RecipeEntry.DIRECTIONS + " STRING  NOT NULL);";

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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.BookmarkedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.RecipeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<Recipe> getBookmarkedRecipes()
    {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.BookmarkedEntry.CONTENT_URI,
                new String[]{Contract.BookmarkedEntry._ID,
                        Contract.BookmarkedEntry.NAME,
                Contract.BookmarkedEntry.PUBLISHER,
                SOURCE_URL,
                Contract.BookmarkedEntry.IMAGE_URL},
                null,
                null,
                null);
        ArrayList<Recipe> arr = new ArrayList<>();
        Recipe recipe;
        if(null == cursor | cursor.getCount()==0)
            return arr;
        while (cursor.moveToNext())
        {
            String _ID = cursor.getString(cursor.getColumnIndex(Contract.BookmarkedEntry._ID)),
              NAME = cursor.getString(cursor.getColumnIndex(Contract.BookmarkedEntry.NAME)),
                    PUBLISHER = cursor.getString(cursor.getColumnIndex(Contract.BookmarkedEntry.PUBLISHER)),
                    SOURCE_URL = cursor.getString(cursor.getColumnIndex(Contract.BookmarkedEntry.SOURCE_URL)),
                    IMAGE_URL = cursor.getString(cursor.getColumnIndex(Contract.BookmarkedEntry.IMAGE_URL));
            recipe = new Recipe(PUBLISHER, NAME, SOURCE_URL, IMAGE_URL, _ID);
            arr.add(recipe);
        }
        return arr;
    }

    public boolean bookmarkedExists(String recipe_name)
    {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.BookmarkedEntry.CONTENT_URI,
                new String[]{Contract.BookmarkedEntry._ID},
                Contract.BookmarkedEntry.NAME + " = ?",
                new String[]{recipe_name},
                null);
        if(null == cursor | cursor.getCount()==0)
            return false;
        else
            return true;
    }

    public long addBookmarkedRecipe(Recipe recipe)
    {
        long id = -1;
        // First, check if recipe is already in fav.
        Cursor cursor = mContext.getContentResolver().query(
                Contract.BookmarkedEntry.CONTENT_URI,
                new String[]{Contract.BookmarkedEntry._ID},
                Contract.BookmarkedEntry.NAME + " = ?",
                new String[]{recipe.getName()},
                null);

        if(null == cursor | cursor.getCount()==0)
        {
            Log.d(LOG_TAG,"Recipe"+ recipe.getName() +" not in bookmarked");
            ContentValues values = new ContentValues();
            values.put(Contract.BookmarkedEntry._ID,recipe.get_ID());
            values.put(Contract.BookmarkedEntry.NAME,recipe.getName());
            values.put(Contract.BookmarkedEntry.PUBLISHER,recipe.getPublisher());
            values.put(Contract.BookmarkedEntry.SOURCE_URL,recipe.getSourceURL());
            values.put(Contract.BookmarkedEntry.IMAGE_URL,recipe.getImageURL());

            Uri insertedUri = mContext.getContentResolver().insert(
                    Contract.BookmarkedEntry.CONTENT_URI,
                    values
            );
            id = ContentUris.parseId(insertedUri);
            Log.d(LOG_TAG,"Recipe inserted" + id);
        }

        cursor.close();
        return id;
    }

    public boolean removeBookmarkedRecipe(String recipename)
    {
        int deletedRows = 0;
        // First, check if recipe is already in fav.
        Cursor cursor = mContext.getContentResolver().query(
                Contract.BookmarkedEntry.CONTENT_URI,
                new String[]{Contract.BookmarkedEntry._ID},
                Contract.BookmarkedEntry.NAME + " = ?",
                new String[]{recipename},
                null);

        if(null == cursor | cursor.getCount()==0)
            cursor.close();
        else
        {
            Log.d(LOG_TAG,"Recipe in Bookamrked");

            deletedRows = mContext.getContentResolver().delete(
                    Contract.BookmarkedEntry.CONTENT_URI,
                    Contract.BookmarkedEntry.NAME + " = ?",
                    new String[]{recipename}
            );
            Log.d(LOG_TAG,"Recipe deleted" + deletedRows);
        }

        if(deletedRows>0)
            return true;
        else
            return false;
    }

    public Boolean addOwnRecipe(Recipe recipe){
        String new_recipe_name = recipe.getName();
        Boolean exists = recipeExists(new_recipe_name);
        if(exists)
            return false;
        ContentValues values = new ContentValues();
        values.put(Contract.RecipeEntry.NAME,recipe.getName());
        values.put(Contract.RecipeEntry.PREPARE_TIME,recipe.getTime());
        values.put(Contract.RecipeEntry.NUMBER_OF_SERVINGS,recipe.getNumServings());
        values.put(Contract.RecipeEntry.INGREDIENTS,recipe.getIngredients());
        values.put(Contract.RecipeEntry.DIRECTIONS,recipe.getDirections());

        Uri insertedUri = mContext.getContentResolver().insert(
                Contract.RecipeEntry.CONTENT_URI,
                values
        );
        Long id = ContentUris.parseId(insertedUri);
        if(id>0)
            return true;
        else
            return false;
    }

    public Boolean removeOwnRecipe(Recipe recipe){
        int deletedRows = mContext.getContentResolver().delete(
                Contract.RecipeEntry.CONTENT_URI,
                Contract.RecipeEntry.NAME + " = ?",
                new String[]{recipe.getName()}
        );
        if(deletedRows>0)
            return true;
        else
            return false;
    }

    public ArrayList<Recipe> getOwnRecipes()
    {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.RecipeEntry.CONTENT_URI,
                new String[]{Contract.RecipeEntry._ID,
                        Contract.RecipeEntry.NAME,
                        Contract.RecipeEntry.PREPARE_TIME,
                        Contract.RecipeEntry.NUMBER_OF_SERVINGS,
                        Contract.RecipeEntry.INGREDIENTS,
                        Contract.RecipeEntry.DIRECTIONS},
                null,
                null,
                null);
        ArrayList<Recipe> arr = new ArrayList<>();
        Recipe recipe;
        if(null == cursor)
            return arr;
        else if(cursor.getCount()==0)
            return arr;
        while (cursor.moveToNext())
        {
            String _ID = cursor.getString(cursor.getColumnIndex(Contract.RecipeEntry._ID)),
                    NAME = cursor.getString(cursor.getColumnIndex(Contract.RecipeEntry.NAME)),
                    INGREDIENTS = cursor.getString(cursor.getColumnIndex(Contract.RecipeEntry.INGREDIENTS)),
                    DIRECTIONS = cursor.getString(cursor.getColumnIndex(Contract.RecipeEntry.DIRECTIONS)),
                    PREPARE_TIME = cursor.getString(cursor.getColumnIndex(Contract.RecipeEntry.PREPARE_TIME));
            Integer NUMBER_OF_SERVINGS = cursor.getInt(cursor.getColumnIndex(Contract.RecipeEntry.NUMBER_OF_SERVINGS));
            recipe = new Recipe(_ID, NAME, PREPARE_TIME, NUMBER_OF_SERVINGS, INGREDIENTS,DIRECTIONS);
            arr.add(recipe);
        }
        return arr;
    }

    public boolean recipeExists(String recipename)
    {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.RecipeEntry.CONTENT_URI,
                new String[]{Contract.RecipeEntry._ID},
                Contract.RecipeEntry.NAME + " = ?",
                new String[]{recipename},
                null);
        if(null == cursor)
            return false;
        else if(cursor.getCount()==0)
            return false;
        else
            return true;
    }


}
