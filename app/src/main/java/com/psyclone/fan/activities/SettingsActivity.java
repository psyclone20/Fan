package com.psyclone.fan.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.psyclone.fan.fragments.SettingsFragment;
import com.psyclone.fan.helpers.JobHelper;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment(), "SettingsFragment").commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        if(settings.getBoolean("auto_refresh", false)) {
            List<JobInfo> jobs = jobScheduler.getAllPendingJobs();
            if(jobs.size() == 0)
                JobHelper.scheduleJob(this); // Schedule new job if no jobs found
        } else
            jobScheduler.cancelAll();

        super.onDestroy();
    }
}
