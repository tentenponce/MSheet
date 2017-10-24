package com.tcorner.msheet.ui.play;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.util.ImageUtil;

import java.util.List;

/**
 * adapter for sheet viewing
 * Created by Tenten Ponce on 10/24/2017.
 */

public class SheetAdapter extends PagerAdapter {

    private List<Sheet> sheets;

    SheetAdapter(List<Sheet> sheets) {
        this.sheets = sheets;
    }

    @Override
    public int getCount() {
        return sheets.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RelativeLayout layoutPlaySheet = (RelativeLayout) LayoutInflater.from(container.getContext())
                .inflate(R.layout.layout_play_sheet, container, false);

        PhotoView pvSheet = layoutPlaySheet.findViewById(R.id.pv_sheet);
        ImageUtil.loadToGlide(container.getContext(), pvSheet, sheets.get(position).imagePath());

        container.addView(layoutPlaySheet);

        return layoutPlaySheet;
    }
}
