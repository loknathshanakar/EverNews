package com.evernews.evernews;

/**
 * Created by Loknath Shankar on 3/5/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<ItemObject> listStorage;
    private Context context;

    public CustomAdapter(Context context, List<ItemObject> customizedListView) {
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
            convertView = layoutinflater.inflate(R.layout.single_news_item, parent, false);
            listViewHolder.thumbnail = (ImageView)convertView.findViewById(R.id.thumbnail);
            listViewHolder.source = (TextView)convertView.findViewById(R.id.source);
            listViewHolder.title = (TextView)convertView.findViewById(R.id.title);

            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        boolean picassodebugFlag=true;
        /*final Picasso mPicasso = Picasso.with(context);
        mPicasso.setIndicatorsEnabled(picassodebugFlag);
        mPicasso.load(R.drawable.news).fit().into(listViewHolder.thumbnail);
        //mPicasso.load(listStorage.get(position).getNewsImage()).fit().into(listViewHolder.thumbnail);
        mPicasso
                .load(listStorage.get(position).getNewsImage())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(listViewHolder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed

                        mPicasso.load(listStorage.get(position).getNewsImage())
                                .into(listViewHolder.thumbnail, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }
                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                        mPicasso.load(R.drawable.news).fit().into(listViewHolder.thumbnail);
                                    }
                                });
                    }
                });*/

        Glide.with(context).load(listStorage.get(position).getNewsImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).fitCenter().placeholder(R.mipmap.news).error(R.mipmap.news).into(listViewHolder.thumbnail);
        listViewHolder.source.setText(listStorage.get(position).getnewsName());
        listViewHolder.title.setText(listStorage.get(position).getnewsTitle());
        return convertView;
    }

    static class ViewHolder{
        ImageView thumbnail;
        TextView source;
        TextView title;
    }

}
