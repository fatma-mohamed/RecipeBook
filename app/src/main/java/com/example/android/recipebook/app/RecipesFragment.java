package com.example.android.recipebook.app;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.recipebook.app.data.Contract;
import com.example.android.recipebook.app.data.DatabaseHelper;
import com.example.android.recipebook.app.data.Recipe;
import com.example.android.recipebook.app.data.RecipesParser;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import static android.content.ContentValues.TAG;


public class RecipesFragment extends Fragment implements SearchView.OnQueryTextListener{

    public static final String LOG_TAG = RecipesFragment.class.getSimpleName();

    private MyRecipesAdapter mAdapter;
    private ListView list;
    private MenuItem searchMenuItem;
    private SearchManager searchManager;
    private ProgressDialog progressDialog;
    public static DatabaseHelper db;



    public RecipesFragment() {

    }

    public MyRecipesAdapter getAdapter(){
        return mAdapter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipes, menu);
        searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = new DatabaseHelper(getContext());
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ArrayList<Recipe> temp = new ArrayList<>();
        final View view = inflater.inflate(R.layout.fragment_recipes_list, container, false);
        list = (ListView)view.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            if(mAdapter.own){
                                                NewRecipeDetailsFragment f = new NewRecipeDetailsFragment();
                                                Bundle args = new Bundle();
                                                Recipe recipe = ((Recipe) mAdapter.getItem(i));
                                                args.putString("id",recipe.get_ID());
                                                args.putString("name",recipe.getName());
                                                args.putString("time",recipe.getTime());
                                                args.putInt("numServings",recipe.getNumServings());
                                                args.putString("ingredients",recipe.getIngredients());
                                                args.putString("directions",recipe.getDirections());
                                                f.setArguments(args);
                                                mAdapter.own = false;
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,f).commit();
                                            }
                                            else{
                                                String path = ((Recipe) mAdapter.getItem(i)).getSourceURL();
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                                                startActivity(Intent.createChooser(browserIntent, null));
                                            }
                                        }
                                    });
        mAdapter = new MyRecipesAdapter(getActivity(),temp);
        list.setAdapter(mAdapter);
        getRecipesRequest(Contract.URL_SORT + "t");
        return view;
    }

    private void getRecipesRequest(String url) {
        showProgressDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            RecipesParser parser = new RecipesParser();
                            ArrayList<Recipe> recipes = parser.parse(response);
                            mAdapter.add(recipes);
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(LOG_TAG,e.getMessage());
                        }

                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG,error.getMessage());
                hideProgressDialog();
            }
        });

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(req);

    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public Boolean getBookmarked(){
        Boolean status = mAdapter.showBookmarked();
        return status;
    }

    public Boolean getOwn(){
        Boolean status = mAdapter.showOwn();
        return status;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchRecipe(Contract.URL_SEARCH+newText);
        mAdapter.getFilter().filter(newText);
        return true;
    }

    public void searchRecipe(String url) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            RecipesParser parser = new RecipesParser();
                            mAdapter.add(parser.parse(response));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(LOG_TAG,e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG,error.getMessage());
            }
        });
        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(req);
    }


}
