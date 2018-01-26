package com.tcorner.msheet.data.model;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.tcorner.msheet.R;
import com.tcorner.msheet.util.ViewUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * group of Groups model
 * Created by Tenten Ponce on 1/26/2018.
 */

@AutoValue
public abstract class Collection extends AbstractItem<Collection, Collection.ViewHolder> {

    public static Collection create(String uuid, String name, Date dateModified, List<Group> groups) {
        return new AutoValue_Collection(uuid, name, dateModified, groups);
    }

    public static Collection create(String name, List<Group> groups) {
        return create(UUID.randomUUID().toString(), name, Calendar.getInstance().getTime(), groups);
    }

    public static Collection create(Collection collection) {
        return create(collection.uuid(), collection.name(), collection.dateModified(), collection.groups());
    }

    public abstract String uuid();

    public abstract String name();

    public abstract Date dateModified();

    public abstract List<Group> groups();

    @Override
    public Collection.ViewHolder getViewHolder(View v) {
        return new Collection.ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.collection_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.layout_collection;
    }

    @Override
    public void bindView(Collection.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        viewHolder.tvCollectionName.setText(name());

        StringBuilder stringBuilder = new StringBuilder();
        for (Group group : groups()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }

            stringBuilder.append(group.name());

            viewHolder.tvGroupNames.setText(stringBuilder.toString());

            if (ViewUtil.isTextEllipsized(viewHolder.tvGroupNames)) {
                stringBuilder.setLength(stringBuilder.length() - 1);

                //append the remaining count of groups
                stringBuilder.append("+");
                stringBuilder.append(String.valueOf(groups().size() - stringBuilder.length()));

                viewHolder.tvGroupNames.setText(stringBuilder.toString());
            }
        }
    }

    @Override
    public void unbindView(Collection.ViewHolder holder) {
        super.unbindView(holder);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_group_names)
        AppCompatTextView tvGroupNames;

        @BindView(R.id.tv_collection_name)
        AppCompatTextView tvCollectionName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
