package com.evernews.evernews;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUp.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUp extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public SignUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUp.
     */
    // TODO: Rename and change types and number of parameters
    private final String USERLOGINDETAILS = "USERLOGINDETAILS" ;
    private static SharedPreferences sharedpreferences;
    public static String uniqueID="";
    TextView userName; //= (TextView) view.findViewById(R.id.userName);
    TextView userEmail; // = (TextView) getActivity().findViewById(R.id.userEmail);
    TextView userNumber ; //= (TextView) getActivity().findViewById(R.id.userNumber);
    TextView password ; //= (TextView) getActivity().findViewById(R.id.password);
    TextView comfirmpassword;
    Button signUp; // = (Button) getActivity().findViewById(R.id.signup);
    Button noAction; // = (Button) getActivity().findViewById(R.id.noAction);
    Button fbSignup ; //= (Button) getActivity().findViewById(R.id.facebook);
    Button gSignup; // = (Button) getActivity().findViewById(R.id.google);
    Pattern emailPattern;
    Account[] accounts;
    public static SignUp newInstance(String param1) {
        SignUp fragment = new SignUp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        // Inflate the layout for this fragment
        emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        accounts = AccountManager.get(getContext()).getAccounts();
        sharedpreferences = getActivity().getSharedPreferences(USERLOGINDETAILS, Context.MODE_PRIVATE);
        userName = (TextView) view.findViewById(R.id.userName);
        userEmail = (TextView) view.findViewById(R.id.userEmail);
        userNumber = (TextView) view.findViewById(R.id.userNumber);
        password = (TextView) view.findViewById(R.id.password);
        comfirmpassword = (TextView) view.findViewById(R.id.comfirmpassword);
        signUp = (Button) view.findViewById(R.id.signup);
        signUp.setOnClickListener(this);
        noAction = (Button) view.findViewById(R.id.noAction);
        noAction.setOnClickListener(this);
        fbSignup = (Button) view.findViewById(R.id.facebook);
        fbSignup.setOnClickListener(this);
        gSignup = (Button) view.findViewById(R.id.google);
        gSignup.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.signup: {
                    final String userNames = userName.getText().toString().replace(" ","");
                    final String emails = userEmail.getText().toString().replace(" ", "");
                    final String pnumbers = userNumber.getText().toString().replace(" ", "");
                    final String passWord = password.getText().toString().replace(" ", "");
                    final String confirmpassWord = comfirmpassword.getText().toString().replace(" ", "");
                    String errorString = "Please correct the following errors...\r\n";
                    boolean validEmail = isValidEmail(emails);
                    boolean validNumber = isValidNumber(pnumbers);
                    boolean validUsername = isValidName(userNames);
                    boolean validPassword = isValidPassword(passWord);
                    if (validUsername == false)
                        errorString = errorString + ">Invalid Username (6 Char min)\r\n";
                    if (validEmail == false)
                        errorString = errorString + ">Invalid email address\r\n";
                    if (validNumber == false)
                        errorString = errorString + ">Invalid mobile number\r\n";
                    if (validPassword == false)
                        errorString = errorString + ">Invalid Password\r\n";
                    if(passWord.compareTo(confirmpassWord)!=0){
                        validPassword=false;
                        errorString = errorString + ">Passwords do not match!";
                    }
                    if (validEmail == true && validNumber == true && validUsername == true && validPassword == true) {
                        //Post details to server
                        Initilization.androidId = android.provider.Settings.Secure.getString(getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                        String urlStr="http://rssapi.psweb.in/everapi.asmx/RegisterUser?FullName=" + userNames + "&Email=" + emails + "&Password=" + passWord + "&Mobile=" + pnumbers + "&AndroidId=" + Initilization.androidId;
                        URL url=null;
                        try{
                            url = new URL(urlStr);
                            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                            url = uri.toURL();
                        }
                        catch(Exception e){}
                        final String urlRequest = url.toString();
                        Main.progress.setVisibility(View.VISIBLE);
                        new AsyncTask<Void, Void, String>() {
                            int ExceptionCode = 0;
                            String JsoupResopnse = "";
                            @Override
                            protected String doInBackground(Void... params) {
                                try {
                                    JsoupResopnse = Jsoup.connect(urlRequest).timeout(Initilization.timeout).ignoreContentType(true).execute().body();
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
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String link) {
                                if (ExceptionCode > 0) {
                                    if (ExceptionCode == 1)
                                        Toast.makeText(getContext(), "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                    if (ExceptionCode == 2)
                                        Toast.makeText(getContext(), "Some server related issue occurred..please try again later", Toast.LENGTH_SHORT).show();
                                }
                                int JsoupResp=-99;
                                try{
                                    JsoupResp=Integer.valueOf(JsoupResopnse);
                                }catch (NumberFormatException e){

                                }
                                if (JsoupResopnse.isEmpty() == false &&  JsoupResp > 0) {
                                    //Store all user data into shared prefrence
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(Main.USERNAME, userNames);
                                    editor.putInt(Main.USERID, JsoupResp);
                                    editor.putString(Main.USEREMAIL, emails);
                                    editor.putString(Main.USERPHONENUMBER, pnumbers);
                                    editor.putBoolean(Main.ISREGISTRED, true);
                                    editor.putBoolean(Main.LOGGEDIN, true);
                                    editor.commit();
                                    Toast.makeText(getContext(), "Registration complete your UserId is " + JsoupResopnse, Toast.LENGTH_LONG).show();
                                    userName.setText("");
                                    userEmail.setText("");
                                    userNumber.setText("");
                                    password.setText("");
                                    comfirmpassword.setText("");
                                    Main.progress.setVisibility(View.GONE);
                                } else
                                    Toast.makeText(getContext(), "Please check the filled details", Toast.LENGTH_SHORT).show();
                            }
                        }.execute();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(errorString)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        return;
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            break;
            case R.id.noAction: {
                Toast.makeText(getContext(),"Login Call will come here",Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.facebook: {
                Toast.makeText(getContext(),"Facebook Call will come here",Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.google: {
                Toast.makeText(getContext(),"Google Call will come here",Toast.LENGTH_SHORT).show();
            }
            break;
        }
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

    public boolean isValidPassword(String inString){
        if(inString.length()>=7)
            return (true);
        else
            return (false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
