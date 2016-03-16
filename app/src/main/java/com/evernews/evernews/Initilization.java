package com.evernews.evernews;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;

public class Initilization extends AppCompatActivity {
    public static final int CategoryId = 0;
    public static final int Category = 1;
    public static final int DisplayOrder = 2;
    public static final int RSSTitle = 3;
    public static final int RSSURL = 4;
    public static final int RSSUrlId = 5;
    public static final int NewsId =6;
    public static final int NewsTitle = 7;
    public static final int Summary = 8;
    public static final int NewsImage = 9;
    public static final int NewsDate = 10;
    public static final int NewsDisplayOrder = 11;
    public static final int CategoryorNews = 12;
    public static final int FullText = 13;
    public static final int NewsUrl = 14;
    //Database related stuff
    public static final String TABLE_NAME = "FULLNEWS";
    public static final String CATEGORYID = "CategoryId";
    public static final String CATEGORYNAME = "Category";
    public static final String DISPLAYORDER = "DisplayOrder";
    public static final String RSSTITLE = "RSSTitle";
    public static final String RSSURL_DB = "RSSURL";
    public static final String RSSURLID = "RSSUrlId";
    public static final String NEWSID = "NewsId";
    public static final String NEWSTITLE = "NewsTitle";
    public static final String SUMMARY = "Summary";
    public static final String NEWSIMAGE = "NewsImage";
    public static final String SUBTITLE = "subtitle";
    public static final String NEWSDATE = "NewsDate";
    public static final String NEWSDISPLAYORDER = "NewsDisplayOrder";
    public static final String CATEGORYORNEWS = "CategoryorNews";
    public static final String FULLTEXT = "FullText";
    public static final String NEWSURL = "NewsUrl";
    public static final String RESERVED_2 = "RESERVED_2";
    public static final String RESERVED_3 = "RESERVED_3";
    public static final String RESERVED_4 = "RESERVED_4";
    public static long numRows=0;
    public static String SQL_CREATE_ENTRIES ="";
    SQLiteDatabase db;
    public static String DB_PATH = "/data/data/com.evernews.evernews/databases/";
    public static String DB_NAME = TABLE_NAME;



