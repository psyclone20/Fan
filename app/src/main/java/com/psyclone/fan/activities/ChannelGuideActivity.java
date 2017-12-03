package com.psyclone.fan.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.psyclone.fan.R;
import com.psyclone.fan.fragments.ChannelGuideFragment;

public class ChannelGuideActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_guide);

        if((getSupportActionBar()) != null) {
            getSupportActionBar().setElevation(0); // No shadow between action bar and tabs
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("channel_name"));
        }

        viewPager = (ViewPager)findViewById(R.id.viewPager_channel_guide);
        tabLayout =(TabLayout)findViewById(R.id.tablayout_channel_guide);

        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyAdapter extends FragmentPagerAdapter {
        MyAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ChannelGuideFragment todayGuide = ChannelGuideFragment.newInstance(getIntent().getStringExtra("today_guide"));
            ChannelGuideFragment tomorrowGuide =  ChannelGuideFragment.newInstance(getIntent().getStringExtra("tomorrow_guide"));
            switch (position) {
                case 0:
                    return todayGuide;
                case 1:
                    return tomorrowGuide;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:return "Today";
                case 1:return "Tomorrow";
            }
            return null;
        }
    }
}
