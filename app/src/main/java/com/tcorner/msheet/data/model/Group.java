package com.tcorner.msheet.data.model;

import android.os.Parcelable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.tcorner.msheet.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gujun.android.taggroup.TagGroup;

/**
 * group of piano sheets/piece
 * 1 song
 * Created by Exequiel Egbert V. Ponce on 9/12/2017.
 */

@AutoValue
public abstract class Group extends AbstractItem<Group, Group.ViewHolder> implements Parcelable {

    public static Group create(String uuid, String name, Date dateModified, List<GroupTag> tags) {
        return new AutoValue_Group(uuid, name, dateModified, tags);
    }

    public static Group create(String uuid, String name, List<GroupTag> tags) {
        return create(uuid, name, Calendar.getInstance().getTime(), tags);
    }

    public static Group create(String name, List<GroupTag> tags) {
        return create(UUID.randomUUID().toString(), name, Calendar.getInstance().getTime(), tags);
    }

    public static Group create(Group group) {
        return create(group.uuid(), group.name(), group.dateModified(), group.tags());
    }

    public abstract String uuid();

    public abstract String name();

    public abstract Date dateModified();

    public abstract List<GroupTag> tags();

    @Override
    public Group.ViewHolder getViewHolder(View v) {
        return new Group.ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.group_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.layout_group;
    }

    @Override
    public void bindView(Group.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        List<String> tags = new ArrayList<>();
        for (GroupTag groupTag : tags()) { //extract the string tag only
            tags.add(groupTag.tag());
        }

        viewHolder.tvTitle.setText(name());
        viewHolder.tagGroup.setTags(tags);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.tvTitle.setText(null);
        holder.tagGroup.setTags(new ArrayList<String>());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        AppCompatTextView tvTitle;

        @BindView(R.id.tag_group)
        TagGroup tagGroup;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
