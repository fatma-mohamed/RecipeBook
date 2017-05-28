package com.example.android.recipebook.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
        db = new DatabaseHelper(getContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_recipe, container, false);
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
                Recipe recipe = new Recipe(
                        null,
                        recipeName.getText().toString(),
                        preparationTime.getText().toString(),
                        Integer.parseInt(numberOfServings.getText().toString()),
                        ingredients.getText().toString(),
                        directions.getText().toString()
                );
                if(db.addOwnRecipe(recipe)){
                    Toast.makeText(getContext(),"Recipe saved successfully!",Toast.LENGTH_LONG).show();
                    NewRecipeDetailsFragment f = new NewRecipeDetailsFragment();
                    Bundle args = new Bundle();
                    args.putString("id",recipe.get_ID());
                    args.putString("name",recipe.getName());
                    args.putString("time",recipe.getTime());
                    args.putInt("numServings",recipe.getNumServings());
                    args.putString("ingredients",recipe.getIngredients());
                    args.putString("directions",recipe.getDirections());
                    f.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,f).commit();
                }
                else
                    Toast.makeText(getContext(),"Something went wrong! A recipe with the same name may already exist.",Toast.LENGTH_LONG).show();

                return false;
            }
        });
    }


}