    private static SharedPreferences sharedpreferences;
    public static String androidId="";
    public static int timeout=10000;
    public static String resultArray[][]=new String[10000][15];
    public static String newsCategories[][]=new String[100][2];
    public static int resultArrayLength=0;
    public static int newsCategoryLength=0;
    public static ArrayList<String> addOnList = new ArrayList <String>(10);
    public static ArrayList<String> addOnListTOCompare = new ArrayList <String>(10);
    public static ArrayList<String> getAddOnListRSSID = new ArrayList <String>(10);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initilization);
        getSupportActionBar().hide();
        sharedpreferences = getApplicationContext().getSharedPreferences(Main.USERLOGINDETAILS, Context.MODE_PRIVATE);
        new GetNewsTask().execute();
    }

    class GetNewsTask extends AsyncTask<Void,Integer,Void>
    {
        int goCode=0;
        String content;
        int ExceptionCode=0;
        TextView tv=(TextView)findViewById(R.id.response);
        @Override
        protected void onPreExecute()
        {
            tv.setText("Connecting...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            if(progress[0]==0)
                tv.setText("Downloading news for the first time...please wait");
            if(progress[0]==1)
                tv.setText("Formatting news...please wait");
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            SQL_CREATE_ENTRIES = "CREATE TABLE  IF NOT EXISTS "+TABLE_NAME+"("
                    + CATEGORYID + " TEXT ,  "
                    + CATEGORYNAME + " TEXT , " + DISPLAYORDER
                    + " TEXT , " + RSSTITLE + " TEXT , "
                    + RSSURL_DB + " TEXT , " + RSSURLID
                    + " TEXT , " + NEWSID + " TEXT UNIQUE, "
                    + NEWSTITLE + " TEXT , " + SUMMARY
                    + " TEXT , " + NEWSIMAGE + " TEXT , " + NEWSDATE
                    + " TEXT , " + NEWSDISPLAYORDER
                    + " TEXT ," + CATEGORYORNEWS  + " TEXT , " + FULLTEXT
                    + " TEXT , " + NEWSURL
                    + " TEXT ," + RESERVED_2  + " TEXT , " + RESERVED_3
                    + " TEXT , " + RESERVED_4 + " TEXT );";

            db= openOrCreateDatabase(TABLE_NAME, MODE_PRIVATE, null);
            db.execSQL(SQL_CREATE_ENTRIES);
            numRows = DatabaseUtils.queryNumEntries(db,TABLE_NAME);
            if(numRows<50) {
                try {
                    publishProgress(0);
                    androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    String fetchLink = "http://rssapi.psweb.in/everapi.asmx/LoadXMLDefaultNews?AndroidId=" + androidId;//Over ride but should be Main.androidId
                    goCode=1;
                    content = Jsoup.connect(fetchLink).ignoreContentType(true).timeout(timeout).execute().body();
                } catch (Exception e) {
                    if (e instanceof SocketTimeoutException) {
                        ExceptionCode = 1;
                        return null;
                    }
                    if (e instanceof HttpStatusException) {
                        ExceptionCode = 2;
                        return null;
                    }
                    e.printStackTrace();
                }
            }else{
                goCode=2;
            }
            db.close();
            publishProgress(1);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(ExceptionCode>0) {
                if(ExceptionCode==1)
                    Toast.makeText(getApplicationContext(),"Please check your internet connection and try again",Toast.LENGTH_SHORT).show();
                if(ExceptionCode==2)
                    Toast.makeText(getApplicationContext(),"Some server related issue occurred..please try again later",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(),"Unknown exception,program will now exit",Toast.LENGTH_SHORT).show();
                finish();
            }
                if((content!=null && goCode==1) || (content!=null && sharedpreferences.getBoolean(Main.NEWCHANNELADDED,false)))
                {
                    String result = content.toString().replaceAll("&lt;", "<").replaceAll("&gt;",">").replaceAll("&amp;","&");
                    Log.d("response", result);
                    //after getting the response we have to parse it
                    parseResults(result);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(Main.NEWCHANNELADDED, false);
                    Intent main=new Intent(Initilization.this,Main.class);
                    startActivity(main);
                    finish();
                }
                else if(goCode==2){
                    offlineparseResults();
                    Intent main=new Intent(Initilization.this,Main.class);
                    startActivity(main);
                    finish();
                }
                super.onPostExecute(aVoid);
            }
        }
    public void parseResults(String response)
    {
        ContentValues values = new ContentValues();
        String path=DB_PATH+DB_NAME;
        db=SQLiteDatabase.openDatabase(path,null,0);
        String categories[][]=new String[1000][2];
        for(int i=0;i<100;i++){
            Initilization.newsCategories[i][0]="";
            Initilization.newsCategories[i][1]="";
        }
        for(int i=0;i<1000;i++){
            categories[i][0]="";
            categories[i][1]="";
        }
        Initilization.addOnList.clear();
        Initilization.addOnListTOCompare.clear();
        for (int i = 0; i < 20; i++) {
            Initilization.addOnList.add("");
            Initilization.addOnListTOCompare.add("");
        }

        int index=0;
        String currentNewsCategory="";
        Initilization.newsCategories[1][1]="EverYou";
        Initilization.newsCategories[2][1]="YouView";
        Initilization.newsCategories[1][0]="999";
        Initilization.newsCategories[2][0]="999";
        XMLDOMParser parser = new XMLDOMParser();
        InputStream stream = new ByteArrayInputStream(response.getBytes());
        Document doc = parser.getDocument(stream);
        NodeList nodeList = doc.getElementsByTagName("Table");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            Initilization.resultArray[i][Initilization.CategoryId] = (parser.getValue(e, "CategoryId"));
            values.put(CATEGORYID, Initilization.resultArray[i][Initilization.CategoryId]);             //lets add data to database

            Initilization.resultArray[i][Initilization.Category] = (parser.getValue(e, "Category"));
            values.put(CATEGORYNAME, Initilization.resultArray[i][Initilization.Category]);

            Initilization.resultArray[i][Initilization.DisplayOrder] = (parser.getValue(e, "DisplayOrder"));
            values.put(DISPLAYORDER, Initilization.resultArray[i][Initilization.DisplayOrder]);

            Initilization.resultArray[i][Initilization.RSSTitle] = (parser.getValue(e, "RSSTitle"));
            values.put(RSSTITLE, Initilization.resultArray[i][Initilization.RSSTitle]);

            Initilization.resultArray[i][Initilization.RSSURL] = (parser.getValue(e, "RSSURL"));
            values.put(RSSURL_DB, Initilization.resultArray[i][Initilization.RSSURL]);

            Initilization.resultArray[i][Initilization.RSSUrlId] = (parser.getValue(e, "RSSUrlId"));
            values.put(RSSURLID, Initilization.resultArray[i][Initilization.RSSUrlId]);

            Initilization.resultArray[i][Initilization.NewsId] = (parser.getValue(e, "NewsId"));
            values.put(NEWSID, Initilization.resultArray[i][Initilization.NewsId]);

            Initilization.resultArray[i][Initilization.NewsTitle] = (parser.getValue(e, "NewsTitle"));
            values.put(NEWSTITLE, Initilization.resultArray[i][Initilization.NewsTitle]);

            Initilization.resultArray[i][Initilization.Summary] = (parser.getValue(e, "Summary"));
            values.put(SUMMARY, Initilization.resultArray[i][Initilization.Summary]);

            Initilization.resultArray[i][Initilization.NewsImage] = (parser.getValue(e, "NewsImage"));
            values.put(NEWSIMAGE, Initilization.resultArray[i][Initilization.NewsImage]);

            Initilization.resultArray[i][Initilization.NewsDate] = (parser.getValue(e, "NewsDate"));
            values.put(NEWSDATE, Initilization.resultArray[i][Initilization.NewsDate]);

            Initilization.resultArray[i][Initilization.NewsDisplayOrder] = (parser.getValue(e, "NewsDisplayOrder"));
            values.put(NEWSDISPLAYORDER, Initilization.resultArray[i][Initilization.NewsDisplayOrder]);


            Initilization.resultArray[i][Initilization.FullText] = (parser.getValue(e, "FullText"));
            values.put(FULLTEXT, Initilization.resultArray[i][Initilization.FullText]);

            Initilization.resultArray[i][Initilization.NewsUrl] = (parser.getValue(e, "NewsURL"));
            values.put(NEWSURL, Initilization.resultArray[i][Initilization.NewsUrl]);

            Initilization.resultArray[i][Initilization.CategoryorNews] = (parser.getValue(e, "CategoryorNews"));
            values.put(CATEGORYORNEWS, Initilization.resultArray[i][Initilization.CategoryorNews]);

            currentNewsCategory=Initilization.resultArray[i][Initilization.DisplayOrder];
            db.insert(TABLE_NAME, null, values);

            int cuDispOrder=0;
            try {
                cuDispOrder = Integer.parseInt(currentNewsCategory);
            }catch (Exception ee){}
            if(cuDispOrder==0){
            }
            if(!Initilization.addOnListTOCompare.contains(Initilization.resultArray[i][Initilization.Category]) && cuDispOrder!=0){
                Initilization.addOnList.add(cuDispOrder,Initilization.resultArray[i][Initilization.Category]);
                Initilization.addOnListTOCompare.add(cuDispOrder,Initilization.resultArray[i][Initilization.Category]);
            }
            if(!Initilization.addOnListTOCompare.contains(Initilization.resultArray[i][Initilization.Category]) && cuDispOrder==0){
                Initilization.addOnList.add(Initilization.resultArray[i][Initilization.Category]);
                Initilization.addOnListTOCompare.add(cuDispOrder,Initilization.resultArray[i][Initilization.Category]);
            }
            //categories[cuDispOrder][1]=Initilization.resultArray[i][Initilization.Category];
            //categories[cuDispOrder][0]=Initilization.resultArray[i][Initilization.DisplayOrder];
            Initilization.resultArrayLength=i;
        }

        db.close(); // Closing database connection

        Initilization.addOnList.add(2, "EverYou");
        Initilization.addOnList.add(3,"YouView");
        for(int i=0;i<Initilization.addOnList.size();){
            if(Initilization.addOnList.get(i).length()<2) {
                Initilization.addOnList.remove(i);
                i--;
            }
            i++;
        }
        Initilization.addOnListTOCompare.clear();
        /*for(int i=0;i<1000;i++) {
            if(categories[i][0].isEmpty())
                continue;
            for(int j=0;j<100;j++){
                if(Initilization.newsCategories[j][0].isEmpty() &&(i-1)>=0){        //cat id can never be 0
                    Initilization.newsCategories[j][0]=categories[i][0];
                    Initilization.newsCategories[j][1]=categories[i][1];
                    break;
                }
            }
        }
        for(int i=0;i<100;i++) {
            if (Initilization.newsCategories[i][0].isEmpty()) {        //cat id can never be 0
                continue;
            } else
                Initilization.newsCategoryLength++;
        }*/
    }


    public void offlineparseResults()
    {
        ContentValues values = new ContentValues();
        String path=DB_PATH+DB_NAME;
        db=SQLiteDatabase.openDatabase(path,null,0);
        String col[] = { CATEGORYID , CATEGORYNAME ,DISPLAYORDER ,RSSTITLE  ,RSSURL_DB,RSSURLID ,NEWSID ,NEWSTITLE ,SUMMARY ,NEWSIMAGE  ,NEWSDATE,NEWSDISPLAYORDER ,CATEGORYORNEWS ,FULLTEXT,NEWSURL };
        Cursor cur = db.query(TABLE_NAME, col, null, null, null, null, null);
        Integer num = cur.getCount();
        setTitle(Integer.toString(num));

        String categories[][]=new String[1000][2];
        for(int i=0;i<100;i++){
            Initilization.newsCategories[i][0]="";
            Initilization.newsCategories[i][1]="";
        }
        for(int i=0;i<1000;i++){
            categories[i][0]="";
            categories[i][1]="";
        }
        Initilization.addOnList.clear();
        for (int i = 0; i < 20; i++) {
            Initilization.addOnList.add("");
        }

        int index=0;
        String currentNewsCategory="";
        Initilization.newsCategories[1][1]="EverYou";
        Initilization.newsCategories[2][1]="YouView";
        Initilization.newsCategories[1][0]="999";
        Initilization.newsCategories[2][0]="999";
        /*XMLDOMParser parser = new XMLDOMParser();
        InputStream stream = new ByteArrayInputStream(response.getBytes());
        Document doc = parser.getDocument(stream);
        NodeList nodeList = doc.getElementsByTagName("Table");*/
        cur.moveToFirst();
        for (int i = 0; i < numRows; i++) {
            Initilization.resultArray[i][Initilization.CategoryId] = cur.getString(CategoryId);
            //values.put(CATEGORYID, Initilization.resultArray[i][Initilization.CategoryId]);             //lets add data to database

            Initilization.resultArray[i][Initilization.Category] = cur.getString(Category);
            //values.put(CATEGORYNAME, Initilization.resultArray[i][Initilization.Category]);

            Initilization.resultArray[i][Initilization.DisplayOrder] = cur.getString(DisplayOrder);
            //values.put(DISPLAYORDER, Initilization.resultArray[i][Initilization.DisplayOrder]);

            Initilization.resultArray[i][Initilization.RSSTitle] = cur.getString(RSSTitle);
            //values.put(RSSTITLE, Initilization.resultArray[i][Initilization.RSSTitle]);

            Initilization.resultArray[i][Initilization.RSSURL] = cur.getString(RSSURL);
            //values.put(RSSURL_DB, Initilization.resultArray[i][Initilization.RSSURL]);

            Initilization.resultArray[i][Initilization.RSSUrlId] = cur.getString(RSSUrlId);
            //values.put(RSSURLID, Initilization.resultArray[i][Initilization.RSSUrlId]);

            Initilization.resultArray[i][Initilization.NewsId] = cur.getString(NewsId);
           // values.put(NEWSID, Initilization.resultArray[i][Initilization.NewsId]);

            Initilization.resultArray[i][Initilization.NewsTitle] = cur.getString(NewsTitle);
            //values.put(NEWSTITLE, Initilization.resultArray[i][Initilization.NewsTitle]);

            Initilization.resultArray[i][Initilization.Summary] = cur.getString(Summary);
            //values.put(SUMMARY, Initilization.resultArray[i][Initilization.Summary]);

            Initilization.resultArray[i][Initilization.NewsImage] = cur.getString(NewsImage);
            //values.put(NEWSIMAGE, Initilization.resultArray[i][Initilization.NewsImage]);

            Initilization.resultArray[i][Initilization.NewsDate] = cur.getString(NewsDate);
            //values.put(NEWSDATE, Initilization.resultArray[i][Initilization.NewsDate]);

            Initilization.resultArray[i][Initilization.NewsDisplayOrder] = cur.getString(NewsDisplayOrder);
            //values.put(NEWSDISPLAYORDER, Initilization.resultArray[i][Initilization.NewsDisplayOrder]);

            Initilization.resultArray[i][Initilization.CategoryorNews] = cur.getString(CategoryorNews);
            //values.put(CATEGORYORNEWS, Initilization.resultArray[i][Initilization.CategoryorNews]);

            Initilization.resultArray[i][Initilization.FullText] = cur.getString(FullText);

            Initilization.resultArray[i][Initilization.NewsUrl] = cur.getString(NewsUrl);

            currentNewsCategory=Initilization.resultArray[i][Initilization.DisplayOrder];
            //db.insert(TABLE_NAME, null, values);

            int cuDispOrder=0;
            try {
                cuDispOrder = Integer.parseInt(currentNewsCategory);
            }catch (Exception ee){}
            if(cuDispOrder==0){
            }
            if(!Initilization.addOnList.contains(Initilization.resultArray[i][Initilization.Category]) && cuDispOrder!=0){
                Initilization.addOnList.add(cuDispOrder,Initilization.resultArray[i][Initilization.Category]);
            }
            if(!Initilization.addOnList.contains(Initilization.resultArray[i][Initilization.Category]) && cuDispOrder==0){
                Initilization.addOnList.add(Initilization.resultArray[i][Initilization.Category]);
            }
            categories[cuDispOrder][1]=Initilization.resultArray[i][Initilization.Category];
            categories[cuDispOrder][0]=Initilization.resultArray[i][Initilization.DisplayOrder];
            Initilization.resultArrayLength=i;
            try {
                cur.moveToNext();
            }catch (Exception e){/*Index out of bounds*/}
        }

        db.close(); // Closing database connection

        Initilization.addOnList.add(2, "EverYou");
        Initilization.addOnList.add(3,"YouView");
        for(int i=0;i<Initilization.addOnList.size();){
            if(Initilization.addOnList.get(i).length()<2) {
                Initilization.addOnList.remove(i);
                i--;
            }
            i++;
        }

        for(int i=0;i<1000;i++) {
            if(categories[i][0].isEmpty())
                continue;
            for(int j=0;j<100;j++){
                if(Initilization.newsCategories[j][0].isEmpty() &&(i-1)>=0){        //cat id can never be 0
                    Initilization.newsCategories[j][0]=categories[i][0];
                    Initilization.newsCategories[j][1]=categories[i][1];
                    break;
                }
            }
        }
        for(int i=0;i<100;i++) {
            if (Initilization.newsCategories[i][0].isEmpty()) {        //cat id can never be 0
                continue;
            } else
                Initilization.newsCategoryLength++;
        }
    }
}

