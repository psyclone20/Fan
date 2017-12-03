package com.psyclone.fan.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.psyclone.fan.R;
import com.psyclone.fan.helpers.DateTimeHelper;
import com.psyclone.fan.modules.GlideApp;
import com.psyclone.fan.utils.TVShow;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ShowsListAdapter extends ArrayAdapter<TVShow> {
    private Context mContext;
    private boolean displayChannel, displayTime;

    public ShowsListAdapter(Context context, ArrayList<TVShow> showSummaries, boolean displayChannel, boolean displayTime) {
        super(context, 0, showSummaries);
        mContext = context;
        this.displayChannel = displayChannel;
        this.displayTime = displayTime;
    }

    public String getShowName(int position)
    {
        return getItem(position).getName();
    }

    public String getShowPoster(int position) {
        return getItem(position).getPoster().replaceAll("_75", "");
    }

    public String getChannelName(int position) {
        return getItem(position).getChannel();
    }

    public String getShowTime(int position) {
        return getItem(position).getTime();
    }

    public String getShowDetails(int position)
    {
        return getItem(position).getDetails();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TVShow TVShow = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_show, parent, false);

            viewHolder.logo = convertView.findViewById(R.id.iv_list_show_logo);
            viewHolder.name = convertView.findViewById(R.id.tv_list_show_name);
            viewHolder.time = convertView.findViewById(R.id.tv_list_show_time);

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        GlideApp.with(mContext)
                .load(TVShow.getPoster())
                .placeholder(R.drawable.placeholder_thumb)
                .transition(withCrossFade())
                .into(viewHolder.logo);

        viewHolder.name.setText(TVShow.getName());

        if(displayChannel && displayTime)
            viewHolder.time.setText(TVShow.getChannel() + " - " + DateTimeHelper.covertTo12Hour(TVShow.getTime()));
        else if(displayChannel)
            viewHolder.time.setText(TVShow.getChannel());
        else if(displayTime)
            viewHolder.time.setText(DateTimeHelper.covertTo12Hour(TVShow.getTime()));

        return convertView;
    }

    private static class ViewHolder {
        ImageView logo;
        TextView name;
        TextView time;
    }
}
