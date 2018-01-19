package com.tcorner.msheet.ui.library.modifygroup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.GroupTag;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.util.DialogFactory;
import com.tcorner.msheet.util.IntentUtil;
import com.tcorner.msheet.util.MapperUtil;

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

public class ModifyGroupActivity extends BaseActivity implements ModifyGroupMvpView, View.OnClickListener {

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
    ModifyGroupPresenter modifyGroupPresenter;

    private int action = -1;
    private Group selectedGroup;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);

        activityComponent().inject(this);
        modifyGroupPresenter.attachView(this);

        initViews();
        initModifyData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        modifyGroupPresenter.detachView();
    }

    @Override
    public void showAddGroup(Group group) {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

        onBackPressed();
    }

    @Override
    public void showUpdateGroup(Group group) {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

        onBackPressed();
    }

    @Override
    public void showError() {
        super.showError();

        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_group) {
            String pieceName = textInputGroupName.getEditText().getText().toString().trim();

            if (pieceName.isEmpty()) {
                Snackbar.make(coorAddGroup, R.string.error_piece_name_empty, Snackbar.LENGTH_LONG).show();
            } else {
                List<GroupTag> groupTags = new ArrayList<>();

                Group group = Group.create(selectedGroup.uuid(), pieceName, new ArrayList<GroupTag>()); //build here to get uuid
                for (String tag : Arrays.asList(tagGroup.getTags())) {
                    groupTags.add(GroupTag.create(tag, group.uuid()));
                }

                group.tags().addAll(groupTags); //add all tags

                if (action == IntentUtil.ADD_ACTION) {
                    if (!loadingDialog.isShowing()) {
                        loadingDialog.show();
                    }

                    modifyGroupPresenter.addGroup(group);
                } else if (action == IntentUtil.UPDATE_ACTION) {
                    if (!loadingDialog.isShowing()) {
                        loadingDialog.show();
                    }

                    modifyGroupPresenter.updateGroup(group);
                }
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
        /* init identify modify action */
        if (!getIntent().hasExtra(IntentUtil.MODIFY_GROUP_ACTION)) { //check if there's action
            Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
            finish();
        }

        action = getIntent().getIntExtra(IntentUtil.MODIFY_GROUP_ACTION, -1);

        if (action == -1) { //if -1, return also
            Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
            finish();
        }

        /* init toolbar */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(action == IntentUtil.ADD_ACTION ? R.string.title_add_piece : R.string.title_update_piece);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* init listeners */
        fabAddGroup.setOnClickListener(this);

        /* init dialogs */
        loadingDialog = DialogFactory.createProgressDialog(this, R.string.loading);
    }

    private void initModifyData() {
        if (action == IntentUtil.UPDATE_ACTION) {
            selectedGroup = getIntent().getParcelableExtra(IntentUtil.UPDATE_GROUP);

            if (selectedGroup != null) {
                textInputGroupName.getEditText().setText(selectedGroup.name());
                tagGroup.setTags(MapperUtil.mapTagsToString(selectedGroup.tags()));
            } else {
                Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}