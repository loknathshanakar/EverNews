package com.evernews.evernews;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

public class Main extends AppCompatActivity implements SignUp.OnFragmentInteractionListener,PostArticle.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static String catListArray[][]=new String[10000][7];
    public static boolean validCategory=false;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public static ProgressBar progress=null;
    private ViewPager mViewPager;
    public static final String USERLOGINDETAILS = "USERLOGINDETAILS" ;
    private static SharedPreferences sharedpreferences;
    public static String uniqueID="";
    SQLiteDatabase db;
    public static String USERNAME="USERNAME";
    public static String USERID="USERID";
    public static String USEREMAIL="USEREMAIL";
    public static String USERPHONENUMBER="USERPHONENUMBER";
    public static String ISREGISTRED="ISREGISTRED";
    public static String LOGGEDIN="LOGGEDIN";
    public static String NEWCHANNELADDED="NEWCHANNELADDED";
    ShareDialog shareDialog;
    TabLayout tabLayout;
    LinearLayout tabStrip;
    Context context;
    int mandetTab=10;
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    private boolean initialTabs(){
        int offset=0;
        try {
            for (int i = 0; i < Initilization.addOnList.size(); i++) {
                offset = i + 5;
                if ((offset % 5) == 0) {
                    View v = View.inflate(context, R.layout.layout_tab_1, null);
                    TextView tvt = (TextView) v.findViewById(R.id.tab_tv_1);
                    tvt.setText(Initilization.addOnList.get(i));
                    tvt.setGravity(Gravity.CENTER_VERTICAL);
                    tabLayout.getTabAt(i).setCustomView(v);
                    tabLayout.getTabAt(i).getCustomView().setBackgroundResource(R.drawable.tab_color1);
                    tabLayout.setSelectedTabIndicatorColor(Color.rgb(0, 0, 100));
                    tabLayout.setSelectedTabIndicatorHeight(5);
                }
                if ((offset % 5) == 1) {
                    View v = View.inflate(context, R.layout.layout_tab_2, null);
                    TextView tvt = (TextView) v.findViewById(R.id.tab_tv_2);
                    tvt.setText(Initilization.addOnList.get(i));
                    tvt.setGravity(Gravity.CENTER);
                    tabLayout.getTabAt(i).setCustomView(v);
                    tabLayout.getTabAt(i).getCustomView().setBackgroundResource(R.drawable.tab_color2);
                    tabLayout.setSelectedTabIndicatorColor(Color.rgb(0, 0, 100));
                    tabLayout.setSelectedTabIndicatorHeight(5);
                }
                if ((offset % 5) == 2) {
                    View v = View.inflate(context, R.layout.layout_tab_3, null);
                    TextView tvt = (TextView) v.findViewById(R.id.tab_tv_3);
                    tvt.setText(Initilization.addOnList.get(i));
                    tvt.setGravity(Gravity.CENTER);
                    tabLayout.getTabAt(i).setCustomView(v);
                    tabLayout.getTabAt(i).getCustomView().setBackgroundResource(R.drawable.tab_color3);
                    tabLayout.setSelectedTabIndicatorColor(Color.rgb(0, 0, 100));
                    tabLayout.setSelectedTabIndicatorHeight(5);
                }
                if ((offset % 5) == 3) {
                    View v = View.inflate(context, R.layout.layout_tab_4, null);
                    TextView tvt = (TextView) v.findViewById(R.id.tab_tv_4);
                    tvt.setText(Initilization.addOnList.get(i));
                    tvt.setGravity(Gravity.CENTER);
                    tabLayout.getTabAt(i).setCustomView(v);
                    tabLayout.getTabAt(i).getCustomView().setBackgroundResource(R.drawable.tab_color4);
                    tabLayout.setSelectedTabIndicatorColor(Color.rgb(0, 0, 100));
                    tabLayout.setSelectedTabIndicatorHeight(5);
                }
                if ((offset % 5) == 4) {
                    View v = View.inflate(context, R.layout.layout_tab_5, null);
                    TextView tvt = (TextView) v.findViewById(R.id.tab_tv_5);
                    tvt.setText(Initilization.addOnList.get(i));
                    tvt.setGravity(Gravity.CENTER);
                    tabLayout.getTabAt(i).setCustomView(v);
                    tabLayout.getTabAt(i).getCustomView().setBackgroundResource(R.drawable.tab_color5);
                    tabLayout.setSelectedTabIndicatorColor(Color.rgb(0, 0, 100));
                    tabLayout.setSelectedTabIndicatorHeight(5);
                }
            }
        }catch (Exception e){
            return(false);
        }
        return(true);
    }

    public void resetOtherTabs(int skipTab){
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            if(i==skipTab) {
                continue;
            }
            else{
                tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color1_rised_uncolored);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setTitle("");
        context=this;

        progress=(ProgressBar)findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        if(validCategory==false)
            new GetCategoryList().execute();
        sharedpreferences = getSharedPreferences(USERLOGINDETAILS, Context.MODE_PRIVATE);
        //tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout= (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        initialTabs();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                int x=tab.getPosition()+5;
                if ((x % 5) == 0) {
                    int i = x - 5;
                    tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color1);
                }
                if ((x % 5) == 1) {
                    int i = x - 5;
                    tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color2);
                }
                if ((x % 5) == 2) {
                    int i = x - 5;
                    tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color3);
                }
                if ((x % 5) == 3) {
                    int i = x - 5;
                    tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color4);
                }
                if ((x % 5) == 4) {
                    int i = x - 5;
                    tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color5);
                }
                resetOtherTabs(x - 5);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        int tabPos=tabLayout.getSelectedTabPosition();
        tabStrip.getChildAt(tabPos).setBackgroundResource(R.drawable.tab_color1);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            final int x = i+5;
            tabStrip.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vv) {
                    if ((x % 5) == 0) {
                        int i = x - 5;
                        tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color1);
                    }
                    if ((x % 5) == 1) {
                        int i = x - 5;
                        tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color2);
                    }
                    if ((x % 5) == 2) {
                        int i = x - 5;
                        tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color3);
                    }
                    if ((x % 5) == 3) {
                        int i = x - 5;
                        tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color4);
                    }
                    if ((x % 5) == 4) {
                        int i = x - 5;
                        tabStrip.getChildAt(i).setBackgroundResource(R.drawable.tab_color5);
                    }
                    resetOtherTabs(x-5);
                }
            });
        }
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
            final int x=i;
            tabStrip.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    if (x > mandetTab) {
                        final ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse("https://developers.facebook.com")).build();
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                        builderSingle.setIcon(R.drawable.ic_launcher);
                        builderSingle.setTitle("Remove Tab");
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
                        arrayAdapter.add("Remove " +Initilization.addOnList.get(x));
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
                                        switch (which) {
                                            case 0: {
                                                new AsyncTask<Void, Integer, String>() {
                                                    String JsoupResopnse="";
                                                    int ExceptionCode = 0;       //sucess;
                                                    ProgressDialog progressdlg;
                                                    @Override
                                                    protected void onProgressUpdate(Integer... text) {
                                                        if(text[0]==1)
                                                            progressdlg.setMessage("Removing channel");
                                                    }

                                                    @Override
                                                    protected void onPreExecute() {
                                                        Main.progress.setVisibility(View.VISIBLE);
                                                        progressdlg = new ProgressDialog(context);
                                                        progressdlg.setMessage("Connecting to server...");
                                                        progressdlg.setTitle("Removing channel");
                                                        progressdlg.setCancelable(false);
                                                        progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                        progressdlg.setIndeterminate(true);
                                                        progressdlg.show();
                                                    }
                                                    @Override
                                                    protected String doInBackground(Void... params) {
                                                        try {
                                                            String RSSUID=Initilization.getAddOnListRSSID.get(x);
                                                            String RSSNAME=Initilization.addOnList.get(x);
                                                            publishProgress(1);
                                                            Initilization.androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                                            String xmlUrl = "http://rssapi.psweb.in/everapi.asmx/RemoveNewsTAB?RSSID="+RSSUID.replace(" ","")+"&AndroidId="+Initilization.androidId;
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
                                                            Snackbar snackbar = Snackbar.make(v, "News removed successfully...", Snackbar.LENGTH_LONG);
                                                            snackbar.show();
                                                        }
                                                        else{
                                                            Snackbar snackbar = Snackbar.make(v, "Sorry news could not be removed...", Snackbar.LENGTH_LONG);
                                                            snackbar.show();
                                                        }
                                                    }
                                                }.execute();
                                            }
                                                break;
                                        }
                                    }
                                });
                        builderSingle.show();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i=new Intent(Main.this,Settings.class);
                startActivity(i);
                return true;

            case R.id.action_refresh:
                new GetNewsTask().execute();
                return true;

            case R.id.action_add:
                Intent iii=new Intent(Main.this,AddTab.class);
                startActivity(iii);
                return true;

            case R.id.action_search:
                Intent intent=new Intent(Main.this,Search.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

   /* @Override
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
    }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        ImageView artImage;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView=null;
            return rootView;
        }

        private File savebitmap(Bitmap bmp) {
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            OutputStream outStream = null;
            File file = new File(extStorageDirectory,uniqueID+".jpg");
            if (file.exists()) {
                file.delete();
                file = new File(extStorageDirectory, uniqueID+".jpg");
            }
            try {
                outStream = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return file;
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals(uniqueID+".jpg")) {
                            f = temp;
                           // File photo = new File(Environment.getExternalStorageDirectory(), uniqueID+".jpg");
                            break;
                        }
                    }
                    try {
                        Bitmap bitmap;
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                        savebitmap(bitmap);
                        artImage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == 2) {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    //Log.w("path of im.", picturePath + "");
                    savebitmap(thumbnail);
                    artImage.setImageBitmap(thumbnail);
                }
            }
        }

        private void selectImage() {
            final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment.getExternalStorageDirectory(), uniqueID+".jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, 1);
                    }
                    else if (options[item].equals("Choose from Gallery")) {
                        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                    else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
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

            //args.putInt(ARG_SECTION_NUMBER, position);
            ReusableFragment fragArray[]=new ReusableFragment[100];
            if(position<Initilization.addOnList.size()) {
                if (position == 1) {
                    if(!sharedpreferences.getBoolean(LOGGEDIN,false)) {
                        return SignUp.newInstance("SignUpInstance");
                    }
                    else if(sharedpreferences.getBoolean(LOGGEDIN,false)){
                        return PostArticle.newInstance("PostArticle","PostArticle_2");
                        //return SignUp.newInstance("SignUpInstance");
                    }
                }
                else if (position == 2) {
                    return YourView.newInstance("NewInstance","NewInstance");
                }
                ////fragArray[position] = ReusableFragment.newInstanceRe(position, Initilization.newsCategories[position][1]);
                fragArray[position] = ReusableFragment.newInstanceRe(position, Initilization.addOnList.get(position));
                return fragArray[position];
            }
            return null;
        }

        @Override
        public int getCount() {
            //Initilization.newsCategoryLength=15;
            //return Initilization.newsCategoryLength;
            return Initilization.addOnList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return(Initilization.newsCategories[position][1]);
            return (Initilization.addOnList.get(position));
        }
    }


    //Non reuse lol
    class GetNewsTask extends AsyncTask<Void,Void,Void>
    {
        String content;
        int ExceptionCode=0;
        @Override
        protected void onPreExecute()
        {
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Initilization.androidId = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
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
        protected void onPostExecute(Void aVoid)
        {
            if(ExceptionCode>0) {
                if(ExceptionCode==1)
                    Toast.makeText(getApplicationContext(), "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                if(ExceptionCode==2)
                    Toast.makeText(getApplicationContext(),"Some server related issue occurred..please try again later",Toast.LENGTH_SHORT).show();
            }
            if(content!=null)
            {
                String result = content.toString().replaceAll("&lt;", "<").replaceAll("&gt;",">").replaceAll("&amp;","&");
                Log.d("response", result);
                //after getting the response we have to parse it
                parseResults(result);
                Toast.makeText(getApplicationContext(),"Refreshing done",Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                /*Intent intent = getIntent();
                finish();
                startActivity(intent);*/
                recreate();
            }
            progress.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }
    public void parseResults_TEMp(String response)
    {
        int totalTabs=2;
        Initilization.addOnList.clear();
        for (int i = 0; i < 20; i++) {
            Initilization.addOnList.add("");
        }
        Initilization.newsCategoryLength=0;
        String categories[][]=new String[1000][2];
        for(int i=0;i<100;i++){
            Initilization.newsCategories[i][0]="";
            Initilization.newsCategories[i][1]="";
        }
        for(int i=0;i<1000;i++){
            categories[i][0]="";
            categories[i][1]="";
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
        recreate();
    }


    public void parseResults(String response)
    {
        ContentValues values = new ContentValues();
        String path=Initilization.DB_PATH+Initilization.DB_NAME;
        db= SQLiteDatabase.openDatabase(path, null, 0);
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

            Initilization.resultArray[i][Initilization.FullText] = (parser.getValue(e, "FullText"));
            values.put(Initilization.FULLTEXT, Initilization.resultArray[i][Initilization.FullText]);

            Initilization.resultArray[i][Initilization.NewsUrl] = (parser.getValue(e, "NewsURL"));
            values.put(Initilization.NEWSURL, Initilization.resultArray[i][Initilization.NewsUrl]);

            Initilization.resultArray[i][Initilization.CategoryorNews] = (parser.getValue(e, "CategoryorNews"));
            values.put(Initilization.CATEGORYORNEWS, Initilization.resultArray[i][Initilization.CategoryorNews]);

            currentNewsCategory=Initilization.resultArray[i][Initilization.DisplayOrder];
            db.insert(Initilization.TABLE_NAME, null, values);

            int cuDispOrder=0;
            try {
                cuDispOrder = Integer.parseInt(currentNewsCategory);
            }catch (Exception ee){}
            if(cuDispOrder==0){
            }
            if(!Initilization.addOnListTOCompare.contains(Initilization.resultArray[i][Initilization.CategoryId]) && cuDispOrder!=0){
                Initilization.addOnList.add(cuDispOrder,Initilization.resultArray[i][Initilization.Category]);
                Initilization.getAddOnListRSSID.add(cuDispOrder,Initilization.resultArray[i][Initilization.RSSUrlId]);
                Initilization.addOnListTOCompare.add(cuDispOrder,Initilization.resultArray[i][Initilization.CategoryId]);
            }
            if(!Initilization.addOnListTOCompare.contains(Initilization.resultArray[i][Initilization.CategoryId]) && cuDispOrder==0){
                Initilization.addOnList.add(Initilization.resultArray[i][Initilization.Category]);
                Initilization.getAddOnListRSSID.add(Initilization.resultArray[i][Initilization.RSSUrlId]);
                Initilization.addOnListTOCompare.add(Initilization.resultArray[i][Initilization.CategoryId]);
            }
            //categories[cuDispOrder][1]=Initilization.resultArray[i][Initilization.Category];
            //categories[cuDispOrder][0]=Initilization.resultArray[i][Initilization.DisplayOrder];
            Initilization.resultArrayLength=i;
        }

        db.close(); // Closing database connection

        Initilization.addOnList.add(2, "EverYou");
        Initilization.addOnList.add(3,"YouView");
        Initilization.getAddOnListRSSID.add(2,"NULL");
        Initilization.getAddOnListRSSID.add(3,"NULL");
        for(int i=0;i<Initilization.addOnList.size();){
            if(Initilization.addOnList.get(i).length()<2) {
                Initilization.addOnList.remove(i);
                Initilization.getAddOnListRSSID.remove(i);
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

    class GetCategoryList extends AsyncTask<Void,Void,Void>
    {
        String content;
        int ExceptionCode=0;
        @Override
        protected void onPreExecute()
        {
            //progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                String fetchLink="http://rssapi.psweb.in/everapi.asmx/GetNewsChannelList";//Over ride but should be Main.androidId
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
        protected void onPostExecute(Void aVoid)
        {
            if(content!=null)
            {
                String result = content.toString().replaceAll("&lt;", "<").replaceAll("&gt;",">").replaceAll("&amp;","&");
               // Log.d("response", result);
                //after getting the response we have to parse it
                parseResultsList(result);
            }
            //progress.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }

    public void parseResultsList(String response)
    {
        XMLDOMParser parser = new XMLDOMParser();
        InputStream stream = new ByteArrayInputStream(response.getBytes());
        Document doc = parser.getDocument(stream);
        NodeList nodeList = doc.getElementsByTagName("Table");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            Main.catListArray[i][0] = (parser.getValue(e, "RSSUrlId"));
            Main.catListArray[i][1] = (parser.getValue(e, "RSSURL"));
            Main.catListArray[i][2] = (parser.getValue(e, "RSSTitle"));
            Main.catListArray[i][3] = (parser.getValue(e, "Detail"));
            Main.catListArray[i][4] = (parser.getValue(e, "Comment"));
            Main.catListArray[i][5] = (parser.getValue(e, "MediaHouse"));
            Main.catListArray[i][6] = (parser.getValue(e, "NewsType"));
            validCategory=true;
        }
    }
}
