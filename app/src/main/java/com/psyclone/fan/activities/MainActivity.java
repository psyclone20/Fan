package com.psyclone.fan.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.psyclone.fan.R;
import com.psyclone.fan.fragments.MyShowsFragment;
import com.psyclone.fan.fragments.NowShowingFragment;
import com.psyclone.fan.fragments.TVGuideFragment;
import com.psyclone.fan.helpers.DateTimeHelper;
import com.psyclone.fan.helpers.InternetConnectionHelper;
import com.psyclone.fan.tasks.TVGuideRefreshTask;
import com.psyclone.fan.helpers.NotificationHelper;

import java.util.Set;

// A sample comment to see if Git sync works

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final long DRAWER_DELAY = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initial fragment to display based on normal launch or via app shortcuts
        if("com.psyclone.fan.nowshowing".equals(getIntent().getAction())) {
            navigationView.setCheckedItem(R.id.nav_now_showing);
            displaySelectedFragment(R.id.nav_now_showing);
        } else if("com.psyclone.fan.tvguide".equals(getIntent().getAction())) {
            navigationView.setCheckedItem(R.id.nav_tv_guide);
            displaySelectedFragment(R.id.nav_tv_guide);
        } else {
            navigationView.setCheckedItem(R.id.nav_my_shows);
            displaySelectedFragment(R.id.nav_my_shows);
        }

        // Check last refresh date
        SharedPreferences lastRefresh = getSharedPreferences("last_refresh", Context.MODE_PRIVATE);
        int refreshDate = Integer.parseInt(lastRefresh.getString("refresh_date", "19700101"));
        if(Integer.parseInt(DateTimeHelper.reverseDate(DateTimeHelper.getTodaysDateString())) > refreshDate) {
            CoordinatorLayout cl_snackbar = (CoordinatorLayout) findViewById(R.id.cl_snackbar);
            Snackbar snackbar = Snackbar
                    .make(cl_snackbar, getString(R.string.snackbar_not_refreshed), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snackbar_refresh_now), snackbarClickListener);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedFragment(item.getItemId());
        return true;
    }

    private void displaySelectedFragment(final int itemID) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(itemID == R.id.nav_settings)
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                else {
                    Fragment fragment = null;

                    switch (itemID) {
                        case R.id.nav_my_shows:
                            fragment = new MyShowsFragment();
                            break;
                        case R.id.nav_now_showing:
                            fragment = new NowShowingFragment();
                            break;
                        case R.id.nav_tv_guide:
                            fragment = new TVGuideFragment();
                            break;
                    }

                    if(fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        ft.replace(R.id.content_frame, fragment).commit();
                    }
                }
            }
        }, DRAWER_DELAY);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    View.OnClickListener snackbarClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            refreshGuide();
        }
    };

    void refreshGuide() {
        if(InternetConnectionHelper.isConnected(MainActivity.this)) {
            TVGuideRefreshTask task = new TVGuideRefreshTask(MainActivity.this) {
                CoordinatorLayout cl_snackbar;
                Snackbar progressSnackbar;

                @Override
                protected void onPreExecute() {
                    cl_snackbar = findViewById(R.id.cl_snackbar);
                    progressSnackbar = Snackbar
                            .make(cl_snackbar, getString(R.string.snackbar_refreshing), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.snackbar_dismiss), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Do nothing
                                }
                            });
                    progressSnackbar.show();

                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(Set<String> shows) {
                    if (refreshSuccessful) {
                        new NotificationHelper(MainActivity.this).showGuideRefreshNotification(shows);
                        progressSnackbar.setText(getString(R.string.snackbar_refresh_complete));
                        MainActivity.this.recreate();
                    }
                }
            };
            task.execute();
        } else {
            CoordinatorLayout cl_snackbar = findViewById(R.id.cl_snackbar);
            Snackbar connectionSnackbar = Snackbar
                    .make(cl_snackbar, getString(R.string.snackbar_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snackbar_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            refreshGuide();
                        }
                    });
            connectionSnackbar.show();
        }
    }
}
