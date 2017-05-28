package com.example.android.recipebook.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by fatma on 12/2/2016.
 */

public class Provider extends ContentProvider {
    private DatabaseHelper dbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int FAV = 101;
    private static final int REC = 103;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case FAV:{
                retCursor= dbHelper.getReadableDatabase().query(
                        Contract.BookmarkedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REC:{
                retCursor= dbHelper.getReadableDatabase().query(
                        Contract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch(sUriMatcher.match(uri))
        {
            case FAV:
            {
                long _id = db.insert(Contract.BookmarkedEntry.TABLE_NAME,null,values);
                if(_id >0)
                    returnUri = Contract.BookmarkedEntry.buildFavouriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REC:
            {
                long _id = db.insert(Contract.RecipeEntry.TABLE_NAME,null,values);
                if(_id >0)
                    returnUri = Contract.RecipeEntry.buildRecipeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch(sUriMatcher.match(uri)){
            case FAV:{
                rowsDeleted = db.delete(
                        Contract.BookmarkedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REC:{
                rowsDeleted = db.delete(
                        Contract.RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FAV: {
                rowsUpdated = db.update(Contract.BookmarkedEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case REC: {
                rowsUpdated = db.update(Contract.RecipeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Contract.PATH_BK, FAV);
        matcher.addURI(authority, Contract.PATH_REC, REC);
        return matcher;
    }
}
