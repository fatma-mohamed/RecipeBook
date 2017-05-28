package com.example.android.recipebook.app;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecipesFragment recipesFragment = new RecipesFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_main, recipesFragment, "recipesFragment")
                    .commit();}
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addRecipe);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, NewRecipeActivity.class);
//                startActivity(intent);
                NewRecipeFragment f = new NewRecipeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,f).commit();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       // SyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bk)
        {
            RecipesFragment fragment = (RecipesFragment)getSupportFragmentManager().findFragmentByTag("recipesFragment");
            if(fragment!=null && fragment.isVisible()){
                Boolean s = fragment.getBookmarked();
                if(!s)
                    Toast.makeText(fragment.getContext(),"You have no bookmarks!", Toast.LENGTH_LONG).show();
                fragment.getActivity().setTitle("Bookmarked");
            }
        } else if (id == R.id.nav_my_own) {
            RecipesFragment fragment = (RecipesFragment)getSupportFragmentManager().findFragmentByTag("recipesFragment");
            if(fragment!=null && fragment.isVisible()){
                Boolean s = fragment.getOwn();
                if(!s)
                    Toast.makeText(fragment.getContext(),"You have not added any recipes of your own!", Toast.LENGTH_LONG).show();
                fragment.getActivity().setTitle("My recipes");
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
