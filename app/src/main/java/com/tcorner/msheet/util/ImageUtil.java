package com.tcorner.msheet.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.tcorner.msheet.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * image utilities
 * Created by Tenten Ponce on 10/6/2017.
 */

public class ImageUtil {

    public static void loadToGlide(Context context, ImageView imageView, Object o) {
        Glide.with(context)
                .asBitmap()
                .apply(new RequestOptions()
                        .placeholder(R.drawable.default_image_loading)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .transition(new BitmapTransitionOptions()
                        .crossFade())
                .load(o)
                .into(imageView);
    }

    public static Observable<Void> clearGlideCache(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Void> e) throws Exception {
                Glide.get(context).clearDiskCache();
                e.onComplete();
            }
        });
    }

    public static Observable<Void> clearGlideMemory(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Void> e) throws Exception {
                Glide.get(context).clearMemory();
                e.onComplete();
            }
        });
    }
}
