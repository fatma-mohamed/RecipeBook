package com.example.android.recipebook.app;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

import static android.R.attr.fragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private InternetConnectionListener con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = new InternetConnectionListener();
        con.onReceive(this.getApplicationContext(),this.getIntent());

        if(!con.isOnline(this))
        {
            //Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_no_internet);
            Button try_again_btn = (Button)findViewById(R.id.try_again_btn);
            try_again_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(MainActivity.this,MainActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
        else {
            setContentView(R.layout.activity_main);
            //Instabug feedback
            new Instabug.Builder(this.getApplication(), BuildConfig.INSTABUG_API_KEY)
                    .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                    .build();
            this.getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Boolean isOwn = getIntent().getBooleanExtra("isOwn", false);
            if(isOwn) {
                getIntent().putExtra("isOwn", false);
                Bundle args = new Bundle();
                args.putBoolean("isOwn", true);
                RecipesFragment recipesFragment = new RecipesFragment();
                recipesFragment.setArguments(args);
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_main, recipesFragment, "RecipesFragment")
                            .commitNow();
                }
                RecipesFragment fragment = (RecipesFragment)getSupportFragmentManager().findFragmentByTag("RecipesFragment");
                fragment.getActivity().setTitle("My recipes");
            }
            else{
                Bundle args = new Bundle();
                args.putBoolean("isOwn", false);
                RecipesFragment recipesFragment = new RecipesFragment();
                recipesFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, recipesFragment, "RecipesFragment")
                        .commit();
            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addRecipe);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewRecipeFragment f = new NewRecipeFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main,f,"NewRecipeFragment")
                            .addToBackStack("MainActivity")
                            .commit();

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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                this.finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bk)
        {
            RecipesFragment fragment = (RecipesFragment)getSupportFragmentManager().findFragmentByTag("RecipesFragment");
            if(fragment!=null && fragment.isVisible()){
                Boolean s = fragment.getBookmarked();
                if(!s)
                    Toast.makeText(fragment.getContext(),"You have no bookmarks!", Toast.LENGTH_SHORT).show();
                fragment.getActivity().setTitle("Bookmarked");
                Bundle args = fragment.getArguments();
                Boolean isOwn = args.getBoolean("isOwn");
                //If coming from own, search item is already invisible
                if(!isOwn)
                    findViewById(R.id.action_search).setVisibility(View.INVISIBLE);
            }
        } else if (id == R.id.nav_my_own) {
            RecipesFragment fragment = (RecipesFragment)getSupportFragmentManager().findFragmentByTag("RecipesFragment");
            if(fragment!=null && fragment.isVisible()){
                Boolean s = fragment.getOwn();
                if(!s)
                    Toast.makeText(fragment.getContext(),"You have not added any recipes of your own!", Toast.LENGTH_SHORT).show();
                fragment.getActivity().setTitle("My recipes");
                try {
                    findViewById(R.id.action_search).setVisibility(View.INVISIBLE);
                }catch (Exception e){
                    System.out.print("Coming from own, search item already invisible, " + e.getMessage().toString());
                }
            }
        } else if (id == R.id.nav_all){
            this.recreate();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
