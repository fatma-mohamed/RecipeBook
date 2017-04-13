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

import static com.example.android.recipebook.app.VolleyController.TAG;


public class MyRecipesAdapter extends BaseAdapter implements Filterable{
    private DatabaseHelper db;
    private Context mContext;
    private Boolean btnClicked = false;
    private Filter filter;
    private Button btnLoadExtra;
    private ArrayList<Recipe> values;
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
        values = db.getFavourites();
        if(values.size()==0)
            return false;
        notifyDataSetChanged();
        return true;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater
                inflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item = inflater.inflate(R.layout.list_item_recipe,null);
        ImageView recipeIcon = (ImageView)item.findViewById(R.id.list_item_icon);
        TextView recipeTitle = (TextView)item.findViewById(R.id.list_item_name);
        Typeface custom_font = Typeface.createFromAsset(item.getContext().getAssets(), "fonts/americanabt.ttf");
        recipeTitle.setTypeface(custom_font);
        String path = values.get(position).getImageURL();
        Utilities.setImage(item,recipeIcon,path);
        recipeTitle.setText(values.get(position).getTitle());
        final ImageButton bookmark_btn = (ImageButton)item.findViewById(R.id.btn_bookmark);
        if(db.exists(values.get(position).getTitle()))
        {
            bookmark_btn.setImageResource(R.drawable.ic_action_bookmarked);
            btnClicked = true;
        }
        bookmark_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btnClicked){
                    bookmark_btn.setImageResource(R.drawable.ic_action_bookmarked);
                    db.addFavourite(values.get(position));
                    btnClicked = true;
                }
                else{
                    bookmark_btn.setImageResource(R.drawable.ic_action_bookmark);
                    db.deleteFavourite(values.get(position).getTitle());
                    btnClicked = false;
                }
            }
        });
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
                    if(recipe.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
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


