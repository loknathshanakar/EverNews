package com.evernews.evernews;

import android.app.ActionBar;
import android.content.Intent;
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
    public static String androidId="";
    public static int timeout=10000;
    public static String resultArray[][]=new String[10000][13];
    public static String newsCategories[][]=new String[100][2];
    public static int resultArrayLength=0;
    public static int newsCategoryLength=0;
    public static ArrayList<String> addOnList = new ArrayList <String>(10);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initilization);
        getSupportActionBar().hide();
        new GetNewsTask().execute();
    }

    class GetNewsTask extends AsyncTask<Void,Integer,Void>
    {
        String content;
        int ExceptionCode=0;
        TextView tv=(TextView)findViewById(R.id.response);
        @Override
        protected void onPreExecute()
        {
            tv.setText("Connecting");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            if(progress[0]==0)
                tv.setText("Downloading news...please wait");
            if(progress[0]==1)
                tv.setText("Formatting news...please wait");
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                publishProgress(0);
                androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                String fetchLink="http://rssapi.psweb.in/everapi.asmx/LoadXMLDefaultNews?AndroidId="+androidId;//Over ride but should be Main.androidId
                content=Jsoup.connect(fetchLink).ignoreContentType(true).timeout(timeout).execute().body();
                publishProgress(1);
            }
            catch(Exception e)
            {
                if(e instanceof SocketTimeoutException) {
                    ExceptionCode=1;
                    return null;
                }
                if(e instanceof HttpStatusException) {
                    ExceptionCode=2;
                    return null;
                }
                e.printStackTrace();
            }
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
                if(content!=null)
                {
                    String result = content.toString().replaceAll("&lt;", "<").replaceAll("&gt;",">").replaceAll("&amp;","&");
                    Log.d("response", result);
                    //after getting the response we have to parse it
                    parseResults(result);
                    Intent main=new Intent(Initilization.this,Main.class);
                    startActivity(main);
                    finish();
                }
                super.onPostExecute(aVoid);
            }
        }
    public void parseResults(String response)
    {
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
        Initilization.newsCategories[2][1]="YourView";
        Initilization.newsCategories[1][0]="999";
        Initilization.newsCategories[2][0]="999";
        XMLDOMParser parser = new XMLDOMParser();
        InputStream stream = new ByteArrayInputStream(response.getBytes());
        Document doc = parser.getDocument(stream);
        NodeList nodeList = doc.getElementsByTagName("Table");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            Initilization.resultArray[i][Initilization.CategoryId] = (parser.getValue(e, "CategoryId"));
            Initilization.resultArray[i][Initilization.Category] = (parser.getValue(e, "Category"));
            Initilization.resultArray[i][Initilization.DisplayOrder] = (parser.getValue(e, "DisplayOrder"));
            Initilization.resultArray[i][Initilization.RSSTitle] = (parser.getValue(e, "RSSTitle"));
            Initilization.resultArray[i][Initilization.RSSURL] = (parser.getValue(e, "RSSURL"));
            Initilization.resultArray[i][Initilization.RSSUrlId] = (parser.getValue(e, "RSSUrlId"));
            Initilization.resultArray[i][Initilization.NewsId] = (parser.getValue(e, "NewsId"));
            Initilization.resultArray[i][Initilization.NewsTitle] = (parser.getValue(e, "NewsTitle"));
            Initilization.resultArray[i][Initilization.Summary] = (parser.getValue(e, "Summary"));
            Initilization.resultArray[i][Initilization.NewsImage] = (parser.getValue(e, "NewsImage"));
            Initilization.resultArray[i][Initilization.NewsDate] = (parser.getValue(e, "NewsDate"));
            Initilization.resultArray[i][Initilization.NewsDisplayOrder] = (parser.getValue(e, "NewsDisplayOrder"));
            Initilization.resultArray[i][Initilization.NewsImage] = (parser.getValue(e, "NewsImage"));
            Initilization.resultArray[i][Initilization.CategoryorNews] = (parser.getValue(e, "CategoryorNews"));
            currentNewsCategory=Initilization.resultArray[i][Initilization.DisplayOrder];
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
        }


        Initilization.addOnList.add(2, "EverYou");
        Initilization.addOnList.add(3,"YourView");
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

