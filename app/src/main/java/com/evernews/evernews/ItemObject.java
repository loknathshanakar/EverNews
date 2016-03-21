package com.evernews.evernews;


public class ItemObject {

    private String newsImage;
    private String newsTitle;
    private String newsName;
    private String newsID;
    private String categoryID;
    private String fullText;
    private String newsURL;

    public ItemObject(String newsImage, String newsTitle, String newsName,String newsID,String categoryID,String fullText,String newsURL) {
        this.newsImage = newsImage;
        this.newsTitle = newsTitle;
        this.newsName = newsName;
        this.newsID = newsID;
        this.categoryID = categoryID;
        this.fullText = fullText;
        this.newsURL = newsURL;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public String getnewsTitle() {
        return newsTitle;
    }

    public String getnewsName() {
        return newsName;
    }

    public String getNewsID() {
        return newsID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getFullText() {
        return fullText;
    }

    public String getNewsURL() {
        return newsURL;
    }

}
