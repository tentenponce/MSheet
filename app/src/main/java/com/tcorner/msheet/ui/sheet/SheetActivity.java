package com.tcorner.msheet.ui.sheet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.util.IntentUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * displays sheet of the selected group
 * Created by Tenten Ponce on 10/20/2017.
 */

public class SheetActivity extends BaseActivity implements SheetMvpView, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_sheets)
    RecyclerView rvSheets;

    @BindView(R.id.fab_add_sheet)
    FloatingActionButton fabAddSheet;

    @Inject
    SheetPresenter sheetPresenter;

    FastItemAdapter<Sheet> fastItemAdapter;

    Group selectedGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        ButterKnife.bind(this);

        activityComponent().inject(this);
        sheetPresenter.attachView(this);

        init();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fastItemAdapter.clear();
        sheetPresenter.getGroupSheets(selectedGroup);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sheetPresenter.detachView();
    }

    @Override
    public void showSheet(Sheet sheet) {
        fastItemAdapter.add(sheet);
        fastItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        //TODO
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        if (getIntent().hasExtra(IntentUtil.SELECTED_GROUP)) {
            selectedGroup = getIntent().getParcelableExtra(IntentUtil.SELECTED_GROUP);
        }
    }

    private void initViews() {
        /* init toolbar */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(selectedGroup.name());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* init click listeners */
        fabAddSheet.setOnClickListener(this);

        /* init recyclerview */
        fastItemAdapter = new FastItemAdapter<>();

        rvSheets.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvSheets.setAdapter(fastItemAdapter);
    }
}
