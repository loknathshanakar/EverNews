package com.evernews.evernews;


public class ListItemObject {

    private String channelLogo;
    private String channelTitle;
    private String channelMeta;
    private String channelRSSID;
    private String categoryType;
    private String channelRSSURL;
    public ListItemObject(String channelLogo, String channelTitle, String channelMeta,String channelRSSID,String categoryType,String channelRSSURL) {
        this.channelLogo = channelLogo;
        this.channelTitle = channelTitle;
        this.channelMeta = channelMeta;
        this.channelRSSID = channelRSSID;
        this.categoryType = categoryType;
        this.channelRSSURL = channelRSSURL;
    }

    public String getChannelLogo() {
        return channelLogo;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getChannelMeta() {
        return channelMeta;
    }

    public String getChannelRSSID() {
        return channelRSSID;
    }

    public String getChannelRSSURL() {
        return channelRSSURL;
    }

    public String getCategoryType() {
        return categoryType;
    }

}
