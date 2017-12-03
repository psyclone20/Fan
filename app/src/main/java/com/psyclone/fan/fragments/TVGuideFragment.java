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
import android.widget.TextView;

import com.psyclone.fan.R;
import com.psyclone.fan.activities.ChannelGuideActivity;
import com.psyclone.fan.adapters.ChannelCursorAdapter;
import com.psyclone.fan.providers.FanContentProvider;

public class TVGuideFragment extends Fragment implements AdapterView.OnItemClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tv_guide, container, false);
        ListView lv_channels = view.findViewById(R.id.lv_channels);

        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = Uri.parse(FanContentProvider.URI_TV_GUIDE);
        String[] columns = {FanContentProvider.COL_TV_GUIDE_ID, FanContentProvider.COL_TV_GUIDE_CHANNEL_LOGO, FanContentProvider.COL_TV_GUIDE_CHANNEL_NAME, FanContentProvider.COL_TV_GUIDE_TODAY_DATE};

        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                int layoutResId = R.layout.list_item_channel;
                String[] cursorColumns = {FanContentProvider.COL_TV_GUIDE_CHANNEL_NAME};
                int[] resId = {R.id.tv_list_channel_name};

                ChannelCursorAdapter cursorAdapter = new ChannelCursorAdapter(getActivity(), layoutResId, cursor, cursorColumns, resId, 0);
                lv_channels.setAdapter(cursorAdapter);
                lv_channels.setOnItemClickListener(this);
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.fragment_title_tv_guide));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView tv_list_channel_name = view.findViewById(R.id.tv_list_channel_name);
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = Uri.parse(FanContentProvider.URI_TV_GUIDE);

        String[] columns = {FanContentProvider.COL_TV_GUIDE_TODAY_GUIDE, FanContentProvider.COL_TV_GUIDE_TOMORROW_GUIDE};
        String selection = FanContentProvider.COL_TV_GUIDE_CHANNEL_NAME + " = ?";
        String[] selectionArgs = {tv_list_channel_name.getText().toString()};

        Cursor cursor = contentResolver.query(uri, columns, selection, selectionArgs, null);
        if(cursor != null) {
            cursor.moveToNext();

            Intent intent = new Intent(getActivity(), ChannelGuideActivity.class);
            intent.putExtra("channel_name", tv_list_channel_name.getText());
            intent.putExtra("today_guide", cursor.getString(cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_TODAY_GUIDE)));
            intent.putExtra("tomorrow_guide", cursor.getString(cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_TOMORROW_GUIDE)));

            cursor.close();

            startActivity(intent);
        }
    }
}
