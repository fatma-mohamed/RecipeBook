package com.example.android.recipebook.app.fragments;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.recipebook.app.adapters.MyRecipesAdapter;
import com.example.android.recipebook.app.R;
import com.example.android.recipebook.app.helpers.VolleyController;
import com.example.android.recipebook.app.activities.OwnRecipeDetailsActivity;
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

    public VolleyController volleyController = VolleyController.getInstance();
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
        Bundle args = RecipesFragment.this.getArguments();
        Boolean isOwn = args.getBoolean("isOwn");
        if(isOwn)
            searchMenuItem.setVisible(false);
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
        getActivity().setTitle(R.string.app_name); //when returning from bookmarked/own, change main_activity's title
        db = new DatabaseHelper(getContext());
        progressDialog = new ProgressDialog(this.getActivity());
        final ArrayList<Recipe> temp = new ArrayList<>();
        mAdapter = new MyRecipesAdapter(getActivity(),temp);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().findViewById(R.id.addRecipe).setVisibility(View.VISIBLE);
        final View view = inflater.inflate(R.layout.fragment_recipes_list, container, false);
        list = (ListView)view.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            if(mAdapter.own){
                                                Recipe recipe = ((Recipe) mAdapter.getItem(i));
                                                OwnRecipeDetailsActivity ownRecipeDetailsActivity = new OwnRecipeDetailsActivity();
                                                Intent intent = new Intent(getActivity(), ownRecipeDetailsActivity.getClass());
                                                intent.putExtra("id",recipe.get_ID());
                                                intent.putExtra("name",recipe.getName());
                                                intent.putExtra("time",recipe.getTime());
                                                intent.putExtra("numServings",recipe.getNumServings());
                                                intent.putExtra("ingredients",recipe.getIngredients());
                                                intent.putExtra("directions",recipe.getDirections());
                                                mAdapter.own = false;
                                                startActivity(intent);
                                            }
                                            else{
                                                String path = ((Recipe) mAdapter.getItem(i)).getSourceURL();
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                                                startActivity(Intent.createChooser(browserIntent, null));
                                            }
                                        }
                                    });
        list.setAdapter(mAdapter);
        Bundle args = RecipesFragment.this.getArguments();
        //check if it's coming from OwnRecipeDetailsActivity, if so set adapter to get own recipes
        Boolean isOwn = args.getBoolean("isOwn");
        if(isOwn){
            Boolean s = getOwn();
            if(!s)
                Toast.makeText(this.getContext(),"You have not added any recipes of your own!", Toast.LENGTH_SHORT).show();
         }
        else
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
                            if(recipes.size() == 0)
                                Toast.makeText(getContext(),"No results found!",Toast.LENGTH_SHORT).show();
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
        //In case of timeout
        req.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        volleyController.addToRequestQueue(req);

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
        // close soft keyboard after pressing the keyboard's search button
        InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getView().getWindowToken(), 0);
        if(query.contains(" ")){
            query = query.replace(" ",",");
        }
        searchRecipe(Contract.URL_SEARCH+query);
        return true;
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
                Log.e(LOG_TAG,error.getMessage().toString());
            }
        });
        //In case of timeout
        req.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        volleyController.addToRequestQueue(req);
    }

}
