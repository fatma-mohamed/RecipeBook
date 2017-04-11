package com.example.android.recipebook.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.recipebook.app.data.Contract;
import com.example.android.recipebook.app.data.Recipe;
import com.example.android.recipebook.app.data.RecipesParser;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import static android.content.ContentValues.TAG;


public class RecipesFragment extends Fragment{

    public static final String LOG_TAG = RecipesFragment.class.getSimpleName();

    private MyRecipesAdapter mAdapter;
    private ListView list;
    private ProgressDialog progressDialog;



    public RecipesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipes, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList<Recipe> temp = new ArrayList<>();
        final View view = inflater.inflate(R.layout.fragment_recipes_list, container, false);
        list = (ListView)view.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = ((Recipe)mAdapter.getItem(i)).getSourceURL();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                startActivity(Intent.createChooser(browserIntent,null));
            }
        });
        mAdapter = new MyRecipesAdapter(getActivity(),temp);
        list.setAdapter(mAdapter);
        getRecipesRequest();
        return view;
    }

    private void getRecipesRequest() {
        showprogressDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                Contract.URL_SORT + "t",
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
                            Toast.makeText(getContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hideprogressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hideprogressDialog();
            }
        });

        // Adding request to request queue
        Log.e(TAG+"REQ",req.toString());
        VolleyController.getInstance().addToRequestQueue(req);

    }

    private void showprogressDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideprogressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
