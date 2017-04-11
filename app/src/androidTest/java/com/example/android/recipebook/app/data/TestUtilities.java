package com.example.android.recipebook.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by fatma on 12/4/2016.
 */

public class TestUtilities extends AndroidTestCase {
    static final String TEST_RECIPE_ID = "35382";
    static final String TEST_RECIPE_NAME = "Jalapeno Popper Grilled Cheese Sandwich";

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createFavouritesValues()
    {
        ContentValues values = new ContentValues();
        values.put(Contract.FavouriteEntry._ID, TEST_RECIPE_ID);
        values.put(Contract.FavouriteEntry.NAME,TEST_RECIPE_NAME);
        return values;
    }
}
