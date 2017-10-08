package com.tcorner.msheet.ui.library;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.ui.library.addgroup.AddGroupActivity;
import com.tcorner.msheet.util.FileUtil;
import com.tcorner.msheet.util.RxUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * displays all of your music sheeeeeets
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class LibraryActivity extends BaseActivity implements LibraryMvpView, View.OnClickListener {

    private static final int REQUEST_GALLERY = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_add_sheet)
    FloatingActionButton fabAddSheet;

    @BindView(R.id.lin_sheets)
    LinearLayout linSheets;

    @Inject
    LibraryPresenter libraryPresenter;

    private Disposable disposable;

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

        libraryPresenter.getGroups();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        libraryPresenter.detachView();
        RxUtil.dispose(disposable);
    }

    @Override
    public void showDeleteGroup() {
        Toast.makeText(this, "Sheet successfully deleted.", Toast.LENGTH_SHORT).show();
        libraryPresenter.getGroups();
    }

    @Override
    public void showGroups(final List<Group> groups) {
        linSheets.removeAllViews();

        RxUtil.dispose(disposable);
        Observable.fromIterable(groups)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Group>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull final Group group) {
                        TextView textView = new TextView(LibraryActivity.this.getApplicationContext());
                        textView.setText(group.name());

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                libraryPresenter.deleteGroup(group.uuid());
                            }
                        });

                        linSheets.addView(textView);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        /**/
                    }

                    @Override
                    public void onComplete() {
                        /**/
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
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
    }

    private void onChooseFile(Uri selectedFileUri) {
        saveToInternalStorage(selectedFileUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull String s) {
//                        libraryPresenter.addGroup(Sheet.create(s, "123"));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(LibraryActivity.this, "Image failed to save.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        /**/
                    }
                });
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
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private Observable<String> saveToInternalStorage(final Uri uri) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("msheet", Context.MODE_PRIVATE); //image directory

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = mime.getExtensionFromMimeType(getApplicationContext().getContentResolver().getType(uri));

        final File mypath = new File(directory, UUID.randomUUID().toString() + "." + ext); //build the name to be save

        RxUtil.dispose(disposable);
        return Observable.zip(Observable.create(new ObservableOnSubscribe<FileOutputStream>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<FileOutputStream> e) throws FileNotFoundException {
                if (!e.isDisposed()) {
                    e.onNext(new FileOutputStream(mypath));
                    e.onComplete();
                }
            }
        }), Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws IOException {
                if (!e.isDisposed()) {
                    e.onNext(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
                    e.onComplete();
                }
            }
        }), new BiFunction<FileOutputStream, Bitmap, String>() {
            @Override
            public String apply(@NonNull FileOutputStream fileOutputStream, @NonNull Bitmap bitmap) throws Exception {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                return mypath.getAbsolutePath();
            }
        });

    }

}
