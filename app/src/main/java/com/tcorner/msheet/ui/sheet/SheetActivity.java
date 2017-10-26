package com.tcorner.msheet.ui.sheet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.ui.play.PlayActivity;
import com.tcorner.msheet.util.FileUtil;
import com.tcorner.msheet.util.ImageUtil;
import com.tcorner.msheet.util.IntentUtil;
import com.tcorner.msheet.util.RxUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * displays sheet of the selected group
 * Created by Tenten Ponce on 10/20/2017.
 */

public class SheetActivity extends BaseActivity implements SheetMvpView, View.OnClickListener {

    private static final int REQUEST_GALLERY = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_sheets)
    RecyclerView rvSheets;

    @BindView(R.id.fab_add_sheet)
    FloatingActionButton fabAddSheet;

    @BindView(R.id.coor_sheet)
    CoordinatorLayout coorSheet;

    @BindView(R.id.swipe_sheets)
    SwipeRefreshLayout swipeSheets;

    @Inject
    SheetPresenter sheetPresenter;

    FastItemAdapter<Sheet> fastItemAdapter;

    Group selectedGroup;

    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        ButterKnife.bind(this);

        activityComponent().inject(this);
        sheetPresenter.attachView(this);

        init();
        initViews();
        getGroupSheets();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sheetPresenter.detachView();
        RxUtil.dispose(disposable);
    }

    @Override
    public void showSheets(List<Sheet> sheets) {
        swipeSheets.post(new Runnable() {
            @Override
            public void run() {
                swipeSheets.setRefreshing(false);
            }
        });
        fastItemAdapter.clear();
        fastItemAdapter.add(sheets);
        fastItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAddSheet(Sheet sheet) {
        getGroupSheets();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_sheet) {
            showFileChooser();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_play) {
            ArrayList<Sheet> sheets = (ArrayList<Sheet>) fastItemAdapter.getAdapterItems();

            Intent intent = new Intent(this, PlayActivity.class);
            intent.putExtra(IntentUtil.PLAY_SHEETS, sheets);
            startActivity(intent);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sheet, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK &&
                requestCode == REQUEST_GALLERY) {
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

    private void init() {
        if (getIntent().hasExtra(IntentUtil.SELECTED_GROUP)) {
            selectedGroup = getIntent().getParcelableExtra(IntentUtil.SELECTED_GROUP);
        }
    }

    private void initViews() {
        /* init toolbar */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(selectedGroup.name());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* init click listeners */
        fabAddSheet.setOnClickListener(this);

        /* init recyclerview */
        fastItemAdapter = new FastItemAdapter<>();

        rvSheets.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvSheets.setAdapter(fastItemAdapter);

        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<Sheet>() {
            @Override
            public boolean onClick(View v, IAdapter<Sheet> adapter, Sheet item, int position) {
                rotateImage(item.imagePath())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap(new Function<String, Observable<Void>>() {
                            @Override
                            public Observable<Void> apply(@NonNull String s) throws Exception {
                                return ImageUtil.clearGlideCache(SheetActivity.this);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<Void, Observable<Void>>() {
                            @Override
                            public Observable<Void> apply(@NonNull Void aVoid) throws Exception {
                                return ImageUtil.clearGlideMemory(SheetActivity.this);
                            }
                        })
                        .subscribe(new Observer<Void>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onNext(@NonNull Void aVoid) {
                                /**/
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.e("androidruntime", e.getMessage());
                                showError();
                            }

                            @Override
                            public void onComplete() {
                                getGroupSheets();
                            }
                        });
                return true;
            }
        });

        /* init swipe refresh */
        swipeSheets.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGroupSheets();
            }
        });
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
                        if (selectedGroup != null) {
                            sheetPresenter.addSheet(Sheet.create(s, selectedGroup.uuid()));
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(SheetActivity.this, "Image failed to save.", Toast.LENGTH_SHORT).show();
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
        Snackbar.make(coorSheet, R.string.saving_image, Snackbar.LENGTH_LONG).show();
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
                fileOutputStream.flush();
                fileOutputStream.close();
                return mypath.getAbsolutePath();
            }
        });
    }

    private void getGroupSheets() {
        swipeSheets.post(new Runnable() {
            @Override
            public void run() {
                swipeSheets.setRefreshing(true);
            }
        });
        sheetPresenter.getGroupSheets(selectedGroup.uuid());
    }

    private Observable<String> rotateImage(final String imagePath) {
        Snackbar.make(coorSheet, R.string.rotating_image, Snackbar.LENGTH_LONG).show();
        final File file = new File(imagePath);

        RxUtil.dispose(disposable);
        return Observable.zip(Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws IOException {
                if (!e.isDisposed()) {
                    e.onNext(BitmapFactory.decodeFile(imagePath));
                    e.onComplete();
                }
            }
        }), Observable.create(new ObservableOnSubscribe<FileOutputStream>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<FileOutputStream> e) throws FileNotFoundException {
                if (!e.isDisposed()) {
                    e.onNext(new FileOutputStream(file));
                    e.onComplete();
                }
            }
        }), new BiFunction<Bitmap, FileOutputStream, String>() {
            @Override
            public String apply(@NonNull Bitmap bitmap, @NonNull FileOutputStream fileOutputStream) throws Exception {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                return file.getAbsolutePath();
            }
        });
    }
}
