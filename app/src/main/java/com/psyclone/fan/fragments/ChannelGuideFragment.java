package com.psyclone.fan.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.psyclone.fan.helpers.ChannelHelper;
import com.psyclone.fan.utils.TVShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelGuideFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ShowsListAdapter adapter;
    private String guide;
    private ArrayList<TVShow> showsList;
    private boolean hideExtras, combineContinuous;

    public static ChannelGuideFragment newInstance(String guide) {
        ChannelGuideFragment fragment = new ChannelGuideFragment();
        Bundle bundle = new Bundle();
        bundle.putString("guide", guide);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        guide = this.getArguments().getString("guide");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        hideExtras = settings.getBoolean("pref_hide_extras", true);
        combineContinuous = settings.getBoolean("pref_combine_continuous", false);

        try {
            JSONArray showsListJson = new JSONArray(guide);

            showsList = new ArrayList<>();

            TVShow previousShow = new TVShow(null, null, null, null);

            for(int i=0; i<showsListJson.length(); i++) {
                JSONObject show = showsListJson.getJSONObject(i);
                boolean shouldAdd = true;

                if(hideExtras)
                    for(String title : ChannelHelper.extras)
                        if(show.getString("showTitle").equals(title)) {
                            shouldAdd = false;
                            break;
                        }

                if(combineContinuous)
                    if(show.getString("showTitle").equals(previousShow.getName()))
                        shouldAdd = false;

                if(shouldAdd) {
                    showsList.add(new TVShow(show.getString("showThumb"), show.getString("showTitle"), show.getString("showTime"), show.getString("showDetails")));
                    previousShow.setAll(show.getString("showThumb"), show.getString("showTitle"), show.getString("showTime"), show.getString("showDetails"));
                }
            }
        } catch(NullPointerException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.empty_shows_placeholder, container, false);
        adapter = new ShowsListAdapter(getActivity().getApplicationContext(), showsList, false, true);

        try {
            if (!adapter.isEmpty()) {
                view = inflater.inflate(R.layout.fragment_channel_guide, container, false);
                ListView lv_shows = view.findViewById(R.id.lv_shows);
                lv_shows.setAdapter(adapter);
                lv_shows.setOnItemClickListener(this);
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
        intent.putExtra("name", adapter.getShowName(i));
        intent.putExtra("poster", adapter.getShowPoster(i));
        intent.putExtra("channel_name", getActivity().getIntent().getStringExtra("channel_name"));
        intent.putExtra("time", adapter.getShowTime(i));
        intent.putExtra("details", adapter.getShowDetails(i));
        startActivity(intent);
    }
}
