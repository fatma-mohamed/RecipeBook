package com.example.android.recipebook.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by Fatma on 28-Nov-16.
 */

public class TestDB extends AndroidTestCase {
    public static final String LOG_TAG = TestDB.class.getSimpleName();

    public void deleteDB()
    {
        mContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    /*Called before each TC to make sure we always have a clean test*/
    public void setUp()
    {
        deleteDB();
    }

    public void testCreateDB()
    {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(Contract.FavouriteEntry.TABLE_NAME);

        setUp();
        SQLiteDatabase db = new DatabaseHelper(this.mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());

        //select table names from db
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst()); //moveFirst will return False if cursor is empty which means db has not been created correctly

        //verify favourites table was created
        do {
            tableNameHashSet.remove(c.getString(0));
            Log.e(LOG_TAG,c.getString(0));
        } while( c.moveToNext() );

        //if set is empty then table has ben created
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, does my table contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + Contract.FavouriteEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information(Columns).",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> favouritesColumnHashSet = new HashSet<String>();
        favouritesColumnHashSet.add(Contract.FavouriteEntry._ID);
        favouritesColumnHashSet.add(Contract.FavouriteEntry.NAME);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favouritesColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that the database doesn't contain all of the required fav. columns
        assertTrue("Error: The database doesn't contain all of the required fav. columns",
                favouritesColumnHashSet.isEmpty());
        db.close();
    }

    public void testFavouritesTable()
    {
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = TestUtilities.createFavouritesValues();

        long dbRowID = db.insert(Contract.FavouriteEntry.TABLE_NAME,null,values);
        assertTrue(dbRowID!=-1);

        Cursor c = db.query(
                Contract.FavouriteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue( "Error: No Records returned from favourites query", c.moveToFirst() );
        TestUtilities.validateCurrentRecord("testInsertReadDb favouritesEntry failed to validate",
                c, values);
        assertFalse( "Error: More than one record returned from favourites query",
                c.moveToNext() );
        c.close();
        db.close();
    }
}
