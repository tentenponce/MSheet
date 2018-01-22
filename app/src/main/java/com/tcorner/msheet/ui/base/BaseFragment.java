package com.tcorner.msheet.ui.base;

import android.support.v4.app.Fragment;

import com.tcorner.msheet.R;
import com.tcorner.msheet.util.DialogFactory;

/**
 * Base Fragment
 * Created by Tenten Ponce on 8/30/2017.
 */

public class BaseFragment extends Fragment implements MvpView {

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.error_generic).show();
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
