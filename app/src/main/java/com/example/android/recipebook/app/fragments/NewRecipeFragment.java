package com.example.android.recipebook.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.recipebook.app.R;
import com.example.android.recipebook.app.activities.OwnRecipeDetailsActivity;
import com.example.android.recipebook.app.data.DatabaseHelper;
import com.example.android.recipebook.app.data.Recipe;

/**
 * Created by Fatma on 08-Jan-17.
 */

public class NewRecipeFragment extends Fragment {
    private static final String LOG_TAG = NewRecipeFragment.class.getSimpleName();
    private View rootView;
    private DatabaseHelper db;


    public NewRecipeFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = RecipesFragment.db;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_recipe, container, false);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.addRecipe);
        fab.setVisibility(View.INVISIBLE);
        getActivity().setTitle("New recipe");
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_newrecipe, menu);
        MenuItem menuItem = menu.findItem(R.id.action_done);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                EditText recipeName = (EditText)rootView.findViewById(R.id.recipe_name),
                        preparationTime = (EditText)rootView.findViewById(R.id.time),
                        numberOfServings = (EditText)rootView.findViewById(R.id.numServings),
                        ingredients = (EditText)rootView.findViewById(R.id.ingredients),
                        directions = (EditText)rootView.findViewById(R.id.directions);
                String recipe_name = "", preparation_time = "",
                ing = "", directn = "";
                Integer number_of_servings;
                if(recipeName.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "A recipe must have a name.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else
                    recipe_name = recipeName.getText().toString();
                if(ingredients.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "It can't be a recipe without ingredients.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else
                    ing = ingredients.getText().toString();
                if(directions.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Directions are important!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else
                    directn = directions.getText().toString();
                if(preparationTime.getText().toString().equals(""))
                  preparation_time = null;
                else
                    preparation_time = preparationTime.getText().toString();
                if(numberOfServings.getText().toString().equals(""))
                    number_of_servings = 0;
                else
                    number_of_servings = Integer.parseInt(numberOfServings.getText().toString());

                Recipe recipe = new Recipe(
                        null,
                        recipe_name,
                        preparation_time,
                        number_of_servings,
                        ing,
                        directn
                );
                if(db.addOwnRecipe(recipe)){
                    Toast.makeText(getContext(),"Recipe saved successfully!",Toast.LENGTH_SHORT).show();
                    //Will not add NewRecipeFragment to the back_stack so when back is pressed, don't return to it
                    Intent intent = new Intent(getActivity(), OwnRecipeDetailsActivity.class);
                    intent.putExtra("id",recipe.get_ID());
                    intent.putExtra("name",recipe.getName());
                    intent.putExtra("time",recipe.getTime());
                    intent.putExtra("numServings",recipe.getNumServings());
                    intent.putExtra("ingredients",recipe.getIngredients());
                    intent.putExtra("directions",recipe.getDirections());
                    startActivity(intent);
                }
                else
                    Toast.makeText(getContext(),"Something went wrong! A recipe with the same name may already exist.",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


}
