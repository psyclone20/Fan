package com.psyclone.fan.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.psyclone.fan.R;
import com.psyclone.fan.providers.FanContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class GuideSaveHelper {
    public static Set<String> refreshDBGuide(Context mContext) {
        Uri tvGuideUri = Uri.parse(FanContentProvider.URI_TV_GUIDE);
        Uri myShowsUri = Uri.parse(FanContentProvider.URI_MY_SHOWS);
        ContentResolver contentResolver = mContext.getContentResolver();

        Cursor cursor = contentResolver.query(tvGuideUri, null, null, null, null);

        String today_date = DateTimeHelper.reverseDateWithHyphens(DateTimeHelper.getTodaysDateString());
        String tomorrow_date = DateTimeHelper.reverseDateWithHyphens(DateTimeHelper.getTomorrowsDateString());

        Set<String> myShows = new HashSet<>();

        SharedPreferences lastRefresh = mContext.getSharedPreferences("last_refresh", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = lastRefresh.edit();
        int channelsRefreshed = 0;

        if(cursor != null) {
            int channelNameColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_CHANNEL_NAME);
            int channelCodeColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_CHANNEL_CODE);
            int todayDateColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_TODAY_DATE);
            int todayGuideColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_TODAY_GUIDE);
            int tomorrowDateColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_TOMORROW_DATE);

            String selection = FanContentProvider.COL_TV_GUIDE_CHANNEL_NAME + " = ?";

            contentResolver.delete(myShowsUri, null, null);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
            String[] favoriteActors = settings.getString("pref_favorite_actors", mContext.getString(R.string.fav_actors_default)).split("\n");
            String[] favoriteShows = settings.getString("pref_favorite_shows", mContext.getString(R.string.fav_shows_default)).split("\n");

            while (cursor.moveToNext()) {
                String[] selectionArgs = {cursor.getString(channelNameColumnIndex)};
                JSONArray shows = null;

                // Shows for today
                if(cursor.getString(todayDateColumnIndex) == null ||
                        cursor.getString(todayDateColumnIndex).length() == 0 ||
                        Integer.valueOf(cursor.getString(todayDateColumnIndex).replaceAll("-", "")) < Integer.valueOf(today_date.replaceAll("-", "")))
                    // Fetch guide from the Internet
                    shows = GuideFetchHelper.getShows(cursor.getString(channelNameColumnIndex), cursor.getInt(channelCodeColumnIndex), today_date);
                else
                    // Get guide already stored in the database
                    try {
                        shows = new JSONArray(cursor.getString(todayGuideColumnIndex));
                    } catch (JSONException e) {
                        System.out.println("Fan: JSONException - Could not retrieve saved guide for " + cursor.getString(channelNameColumnIndex));
                    }

                if (shows != null && shows.toString().length() > 0) {
                    try {
                        for (int i = 0; i < shows.length(); i++) {
                            JSONObject show = shows.getJSONObject(i);
                            String showTitle = show.getString("showTitle");
                            boolean isMyShow = false;

                            try {
                                String actor = show.getJSONObject("showDetails").getString("Actor");
                                for (String favoriteActor : favoriteActors)
                                    if (actor.trim().toUpperCase().contains(favoriteActor.trim().toUpperCase())) {
                                        isMyShow = true;
                                        break;
                                    }
                            } catch(JSONException e) {
                                System.out.println("Fan: JSONException - No actor for " + showTitle);
                            }

                            if (!isMyShow) {
                                for (String favoriteShow : favoriteShows)
                                    if (showTitle.trim().toUpperCase().contains(favoriteShow.trim().toUpperCase())) {
                                        isMyShow = true;
                                        break;
                                    }
                            }

                            if (isMyShow) {
                                myShows.add(showTitle);

                                ContentValues cv = new ContentValues();
                                cv.put("channel", cursor.getString(channelNameColumnIndex));
                                cv.put("time", show.getString("showTime"));
                                cv.put("details", show.toString());

                                contentResolver.insert(myShowsUri, cv);
                            }
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                    ContentValues cv = new ContentValues();
                    cv.put("today_date", today_date);
                    cv.put("today_guide", shows.toString());

                    contentResolver.update(tvGuideUri, cv, selection, selectionArgs);
                    editor.putInt("channels_refreshed", ++channelsRefreshed);
                }

                // Shows for tomorrow
                if(settings.getBoolean("next_day_refresh", false))
                    if(cursor.getString(tomorrowDateColumnIndex) == null ||
                            cursor.getString(tomorrowDateColumnIndex).length() == 0 ||
                            Integer.valueOf(cursor.getString(tomorrowDateColumnIndex).replaceAll("-", "")) < Integer.valueOf(tomorrow_date.replaceAll("-", ""))) {
                        // Fetch guide from the Internet
                        shows = GuideFetchHelper.getShows(cursor.getString(channelNameColumnIndex), cursor.getInt(channelCodeColumnIndex), tomorrow_date);

                        if (shows != null && shows.toString().length() > 0) {
                            ContentValues cv = new ContentValues();
                            cv.put("tomorrow_date", tomorrow_date);
                            cv.put("tomorrow_guide", shows.toString());

                            contentResolver.update(tvGuideUri, cv, selection, selectionArgs);
                        }
                    }
            }
        }

        if(channelsRefreshed == ChannelHelper.channels.length) {
            editor.putString("refresh_date", DateTimeHelper.reverseDate(DateTimeHelper.getTodaysDateString()));
            Glide.get(mContext).clearDiskCache(); // Clear Glide cache
        }
        editor.commit();

        return myShows;
    }
}
