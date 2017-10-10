package com.tcorner.msheet.ui.library.addgroup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.GroupTag;
import com.tcorner.msheet.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gujun.android.taggroup.TagGroup;

/**
 * add a piece (group) to library
 * Created by Tenten Ponce on 10/8/2017.
 */

public class AddGroupActivity extends BaseActivity implements AddGroupMvpView, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.coor_add_group)
    CoordinatorLayout coorAddGroup;

    @BindView(R.id.textinput_group_name)
    TextInputLayout textInputGroupName;

    @BindView(R.id.tag_group)
    TagGroup tagGroup;

    @BindView(R.id.fab_add_group)
    FloatingActionButton fabAddGroup;

    @Inject
    AddGroupPresenter addGroupPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);

        activityComponent().inject(this);
        addGroupPresenter.attachView(this);

        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        addGroupPresenter.detachView();
    }

    @Override
    public void showAddGroup(Group group) {
        onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_group) {
            String pieceName = textInputGroupName.getEditText().getText().toString().trim();

            if (pieceName.isEmpty()) {
                Snackbar.make(coorAddGroup, R.string.error_piece_name_empty, Snackbar.LENGTH_LONG).show();
            } else {
                List<GroupTag> groupTags = new ArrayList<>();

                Group group = Group.create(pieceName, new ArrayList<GroupTag>()); //build here to get uuid
                for (String tag : Arrays.asList(tagGroup.getTags())) {
                    groupTags.add(GroupTag.create(tag, group.uuid()));
                }

                group.tags().addAll(groupTags); //add all tags
                addGroupPresenter.addGroup(group);
            }
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
        /* init toolbar */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_add_piece);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* init listeners */
        fabAddGroup.setOnClickListener(this);
    }
}
