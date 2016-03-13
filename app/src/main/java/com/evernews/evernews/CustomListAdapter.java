package com.evernews.evernews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Loknath Shankar on 3/12/2016.
 */
public class CustomListAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<ListItemObject> listStorage;
    private Context context;

    public CustomListAdapter(Context context, List<ListItemObject> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.list_view_single_item, parent, false);
            listViewHolder.thumbnail = (ImageView)convertView.findViewById(R.id.list_image);
            listViewHolder.channelMeta = (TextView)convertView.findViewById(R.id.channelMeta);
            listViewHolder.channelTitle = (TextView)convertView.findViewById(R.id.channelTitle);

            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        Glide.with(context).load(listStorage.get(position).getChannelLogo()).placeholder(R.mipmap.ic_launcher).crossFade().fitCenter().into(listViewHolder.thumbnail);
        listViewHolder.channelMeta.setText(listStorage.get(position).getChannelMeta());
        listViewHolder.channelTitle.setText(listStorage.get(position).getChannelTitle());
        return convertView;
    }

    static class ViewHolder{
        ImageView thumbnail;
        TextView channelMeta;
        TextView channelTitle;
    }
}
