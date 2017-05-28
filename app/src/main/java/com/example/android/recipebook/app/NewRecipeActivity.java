package com.example.android.recipebook.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NewRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        NewRecipeFragment recipeFragment = new NewRecipeFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_new_recipe, recipeFragment)
                    .commit();
        }
    }
}
