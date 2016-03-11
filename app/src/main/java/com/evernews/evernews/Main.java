package com.evernews.evernews;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.jibble.simpleftp.SimpleFTP;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public static ProgressBar progress=null;
    private ViewPager mViewPager;
    private final String USERLOGINDETAILS = "USERLOGINDETAILS" ;
    private static SharedPreferences sharedpreferences;
    public static String uniqueID="";

    public static String USERNAME="USERNAME";
    public static String USERID="USERID";
    public static String USEREMAIL="USEREMAIL";
    public static String USERPHONENUMBER="USERPHONENUMBER";
    public static String ISREGISTRED="ISREGISTRED";
    public static String LOGGEDIN="LOGGEDIN";
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
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


        //tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout= (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setTitle("");


        progress=(ProgressBar)findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        sharedpreferences = getSharedPreferences(USERLOGINDETAILS, Context.MODE_PRIVATE);
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
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
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
//            if(sharedpreferences.getBoolean("ISREGISTRED", false) == false && getArguments().getInt(ARG_SECTION_NUMBER)==1) {
//
//               rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
//            }
//            else if(sharedpreferences.getBoolean("ISREGISTRED", false) == true && getArguments().getInt(ARG_SECTION_NUMBER)==1){
//                rootView = inflater.inflate(R.layout.fragment_post_article, container, false);
//            }
//            if(getArguments().getInt(ARG_SECTION_NUMBER)==2) {
//                rootView = inflater.inflate(R.layout.fragment_your_view, container, false);
//            }
//            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            //int position=getArguments().getInt(ARG_SECTION_NUMBER);*/
//            if(sharedpreferences.getBoolean("ISREGISTRED",false) == false && getArguments().getInt(ARG_SECTION_NUMBER)==1) {
//                final TextView userName = (TextView) getActivity().findViewById(R.id.userName);
//                final TextView userEmail = (TextView) getActivity().findViewById(R.id.userEmail);
//                final TextView userNumber = (TextView) getActivity().findViewById(R.id.userNumber);
//                final TextView password = (TextView) getActivity().findViewById(R.id.password);
//                final Button signUp = (Button) getActivity().findViewById(R.id.signup);
//                final Button noAction = (Button) getActivity().findViewById(R.id.noAction);
//                final Button fbSignup = (Button) getActivity().findViewById(R.id.facebook);
//                final Button gSignup = (Button) getActivity().findViewById(R.id.google);
//                final Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
//                final Account[] accounts = AccountManager.get(getContext()).getAccounts();
//                signUp.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        final String userNames = userName.getText().toString().replace(" ","");
//                        final String emails = userEmail.getText().toString().replace(" ", "");
//                        final String pnumbers = userNumber.getText().toString().replace(" ", "");
//                        final String passWord = password.getText().toString().replace(" ", "");
//                        String errorString = "Please correct the following errors...\r\n";
//                        boolean validEmail = isValidEmail(emails);
//                        boolean validNumber = isValidNumber(pnumbers);
//                        boolean validUsername = isValidName(userNames);
//                        boolean validPassword = isValidPassowod(passWord);
//                        if (validUsername == false)
//                            errorString = errorString + ">Invalid Username (6 Char min)\r\n";
//                        if (validEmail == false)
//                            errorString = errorString + ">Invalid email address\r\n";
//                        if (validNumber == false)
//                            errorString = errorString + ">Invalid mobile number\r\n";
//                        if (validPassword == false)
//                            errorString = errorString + ">Invalid Password";
//                        if (validEmail == true && validNumber == true && validUsername == true && validPassword == true) {
//                            //Post details to server
//                            Initilization.androidId = android.provider.Settings.Secure.getString(getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//                            String urlStr="http://rssapi.psweb.in/everapi.asmx/RegisterUser?FullName=" + userNames + "&Email=" + emails + "&Password=" + passWord + "&Mobile=" + pnumbers + "&AndroidId=" + Initilization.androidId;
//                            URL url=null;
//                            try{
//                            url = new URL(urlStr);
//                            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
//                            url = uri.toURL();
//                            }
//                            catch(Exception e){}
//                            final String urlRequest = url.toString();
//                            progress.setVisibility(View.VISIBLE);
//                            new AsyncTask<Void, Void, String>() {
//                                int ExceptionCode = 0;
//                                String JsoupResopnse = "";
//                                @Override
//                                protected String doInBackground(Void... params) {
//                                    try {
//                                        JsoupResopnse = Jsoup.connect(urlRequest).timeout(Initilization.timeout).ignoreContentType(true).execute().body();
//                                        int iIndex = JsoupResopnse.indexOf("\">") + 2;
//                                        int eIndex = JsoupResopnse.indexOf("</");
//                                        char jChar[] = JsoupResopnse.toCharArray();
//                                        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex)
//                                            JsoupResopnse = JsoupResopnse.copyValueOf(jChar, iIndex, (eIndex - iIndex));
//                                    } catch (IOException e) {
//                                        if (e instanceof SocketTimeoutException) {
//                                            ExceptionCode = 1;
//                                            return null;
//                                        }
//                                        if (e instanceof HttpStatusException) {
//                                            ExceptionCode = 2;
//                                            return null;
//                                        }
//                                    }
//                                    return null;
//                                }
//
//                                @Override
//                                protected void onPostExecute(String link) {
//                                    if (ExceptionCode > 0) {
//                                        if (ExceptionCode == 1)
//                                            Toast.makeText(getContext(), "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
//                                        if (ExceptionCode == 2)
//                                            Toast.makeText(getContext(), "Some server related issue occurred..please try again later", Toast.LENGTH_SHORT).show();
//                                    }
//                                    if (JsoupResopnse.isEmpty() == false && Integer.valueOf(JsoupResopnse) > 0) {
//                                        //Store all user data into shared prefrence
//                                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                                        editor.putString("USERNAME", userNames);
//                                        editor.putInt("USERID", Integer.valueOf(JsoupResopnse));
//                                        editor.putString("USEREMAIL", emails);
//                                        editor.putString("USERPHONENUMBER", pnumbers);
//                                        editor.putBoolean("ISREGISTRED", true);
//                                        editor.commit();
//                                        Toast.makeText(getContext(), "Registration complete your UserId is " + JsoupResopnse, Toast.LENGTH_LONG).show();
//                                        userName.setText("");
//                                        userEmail.setText("");
//                                        userNumber.setText("");
//                                        password.setText("");
//                                        progress.setVisibility(View.GONE);
//                                    } else
//                                        Toast.makeText(getContext(), "Please check the filled details", Toast.LENGTH_SHORT).show();
//                                }
//                            }.execute();
//                        } else {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                            builder.setMessage(errorString)
//                                    .setCancelable(false)
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            return;
//                                        }
//                                    });
//                            AlertDialog alert = builder.create();
//                            alert.show();
//                        }
//                    }
//                });
//                noAction.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        Toast.makeText(getContext(), "LogIn", Toast.LENGTH_LONG).show();
//                    }
//                });
//                fbSignup.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        Toast.makeText(getContext(), "Facebook", Toast.LENGTH_LONG).show();
//                    }
//                });
//                gSignup.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        String possibleEmail = "Nothing";
//                        String username = getUsername();
//                        for (Account account : accounts) {
//                            if (emailPattern.matcher(account.name).matches()) {
//                                possibleEmail = account.name;
//                                break;
//                            }
//                        }
//                        Toast.makeText(getContext(), "EMail : " + possibleEmail + "\r\n Username : " + username, Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//
//            if(sharedpreferences.getBoolean("ISREGISTRED", false) == true && getArguments().getInt(ARG_SECTION_NUMBER)==1){
//                final TextView artTitle=(TextView)getActivity().findViewById(R.id.title);
//                final TextView artDesc=(TextView)getActivity().findViewById(R.id.post);
//                artImage=(ImageView)getActivity().findViewById(R.id.viewImage);
//                final TextView artPhoto=(TextView)getActivity().findViewById(R.id.btnSelectPhoto);
//                final TextView artSubmit=(TextView)getActivity().findViewById(R.id.submitPost);
//
//                artSubmit.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        final String articleTitle=artTitle.getText().toString();
//                        final String articleContent=artDesc.getText().toString();
//                        final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//                        if(articleTitle.length()<8 || articleContent.length()<20){
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                builder.setMessage("Article title or content does not meet the required specification")
//                                        .setCancelable(false)
//                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                                return;
//                                            }
//                                        });
//                                AlertDialog alert = builder.create();
//                                alert.show();
//                        }
//                        else{
//                            new AsyncTask<Void, Integer, String>() {
//                                int ExceptionCode = 0;
//                                String JsoupResopnse = "";
//                                int FtpExceptions= 0;
//                                ProgressDialog progressdlg;
//                                @Override
//                                protected void onProgressUpdate(Integer... text) {
//                                    if(text[0]==1)
//                                        progressdlg.setMessage("Uploading image...");
//                                    if(text[0]==2)
//                                        progressdlg.setMessage("Connecting to content server");
//                                    if(text[0]==3)
//                                        progressdlg.setMessage("Uploading content...");
//                                }
//
//                                @Override
//                                protected void onPreExecute() {
//                                    progress.setVisibility(View.VISIBLE);
//                                    progressdlg = new ProgressDialog(getContext());
//                                    progressdlg.setMessage("Connecting to server...");
//                                    progressdlg.setTitle("Posting article");
//                                    progressdlg.setCancelable(false);
//                                    progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                                    progressdlg.setIndeterminate(true);
//                                    progressdlg.show();
//                                }
//                                @Override
//                                protected String doInBackground(Void... params) {
//                                    try {
//                                        SimpleFTP ftp = new SimpleFTP();
//                                        //HostName : 178.77.67.207
//                                        //username : APPUser
//                                        //Password : 7Prr1z@6
//                                        // Connect to an FTP server on port 21.
//                                        ftp.connect("178.77.67.207", 21, "APPUser", "7Prr1z@6");
//
//                                        // Set binary mode.
//                                        //ftp.bin();
//
//                                        // Change to a new working directory on the FTP server.
//                                        //ftp.cwd("web");
//
//                                        // Upload some files.
//                                        publishProgress(1);
//                                        ftp.stor(new File(extStorageDirectory, uniqueID + ".jpg"));
//                                        //ftp.stor(new File("comicbot-latest.png"));
//
//                                        // You can also upload from an InputStream, e.g.
//                                        //ftp.stor(new FileInputStream(new File("test.png")), "test.png");
//                                        //ftp.stor(someSocket.getInputStream(), "blah.dat");
//
//                                        // Quit from the FTP server.
//                                        ftp.disconnect();
//                                    }
//                                    catch (IOException e) {
//                                        if(e instanceof  FileNotFoundException)
//                                            FtpExceptions=1;
//                                        else
//                                            FtpExceptions=2;
//                                        uniqueID = "ImageNotSelected";
//                                        e.printStackTrace();
//                                    }
//                                    return null;
//                                }
//                                @Override
//                                protected void onPostExecute(String link) {
//                                    if(FtpExceptions==1){
//                                        //Toast.makeText(getContext(),"File does not exist,please reselect the file...",Toast.LENGTH_LONG).show();
//                                        //progressdlg.dismiss();
//                                    }
//                                    else if(FtpExceptions==2) {
//                                        Toast.makeText(getContext(), "Some unknown error during image upload...", Toast.LENGTH_LONG).show();
//                                        // progressdlg.dismiss();
//                                    }
//
//                                    Initilization.androidId = android.provider.Settings.Secure.getString(getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//                                    int AppUserId=sharedpreferences.getInt("USERID",0);
//                                    String urlStr="http://rssapi.psweb.in/everapi.asmx/NewPost?AppUserId="+AppUserId+"&Title="+articleTitle+"&Description="+articleContent+"&PostImage="+uniqueID+"&AndroidId="+Initilization.androidId;
//                                    URL url=null;
//                                    try{
//                                        url = new URL(urlStr);
//                                        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
//                                        url = uri.toURL();
//                                    }
//                                    catch(Exception e){}
//                                    final String urlRequest = url.toString();
//                                    new AsyncTask<Void, Integer, String>() {
//                                        int ExceptionCode = 0;
//                                        String JsoupResopnse = "";
//                                        @Override
//                                        protected void onProgressUpdate(Integer... text) {
//                                            if(text[0]==2)
//                                                progressdlg.setMessage("Connecting to content server");
//                                            if(text[0]==3)
//                                                progressdlg.setMessage("Uploading content...");
//                                        }
//                                        @Override
//                                        protected String doInBackground(Void... params) {
//                                            try {
//                                                publishProgress(3);
//                                                JsoupResopnse = Jsoup.connect(urlRequest).timeout(Initilization.timeout-5).ignoreContentType(true).execute().body();
//                                                int iIndex = JsoupResopnse.indexOf("\">") + 2;
//                                                int eIndex = JsoupResopnse.indexOf("</");
//                                                char jChar[] = JsoupResopnse.toCharArray();
//                                                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex)
//                                                    JsoupResopnse = JsoupResopnse.copyValueOf(jChar, iIndex, (eIndex - iIndex));
//                                            } catch (IOException e) {
//                                                if (e instanceof SocketTimeoutException) {
//                                                    ExceptionCode = 1;
//                                                    return null;
//                                                }
//                                                if (e instanceof HttpStatusException) {
//                                                    ExceptionCode = 2;
//                                                    return null;
//                                                }
//                                            } finally {
//                                                File file = new File(extStorageDirectory, uniqueID + ".jpg");
//                                                file.delete();
//                                                uniqueID="";
//                                            }
//                                            return null;
//                                        }
//                                        @Override
//                                        protected void onPostExecute(String link) {
//                                                if (ExceptionCode == 1)
//                                                    Toast.makeText(getContext(), "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
//                                                else if (ExceptionCode == 2)
//                                                    Toast.makeText(getContext(), "Some server related issue occurred..please try again later", Toast.LENGTH_SHORT).show();
//                                                else if(JsoupResopnse.isEmpty())
//                                                    Toast.makeText(getContext(), "Something went wrong..sorry", Toast.LENGTH_SHORT).show();
//                                                else
//                                                    Toast.makeText(getContext(), "Post upload done "+JsoupResopnse, Toast.LENGTH_SHORT).show();
//
//                                            artImage.setImageResource(R.mipmap.camera2);
//                                            artTitle.setText("");
//                                            artDesc.setText("");
//                                            progress.setVisibility(View.GONE);
//                                            progressdlg.dismiss();
//                                        }
//                                    }.execute();
//                                }
//                            }.execute();
//                        }
//                    }
//                });
//                artPhoto.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        if(uniqueID.length()<4)
//                            uniqueID = UUID.randomUUID().toString().toLowerCase();
//                        selectImage();
//                    }
//                });
//            }
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
                    Log.w("path of image from gallery......******************.........", picturePath + "");
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

        public String getUsername() {
            AccountManager manager = AccountManager.get(getContext());
            Account[] accounts = manager.getAccountsByType("com.google");
            List<String> possibleEmails = new LinkedList<String>();

            for (Account account : accounts) {
                // TODO: Check possibleEmail against an email regex or treat
                possibleEmails.add(account.name);
            }

            if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
                String email = possibleEmails.get(0);
                String[] parts = email.split("@");
                if (parts.length > 0 && parts[0] != null)
                    return parts[0];
                else
                    return "";
            } else
                return "";
        }

        public final static boolean isValidEmail(CharSequence target) {
            return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }

        public boolean isValidNumber(String inString) {
            if(inString.length()==10)
                return (true);
            else
                return (false);
        }

        public boolean isValidName(String inString){
            if(inString.length()>=6)
                return (true);
            else
                return (false);
        }

        public boolean isValidPassowod(String inString){
            if(inString.length()>=7)
                return (true);
            else
                return (false);
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
            Bundle args = new Bundle();
            //args.putInt(ARG_SECTION_NUMBER, position);
            ReusableFragment fragArray[]=new ReusableFragment[100];
            /*switch(position){
                case 0:
                    index=1;
                    ReusableFragment fragmentRe=ReusableFragment.newInstanceRe(position);
                    return fragmentRe;
                    //fragment.setArguments(bundle);
                case 1:
                    index=2;
                    ReusableFragment fragmentRe2=ReusableFragment.newInstanceRe(position);
                    return fragmentRe2;
                    //fragment = new RaggaeMusicFragment();
                case 2:
                    index=3;
                    ReusableFragment fragmentRe3=ReusableFragment.newInstanceRe(position);
                    return fragmentRe3;
                case 3:
                    index=1;
                    ReusableFragment fragmentRe4=ReusableFragment.newInstanceRe(0);
                    return fragmentRe4;
                //fragment.setArguments(bundle);
                case 4:
                    index=2;
                    ReusableFragment fragmentRe5=ReusableFragment.newInstanceRe(1);
                    return fragmentRe5;
                //fragment = new RaggaeMusicFragment();
                case 5:
                    index=3;
                    ReusableFragment fragmentRe6=ReusableFragment.newInstanceRe(2);
                    return fragmentRe6;
                    //fragment = new RapMusicFragment();
            }*/
            if(position<Initilization.newsCategoryLength) {
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
                fragArray[position] = ReusableFragment.newInstanceRe(position, Initilization.newsCategories[position][1]);
                return fragArray[position];
            }
            return null;
        }

        @Override
        public int getCount() {
            //Initilization.newsCategoryLength=15;
            return Initilization.newsCategoryLength;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            /*switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
                case 4:
                    return "SECTION 5";
                case 5:
                    return "SECTION 6";
            }*/
            //For Testing ive done this part
            return(Initilization.newsCategories[position][1]);
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
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            progress.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }
    public void parseResults(String response)
    {
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
            int cuDispOrder=Integer.parseInt(currentNewsCategory);
            categories[cuDispOrder][1]=Initilization.resultArray[i][Initilization.Category];
            categories[cuDispOrder][0]=Initilization.resultArray[i][Initilization.DisplayOrder];
            Initilization.resultArrayLength=i;
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
}
