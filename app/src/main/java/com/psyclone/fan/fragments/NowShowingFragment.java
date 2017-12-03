package com.psyclone.fan.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.psyclone.fan.R;
import com.psyclone.fan.activities.ShowDetailsActivity;
import com.psyclone.fan.adapters.ShowsListAdapter;
import com.psyclone.fan.helpers.DateTimeHelper;
import com.psyclone.fan.providers.FanContentProvider;
import com.psyclone.fan.utils.TVShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NowShowingFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<TVShow> nowShowingList;
    private ShowsListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nowShowingList = new ArrayList<>();

        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = Uri.parse(FanContentProvider.URI_TV_GUIDE);
        Cursor cursor = contentResolver.query(uri, new String[]{FanContentProvider.COL_TV_GUIDE_CHANNEL_NAME, FanContentProvider.COL_TV_GUIDE_TODAY_GUIDE}, null, null, null);

        if(cursor != null) {
            int channelColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_CHANNEL_NAME);
            int guideColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_TODAY_GUIDE);

            while(cursor.moveToNext()) {
                try {
                    JSONArray showsList = new JSONArray(cursor.getString(guideColumnIndex));
                    JSONObject currentlyPlayingShow = showsList.getJSONObject(0);

                    for (int i = 1; i < showsList.length(); i++) {
                        JSONObject show = showsList.getJSONObject(i);

                        if(!DateTimeHelper.startsAfterCurrentTime(show.getString("showTime")))
                            currentlyPlayingShow = show;
                        else
                            break;
                    }

                    if(!DateTimeHelper.startsAfterCurrentTime(currentlyPlayingShow.getString("showTime")))
                        nowShowingList.add(new TVShow(
                                currentlyPlayingShow.getString("showThumb"),
                                currentlyPlayingShow.getString("showTitle"),
                                cursor.getString(channelColumnIndex),
                                currentlyPlayingShow.getString("showTime"),
                                currentlyPlayingShow.getString("showDetails")
                        ));
                } catch(NullPointerException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.empty_shows_placeholder, container, false);
        adapter = new ShowsListAdapter(getActivity().getApplicationContext(), nowShowingList, true, false);

        if(!adapter.isEmpty()) {
            view = inflater.inflate(R.layout.fragment_now_showing, container, false);
            ListView lv_my_shows = view.findViewById(R.id.lv_now_showing);
            lv_my_shows.setAdapter(adapter);
            lv_my_shows.setOnItemClickListener(this);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.fragment_title_now_showing));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
        intent.putExtra("name", adapter.getShowName(i));
        intent.putExtra("poster", adapter.getShowPoster(i));
        intent.putExtra("channel_name", adapter.getChannelName(i));
        intent.putExtra("time", adapter.getShowTime(i));
        intent.putExtra("details", adapter.getShowDetails(i));
        startActivity(intent);
    }
}
