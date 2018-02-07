package com.tcorner.msheet.ui.library;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.ui.base.BaseFragment;
import com.tcorner.msheet.ui.library.modifygroup.ModifyGroupActivity;
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

public class LibraryFragment extends BaseFragment implements LibraryMvpView, View.OnClickListener {

    private static final String[] ACTION = {"Update", "Delete"};
    private static final int UPDATE_ID = 0;

    @BindView(R.id.fab_add_sheet)
    FloatingActionButton fabAddSheet;

    @BindView(R.id.rv_sheets)
    RecyclerView rvSheets;

    @BindView(R.id.lin_empty)
    LinearLayout linEmpty;

    @Inject
    LibraryPresenter libraryPresenter;

    FastItemAdapter<Group> fastItemAdapter;

    private AlertDialog deleteGroupDialog;
    private AlertDialog actionDialog;
    private Group selectedGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);
        libraryPresenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        /* init click listeners */
        fabAddSheet.setOnClickListener(this);

        /* init recyclerview */
        fastItemAdapter = new FastItemAdapter<>();

        rvSheets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSheets.setAdapter(fastItemAdapter);

        fastItemAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener<Group>() {
            @Override
            public boolean onLongClick(View v, IAdapter<Group> adapter, Group item, int position) {
                selectedGroup = item;
                actionDialog.show();
                return true;
            }
        });

        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<Group>() {
            @Override
            public boolean onClick(View v, IAdapter<Group> adapter, Group item, int position) {
                Intent intent = new Intent(getContext(), SheetActivity.class);
                intent.putExtra(IntentUtil.SELECTED_GROUP, item);
                startActivity(intent);
                return true;
            }
        });

        /* init dialogs */
        deleteGroupDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_delete_group_title)
                .setMessage(R.string.dialog_delete_group_message)
                .setPositiveButton(R.string.dialog_delete_group_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (selectedGroup != null) {
                            libraryPresenter.deleteGroup(selectedGroup.uuid());
                        }
                    }
                }).create();

        actionDialog = new AlertDialog.Builder(getContext())
                .setItems(ACTION, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        if (i == UPDATE_ID) {
                            Intent intent = new Intent(getContext(), ModifyGroupActivity.class);
                            intent.putExtra(IntentUtil.MODIFY_ACTION, IntentUtil.UPDATE_ACTION);
                            intent.putExtra(IntentUtil.UPDATE_GROUP, selectedGroup);
                            startActivity(intent);
                        } else {
                            deleteGroupDialog.show();
                        }
                    }
                }).create();
    }

    @Override
    public void onResume() {
        super.onResume();

        fastItemAdapter.clear();
        libraryPresenter.getGroups();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        libraryPresenter.detachView();
    }

    @Override
    public void showDeleteGroup() {
        Toast.makeText(getContext(), R.string.success_delete_group, Toast.LENGTH_SHORT).show();

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
    public void showEmptyGroup() {
        rvSheets.setVisibility(View.GONE);
        linEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGroupList() {

        rvSheets.setVisibility(View.VISIBLE);
        linEmpty.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_sheet) {
            Intent intent = new Intent(getContext(), ModifyGroupActivity.class);
            intent.putExtra(IntentUtil.MODIFY_ACTION, IntentUtil.ADD_ACTION);
            startActivity(intent);
        }
    }
}
