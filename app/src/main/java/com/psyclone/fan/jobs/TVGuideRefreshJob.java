package com.psyclone.fan.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.psyclone.fan.tasks.TVGuideRefreshTask;
import com.psyclone.fan.helpers.NotificationHelper;

import java.util.Set;

public class TVGuideRefreshJob extends JobService {
    private TVGuideRefreshTask task;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        task = new TVGuideRefreshTask(this) {
            @Override
            protected void onPostExecute(Set<String> shows) {
                if(refreshSuccessful) {
                    new NotificationHelper(TVGuideRefreshJob.this).showGuideRefreshNotification(shows);
                    jobFinished(jobParameters, false);
                }
                else
                    jobFinished(jobParameters, true);
            }
        };
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
