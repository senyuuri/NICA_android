package com.dhs.nica;

import com.dhs.nica.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.http.util.EncodingUtils;

import com.dhs.nica.Constant;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class User_Login extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    /**
     * Tag for log output
     */
    static final String TAG = "dhs_nica";


    /**
     * Result for user verification
     */
    private String lineStr;

    String phoneNumber = new String();

    String FileName = "PN";

    private int REQUEST_CODE = 1;


    //Handler dealing with user verification result
    android.os.Handler h = new android.os.Handler(){
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {
                //"y" Registered user
                case 1:
                    Intent intent = new Intent(getApplicationContext(), PreMain.class);
                    startActivity(intent);
                    Log.d(TAG, "LoginResult:"+"y");
                    Toast toast = Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG);
                    toast.show();
                    break;

                // "n"
                case 2:
                    Log.d(TAG, "Result:"+"n");
                    Intent intent2 = new Intent(getApplicationContext(), User_GetName.class);
                    intent2.putExtra("pn",phoneNumber);
                    startActivity(intent2);
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Welcome, new user:)",Toast.LENGTH_LONG);
                    toast2.show();
                    break;



                // Invalid return content
                case 3:
                    //do something here to show error
                    Log.e(TAG,"Handler: Invalid return content");
                    Toast toast3 = Toast.makeText(getApplicationContext(),"Network not available. Please check your 3G/wifi connection:(",Toast.LENGTH_LONG);
                    toast3.show();
                    break;


            }
        }
    };




    //Verify if the phone number has been registered
    private Message msg = new Message();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            BufferedReader in = null;
            try{
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(Constant.SERVER_LOGIN);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

                //Add parameter to POST
                postParameters.add(new BasicNameValuePair("phonenum", phoneNumber));
                Log.d(TAG,"Parameter added");
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                        postParameters);
                request.setEntity(formEntity);
                HttpResponse response = client.execute(request);
                in = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent()));
                lineStr = in.readLine();
                Log.d(TAG, "LineStr:" + lineStr);
                in.close();

                //Check response status
                msg = h.obtainMessage();
                if(lineStr.equals("y") ){
                    msg.what = 1;
                    Log.d(TAG,"msg.what:1");

                } else if(lineStr.equals("n")){
                    msg.what = 2;
                    Log.d(TAG,"msg.what:2");
                }
                else{
                    msg.what = 3;
                    Log.d(TAG,"msg.what:3");
                }
                h.sendMessage(msg);

            }catch (Exception e){Log.d(TAG,"Expection:"+ e.toString());
                msg = h.obtainMessage();
                msg.what = 3;
                h.sendMessage(msg);}
            finally{
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.d(TAG,"Expection:"+ e.toString());
                        e.printStackTrace();
                        msg = h.obtainMessage();
                        msg.what = 3;
                        h.sendMessage(msg);}
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        try{Thread.sleep(2000);}catch (Exception e){Log.e(TAG,e.toString());};

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayUseLogoEnabled(true);
        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        //test
        //Intent i = new Intent(this,Main.class);
        //startActivity(i);

        Context context = getApplicationContext();
        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config  =
                ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(config);

        //Main
        login();


    }


    private void login(){

        String PN = loadPhoneStatus();
        String PN_local = readFileData(FileName);
        if(PN_local.length()== 8){
            //Read number from local cache if exist
            phoneNumber = PN_local;
            new Thread(runnable).start();
        }
        else if(PN!="N"){
            //Get phone number successfully, check with server
            //Start a new thread from runnable
            writeFileData(FileName,phoneNumber);
            new Thread(runnable).start();

        }else{
            //Failed, open User_GetPhoto activity to get user input
            Intent i = new Intent(this, User_GetPhoneNumber.class);
            startActivityForResult(i,REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("returnNumber")) {

                phoneNumber = data.getExtras().getString("returnNumber");
                writeFileData(FileName,phoneNumber);
                new Thread(runnable).start();
            }
        }
    }

    //To-do: activate button only if connection failed
    public void retry(View view){
        login();
    }

    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    private String loadPhoneStatus(){
        TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = phoneMgr.getLine1Number();

        //Verify validity of phone number

        /* !regex slow performance

        Pattern pattern = Pattern.compile("^[0-9]{8}");
        Matcher matcher = pattern.matcher(phoneNumber);
        Boolean result = matcher.matches();
        */

        if(phoneNumber!= null && isNumeric(phoneNumber) && phoneNumber.length() == 8){
            //Obtain phone number
            Log.d(TAG, "Phone Number:" + "Success. "+ phoneMgr.getLine1Number());
            Log.d(TAG,"SIM status:"+phoneMgr.getSimState());
            return phoneNumber;
        } else {
            Log.d(TAG, "Phone Number:" + "Failed. "+ phoneMgr.getLine1Number());
            Log.d(TAG,"SIM status:"+phoneMgr.getSimState());
            Log.d(TAG, "PN not exist.");}
        return "N";
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    public void writeFileData(String filename, String message){
        try {
            FileOutputStream fout = openFileOutput(filename, MODE_PRIVATE);
            byte[]  bytes = message.getBytes();
            fout.write(bytes);//
            fout.close();//
            Log.d(TAG,"File " + filename+" : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "I/O error" + e);
        }
    }

    public String readFileData(String fileName){
        String result="";
        try {
            FileInputStream fin = openFileInput(fileName);
            int lenght = fin.available();
            byte[] buffer = new byte[lenght];
            fin.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
            Log.d(TAG, "File " + fileName + " : " + result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"I/O error" + e);
        }
        return result;
    }

}
