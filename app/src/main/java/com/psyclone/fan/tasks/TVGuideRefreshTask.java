package com.psyclone.fan.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.psyclone.fan.helpers.DateTimeHelper;
import com.psyclone.fan.helpers.GuideFetchHelper;
import com.psyclone.fan.helpers.GuideSaveHelper;

import java.util.Set;

public class TVGuideRefreshTask extends AsyncTask<Void, Void, Set<String>> {
    private Context mContext;
    protected boolean refreshSuccessful;

    protected TVGuideRefreshTask(Context context) {
        mContext = context;
    }

    @Override
    protected Set<String> doInBackground(Void... voids) {
        SharedPreferences lastRefresh = mContext.getSharedPreferences("last_refresh", Context.MODE_PRIVATE);
        int refreshDate = Integer.parseInt(lastRefresh.getString("refresh_date", "19700101"));
        int todayDate = Integer.parseInt(DateTimeHelper.reverseDate(DateTimeHelper.getTodaysDateString()));

        if(todayDate > refreshDate) {
            Set<String> shows = GuideSaveHelper.refreshDBGuide(mContext);
            SharedPreferences newLastRefresh = mContext.getSharedPreferences("last_refresh", Context.MODE_PRIVATE);
            if(newLastRefresh.getString("refresh_date", "19700101").equals(String.valueOf(todayDate))) {
                refreshSuccessful = true;
                return shows;
            }
            return null;
        }
        else
            return null;
    }
}
