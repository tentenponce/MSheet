package com.tcorner.msheet.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.tcorner.msheet.R;

import java.io.ByteArrayOutputStream;

/**
 * image utilities
 * Created by Tenten Ponce on 10/6/2017.
 */

public class ImageUtil {

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static void loadToGlide(Context context, ImageView imageView, Object o) {
        Glide.with(context)
                .asBitmap()
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.default_image_loading))
                .transition(new BitmapTransitionOptions()
                        .crossFade())
                .load(o)
                .into(imageView);
    }
}
