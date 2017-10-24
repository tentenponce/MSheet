package com.tcorner.msheet.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.tcorner.msheet.R;

/**
 * image utilities
 * Created by Tenten Ponce on 10/6/2017.
 */

public class ImageUtil {

    public static void loadToGlide(Context context, ImageView imageView, Object o) {
        Glide.with(context)
                .asBitmap()
                .apply(new RequestOptions()
                        .placeholder(R.drawable.default_image_loading))
                .transition(new BitmapTransitionOptions()
                        .crossFade())
                .load(o)
                .into(imageView);
    }
}
