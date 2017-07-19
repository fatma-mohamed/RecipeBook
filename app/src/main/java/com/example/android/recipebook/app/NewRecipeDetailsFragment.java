package com.example.android.recipebook.app;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.recipebook.app.data.DatabaseHelper;
import com.example.android.recipebook.app.data.Recipe;

import static com.example.android.recipebook.app.R.id.ingredients;

/**
 * Created by Fatma on 08-Jan-17.
 */

public class NewRecipeDetailsFragment extends Fragment {
    private static final String LOG_TAG = NewRecipeDetailsFragment.class.getSimpleName();
    private View rootView;
    private DatabaseHelper db;
    private Bundle recipe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipe = getArguments();
        setHasOptionsMenu(true);
        db = RecipesFragment.db;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_recipe_details, container, false);
        getActivity().setTitle(recipe.getString("name"));
        TextView preparation_time = (TextView)rootView.findViewById(R.id.time);
        preparation_time.setText(recipe.getString("time"));
        TextView num_servings = (TextView)rootView.findViewById(R.id.numServings);
        String num_serv = String.valueOf(recipe.getInt("numServings"));
        if (num_serv.equals("0"))
            num_servings.setText("");
        else
            num_servings.setText(num_serv);
        TextView ingredients = (TextView)rootView.findViewById(R.id.ingredients);
        ingredients.setText(recipe.getString("ingredients"));
        TextView directions = (TextView)rootView.findViewById(R.id.directions);
        directions.setText(recipe.getString("directions"));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_delete);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Recipe new_recipe = new Recipe(recipe.getString("id"),
                        recipe.getString("name"),
                        recipe.getString("time"),
                        recipe.getInt("numServings"),
                        recipe.getString("ingredients"),
                        recipe.getString("directions"));
                if(db.removeOwnRecipe(new_recipe)){
                    Toast.makeText(getContext(),"Recipe deleted!",Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                else
                    Toast.makeText(getContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }


}
