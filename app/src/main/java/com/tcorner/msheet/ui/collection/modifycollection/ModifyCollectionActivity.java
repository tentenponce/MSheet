package com.tcorner.msheet.ui.collection.modifycollection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Collection;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.util.IntentUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * add or update a collection of groups/library.
 * Created by Tenten Ponce on 2/5/2018.
 */

public class ModifyCollectionActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textinput_collection_name)
    TextInputLayout textInputCollectionName;

    @BindView(R.id.rv_groups)
    RecyclerView rvGroups;

    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty;

    @BindView(R.id.tv_empty_message)
    AppCompatTextView tvEmptyMessage;

    private int action = -1;
    private Collection selectedCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_collection);
        ButterKnife.bind(this);

        activityComponent().inject(this);

        initViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_empty:
                break;
        }
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

    private void initViews() {
         /* init identify modify action */
        if (!getIntent().hasExtra(IntentUtil.MODIFY_ACTION)) { //check if there's action
            Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
            finish();
        }

        action = getIntent().getIntExtra(IntentUtil.MODIFY_ACTION, -1);

        if (action == -1) { //if -1, return also
            Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
            finish();
        }

        /* init toolbar */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(action == IntentUtil.ADD_ACTION ? R.string.title_add_collection : R.string.title_update_collection);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* init groups of the collection */
        if (action == IntentUtil.ADD_ACTION) { //check if add action to initialize empty collection
            selectedCollection = Collection.create(getString(R.string.default_collection_name), new ArrayList<Group>());
        } else if (action == IntentUtil.UPDATE_ACTION) {
            if (getIntent().hasExtra(IntentUtil.SELECTED_COLLECTION)) { //check if the collection to be updated is passed
                selectedCollection = getIntent().getParcelableExtra(IntentUtil.SELECTED_COLLECTION);

                if (selectedCollection == null) { //check if it is not null
                    Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        /* init collection */
        initCollection();

        /* init listeners */
        tvEmptyMessage.setText(R.string.collection_group_empty);
        layoutEmpty.setOnClickListener(this);
    }

    private void initCollection() {
        if (selectedCollection.groups().isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvGroups.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvGroups.setVisibility(View.VISIBLE);
        }

        textInputCollectionName.getEditText().setText(selectedCollection.name());
    }
}
