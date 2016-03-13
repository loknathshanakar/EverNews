package com.evernews.evernews;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReusableFragment extends Fragment {
    private static final String TYPE_KEY = "type";
    private static final int REQUESTCODE = 1900;
    private static final String TAB_NAME = "tab_name";
    private static  String asyncCatId="";
    private static  String asyncNewsId="";
    List<ItemObject> asyncitems = new ArrayList<>();
    Button btn;
    private int refrenceCounter=0;
    public ReusableFragment() {
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
        final GridView gridView = (GridView)rootView.findViewById(R.id.gridview);
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
                final String newsID=itemCollection.get(position).getNewsID();
                if(newsID==null || newsID.isEmpty()) {
                    try {
                        wait(200);
                    }
                    catch (Exception e) {
                    }
                }
                final Intent i = new Intent(getActivity().getBaseContext(), ViewNews.class);
                if(newsID==null)
                    i.putExtra("NEWS_ID", newsID);
                else
                    i.putExtra("NEWS_ID", newsID);
                i.putExtra("NEWS_LINK", "EMPTY");
                i.putExtra("NEWS_TITLE", "EMPTY");
                i.putExtra("CALLER","MAIN");
                new AsyncTask<Void, Void, String>() {
                    String newsLink="";
                    String source="",title="",news="";
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            ViewNews.finalHtml="";
                            String xmlUrl = "http://rssapi.psweb.in/everapi.asmx/LoadSingleNews?NewsID=" + newsID;
                            URL cleanURL=new URL(xmlUrl.toString());
                            String Xml = Jsoup.connect(xmlUrl).ignoreContentType(true).execute().body();
                            char Xmlchar[] = Xml.toCharArray();
                            int iIndex = -1;
                            int eIndex = -1;
                            iIndex = Xml.indexOf("<FullText>") + 10;
                            eIndex = Xml.indexOf("</FullText>");
                            if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                news= Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                            }
                            iIndex = Xml.indexOf("<NewsTitle>") + 11;
                            eIndex = Xml.indexOf("</NewsTitle>");
                            if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                title= Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                if(title==null)
                                    i.putExtra("NEWS_TITLE", "NULL");
                                else
                                    i.putExtra("NEWS_TITLE", title);
                                title="<h1><center>"+title+"</center></h1><br>";
                            }
                            iIndex = Xml.indexOf("<RSSTitle>") + 10;
                            eIndex = Xml.indexOf("</RSSTitle>");
                            if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                source= Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                source="<h2><center>"+source+"</center></h2>";
                            }
                            iIndex = Xml.indexOf("<NewsURL>") + 9;
                            eIndex = Xml.indexOf("</NewsURL>");
                            if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                newsLink= Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                if(title==null)
                                    i.putExtra("NEWS_LINK", "NULL");
                                else
                                    i.putExtra("NEWS_LINK", newsLink);
                            }
                            ViewNews.finalHtml = source+title+news;
                            String Temp=ViewNews.finalHtml;
                            Temp=Temp.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
                            Temp="<p>"+Temp+"</p>";
                            ViewNews.finalHtml=Temp;
                        }
                        catch (IOException e) {

                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(String link) {
                        ViewNews.finalHtml = "<!DOCTYPE html> <html> <body>"+ ViewNews.finalHtml + "</p> </body> </html>";
                    }
                }.execute();

                startActivity(i);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String newsTitle=itemCollection.get(position).getnewsTitle();
                //Toast.makeText(getActivity(), "Long Clicked : " + position, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle(newsTitle);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item);
                arrayAdapter.add("Facebook");
                arrayAdapter.add("Twitter");
                arrayAdapter.add("Email");
                arrayAdapter.add("Share by other means");
                arrayAdapter.add("Open in browser");
                arrayAdapter.add("Copy link");
                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
                                Toast.makeText(getContext()," "+which,Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getContext(),"Unknown error occured",Toast.LENGTH_SHORT).show();
                                    }
                                    if(content!=null && ExceptionCode==0)
                                    {
                                        String result = content.toString().replaceAll("&lt;", "<").replaceAll("&gt;",">").replaceAll("&amp;","&");
                                        Log.d("response", result);
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
                //try {
                    if (tabName.compareTo(Initilization.resultArray[j][Initilization.Category]) == 0) {
                        String par1 = Initilization.resultArray[j][Initilization.NewsImage];
                        String par2 = Initilization.resultArray[j][Initilization.NewsTitle];
                        String par3 = Initilization.resultArray[j][Initilization.RSSTitle];
                        String par4 = Initilization.resultArray[j][Initilization.NewsId];
                        String par5 = Initilization.resultArray[j][Initilization.CategoryId];
                        items.add(new ItemObject(par1, par2, par3, par4, par5));
                        refrenceCounter++;
                        //Need to implement a filter to prevent re adding of data
                        for (int k = 0; k < itemCollection.size(); k++) {
                            if (itemCollection.get(k).getNewsID().compareTo(par4) == 0) {
                                items.remove(items.size() - 1);
                            }
                        }
                    }
                //}catch(Exception e){}
            }
        }
        return items;
    }

    private List<ItemObject> getMoreNews(String incategoryId,String lastNewsId){
        List<ItemObject> items = new ArrayList<>();
        int i= getArguments().getInt(TYPE_KEY);
        if(i==1 || i==2)
            return items;
        if(i>=0) {
            for(int j=0;j<=Initilization.resultArrayLength;j++){
                int categoryId=Integer.parseInt(Initilization.resultArray[j][Initilization.CategoryId]);
                if(categoryId>1)
                    categoryId= categoryId+2;
                if((categoryId-1)==i) {
                    String par1 = Initilization.resultArray[j][Initilization.NewsImage];
                    String par2 = Initilization.resultArray[j][Initilization.NewsTitle];
                    String par3 = Initilization.resultArray[j][Initilization.RSSTitle];     //RSSTitle
                    String par4 = Initilization.resultArray[j][Initilization.NewsId];
                    String par5 = Initilization.resultArray[j][Initilization.RSSURL];
                    items.add(new ItemObject(par1, par2, par3, par4,par5));
                    refrenceCounter++;
                    //Need to implement a filter to prevent re adding of data
                    for(int k=0;k<itemCollection.size();k++){
                        if(itemCollection.get(k).getNewsID().compareTo(par4)==0){
                            items.remove(items.size()-1);
                        }
                    }
                }
            }
        }
        return items;
    }


    //Non reuse lol
    public void parseResults(String response)
    {
        XMLDOMParser parser = new XMLDOMParser();
        InputStream stream = new ByteArrayInputStream(response.getBytes());
        Document doc = parser.getDocument(stream);
        NodeList nodeList;
        try{
            nodeList = doc.getElementsByTagName("Table");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element e = (Element) nodeList.item(i);
                String par1 = (parser.getValue(e, "NewsImage"));
                String par2 = (parser.getValue(e, "NewsTitle"));
                String par3 = (parser.getValue(e, "RSSTitle"));
                String par4 = (parser.getValue(e, "NewsId"));
                String par5 = (parser.getValue(e, "CategoryId"));
                asyncitems.add(new ItemObject(par1, par2, par3, par4,par5));
                for(int k=0;k<itemCollection.size();k++){
                    if(itemCollection.get(k).getNewsID().compareTo(par4)==0){
                        asyncitems.remove(asyncitems.size()-1);
                    }
                }
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),"Exception found,news wont load",Toast.LENGTH_SHORT).show();
        }
    }
}
