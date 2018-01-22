package com.tcorner.msheet.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tcorner.msheet.R;
import com.tcorner.msheet.ui.base.BaseActivity;
import com.tcorner.msheet.ui.library.LibraryFragment;
import com.tcorner.msheet.ui.settings.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Drawer for all activities
 * Created by Exequiel Egbert V. Ponce on 7/1/2016.
 */
public class DrawerActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int LIBRARY = 0;

    @BindView(R.id.nav_view)
    NavigationView navView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);

        initViews();
        onCreateDrawer();
        onNavigationItemSelected(navView.getMenu().getItem(LIBRARY));
        navView.getMenu().getItem(LIBRARY).setChecked(true);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(navView)) {
            drawer.closeDrawer(navView);
        } else {
            if (navView.getMenu().getItem(LIBRARY).isChecked()) {
                super.onBackPressed();
            } else {
                setupFragment(LibraryFragment.class, getApplicationContext().getString(R.string.library));
                navView.getMenu().getItem(LIBRARY).setChecked(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawer.isDrawerOpen(GravityCompat.START)) { //toggle drawer open close
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        Class fragmentClass = null;
        String title = null;

        if (item.isChecked()) { //check if they are on the fragment already
            return false;
        }

        switch (item.getItemId()) {
            case R.id.nav_library:
                fragmentClass = LibraryFragment.class;
                title = getApplicationContext().getString(R.string.library);
                break;
            case R.id.nav_collections:
                fragmentClass = LibraryFragment.class;
                title = getApplicationContext().getString(R.string.collections);
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                title = getApplicationContext().getString(R.string.settings);
                break;
            case R.id.nav_about:
                fragmentClass = LibraryFragment.class;
                title = getApplicationContext().getString(R.string.about);
                break;
            default:
                break;
        }

        if (fragmentClass != null) setupFragment(fragmentClass, title);

        return true;
    }

    private void initViews() {
        setSupportActionBar(toolbar);
    }

    protected void onCreateDrawer() {
        toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        drawer.addDrawerListener(toggle);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START); //add shadow

        toggle.syncState();

        //setup the name on navigation drawer header
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);
    }

    private void setupFragment(final Class fragmentClass, final String title) {
        Fragment fragment = null;

        try {
            if (fragmentClass != null) {
                fragment = (Fragment) fragmentClass.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);

        fragmentTransaction.commit();

        setTitle(title);
    }
}
