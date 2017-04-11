package com.example.android.recipebook.app;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.recipebook.app.data.Recipe;

import java.util.ArrayList;


public class MyRecipesAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Recipe> values;
    private static final String LOG_TAG = MyRecipesAdapter.class.getSimpleName();

    public MyRecipesAdapter(Context context, ArrayList<Recipe> r) {
        mContext = context;
        values = r;
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
        Log.e(LOG_TAG,"getID");
        return 0;
    }

    public void add(ArrayList<Recipe> r)
    {
        values = r;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
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
        bookmark_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookmark_btn.setImageResource(R.drawable.ic_action_bookmarked);
            }
        });
        return item;
    }

}
