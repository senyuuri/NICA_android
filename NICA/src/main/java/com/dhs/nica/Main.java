package com.dhs.nica;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by natsuyuu on 13-7-20.
 */
public class Main extends Activity {
    static final String TAG = "dhs_nica";
    private String lineStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        int a = 1;
    }


    android.os.Handler h = new android.os.Handler(){
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {
                //"yy" Valid number and valid avatar
                case 1:
                Intent i = new Intent(getApplicationContext(), Main_PhoneCall.class);
                i.putExtra("JSON_String",lineStr);
                startActivity(i);
                break;
            }
        }
    };



    /*
    Update contact from circle
     */
    private Message msg = new Message();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            BufferedReader in = null;
            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(Constant.SERVER_CIRCLE_INFO);
                HttpResponse response = client.execute(request);
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    Log.d(TAG, "Circle response downloaded.");
                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                }
                StringBuffer string = new StringBuffer("");
                lineStr = "";
                while ((lineStr = in.readLine()) != null) {
                    string.append(lineStr + "\n");
                }
                in.close();

                msg.what = 1;
                Log.d(TAG, "msg.what:1，raw Json: " + lineStr);

                h.sendMessage(msg);

            }catch (Exception e){Log.d(TAG,"Expection:"+ e.toString());
              }
            finally{
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.d(TAG,"Expection:"+ e.toString());
                        e.printStackTrace();
                      }
                }
            }
        }
    };
}
