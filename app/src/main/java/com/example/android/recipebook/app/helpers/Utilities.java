package com.example.android.recipebook.app.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Fatma on 27-Jan-17.
 */

public class Utilities {
    public static void setImage(View view, ImageView imageView, String path){
        Picasso.with(view.getContext())
                .load(path)
                .resize(135,135)
                .into(imageView);
    }

//    public Bitmap transform(Bitmap source) {
//        int size = Math.min(source.getWidth(), source.getHeight());
//        int x = (source.getWidth() - size) / 2;
//        int y = (source.getHeight() - size) / 2;
//        Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
//        if (result != source) {
//            source.recycle();
//        }
//        return result;
//    }
}
