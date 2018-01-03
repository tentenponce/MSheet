package com.tcorner.msheet.ui.library;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.ui.library.addgroup.AddGroupActivity;
import com.tcorner.msheet.ui.sheet.SheetActivity;
import com.tcorner.msheet.util.IntentUtil;

import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * displays all of your music sheeeeeets
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class LibraryActivity extends BaseActivity implements LibraryMvpView, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_add_sheet)
    FloatingActionButton fabAddSheet;

    @BindView(R.id.rv_sheets)
    RecyclerView rvSheets;

    @Inject
    LibraryPresenter libraryPresenter;

    FastItemAdapter<Group> fastItemAdapter;

    private AlertDialog deleteGroupDialog;
    private Group deleteGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);

        activityComponent().inject(this);
        libraryPresenter.attachView(this);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fastItemAdapter.clear();
        libraryPresenter.getGroups();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        libraryPresenter.detachView();
    }

    @Override
    public void showDeleteGroup() {
        Toast.makeText(this, "Sheet successfully deleted.", Toast.LENGTH_SHORT).show();

        fastItemAdapter.clear();
        libraryPresenter.getGroups();
    }

    @Override
    public void showGroup(final Group group) {
        fastItemAdapter.add(group);
        Collections.sort(fastItemAdapter.getAdapterItems(), new Comparator<Group>() {
            @Override
            public int compare(Group group, Group t1) {
                return group.name().compareToIgnoreCase(t1.name());
            }
        });
        fastItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_sheet) {
            Intent intent = new Intent(this, AddGroupActivity.class);
            startActivity(intent);
        }
    }

    private void initViews() {
        /* init toolbar */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_library);
        }

        /* init click listeners */
        fabAddSheet.setOnClickListener(this);

        /* init recyclerview */
        fastItemAdapter = new FastItemAdapter<>();

        rvSheets.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvSheets.setAdapter(fastItemAdapter);

        fastItemAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener<Group>() {
            @Override
            public boolean onLongClick(View v, IAdapter<Group> adapter, Group item, int position) {
                deleteGroup = item;
                deleteGroupDialog.show();
                return true;
            }
        });

        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<Group>() {
            @Override
            public boolean onClick(View v, IAdapter<Group> adapter, Group item, int position) {
                Intent intent = new Intent(LibraryActivity.this, SheetActivity.class);
                intent.putExtra(IntentUtil.SELECTED_GROUP, item);
                startActivity(intent);
                return true;
            }
        });

        /* init dialogs */
        deleteGroupDialog = new AlertDialog.Builder(LibraryActivity.this)
                .setTitle(R.string.dialog_delete_group_title)
                .setMessage(R.string.dialog_delete_group_message)
                .setPositiveButton(R.string.dialog_delete_group_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (deleteGroup != null) {
                            libraryPresenter.deleteGroup(deleteGroup.uuid());
                        }
                    }
                }).create();
    }
}
