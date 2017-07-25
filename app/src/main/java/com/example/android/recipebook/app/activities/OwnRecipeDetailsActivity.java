package com.example.android.recipebook.app.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.recipebook.app.fragments.NewRecipeDetailsFragment;
import com.example.android.recipebook.app.R;


public class OwnRecipeDetailsActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_own_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_own_recipe);
        setSupportActionBar(toolbar);

        NewRecipeDetailsFragment fragment = new NewRecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putString("id",getIntent().getStringExtra("id"));
        args.putString("name",getIntent().getStringExtra("name"));
        args.putString("time",getIntent().getStringExtra("time"));
        args.putInt("numServings",getIntent().getIntExtra("numServings",0));
        args.putString("ingredients",getIntent().getStringExtra("ingredients"));
        args.putString("directions",getIntent().getStringExtra("directions"));
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_own_recipe, fragment)
                .addToBackStack("OwnRecipeDetailsActivity")
                .commit();


    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            this.finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("isOwn",true).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }
}
