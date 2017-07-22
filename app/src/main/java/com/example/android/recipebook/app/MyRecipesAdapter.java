package com.example.android.recipebook.app;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.recipebook.app.data.DatabaseHelper;
import com.example.android.recipebook.app.data.Recipe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class MyRecipesAdapter extends BaseAdapter implements Filterable{
    private DatabaseHelper db;
    private Context mContext;
    private Boolean btnClicked = false;
    private Filter filter;
    private Button btnLoadExtra;
    private ArrayList<Recipe> values;
    public Boolean own = false;
    private static final String LOG_TAG = MyRecipesAdapter.class.getSimpleName();

    public MyRecipesAdapter(Context context, ArrayList<Recipe> r) {
        db = RecipesFragment.db;
        mContext = context;
        values = r;
        btnLoadExtra = new Button(context);
        btnLoadExtra.setText("Load More...");
        getFilter();
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        Log.e(LOG_TAG,values.get(position).toString());
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void add(ArrayList<Recipe> r)
    {
        values = r;
        notifyDataSetChanged();
    }

    public Boolean showBookmarked(){
        values = db.getBookmarkedRecipes();
        if(values.size()==0)
            return false;
        notifyDataSetChanged();
        own = false;
        return true;
    }

    public Boolean showOwn(){
        values = db.getOwnRecipes();
        if(values.size()==0)
            return false;
        own = true;
        notifyDataSetChanged();
        return true;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater
                inflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item = inflater.inflate(R.layout.list_item_recipe,null);
        if(own){
            ImageButton btn = (ImageButton)item.findViewById(R.id.btn_bookmark);
            btn.setVisibility(View.INVISIBLE);
        }
        ImageView recipeIcon = (ImageView)item.findViewById(R.id.list_item_icon);
        TextView recipeTitle = (TextView)item.findViewById(R.id.list_item_name);
        Typeface custom_font = Typeface.createFromAsset(item.getContext().getAssets(), "fonts/americanabt.ttf");
        recipeTitle.setTypeface(custom_font);
        if(own){
            recipeTitle.setText(values.get(position).getName());
        }
        else{
            String path = values.get(position).getImageURL();
            Utilities.setImage(item,recipeIcon,path);
            recipeTitle.setText(values.get(position).getName());
            final ImageButton bookmark_btn = (ImageButton)item.findViewById(R.id.btn_bookmark);
            if(db.bookmarkedExists(values.get(position).getName()))
            {
                bookmark_btn.setImageResource(R.drawable.ic_action_bookmarked);
                btnClicked = true;
            }
            bookmark_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!btnClicked){
                        bookmark_btn.setImageResource(R.drawable.ic_action_bookmarked);
                        db.addBookmarkedRecipe(values.get(position));
                        btnClicked = true;
                    }
                    else{
                        bookmark_btn.setImageResource(R.drawable.ic_action_bookmark);
                        db.removeBookmarkedRecipe(values.get(position).getName());
                        btnClicked = false;
                    }
                }
            });
        }
        return item;
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
            filter = new MyFilter();
        return filter;
    }

    private class MyFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            if(constraint!=null && constraint.length()>0){
                ArrayList<Recipe> results = new ArrayList<>();
                for(Recipe recipe:values){
                    if(recipe.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        results.add(recipe);
                }
                filterResults.count = results.size();
                filterResults.values = results;
            }
            else{
                filterResults.count = values.size();
                filterResults.values = values;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            values = (ArrayList<Recipe>)filterResults.values;
            notifyDataSetChanged();
        }
    }
}


