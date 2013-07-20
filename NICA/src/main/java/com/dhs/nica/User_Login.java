package com.dhs.nica;

import com.dhs.nica.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.dhs.nica.Constant;

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


    //Handler dealing with user verification result
    android.os.Handler h = new android.os.Handler(){
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {
                //"yy" Valid number and valid avatar
                case 1:
                    Intent intent = new Intent(getApplicationContext(), Main.class);
                    startActivity(intent);
                    Log.d(TAG, "Result:"+"yy");
                    Toast toast = Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG);
                    toast.show();
                    break;

                // "yn" Valid number without avatar
                case 2:

                    Log.d(TAG, "Result:"+"yn");
                    Intent intent2 = new Intent(getApplicationContext(),)
                    break;

                // "n" Number not registrated
                case 3:
                    break;

                // Invalid return content
                case 4:
                    break;

                // Exception Handling
                case 5:
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

                if(lineStr == "yy"){
                    msg.what = 1;
                } else if(lineStr == "yn"){
                    msg.what = 2;
                }
                else if(lineStr == "n"){
                    msg.what = 3;
                }
                else{
                    msg.what = 4;
                }
                h.sendMessage(msg);

            }catch (Exception e){Log.d(TAG,"Expection:"+ e.toString());
                msg.what = 5;
                h.sendMessage(msg);}
            finally{
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.d(TAG,"Expection:"+ e.toString());
                        e.printStackTrace();
                        msg.what = 5;
                        h.sendMessage(msg);}
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

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

        //Main
        login();


    }


    private void login(){

        String PN = loadPhoneStatus();
        if(PN!="N"){
            //Get phone number successfully, check with server
            //Start a new thread from runnable
            new Thread(runnable).start();

        }else{
            //Failed, open User_GetPhoto activity to get user input
            Intent i = new Intent(this, User_GetPhoneNumber.class);

        }
    }


    private String loadPhoneStatus(){
        TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = phoneMgr.getLine1Number();

        //Verify validity of phone number
        Pattern pattern = Pattern.compile("^[0-9]{8}");
        Matcher matcher = pattern.matcher(phoneNumber);
        Boolean result = matcher.matches();

        if(phoneNumber!= null && result){
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
}
