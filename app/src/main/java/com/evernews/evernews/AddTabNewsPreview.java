package com.evernews.evernews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddTabNewsPreview extends DialogFragment {
    SwipeRefreshLayout refresh;
    private static String RSSUID="";
    private static String channelDetails="";
    public interface AddListener {
        void onAdd(AddTabNewsPreview dialog);
        void onRemove(AddTabNewsPreview dialog);
    }

    private static SharedPreferences sharedpreferences;
    private static AddListener listener;
    public AddTabNewsPreview setListener(AddListener listener) {
        AddTabNewsPreview.listener = listener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View add = inflater.inflate(R.layout.activity_add_tab_news_preview, container, false);
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        final ProgressBar prog=(ProgressBar)add.findViewById(R.id.progressBar_ADD);
        final Button addBtn=(Button)add.findViewById(R.id.add_add);
        final TextView channelName=(TextView) add.findViewById(R.id.channel_name_add);
        final TextView channelMeta=(TextView) add.findViewById(R.id.channelMeta_add);
        prog.setVisibility(View.VISIBLE);
        GridView gridView = (GridView)add.findViewById(R.id.gridview_add);
        List <ItemObject> tempItems = parseResultsEmpty();
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), tempItems);
        gridView.setAdapter(customAdapter);
        sharedpreferences = getActivity().getSharedPreferences(Main.USERLOGINDETAILS, Context.MODE_PRIVATE);
        new AsyncTask<Void, Void, String>() {
            int ExceptionCode=0;    //Sucess
            String returnContent="";
            @Override
            protected String doInBackground(Void... params) {
                try {
                    String xmlUrl = "http://rssapi.psweb.in/everapi.asmx/LoadDefaultNewsbyNewsChannel?RSSUrlId=" + RSSUID.replace(" ","");
                    URL cleanURL=new URL(xmlUrl.toString());
                    returnContent = Jsoup.connect(xmlUrl).timeout(Initilization.timeout).ignoreContentType(true).execute().body();
                }
                catch (IOException e) {
                    ExceptionCode=1;        //Failure
                }
                return null;
            }
            @Override
            protected void onPostExecute(String link) {
                if(ExceptionCode==0 && returnContent.length()>50){
                    String result = returnContent.toString().replaceAll("&lt;", "<").replaceAll("&gt;",">").replaceAll("&amp;","&");
                    GridView gridView = (GridView)add.findViewById(R.id.gridview_add);
                    List <ItemObject> tempItems = parseResults(result);
                    if(tempItems.size()>0){
                    CustomAdapter customAdapter = new CustomAdapter(getActivity(), tempItems);
                    gridView.setAdapter(customAdapter);
                    channelName.setText(tempItems.get(0).getnewsName());
                    channelMeta.setText(channelDetails);
                    }
                    else{
                        List<ItemObject> items = new ArrayList<>();
                        String NewsImage = "No news avaliable";
                        String NewsTitle ="No news avaliable";
                        String RSSTitle = "No news avaliable";
                        String NewsId = "No news avaliable";
                        String CATID = "No news avaliable";
                        String fullText="NA";
                        String newsLink="NA";
                        channelName.setText("Not avaliable");
                        channelMeta.setText("Not avaliable");
                        items.add(new ItemObject(NewsImage, NewsTitle, RSSTitle, NewsId, CATID,fullText,newsLink));
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), items);
                        gridView.setAdapter(customAdapter);
                    }
                    prog.setVisibility(View.INVISIBLE);
                }else {
                    Toast.makeText(getContext(),"An error occured while fetching the news...",Toast.LENGTH_LONG).show();
                    prog.setVisibility(View.INVISIBLE);
                }
            }
        }.execute();

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncTask<Void, Integer, String>() {
                    int ExceptionCode = 0;       //sucess;
                    String JsoupResopnse = "";
                    int FtpExceptions= 0;
                    ProgressDialog progressdlg;
                    @Override
                    protected void onProgressUpdate(Integer... text) {
                        if(text[0]==1)
                            progressdlg.setMessage("Adding channel");
                    }

                    @Override
                    protected void onPreExecute() {
                        Main.progress.setVisibility(View.VISIBLE);
                        progressdlg = new ProgressDialog(getContext());
                        progressdlg.setMessage("Connecting to server...");
                        progressdlg.setTitle("Adding channel");
                        progressdlg.setCancelable(false);
                        progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressdlg.setIndeterminate(true);
                        progressdlg.show();
                    }
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            publishProgress(1);
                            Initilization.androidId = android.provider.Settings.Secure.getString(getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                            String xmlUrl = "http://rssapi.psweb.in/everapi.asmx/AddNewsTAB?RSSID="+RSSUID.replace(" ","")+"&AndroidId="+Initilization.androidId;
                            JsoupResopnse= Jsoup.connect(xmlUrl).ignoreContentType(true).timeout(Initilization.timeout).execute().body();
                            if(!JsoupResopnse.contains("<int xmlns=\"http://tempuri.org/\">1</int>")){
                                ExceptionCode=2;//Add failure but not connection
                            }
                        }
                        catch (IOException e) {
                            ExceptionCode=1;    //failure
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(String link) {
                        progressdlg.dismiss();
                        if(ExceptionCode==0) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean(Main.NEWCHANNELADDED, true);
                            editor.commit();
                            Snackbar snackbar = Snackbar.make(add, "News added successfully...", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        else{
                            Snackbar snackbar = Snackbar.make(add, "Sorry news could not be added...", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                }.execute();
            }
        });

        return add;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public List<ItemObject> parseResults(String response)
    {
       /*org.jsoup.nodes.Document doc2 = Jsoup.parse(response, "", Parser.xmlParser());
        for ( org.jsoup.nodes.Element e : doc2.select("NewsImage")) {
            System.out.println(e.text());
        }*/

        List<ItemObject> items = new ArrayList<>();
        XMLDOMParser parser = new XMLDOMParser();
        InputStream stream = new ByteArrayInputStream(response.getBytes());
        Document doc = parser.getDocument(stream);
        NodeList nodeList=null;
        try {
            nodeList = doc.getElementsByTagName("Table");
        }catch (Exception e){}
        if(nodeList!=null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element e = (Element) nodeList.item(i);
                String NewsImage = (parser.getValue(e, "NewsImage"));
                String NewsTitle = (parser.getValue(e, "NewsTitle"));
                String RSSTitle = (parser.getValue(e, "RSSTitle"));
                String NewsId = (parser.getValue(e, "NewsId"));
                String CATID = (parser.getValue(e, "RSSUrlId"));
                items.add(new ItemObject(NewsImage, NewsTitle, RSSTitle, NewsId, CATID,"FullText","NewsLink"));
            }
        }
        return (items);
    }

    public List<ItemObject> parseResultsEmpty()
    {
        List<ItemObject> items = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            String NewsImage = "Updating";
            String NewsTitle ="Updating";
            String RSSTitle = "Updating";
            String NewsId = "Updating";
            String CATID = "Updating";
            items.add(new ItemObject(NewsImage, NewsTitle, RSSTitle, NewsId,CATID,"FullText","NewsLink"));
        }
        return (items);
    }
    public AddTabNewsPreview setRSSUID(String RSSUID) {
        AddTabNewsPreview.RSSUID = RSSUID;
        return this;
    }

    public AddTabNewsPreview setChannelDetails(String channelDetails) {
        AddTabNewsPreview.channelDetails = channelDetails;
        return this;
    }
}
