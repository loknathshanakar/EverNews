package com.evernews.evernews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReusableFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private static GridView gridView;
    private static final String TYPE_KEY = "type";
    private static final int REQUESTCODE = 1900;
    private static final String TAB_NAME = "tab_name";
    private static  String asyncCatId="";
    private static  String asyncNewsId="";
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String newsTitle;
    String newsLink;
    Context context;
    List<ItemObject> asyncitems = new ArrayList<>();
    Button btn;
    private int refrenceCounter=0;
    public ReusableFragment() {
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public static ReusableFragment newInstanceRe(int sectionNumber,String tabName) {
        Bundle args = new Bundle();
        args.putSerializable(TYPE_KEY, sectionNumber);
        args.putSerializable(TAB_NAME, tabName);
        ReusableFragment fragment = new ReusableFragment();
        fragment.setArguments(args);
        return fragment;
    }
    List <ItemObject> itemCollection=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView)rootView.findViewById(R.id.gridview);

        context=getContext();
        refreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swipe_view);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
               // Toast.makeText(context,"Refresh in background has started...",Toast.LENGTH_LONG).show();
                new GetNewsTask().execute();
            }
        });
        //btn=(Button)getActivity().findViewById(R.id.loadmore);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(4);
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridView.setNumColumns(2);
        }
        List<ItemObject> allItems = getDefaultNews(getArguments().getInt(TYPE_KEY));
        //if(itemCollection.get(0).getnewsName().isEmpty())
        itemCollection.addAll(allItems);
        int postionToMaintain= gridView.getFirstVisiblePosition();
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), itemCollection);
        gridView.setAdapter(customAdapter);
        gridView.setSelection(postionToMaintain);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
                final String newsID = itemCollection.get(position).getNewsID();
                if (newsID == null || newsID.isEmpty()) {
                    try {
                        wait(200);
                    } catch (Exception e) {
                    }
                }
                final Intent i = new Intent(getActivity().getBaseContext(), ViewNews.class);
                if (newsID == null)
                    i.putExtra("NEWS_ID", newsID + "");
                else
                    i.putExtra("NEWS_ID", newsID);
                i.putExtra("CALLER", "MAIN");
                i.putExtra("NEWS_TITLE", itemCollection.get(position).getnewsTitle());
                i.putExtra("RSS_TITLE", itemCollection.get(position).getnewsName());
                i.putExtra("FULL_TEXT", itemCollection.get(position).getFullText());
                i.putExtra("NEWS_LINK", itemCollection.get(position).getNewsURL());
                if (itemCollection.get(position).getFullText() != null && itemCollection.get(position).getFullText().length() < 15) {
                    new AsyncTask<Void, Void, String>() {
                        String newsLink = "";
                        String source = "", title = "", news = "";

                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                ViewNews.finalHtml = "";
                                String xmlUrl = "http://rssapi.psweb.in/everapi.asmx/LoadSingleNews?NewsID=" + newsID;
                                URL cleanURL = new URL(xmlUrl.toString());
                                String Xml = Jsoup.connect(xmlUrl).ignoreContentType(true).execute().body();
                                char Xmlchar[] = Xml.toCharArray();
                                int iIndex = -1;
                                int eIndex = -1;
                                iIndex = Xml.indexOf("<FullText>") + 10;
                                eIndex = Xml.indexOf("</FullText>");
                                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                    news = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                }
                                iIndex = Xml.indexOf("<NewsTitle>") + 11;
                                eIndex = Xml.indexOf("</NewsTitle>");
                                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                    title = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                    if (title == null)
                                        i.putExtra("NEWS_TITLE", "NULL");
                                    else
                                        i.putExtra("NEWS_TITLE", title);
                                    title = "<h1><center>" + title + "</center></h1><br>";
                                }
                                iIndex = Xml.indexOf("<RSSTitle>") + 10;
                                eIndex = Xml.indexOf("</RSSTitle>");
                                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                    source = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                    source = "<h2><center>" + source + "</center></h2>";
                                }
                                iIndex = Xml.indexOf("<NewsURL>") + 9;
                                eIndex = Xml.indexOf("</NewsURL>");
                                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                    newsLink = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                    if (title == null)
                                        i.putExtra("NEWS_LINK", "NULL");
                                    else
                                        i.putExtra("NEWS_LINK", newsLink);
                                }
                                ViewNews.finalHtml = source + title + news;
                                String Temp = ViewNews.finalHtml;
                                Temp = Temp.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
                                Temp = "<p>" + Temp + "</p>";
                                ViewNews.finalHtml = Temp;
                            } catch (IOException e) {

                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String link) {
                            ViewNews.finalHtml = "<!DOCTYPE html> <html> <body>" + ViewNews.finalHtml + "</p> </body> </html>";
                        }
                    }.execute();
                }
                startActivity(i);
            }
        });


        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                newsTitle=itemCollection.get(position).getnewsTitle();
                newsLink=itemCollection.get(position).getNewsURL();
                final ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse("https://developers.facebook.com")).build();
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle(newsTitle);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item);
                arrayAdapter.add("Facebook");
                arrayAdapter.add("Twitter");
                arrayAdapter.add("Email");
                arrayAdapter.add("Open in browser");
                arrayAdapter.add("Copy link");
                arrayAdapter.add("Share by other means");
                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                switch (which) {
                                    case 0:
                                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                                    .setContentTitle(newsTitle)
                                                    .setContentUrl(Uri.parse(newsLink))
                                                    .build();
                                            shareDialog.show(linkContent);
                                        }
                                        break;
                                    case 1:
                                        tweet();
                                        break;
                                    case 2:
                                        sharebyMail();
                                        break;
                                    case 3:
                                        openInBrowser();
                                        break;
                                    case 4: ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText(newsTitle, newsLink);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(getContext() == null ? context : context, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 5:shareByOther();
                                        break;
                                }
                            }
                        });
                builderSingle.show();
                return(true);
            }
        });


        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            FloatingActionButton fab=(FloatingActionButton) getActivity().findViewById(R.id.fabMain);
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    int i=getArguments().getInt(TYPE_KEY);
                        fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getContext(),"Loading more news",Toast.LENGTH_LONG).show();
                            new AsyncTask<Void,Void,String>()
                            {
                                String content;
                                int ExceptionCode=0;
                                @Override
                                protected void onPreExecute()
                                {
                                    asyncitems.clear();
                                    Main.progress.setVisibility(View.VISIBLE);
                                    ExceptionCode=0;
                                    super.onPreExecute();
                                }
                                @Override
                                protected String doInBackground(Void... params)
                                {
                                    try
                                    {
                                        asyncNewsId=itemCollection.get(itemCollection.size()-1).getNewsID();
                                        asyncCatId=itemCollection.get(itemCollection.size()-1).getCategoryID();
                                        Initilization.androidId = android.provider.Settings.Secure.getString(getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                        String fetchLink="http://rssapi.psweb.in/everapi.asmx/LoadNextNewsForCategory?CategoryId="+asyncCatId+"&LastNewsId="+asyncNewsId;//+Initilization.androidId;//Over ride but should be Main.androidId
                                        content= Jsoup.connect(fetchLink).ignoreContentType(true).timeout(Initilization.timeout).execute().body();
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
                                        ExceptionCode=3;
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(String link)
                                {
                                    if(ExceptionCode>0) {
                                        if(ExceptionCode==1)
                                            Toast.makeText(getContext(), "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                        if(ExceptionCode==2)
                                            Toast.makeText(getContext(),"Some server related issue occurred..please try again later",Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getContext(),"Unknown error occurred,check your internet connection",Toast.LENGTH_SHORT).show();
                                    }
                                    if(content!=null && ExceptionCode==0)
                                    {
                                        String result = content.toString().replaceAll("&lt;", "<").replaceAll("&gt;",">").replaceAll("&amp;","&");
                                        //Log.d("response", result);
                                        //after getting the response we have to parse it
                                        parseResults(result);
                                        itemCollection.addAll(asyncitems);
                                        int postionToMaintain = gridView.getLastVisiblePosition();
                                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), itemCollection);
                                        gridView.setAdapter(customAdapter);
                                        gridView.setSelection(postionToMaintain);
                                        //Toast.makeText(getContext(),"More news loaded",Toast.LENGTH_LONG).show();
                                    }
                                    Main.progress.setVisibility(View.GONE);
                                }
                            }.execute();
                        }
                    });
                }
                else{
                    fab.setVisibility(View.GONE);           //hide load more
                }
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });
        return rootView;
    }

    private List<ItemObject> getDefaultNews(int ii){
        List<ItemObject> items = new ArrayList<>();
        int i= getArguments().getInt(TYPE_KEY);
        String tabName=getArguments().getString(TAB_NAME);
        if(i==1 || i==2)
            return items;
        if(i>=0) {
            for(int j=0;j<=Initilization.resultArrayLength;j++){
                /*int categoryId=-100;
                try{
                    categoryId=Integer.parseInt(Initilization.resultArray[j][Initilization.CategoryId]);
                }catch (Exception e){e.printStackTrace();}
                if(categoryId>1)
                    categoryId= categoryId+2;*/
                    if (tabName.compareTo(Initilization.resultArray[j][Initilization.Category]) == 0) {
                        String NewsImage = Initilization.resultArray[j][Initilization.NewsImage];
                        String NewsTitle = Initilization.resultArray[j][Initilization.NewsTitle];
                        String RSSTitle = Initilization.resultArray[j][Initilization.RSSTitle];
                        String NewsId = Initilization.resultArray[j][Initilization.NewsId];
                        String CategoryId = Initilization.resultArray[j][Initilization.CategoryId];
                        String FullText = Initilization.resultArray[j][Initilization.FullText];
                        String NewsUrl = Initilization.resultArray[j][Initilization.NewsUrl];
                        items.add(new ItemObject(NewsImage, NewsTitle, RSSTitle, NewsId, CategoryId,FullText,NewsUrl));
                        refrenceCounter++;
                        //Need to implement a filter to prevent re adding of data
                        for (int k = 0; k < itemCollection.size(); k++) {
                            if (itemCollection.get(k).getNewsID().compareTo(NewsId) == 0) {
                                items.remove(items.size() - 1);
                            }
                        }
                    }
            }
        }
        return items;
    }

    class GetNewsTask extends AsyncTask<Void,Void,Void>
    {
        String content;
        int ExceptionCode=0;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Initilization.androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                String fetchLink="http://rssapi.psweb.in/everapi.asmx/LoadXMLDefaultNews?AndroidId="+Initilization.androidId;//Over ride but should be Main.androidId
                content= Jsoup.connect(fetchLink).ignoreContentType(true).timeout(Initilization.timeout).execute().body();
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
        protected void onPostExecute(Void aVoid) {
            refreshLayout.setRefreshing(false);
            refreshLayout.destroyDrawingCache();
            refreshLayout.clearAnimation();
            if (ExceptionCode > 0) {
                if (ExceptionCode == 1)
                    Toast.makeText(context, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                if (ExceptionCode == 2)
                    Toast.makeText(context, "Some server related issue occurred..please try again later", Toast.LENGTH_SHORT).show();
            }
            if (content != null) {
                String result = content.toString().replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
                parseResultsMAIN(result);
                getActivity().recreate();
                /*List<ItemObject> allItems = getDefaultNews(getArguments().getInt(TYPE_KEY));
                //if(itemCollection.get(0).getnewsName().isEmpty())
                itemCollection.addAll(allItems);
                int postionToMaintain= gridView.getFirstVisiblePosition();
                CustomAdapter customAdapter = new CustomAdapter(getActivity(), itemCollection);
                gridView.setAdapter(customAdapter);
                gridView.setSelection(postionToMaintain);*/
                super.onPostExecute(aVoid);
            }
        }
    }

    public void parseResultsMAIN(String response)
    {
        ContentValues values = new ContentValues();
        String path=Initilization.DB_PATH+Initilization.DB_NAME;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, 0);
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
        Initilization.getAddOnListRSSID.clear();
        for (int i = 0; i < 20; i++) {
            Initilization.addOnList.add("");
            Initilization.addOnListTOCompare.add("");
            Initilization.getAddOnListRSSID.add("");
        }

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
            values.put(Initilization.CATEGORYID, Initilization.resultArray[i][Initilization.CategoryId]);             //lets add data to database

            Initilization.resultArray[i][Initilization.Category] = (parser.getValue(e, "Category"));
            values.put(Initilization.CATEGORYNAME, Initilization.resultArray[i][Initilization.Category]);

            Initilization.resultArray[i][Initilization.DisplayOrder] = (parser.getValue(e, "DisplayOrder"));
            values.put(Initilization.DISPLAYORDER, Initilization.resultArray[i][Initilization.DisplayOrder]);

            Initilization.resultArray[i][Initilization.RSSTitle] = (parser.getValue(e, "RSSTitle"));
            values.put(Initilization.RSSTITLE, Initilization.resultArray[i][Initilization.RSSTitle]);

            Initilization.resultArray[i][Initilization.RSSURL] = (parser.getValue(e, "RSSURL"));
            values.put(Initilization.RSSURL_DB, Initilization.resultArray[i][Initilization.RSSURL]);

            Initilization.resultArray[i][Initilization.RSSUrlId] = (parser.getValue(e, "RSSUrlId"));
            values.put(Initilization.RSSURLID, Initilization.resultArray[i][Initilization.RSSUrlId]);

            Initilization.resultArray[i][Initilization.NewsId] = (parser.getValue(e, "NewsId"));
            values.put(Initilization.NEWSID, Initilization.resultArray[i][Initilization.NewsId]);

            Initilization.resultArray[i][Initilization.NewsTitle] = (parser.getValue(e, "NewsTitle"));
            values.put(Initilization.NEWSTITLE, Initilization.resultArray[i][Initilization.NewsTitle]);

            Initilization.resultArray[i][Initilization.Summary] = (parser.getValue(e, "Summary"));
            values.put(Initilization.SUMMARY, Initilization.resultArray[i][Initilization.Summary]);

            Initilization.resultArray[i][Initilization.NewsImage] = (parser.getValue(e, "NewsImage"));
            values.put(Initilization.NEWSIMAGE, Initilization.resultArray[i][Initilization.NewsImage]);

            Initilization.resultArray[i][Initilization.NewsDate] = (parser.getValue(e, "NewsDate"));
            values.put(Initilization.NEWSDATE, Initilization.resultArray[i][Initilization.NewsDate]);

            Initilization.resultArray[i][Initilization.NewsDisplayOrder] = (parser.getValue(e, "NewsDisplayOrder"));
            values.put(Initilization.NEWSDISPLAYORDER, Initilization.resultArray[i][Initilization.NewsDisplayOrder]);

            Initilization.resultArray[i][Initilization.CategoryorNews] = (parser.getValue(e, "CategoryorNews"));
            values.put(Initilization.CATEGORYORNEWS, Initilization.resultArray[i][Initilization.CategoryorNews]);

            Initilization.resultArray[i][Initilization.FullText] = (parser.getValue(e, "FullText"));
            values.put(Initilization.FULLTEXT, Initilization.resultArray[i][Initilization.FullText]);

            Initilization.resultArray[i][Initilization.NewsUrl] = (parser.getValue(e, "NewsURL"));
            values.put(Initilization.NEWSURL, Initilization.resultArray[i][Initilization.NewsUrl]);

            currentNewsCategory=Initilization.resultArray[i][Initilization.DisplayOrder];
            db.insert(Initilization.TABLE_NAME, null, values);

            int cuDispOrder=0;
            try {
                cuDispOrder = Integer.parseInt(currentNewsCategory);
            }catch (Exception ee){}
            if(cuDispOrder==0){
            }
            if(!Initilization.addOnListTOCompare.contains(Initilization.resultArray[i][Initilization.Category]) && cuDispOrder!=0){
                Initilization.addOnList.add(cuDispOrder,Initilization.resultArray[i][Initilization.Category]);
                Initilization.getAddOnListRSSID.add(cuDispOrder,Initilization.resultArray[i][Initilization.RSSUrlId]);
                Initilization.addOnListTOCompare.add(cuDispOrder,Initilization.resultArray[i][Initilization.Category]);
            }
            if(!Initilization.addOnListTOCompare.contains(Initilization.resultArray[i][Initilization.CategoryId]) && cuDispOrder==0){
                Initilization.addOnList.add(Initilization.resultArray[i][Initilization.Category]);
                Initilization.getAddOnListRSSID.add(Initilization.resultArray[i][Initilization.RSSUrlId]);
                Initilization.addOnListTOCompare.add(Initilization.resultArray[i][Initilization.CategoryId]);
            }
            Initilization.resultArrayLength=i;
        }

        db.close(); // Closing database connection

        Initilization.addOnList.add(2, "EverYou");
        Initilization.addOnList.add(3, "YouView");
        Initilization.getAddOnListRSSID.add(2,"NULL");
        Initilization.getAddOnListRSSID.add(3,"NULL");
        Initilization.getAddOnListRSSID.removeAll(Arrays.asList(null, ""));
        Initilization.addOnList.removeAll(Arrays.asList(null, ""));
        Initilization.addOnListTOCompare.clear();
    }



    //Non reuse lol
    public void parseResults(String response)
    {
        ContentValues values = new ContentValues();
        String path=Initilization.DB_PATH+Initilization.DB_NAME;
        SQLiteDatabase db= SQLiteDatabase.openDatabase(path, null, 0);
        XMLDOMParser parser = new XMLDOMParser();
        InputStream stream = new ByteArrayInputStream(response.getBytes());
        Document doc = parser.getDocument(stream);
        NodeList nodeList;
        //Neeed to use XML prasing using JSOUP
        org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(response, "", org.jsoup.parser.Parser.xmlParser());
        for(int i=0;i<15;i++){
            if(i==Initilization.CategoryId) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("CategoryId")) {
                    values.put(Initilization.CATEGORYID,e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.Category) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("Category")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.DisplayOrder) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("DisplayOrder")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.RSSTitle) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("RSSTitle")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.RSSURL) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("RSSURL")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.RSSUrlId) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("RSSUrlId")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.NewsId ) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("NewsId")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.NewsTitle ) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("NewsTitle")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.Summary) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("Summary")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.NewsImage) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("NewsImage")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.NewsDate) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("NewsDate")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.NewsDisplayOrder) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("NewsDisplayOrder")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.CategoryorNews) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("CategoryorNews")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.FullText) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("FullText")) {
                    values.put(Initilization.CATEGORYID, e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }
            if(i==Initilization.NewsUrl) {
                for (org.jsoup.nodes.Element e : jsoupDoc.select("NewsUrl")) {
                    values.put(Initilization.CATEGORYID,e.text());
                    db.insert(Initilization.TABLE_NAME, null, values);
                    values.clear();
                }
            }



            String NewsImage = (parser.getValue(e, "NewsImage"));
            //values.put(Initilization.NEWSIMAGE, NewsImage);             //lets add data to database

            String NewsTitle = (parser.getValue(e, "NewsTitle"));
            //values.put(Initilization.NEWSTITLE, NewsTitle);

            String RSSTitle = (parser.getValue(e, "RSSTitle"));
            //values.put(Initilization.RSSTITLE, RSSTitle);

            String NewsId = (parser.getValue(e, "NewsId"));
            //values.put(Initilization.NEWSID, NewsId);

            String CategoryId = (parser.getValue(e, "CategoryId"));
            //values.put(Initilization.CATEGORYID, CategoryId);

            String FullText = (parser.getValue(e, "FullText"));
            //values.put(Initilization.FULLTEXT, FullText);

            String NewsUrl = (parser.getValue(e, "NewsURL"));
            //values.put(Initilization.NEWSURL, NewsUrl);



            asyncitems.add(new ItemObject(NewsImage, NewsTitle, RSSTitle, NewsId,CategoryId,FullText,NewsUrl));
            for(int k=0;k<itemCollection.size();k++){
                if(itemCollection.get(k).getNewsID().compareTo(NewsId)==0){
                    asyncitems.remove(asyncitems.size()-1);
                }
        }
        /*try{
            nodeList = doc.getElementsByTagName("Table");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element e = (Element) nodeList.item(i);


                //Initilization.resultArray[i][Initilization.CategoryId] = (parser.getValue(e, "CategoryId"));
                values.put(Initilization.CATEGORYID, parser.getValue(e, "CategoryId"));             //lets add data to database

                //Initilization.resultArray[i][Initilization.Category] = (parser.getValue(e, "Category"));
                values.put(Initilization.CATEGORYNAME, parser.getValue(e, "Category"));

               // Initilization.resultArray[i][Initilization.DisplayOrder] = (parser.getValue(e, "DisplayOrder"));
                values.put(Initilization.DISPLAYORDER, parser.getValue(e, "DisplayOrder"));

                //Initilization.resultArray[i][Initilization.RSSTitle] = (parser.getValue(e, "RSSTitle"));
                values.put(Initilization.RSSTITLE, parser.getValue(e, "RSSTitle"));

                //Initilization.resultArray[i][Initilization.RSSURL] = (parser.getValue(e, "RSSURL"));
                values.put(Initilization.RSSURL_DB, parser.getValue(e, "RSSURL"));

                //Initilization.resultArray[i][Initilization.RSSUrlId] = (parser.getValue(e, "RSSUrlId"));
                values.put(Initilization.RSSURLID, parser.getValue(e, "RSSUrlId"));

                //Initilization.resultArray[i][Initilization.NewsId] = (parser.getValue(e, "NewsId"));
                values.put(Initilization.NEWSID, parser.getValue(e, "NewsId"));

                //Initilization.resultArray[i][Initilization.NewsTitle] = (parser.getValue(e, "NewsTitle"));
                values.put(Initilization.NEWSTITLE, parser.getValue(e, "NewsTitle"));

                //Initilization.resultArray[i][Initilization.Summary] = (parser.getValue(e, "Summary"));
                values.put(Initilization.SUMMARY, parser.getValue(e, "Summary"));

                //Initilization.resultArray[i][Initilization.NewsImage] = (parser.getValue(e, "NewsImage"));
                values.put(Initilization.NEWSIMAGE, parser.getValue(e, "NewsImage"));

               // Initilization.resultArray[i][Initilization.NewsDate] = (parser.getValue(e, "NewsDate"));
                values.put(Initilization.NEWSDATE, parser.getValue(e, "NewsDate"));

                //Initilization.resultArray[i][Initilization.NewsDisplayOrder] = (parser.getValue(e, "NewsDisplayOrder"));
                values.put(Initilization.NEWSDISPLAYORDER, parser.getValue(e, "NewsDisplayOrder"));

                //Initilization.resultArray[i][Initilization.FullText] = (parser.getValue(e, "FullText"));
                values.put(Initilization.FULLTEXT, parser.getValue(e, "FullText"));

                //Initilization.resultArray[i][Initilization.NewsUrl] = (parser.getValue(e, "NewsURL"));
                values.put(Initilization.NEWSURL, parser.getValue(e, "NewsURL"));

               // Initilization.resultArray[i][Initilization.CategoryorNews] = (parser.getValue(e, "CategoryorNews"));
                values.put(Initilization.CATEGORYORNEWS, parser.getValue(e, "CategoryorNews"));



                String NewsImage = (parser.getValue(e, "NewsImage"));
                //values.put(Initilization.NEWSIMAGE, NewsImage);             //lets add data to database

                String NewsTitle = (parser.getValue(e, "NewsTitle"));
                //values.put(Initilization.NEWSTITLE, NewsTitle);

                String RSSTitle = (parser.getValue(e, "RSSTitle"));
                //values.put(Initilization.RSSTITLE, RSSTitle);

                String NewsId = (parser.getValue(e, "NewsId"));
                //values.put(Initilization.NEWSID, NewsId);

                String CategoryId = (parser.getValue(e, "CategoryId"));
                //values.put(Initilization.CATEGORYID, CategoryId);

                String FullText = (parser.getValue(e, "FullText"));
                //values.put(Initilization.FULLTEXT, FullText);

                String NewsUrl = (parser.getValue(e, "NewsURL"));
                //values.put(Initilization.NEWSURL, NewsUrl);

                db.insert(Initilization.TABLE_NAME, null, values);

                asyncitems.add(new ItemObject(NewsImage, NewsTitle, RSSTitle, NewsId,CategoryId,FullText,NewsUrl));
                for(int k=0;k<itemCollection.size();k++){
                    if(itemCollection.get(k).getNewsID().compareTo(NewsId)==0){
                        asyncitems.remove(asyncitems.size()-1);
                    }
                }
            }

        }
        catch (Exception e){
            Toast.makeText(getContext(),"Exception found,news wont load",Toast.LENGTH_SHORT).show();
        }finally {
            db.close();
        }*/
        db.close();
    }


    public void tweet() {
        try {
            String tweetUrl =
                    String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                            urlEncode(newsTitle), urlEncode(newsLink));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

            List<ResolveInfo> matches = ((getContext() == null ? getContext() : getContext())).getPackageManager().queryIntentActivities(intent, 0);
            boolean exists = false;
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    exists = true;
                    intent.setPackage(info.activityInfo.packageName);
                }
            }
            if (!exists) {
                Toast.makeText(getContext() == null ? getContext() : getContext(), "Twitter not found", Toast.LENGTH_SHORT).show();
                Intent tweet = new Intent(Intent.ACTION_VIEW);
                tweet.setData(Uri.parse("http://twitter.com/?status=" + Uri.encode(newsLink)));//where message is your string message
                startActivity(tweet);
            }
            else {
                if (context == null)
                    context.startActivity(intent);
                else context.startActivity(intent);
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf("TAG", "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    public void openInBrowser() {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(newsLink));
            if (context == null)
                startActivity(i);
            else
                (context).startActivity(i);
        }catch (Exception e){e.printStackTrace();}
    }

    public void shareByOther() {
        try {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, newsLink);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
        }catch (Exception e){e.printStackTrace();}
    }

    public void sharebyMail() {
        try {
            Intent gmail = new Intent(Intent.ACTION_VIEW);
            gmail.setType("plain/text");
            gmail.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            gmail.putExtra(Intent.EXTRA_SUBJECT, newsTitle);
            gmail.putExtra(Intent.EXTRA_TEXT, newsLink);
            if (getContext() != null) {
                getContext().startActivity(gmail);
                return;
            }
            startActivity(gmail);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext() == null ? getContext() : getContext(), "Gmail client not found \nUse Share by other ", Toast.LENGTH_SHORT).show();
        }

    }

}
