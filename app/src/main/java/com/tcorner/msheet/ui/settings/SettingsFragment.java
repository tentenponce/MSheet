package com.tcorner.msheet.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tcorner.msheet.R;
import com.tcorner.msheet.ui.base.BaseFragment;
import com.tcorner.msheet.util.SessionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * settings fragment
 * Created by Tenten Ponce on 1/22/2018.
 */

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.rl_sensor)
    RelativeLayout rlSensor;

    @BindView(R.id.switch_sensor)
    SwitchCompat switchSensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rlSensor.setOnClickListener(this);
        switchSensor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_sensor:
                switchSensor.setChecked(!switchSensor.isChecked());
                flipSensor();
                break;
            case R.id.switch_sensor:
                flipSensor();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initSettings();
    }

    private void flipSensor() {
        SessionUtil.writeBoolean(getContext(), SessionUtil.SENSOR_SETTINGS, switchSensor.isChecked());
    }

    private void initSettings() {
        switchSensor.setChecked(SessionUtil.readBoolean(getContext(), SessionUtil.SENSOR_SETTINGS, true));
    }
}
