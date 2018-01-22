package com.tcorner.msheet.ui.play;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.WindowManager;

import com.tcorner.msheet.R;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.util.IntentUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * plays the sheet on a fullscreen view
 * Created by Tenten Ponce on 10/24/2017.
 */

public class PlayActivity extends BaseActivity implements SensorEventListener {

    private static final int SENSOR_SENSITIVITY = 4;
    @BindView(R.id.vp_sheets)
    SheetViewPager vpSheets;
    @BindView(R.id.tablayout_indicator)
    TabLayout tabLayoutIndicator;
    private SensorManager mSensorManager;
    private Sensor mProximity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);

        init();
        initViews();
        initSheets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (sensorEvent.values[0] >= -SENSOR_SENSITIVITY && sensorEvent.values[0] <= SENSOR_SENSITIVITY) {
                vpSheets.setCurrentItem(vpSheets.getCurrentItem() + 1, true);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void init() {
        /* init proximity sensor */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        /* go fullscreen */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initViews() {
        tabLayoutIndicator.setupWithViewPager(vpSheets, true);
    }

    private void initSheets() {
        if (getIntent().hasExtra(IntentUtil.PLAY_SHEETS)) {
            ArrayList<Sheet> sheets = getIntent().getParcelableArrayListExtra(IntentUtil.PLAY_SHEETS);
            SheetAdapter sheetAdapter = new SheetAdapter(sheets);

            vpSheets.setAdapter(sheetAdapter);
        }
    }
}
