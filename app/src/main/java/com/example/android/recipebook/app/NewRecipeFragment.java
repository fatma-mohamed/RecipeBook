package com.example.android.recipebook.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Fatma on 08-Jan-17.
 */

public class NewRecipeFragment extends Fragment {
    private static final String LOG_TAG = NewRecipeFragment.class.getSimpleName();

    public NewRecipeFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_recipe, container, false);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_newrecipe, menu);
        MenuItem menuItem = menu.findItem(R.id.action_done);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO action Done ..add new recipe
                return false;
            }
        });
    }


}
