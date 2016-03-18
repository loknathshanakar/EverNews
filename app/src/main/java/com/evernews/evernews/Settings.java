package com.evernews.evernews;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Settings extends AppCompatActivity {
    TextView newsChannel, newsOrientationType,newsOrientation, newsFont, newsSupport, newsReview, newsRecomend, newsPolicy, newsTerms, newsCredits, newsWeb, newsVersion;
    //private static Activity context;
    RelativeLayout orientationListiner,logoutlistiner;
    Context context;
    private static SharedPreferences sharedpreferences;
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

        context=this;
        sharedpreferences = getSharedPreferences(Main.USERLOGINDETAILS, Context.MODE_PRIVATE);
        if(sharedpreferences.getString(Main.APPLICATIONORIENTATION,"A").compareTo("L")==0){
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if(sharedpreferences.getString(Main.APPLICATIONORIENTATION,"A").compareTo("P")==0){
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }




        newsChannel= (TextView) findViewById(R.id.newsChannel);
        newsChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent iii = new Intent(Settings.this, AddTab.class);
                    iii.putExtra("CALLER", "SETTINGS");
                    startActivity(iii);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        newsOrientation= (TextView) findViewById(R.id.orientationtype);
        newsOrientationType= (TextView) findViewById(R.id.orientationtype);
        if(sharedpreferences.getString(Main.APPLICATIONORIENTATION,"A").compareTo("L")==0){
            newsOrientationType.setText("Landscape");
        }
        else if(sharedpreferences.getString(Main.APPLICATIONORIENTATION,"A").compareTo("P")==0){
            newsOrientationType.setText("Portrait");
        }else {
            newsOrientationType.setText("Automatic");
        }


        orientationListiner=(RelativeLayout)findViewById(R.id.orientation_listiner);
        orientationListiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                builderSingle.setIcon(R.drawable.ic_launcher);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
                builderSingle.setTitle("Select orientation");
                arrayAdapter.add("Automatic");
                arrayAdapter.add("Portrait");
                arrayAdapter.add("Landscape");
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
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                switch (which) {
                                    case 0:
                                        editor.putString(Main.APPLICATIONORIENTATION, "A");
                                        newsOrientationType.setText("Automatic");
                                        editor.apply();
                                        recreate();
                                        break;
                                    case 1:
                                        editor = sharedpreferences.edit();
                                        editor.putString(Main.APPLICATIONORIENTATION, "P");
                                        newsOrientationType.setText("Portrait");
                                        recreate();
                                        editor.apply();
                                        break;
                                    case 2:
                                        editor = sharedpreferences.edit();
                                        editor.putString(Main.APPLICATIONORIENTATION, "L");
                                        newsOrientationType.setText("Landscape");
                                        recreate();
                                        editor.apply();
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                    case 5:
                                        break;
                                }
                            }
                        });
                builderSingle.show();
            }
        });



        logoutlistiner=(RelativeLayout)findViewById(R.id.logout_lisetiner);
        logoutlistiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(sharedpreferences.getBoolean(Main.ISREGISTRED,false) && sharedpreferences.getBoolean(Main.LOGGEDIN,false)) {
                    editor.putBoolean(Main.ISREGISTRED, false);
                    editor.putBoolean(Main.LOGGEDIN, false);
                    editor.apply();
                    //Toast.makeText(context, "You were logged out application will now restart", Toast.LENGTH_SHORT).show();
                    ProgressDialog progressdlg = new ProgressDialog(context);
                    progressdlg.setMessage("Restarting Application");
                    progressdlg.setTitle("Log out successful,Please Wait...");
                    progressdlg.setCancelable(false);
                    progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressdlg.setIndeterminate(true);
                    progressdlg.show();

                    new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            Intent i = context.getPackageManager().getLaunchIntentForPackage( context.getPackageName() );
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    }.start();
                }
                else{
                    Toast.makeText(context, "You are not logged in to EverNews", Toast.LENGTH_LONG).show();
                }
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

