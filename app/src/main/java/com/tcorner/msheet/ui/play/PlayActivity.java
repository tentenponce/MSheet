package com.tcorner.msheet.ui.play;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.util.IntentUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * plays the sheet on a fullscreen view
 * Created by Tenten Ponce on 10/24/2017.
 */

public class PlayActivity extends BaseActivity {

    @BindView(R.id.vp_sheets)
    SheetViewPager vpSheets;

    @BindView(R.id.tablayout_indicator)
    TabLayout tabLayoutIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);

        initViews();
        initSheets();
    }

    private void initViews() {
        tabLayoutIndicator.setupWithViewPager(vpSheets, true);
    }

    private void initSheets() {
        if (getIntent().hasExtra(IntentUtil.PLAY_SHEETS)) {
            ArrayList<Sheet> sheets = getIntent().getParcelableArrayListExtra(IntentUtil.PLAY_SHEETS);
            SheetAdapter sheetAdapter = new SheetAdapter(sheets);

            vpSheets.setAdapter(sheetAdapter);
        }
    }
}
