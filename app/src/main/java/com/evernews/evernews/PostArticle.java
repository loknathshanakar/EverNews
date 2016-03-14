package com.evernews.evernews;

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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jibble.simpleftp.SimpleFTP;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostArticle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostArticle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostArticle extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView title,post;
    private ImageView viewImage;
    private final String USERLOGINDETAILS = "USERLOGINDETAILS" ;
    private static SharedPreferences sharedpreferences;
    public static String uniqueID="";
    private OnFragmentInteractionListener mListener;
    EditText mSpinner;
    public PostArticle() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostArticle.
     */
    // TODO: Rename and change types and number of parameters
    public static PostArticle newInstance(String param1, String param2) {
        PostArticle fragment = new PostArticle();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_article, container, false);
        Button btnSelectPhoto = (Button) view.findViewById(R.id.btnSelectPhoto);
        btnSelectPhoto.setOnClickListener(this);
        Button submitPost = (Button) view.findViewById(R.id.submitPost);
        submitPost.setOnClickListener(this);
        title=(TextView)view.findViewById(R.id.title);
        post=(TextView)view.findViewById(R.id.post);
        viewImage=(ImageView)view.findViewById(R.id.viewImage);
        sharedpreferences = getActivity().getSharedPreferences(USERLOGINDETAILS, Context.MODE_PRIVATE);
        mSpinner = (EditText) view.findViewById(R.id.spinner_post);
        //Fill spinner
        /*String[] arraySpinner=new String[] {
                "Select your city", "Delhi", "Mumbai", "Bangalore", "One more city"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arraySpinner);
        mSpinner.setAdapter(adapter);*/
        return view;
        //return inflater.inflate(R.layout.fragment_post_article, container, false);
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.submitPost: {
                    final String articleTitle=title.getText().toString();
                    final String articleContent=post.getText().toString();
                    final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    if(articleTitle.length()<8 || articleContent.length()<20 || mSpinner.getText().toString().length()<=2){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Article title or content does not meet the required specification")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        return;
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else{
                        new AsyncTask<Void, Integer, String>() {
                            int ExceptionCode = 0;
                            String JsoupResopnse = "";
                            int FtpExceptions= 0;
                            ProgressDialog progressdlg;
                            @Override
                            protected void onProgressUpdate(Integer... text) {
                                if(text[0]==1)
                                    progressdlg.setMessage("Uploading image...");
                                if(text[0]==2)
                                    progressdlg.setMessage("Connecting to content server");
                                if(text[0]==3)
                                    progressdlg.setMessage("Uploading content...");
                            }

                            @Override
                            protected void onPreExecute() {
                                Main.progress.setVisibility(View.VISIBLE);
                                progressdlg = new ProgressDialog(getContext());
                                progressdlg.setMessage("Connecting to server...");
                                progressdlg.setTitle("Posting article");
                                progressdlg.setCancelable(false);
                                progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressdlg.setIndeterminate(true);
                                progressdlg.show();
                            }
                            @Override
                            protected String doInBackground(Void... params) {
                                try {
                                    SimpleFTP ftp = new SimpleFTP();
                                    //HostName : 178.77.67.207
                                    //username : APPUser
                                    //Password : 7Prr1z@6
                                    // Connect to an FTP server on port 21.
                                    ftp.connect("178.77.67.207", 21, "APPUser", "7Prr1z@6");

                                    // Set binary mode.
                                    //ftp.bin();

                                    // Change to a new working directory on the FTP server.
                                    //ftp.cwd("web");

                                    // Upload some files.
                                    publishProgress(1);
                                    ftp.stor(new File(extStorageDirectory, uniqueID + ".jpg"));
                                    //ftp.stor(new File("comicbot-latest.png"));

                                    // You can also upload from an InputStream, e.g.
                                    //ftp.stor(new FileInputStream(new File("test.png")), "test.png");
                                    //ftp.stor(someSocket.getInputStream(), "blah.dat");

                                    // Quit from the FTP server.
                                    ftp.disconnect();
                                }
                                catch (IOException e) {
                                    if(e instanceof FileNotFoundException)
                                        FtpExceptions=1;
                                    else
                                        FtpExceptions=2;
                                    uniqueID = "ImageNotSelected";
                                    e.printStackTrace();
                                }
                                return null;
                            }
                            @Override
                            protected void onPostExecute(String link) {
                                if(FtpExceptions==1){
                                    //Toast.makeText(getContext(),"File does not exist,please reselect the file...",Toast.LENGTH_LONG).show();
                                    //progressdlg.dismiss();
                                }
                                else if(FtpExceptions==2) {
                                    Toast.makeText(getContext(), "Some unknown error during image upload...", Toast.LENGTH_LONG).show();
                                    // progressdlg.dismiss();
                                }

                                Initilization.androidId = android.provider.Settings.Secure.getString(getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                int AppUserId=sharedpreferences.getInt("USERID", 0);
                                String urlStr="http://rssapi.psweb.in/everapi.asmx/NewPost?AppUserId="+AppUserId+"&Title="+articleTitle+"&Description="+articleContent+"&PostImage="+uniqueID+"&AndroidId="+Initilization.androidId;
                                URL url=null;
                                try{
                                    url = new URL(urlStr);
                                    URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                                    url = uri.toURL();
                                }
                                catch(Exception e){}
                                final String urlRequest = url.toString();
                                new AsyncTask<Void, Integer, String>() {
                                    int ExceptionCode = 0;
                                    String JsoupResopnse = "";
                                    @Override
                                    protected void onProgressUpdate(Integer... text) {
                                        if(text[0]==2)
                                            progressdlg.setMessage("Connecting to content server");
                                        if(text[0]==3)
                                            progressdlg.setMessage("Uploading content...");
                                    }
                                    @Override
                                    protected String doInBackground(Void... params) {
                                        try {
                                            publishProgress(3);
                                            JsoupResopnse = Jsoup.connect(urlRequest).timeout(Initilization.timeout-5).ignoreContentType(true).execute().body();
                                            int iIndex = JsoupResopnse.indexOf("\">") + 2;
                                            int eIndex = JsoupResopnse.indexOf("</");
                                            char jChar[] = JsoupResopnse.toCharArray();
                                            if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex)
                                                JsoupResopnse = JsoupResopnse.copyValueOf(jChar, iIndex, (eIndex - iIndex));
                                        } catch (IOException e) {
                                            if (e instanceof SocketTimeoutException) {
                                                ExceptionCode = 1;
                                                return null;
                                            }
                                            if (e instanceof HttpStatusException) {
                                                ExceptionCode = 2;
                                                return null;
                                            }
                                        } finally {
                                            File file = new File(extStorageDirectory, uniqueID + ".jpg");
                                            file.delete();
                                            uniqueID="";
                                        }
                                        return null;
                                    }
                                    @Override
                                    protected void onPostExecute(String link) {
                                        int JsoupResp=-99;
                                        try{
                                            JsoupResp=Integer.valueOf(JsoupResopnse);
                                        }catch (NumberFormatException e){
                                        }
                                        if (ExceptionCode == 1)
                                            Toast.makeText(getContext(), "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                        else if (ExceptionCode == 2)
                                            Toast.makeText(getContext(), "Some server related issue occurred..please try again later", Toast.LENGTH_SHORT).show();
                                        else if(JsoupResopnse.isEmpty())
                                            Toast.makeText(getContext(), "Something went wrong..sorry", Toast.LENGTH_SHORT).show();
                                        else if(JsoupResp>0)
                                            Toast.makeText(getContext(), "Post upload done "+JsoupResopnse, Toast.LENGTH_SHORT).show();
                                        viewImage.setImageResource(R.mipmap.camera2);
                                        title.setText("");
                                        post.setText("");
                                        mSpinner.setText("");
                                        Main.progress.setVisibility(View.GONE);
                                        progressdlg.dismiss();
                                    }
                                }.execute();
                            }
                        }.execute();
                    }
                }
            break;
            case R.id.btnSelectPhoto: {
                if(uniqueID.length()<4)
                    uniqueID = UUID.randomUUID().toString().toLowerCase();
                selectImage();
            }
            break;
        }
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
        if (resultCode == getActivity().RESULT_OK) {
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
                    viewImage.setImageBitmap(bitmap);
                    //Glide.with(getContext()).load(bitmap).asBitmap().fitCenter().into(viewImage);
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
                //Glide.with(getContext()).load(thumbnail).asBitmap().fitCenter().into(viewImage);
                viewImage.setImageBitmap(thumbnail);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
