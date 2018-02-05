package com.tcorner.msheet.ui.collection;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Collection;
import com.tcorner.msheet.ui.base.BaseFragment;
import com.tcorner.msheet.ui.collection.modifycollection.ModifyCollectionActivity;
import com.tcorner.msheet.util.IntentUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * collection fragment
 * Created by Tenten Ponce on 1/26/2018.
 */

public class CollectionFragment extends BaseFragment implements View.OnClickListener, CollectionMvpView {

    @BindView(R.id.swipe_collections)
    SwipeRefreshLayout swipeCollections;

    @BindView(R.id.rv_collections)
    RecyclerView rvCollections;

    @BindView(R.id.lin_empty)
    LinearLayout linEmpty;

    @BindView(R.id.fab_add_collection)
    FloatingActionButton fabAddCollection;

    @Inject
    CollectionPresenter collectionPresenter;

    FastItemAdapter<Collection> fastItemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);
        collectionPresenter.attachView(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        /* init listeners */
        fabAddCollection.setOnClickListener(this);

        /* init recyclerview */
        fastItemAdapter = new FastItemAdapter<>();

        rvCollections.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCollections.setAdapter(fastItemAdapter);

        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<Collection>() {
            @Override
            public boolean onClick(View v, IAdapter<Collection> adapter, Collection item, int position) {

                return true;
            }
        });

        /* init swipe refresh layout */
        swipeCollections.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCollections();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        collectionPresenter.detachView();
    }

    @Override
    public void onResume() {
        super.onResume();

        getCollections();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_collection:
                Intent intent = new Intent(getContext(), ModifyCollectionActivity.class);
                intent.putExtra(IntentUtil.MODIFY_GROUP_ACTION, IntentUtil.ADD_ACTION);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void showCollections(List<Collection> collections) {
        swipeCollections.post(new Runnable() {
            @Override
            public void run() {
                swipeCollections.setRefreshing(false);
            }
        });
        fastItemAdapter.clear();
        fastItemAdapter.add(collections);
        fastItemAdapter.notifyDataSetChanged();

        if (collections.isEmpty()) {
            linEmpty.setVisibility(View.VISIBLE);
            rvCollections.setVisibility(View.GONE);
        } else {
            linEmpty.setVisibility(View.GONE);
            rvCollections.setVisibility(View.VISIBLE);
        }
    }

    private void getCollections() {
        swipeCollections.post(new Runnable() {
            @Override
            public void run() {
                swipeCollections.setRefreshing(true);
            }
        });

        collectionPresenter.getCollections();
    }
}
