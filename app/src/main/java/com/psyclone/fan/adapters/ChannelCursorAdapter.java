package com.psyclone.fan.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.psyclone.fan.R;
import com.psyclone.fan.modules.GlideApp;
import com.psyclone.fan.providers.FanContentProvider;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ChannelCursorAdapter extends SimpleCursorAdapter {
    public ChannelCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        ImageView iv_list_channel_logo = view.findViewById(R.id.iv_list_channel_logo);
        TextView tv_list_channel_name = view.findViewById(R.id.tv_list_channel_name);

        GlideApp.with(context)
                .load(cursor.getString(cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_CHANNEL_LOGO)))
                .placeholder(R.drawable.placeholder_thumb)
                .transition(withCrossFade())
                .into(iv_list_channel_logo);

        tv_list_channel_name.setText(cursor.getString(cursor.getColumnIndex(FanContentProvider.COL_TV_GUIDE_CHANNEL_NAME)));
    }
}
