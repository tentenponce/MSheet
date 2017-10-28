package com.tcorner.msheet.ui.sheet;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.ui.play.PlayActivity;
import com.tcorner.msheet.util.FileUtil;
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

    private ProgressDialog progressDialog;

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
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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

        final int gridSpacing = getResources().getDimensionPixelSize(R.dimen.s_margin);

        rvSheets.setLayoutManager(new GridLayoutManager(this, 2));
        rvSheets.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {
                outRect.left = gridSpacing;
                outRect.right = gridSpacing;
                outRect.bottom = gridSpacing;

                // Add top margin only for the first item to avoid double space between items
                if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1) {
                    outRect.top = gridSpacing;
                } else {
                    outRect.top = 0;
                }
            }
        });

        rvSheets.setAdapter(fastItemAdapter);

        fastItemAdapter.withEventHook(new ClickEventHook<Sheet>() {

            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                //return the views on which you want to bind this event
                if (viewHolder instanceof Sheet.ViewHolder) {
                    return ((Sheet.ViewHolder) viewHolder).ivRedo;
                }

                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<Sheet> fastAdapter, Sheet item) {
                progressDialog.setMessage(getString(R.string.rotating_image));

                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
                rotateImage(item.imagePath(), 90)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onNext(@NonNull String s) {
                                /**/
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                showError();
                            }

                            @Override
                            public void onComplete() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                getGroupSheets();
                            }
                        });
            }
        }).withEventHook(new ClickEventHook<Sheet>() {

            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                //return the views on which you want to bind this event
                if (viewHolder instanceof Sheet.ViewHolder) {
                    return ((Sheet.ViewHolder) viewHolder).ivUndo;
                }

                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<Sheet> fastAdapter, Sheet item) {
                progressDialog.setMessage(getString(R.string.rotating_image));
                progressDialog.show();
                rotateImage(item.imagePath(), -90)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onNext(@NonNull String s) {
                                /**/
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                showError();
                            }

                            @Override
                            public void onComplete() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                getGroupSheets();
                            }
                        });
            }
        });

        /* init swipe refresh */
        swipeSheets.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGroupSheets();
            }
        });

        /* init dialogs */
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void onChooseFile(Uri selectedFileUri) {
        progressDialog.setMessage(getString(R.string.saving_image));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

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

    private Observable<String> rotateImage(final String imagePath, final int rotation) {
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
                matrix.postRotate(rotation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                return file.getAbsolutePath();
            }
        });
    }
}
