package com.psyclone.fan.helpers;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.psyclone.fan.jobs.TVGuideRefreshJob;

import java.util.concurrent.TimeUnit;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;

public class JobHelper {
    private static int JOB_ID = 2810;
    private static int HOURS_BETWEEN_REFRESH = 6;

    public static void scheduleJob(Context mContext) {
        JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(JOB_ID, new ComponentName(mContext, TVGuideRefreshJob.class))
                .setPeriodic(TimeUnit.HOURS.toMillis(HOURS_BETWEEN_REFRESH))
                .setPersisted(true)
                .setRequiredNetworkType(NETWORK_TYPE_ANY)
                .build());
    }
}
