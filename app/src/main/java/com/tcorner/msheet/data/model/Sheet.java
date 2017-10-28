package com.tcorner.msheet.data.model;

import android.os.Parcelable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.tcorner.msheet.R;
import com.tcorner.msheet.util.ImageUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * piano sheet/piece model
 * Created by Exequiel Egbert V. Ponce on 9/12/2017.
 */

@AutoValue
public abstract class Sheet extends AbstractItem<Sheet, Sheet.ViewHolder> implements Parcelable {

    public static Sheet create(String uuid, String imagePath, String groupUuid, Date dateModified) {
        return new AutoValue_Sheet(uuid, imagePath, groupUuid, dateModified);
    }

    public static Sheet create(String imagePath, String groupUuid) {
        return create(UUID.randomUUID().toString(), imagePath, groupUuid, Calendar.getInstance().getTime());
    }

    public static Sheet create(Sheet sheet) {
        return create(sheet.uuid(), sheet.imagePath(), sheet.groupUuid(), sheet.dateModified());
    }

    public abstract String uuid();

    public abstract String imagePath();

    public abstract String groupUuid();

    public abstract Date dateModified();

    @Override
    public Sheet.ViewHolder getViewHolder(View v) {
        return new Sheet.ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.sheet_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.layout_sheet;
    }

    @Override
    public void bindView(Sheet.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        ImageUtil.loadToGlide(viewHolder.ivSheet.getContext(), viewHolder.ivSheet, imagePath());
    }

    @Override
    public void unbindView(Sheet.ViewHolder holder) {
        super.unbindView(holder);
        holder.ivSheet.setImageDrawable(null);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_sheet)
        public AppCompatImageView ivSheet;

        @BindView(R.id.iv_undo)
        public AppCompatImageView ivUndo;

        @BindView(R.id.iv_redo)
        public AppCompatImageView ivRedo;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
