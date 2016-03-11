package com.evernews.evernews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ViewNews extends AppCompatActivity {
    int selectedColor = Color.rgb(125, 125, 125);
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public static String finalHtml = "";
    private ViewPager mViewPager;
    private static float mDownX;
    private static float mDownY;
    static boolean isOnClick;
    static String newsID="";
    static String newsLink="";
    static String newsTitle="";
    static String finalHtml2 = "";
    public static FloatingActionButton fab;
    private final static float SCROLL_THRESHOLD = 10;
    Context context;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news2);
        context=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.go_backpng);
        getSupportActionBar().setTitle("");
        Intent intent = getIntent();
        newsID = intent.getStringExtra("NEWS_ID");
        newsLink = intent.getStringExtra("NEWS_LINK")+"";
        newsTitle = intent.getStringExtra("NEWS_TITLE")+"";
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse("https://developers.facebook.com")).build();
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle(newsTitle);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
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
                                    case 4:copyToClipBoard();
                                        break;
                                    case 5:shareByOther();
                                        break;
                                }
                            }
                        });
                builderSingle.show();
            }
        });


        if (newsLink.compareTo("EMPTY")==0) {
            new AsyncTask<Void, Void, String>() {
                boolean noException=true;
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        String xmlUrl = "http://rssapi.psweb.in/everapi.asmx/LoadSingleNews?NewsID=" + newsID;
                        URL cleanURL = new URL(xmlUrl.toString());
                        String Xml = Jsoup.connect(xmlUrl).ignoreContentType(true).execute().body();
                        char Xmlchar[] = Xml.toCharArray();
                        int iIndex = -1;
                        int eIndex = -1;
                        iIndex = Xml.indexOf("<FullText>") + 10;
                        eIndex = Xml.indexOf("</FullText>");
                        String source = "", title = "", news = "";
                        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                            news = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                        }
                        iIndex = Xml.indexOf("<NewsTitle>") + 11;
                        eIndex = Xml.indexOf("</NewsTitle>");
                        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                            title = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                            newsTitle=title;
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
                            newsLink= Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                        }
                        finalHtml = source + title + news;
                        String Temp = finalHtml;
                        Temp = Temp.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
                        Temp = "<p>" + Temp + "</p>";
                        finalHtml = Temp;
                    } catch (IOException e) {
                        noException=false;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String link) {
                    if(finalHtml.length()>30)
                        finalHtml = "<!DOCTYPE html> <html> <body>" + finalHtml + "</p> </body> </html>";
                    if(noException==true && newsTitle.length()>5)
                        fab.setVisibility(View.VISIBLE);
                }
            }.execute();
        }
        else
            fab.setVisibility(View.VISIBLE);
    }


    public void tweet() {
        try {
            String tweetUrl =
                    String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                            urlEncode(newsTitle), urlEncode(newsLink));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

            List<ResolveInfo> matches = ((context == null ? context : context)).getPackageManager().queryIntentActivities(intent, 0);
            boolean exists = false;
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    exists = true;
                    intent.setPackage(info.activityInfo.packageName);
                }
            }
            if (!exists) {
                Toast.makeText(context == null ? context : context, "Twitter not found", Toast.LENGTH_SHORT).show();
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
            if (context != null) {
                context.startActivity(gmail);
                return;
            }
            startActivity(gmail);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context == null ? context : context, "Gmail client not found \nUse Share by other ", Toast.LENGTH_SHORT).show();
        }

    }

    public void copyToClipBoard() {
        try {
            ClipboardManager clipboard = (ClipboardManager) ((context == null ? this : context).getSystemService(Context.CLIPBOARD_SERVICE));
            ClipData clip = ClipData.newPlainText("link", newsLink);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context == null ? context : context, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ALLOWED_URL = "allowed_url";
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            final Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            if(sectionNumber==1)
                args.putString(ALLOWED_URL,newsLink);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_view_news2, container, false);

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        String xmlUrl = "http://rssapi.psweb.in/everapi.asmx/LoadSingleNews?NewsID=" + newsID;
                        URL cleanURL=new URL(xmlUrl.toString());
                        String Xml = Jsoup.connect(xmlUrl).ignoreContentType(true).execute().body();
                        char Xmlchar[] = Xml.toCharArray();
                        int iIndex = -1;
                        int eIndex = -1;
                        iIndex = Xml.indexOf("<FullText>") + 10;
                        eIndex = Xml.indexOf("</FullText>");
                        String source="",title="",news="";
                        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                            news= Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                        }
                        iIndex = Xml.indexOf("<NewsTitle>") + 11;
                        eIndex = Xml.indexOf("</NewsTitle>");
                        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                            title= Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
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
                    if(getArguments().getInt(ARG_SECTION_NUMBER)==1 && newsLink.isEmpty()==false) {
                        final WebView mWebView = (WebView) rootView.findViewById(R.id.webView_news);
                        mWebView.setWebViewClient(new WebViewClient());
                        mWebView.loadUrl(newsLink);
                        mWebView.setOnTouchListener(new View.OnTouchListener() {

                            public boolean onTouch(View v, MotionEvent ev) {
                                WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
                                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                                    case MotionEvent.ACTION_DOWN:
                                        mDownX = ev.getX();
                                        mDownY = ev.getY();
                                        isOnClick = true;
                                        break;
                                    case MotionEvent.ACTION_CANCEL:
                                    case MotionEvent.ACTION_UP:
                                        if (isOnClick && hr != null){
                                            if (hr.getType() == WebView.HitTestResult.IMAGE_TYPE){}
                                            //new ImageViewer().setURI(hr.getExtra()).show(getFragmentManager(), "#show");
                                            return true;
                                        }
                                        break;
                                    case MotionEvent.ACTION_MOVE:
                                        if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD
                                                || Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
                                            isOnClick = false;
                                        }
                                        break;
                                    default:
                                        break;
                                }

                                return false;
                            }
                        });


                    }
                }
            }.execute();


            if(newsID.isEmpty()==false && getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                final WebView mWebView = (WebView) rootView.findViewById(R.id.webView_news);
                mWebView.loadData(finalHtml, "text/html", null);
                if(finalHtml.isEmpty()) {
                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                finalHtml2="";
                                String xmlUrl = "http://rssapi.psweb.in/everapi.asmx/LoadSingleNews?NewsID=" + newsID;
                                URL cleanURL = new URL(xmlUrl.toString());
                                String Xml = Jsoup.connect(xmlUrl).ignoreContentType(true).execute().body();
                                char Xmlchar[] = Xml.toCharArray();
                                int iIndex = -1;
                                int eIndex = -1;
                                iIndex = Xml.indexOf("<FullText>") + 10;
                                eIndex = Xml.indexOf("</FullText>");
                                String source = "", title = "", news = "";
                                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                    news = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                }
                                iIndex = Xml.indexOf("<NewsTitle>") + 11;
                                eIndex = Xml.indexOf("</NewsTitle>");
                                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                    title = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                    title = "<h1><center>" + title + "</center></h1><br>";
                                }
                                iIndex = Xml.indexOf("<RSSTitle>") + 10;
                                eIndex = Xml.indexOf("</RSSTitle>");
                                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                                    source = Xml.copyValueOf(Xmlchar, iIndex, (eIndex - iIndex));
                                    source = "<h2><center>" + source + "</center></h2>";
                                }
                                finalHtml2 = source + title + news;
                                String Temp = finalHtml2;
                                Temp = Temp.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
                                Temp = "<p>" + Temp + "</p>";
                                finalHtml2 = Temp;
                            } catch (IOException e) {

                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String link) {
                            finalHtml2 = "<!DOCTYPE html> <html> <body>" + finalHtml2 + "</p> </body> </html>";
                            mWebView.loadData(finalHtml2, "text/html", null);
                        }
                    }.execute();
                }
            }
            else{
                Toast.makeText(getContext(),"Sorry news not avaliable",Toast.LENGTH_LONG);
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "WebView";
                case 1:
                    return "EverView";
            }
            return null;
        }
    }
}
