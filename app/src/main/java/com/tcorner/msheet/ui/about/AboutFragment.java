package com.tcorner.msheet.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tcorner.msheet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * About the application
 * Created by Tenten Ponce on 11/03/2017.
 */

public class AboutFragment extends Fragment {

    private static final String TCORNER = "https://www.facebook.com/tencorner/";

    @BindView(R.id.coor_about)
    CoordinatorLayout coorAbout;

    @BindView(R.id.linear_developer)
    LinearLayout linearDeveloper;

    @BindView(R.id.tv_about_message)
    AppCompatTextView tvAboutMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        tvAboutMessage.setText(Html.fromHtml(getString(R.string.about_message)));
        linearDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(TCORNER));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(coorAbout, "Please try again later.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
