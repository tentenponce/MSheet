package com.tcorner.msheet.ui.library;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.util.FileUtil;
import com.tcorner.msheet.util.ImageUtil;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * displays all of your music sheeeeeets
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class LibraryActivity extends BaseActivity implements LibraryMvpView, View.OnClickListener {

    private static final int REQUEST_CAMERA = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_add_sheet)
    FloatingActionButton fabAddSheet;

    @BindView(R.id.lin_sheets)
    LinearLayout linSheets;

    @Inject
    LibraryPresenter libraryPresenter;

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
    protected void onDestroy() {
        super.onDestroy();

        libraryPresenter.detachView();
    }

    @Override
    public void showError() {
        /**/
    }

    @Override
    public void showAddSheet(Sheet sheet) {
        Toast.makeText(this, "Success adding sheets.", Toast.LENGTH_SHORT).show();
        libraryPresenter.getSheets();
    }

    @Override
    public void showMusicSheets(List<Sheet> sheets) {
        linSheets.removeAllViews();
        for (Sheet sheet : sheets) {
            ImageView imageView = new ImageView(this);

            ImageUtil.loadToGlide(getApplicationContext(), imageView, sheet.image());
            linSheets.addView(imageView);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data == null) {
                    Toast.makeText(this, R.string.error_no_image, Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri selectedFileUri = data.getData();
                String selectedFilePath = FileUtil.getPath(this, selectedFileUri);

                if (selectedFilePath == null || selectedFilePath.equals("")) {
                    Toast.makeText(this, R.string.error_image_chooser, Toast.LENGTH_SHORT).show();
                } else {
                    onChooseFile(selectedFileUri);
                }
            }
        }
    }

    private void onChooseFile(Uri selectedFileUri) {
        try {
            libraryPresenter.addSheet(Sheet.create(ImageUtil.getBitmapAsByteArray(MediaStore.Images.Media.getBitmap(
                    getApplicationContext().getContentResolver(), selectedFileUri)), "123"));
        } catch (IOException e) {
            Toast.makeText(this, "There's a problem adding sheet.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void showFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        //starts new activity to select file and return data
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_sheet) {
            showFileChooser();
        }
    }
}
