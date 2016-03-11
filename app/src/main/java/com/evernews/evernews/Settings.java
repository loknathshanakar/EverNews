package com.evernews.evernews;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Settings extends AppCompatActivity {
    TextView newsChannel, newsNotify, newsFont, themeType, newsTwit, newsFb, newsSupport, newsReview, newsRecomend, newsPolicy, newsTerms, newsCredits, newsWeb, newsVersion;
    private static Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.go_backpng);
        getSupportActionBar().setTitle("");




        newsTwit= (TextView) findViewById(R.id.newsTwit);
        newsTwit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tweet();
                }catch (Exception e){e.printStackTrace();}
            }
        });



        newsFb= (TextView) findViewById(R.id.newsFb);
        newsFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                new ShareDialog().
//                Share.newShare((context == null ? getApplicationContext() : context), ShareHosts.FACEBOOK, proto);
                }catch (Exception e){e.printStackTrace();}
                Toast.makeText(Settings.this, "Facebook..", Toast.LENGTH_SHORT).show();
            }
        });




        newsSupport= (TextView) findViewById(R.id.newsSupport);
        newsSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this, "Support Setting is populated soon..", Toast.LENGTH_SHORT).show();
                //Intent intent=new Intent(Settings.this, SupportActivity.class);
                //startActivity(intent);
            }
        });


        newsReview= (TextView) findViewById(R.id.newsReview);
        newsReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this, "Review Setting is populated soon..", Toast.LENGTH_SHORT).show();
            }
        });



        newsRecomend= (TextView) findViewById(R.id.newsRecomend);
        newsRecomend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this, "Recomend Setting is populated soon..", Toast.LENGTH_SHORT).show();
            }
        });



        newsPolicy= (TextView) findViewById(R.id.newsPolicy);
        newsPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Settings.this, "Policy Setting is populated soon..", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Settings.this, ViewMessage.class);
                startActivity(intent);
            }
        });


        newsTerms= (TextView) findViewById(R.id.newsTerms);
        newsTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this, "Terms Setting is populated soon..", Toast.LENGTH_SHORT).show();
            }
        });



        newsCredits= (TextView) findViewById(R.id.newsCredits);
        newsCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this, "Credits Setting is populated soon..", Toast.LENGTH_SHORT).show();
            }
        });


        newsWeb= (TextView) findViewById(R.id.newsWeb);
        newsWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this, "Web Setting is populated soon..", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent(Settings.this,Main.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home){
            Intent intent=new Intent(Settings.this,Main.class);
            startActivity(intent);
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void tweet() {
        try {
            String tweetUrl = String.format("https://twitter.com/");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

            List<ResolveInfo> matches = ((context == null ? getApplicationContext() : context)).getPackageManager().queryIntentActivities(intent, 0);
            boolean exists = false;
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    exists = true;
                    intent.setPackage(info.activityInfo.packageName);
                }
            }
            if (!exists)
                Toast.makeText(context == null ? getApplicationContext() : context, "Twitter not found", Toast.LENGTH_SHORT).show();
            else {
                if (context == null)
                    context.startActivity(intent);
                else context.startActivity(intent);
            }
        }catch (Exception e){e.printStackTrace();}
    }

}

