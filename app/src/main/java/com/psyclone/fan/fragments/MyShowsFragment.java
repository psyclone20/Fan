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
import com.psyclone.fan.providers.FanContentProvider;
import com.psyclone.fan.utils.TVShow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyShowsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<TVShow> myShowsList;
    private ShowsListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myShowsList = new ArrayList<>();

        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = Uri.parse(FanContentProvider.URI_MY_SHOWS);
        Cursor cursor = contentResolver.query(uri, null, null, null, FanContentProvider.COL_MY_SHOWS_TIME);

        if(cursor != null) {
            int channelColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_MY_SHOWS_CHANNEL);
            int timeColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_MY_SHOWS_TIME);
            int detailsColumnIndex = cursor.getColumnIndex(FanContentProvider.COL_MY_SHOWS_DETAILS);

            while(cursor.moveToNext()) {
                try {
                    JSONObject json = new JSONObject(cursor.getString(detailsColumnIndex));
                    myShowsList.add(new TVShow(
                            json.getString("showThumb"),
                            json.getString("showTitle"),
                            cursor.getString(channelColumnIndex),
                            cursor.getString(timeColumnIndex),
                            json.getString("showDetails")
                    ));
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.empty_shows_placeholder, container, false);
        adapter = new ShowsListAdapter(getActivity().getApplicationContext(), myShowsList, true, true);

        if(!adapter.isEmpty()) {
            view = inflater.inflate(R.layout.fragment_my_shows, container, false);
            ListView lv_my_shows = view.findViewById(R.id.lv_my_shows);
            lv_my_shows.setAdapter(adapter);
            lv_my_shows.setOnItemClickListener(this);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.fragment_title_my_shows));
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
