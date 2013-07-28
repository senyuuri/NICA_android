package com.dhs.nica;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by natsuyuu on 13-7-28.
 */
public class DownloadService {
    static final String TAG = "dhs_nica";
    private String lineStr;


    static final String filename = "PN";
    static final String filename2 = "JsonCircle";

    public String circleupdate(String phonenumber){


        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            String urlnew = (String) Constant.SERVER_CIRCLE_INFO+"?pn="+phonenumber;
            Log.d(TAG,urlnew);
            HttpGet request = new HttpGet(urlnew);
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                HttpEntity resEntity = response.getEntity();
                lineStr = EntityUtils.toString(resEntity);
            }
            return lineStr;
            /**
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Messenger messenger = (Messenger) extras.get("MESSENGER");
                Message msg = Message.obtain();
                //msg.arg1 = result;
                //msg.obj = output.getAbsolutePath();
                try {
                    messenger.send(msg);
                } catch (android.os.RemoteException e1) {
                    Log.e(TAG, "Exception sending message", e1);
                }

            }
            **/

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
        return null;
    }

    public void downloadAvater(){

    try{
        JSONArray jsonArray = new JSONObject(lineStr).getJSONArray("user_info");

        for(int i  = 0; i < jsonArray.length(); i++){

            JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
            String jsonpn = jsonObject2.getString("phonenumber");
            String jsonun = jsonObject2.getString("username");
            Log.d(TAG,"Json:" +jsonpn + "  " +"jsonun");
        }


    }catch(Exception e){
        Log.e(TAG,e.toString());
    }
    }



    /** Intent method
    @Override
    protected void onHandleIntent(Intent intent) {
        Uri data = intent.getData();
        String urlPath = intent.getStringExtra("urlpath");
        String fileName = data.getLastPathSegment();
        File output = new File(Environment.getExternalStorageDirectory(),
                fileName);
        if (output.exists()) {
            output.delete();
        }

        InputStream stream = null;
        FileOutputStream fos = null;
        try {

            URL url = new URL(urlPath);
            stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            fos = new FileOutputStream(output.getPath());
            int next = -1;
            while ((next = reader.read()) != -1) {
                fos.write(next);
            }
            // Sucessful finished
            //result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Messenger messenger = (Messenger) extras.get("MESSENGER");
            Message msg = Message.obtain();
            //msg.arg1 = result;
            msg.obj = output.getAbsolutePath();
            try {
                messenger.send(msg);
            } catch (android.os.RemoteException e1) {
                Log.w(getClass().getName(), "Exception sending message", e1);
            }

        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };
     **/


    public String readFileData(String fileName, Context ctx){
            String result="";
            try {
                FileInputStream fin = ctx.openFileInput(filename);
                int lenght = fin.available();
                byte[] buffer = new byte[lenght];
                fin.read(buffer);
                result = EncodingUtils.getString(buffer, "UTF-8");
                Log.d(TAG, fileName + " : " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"I/O error" + e);
            }
            return result;
        }



        public void writeFileData(String filename, String message,Context ctx){
            try {
                FileOutputStream fout = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
                byte[]  bytes = message.getBytes();
                fout.write(bytes);//
                fout.close();//
                Log.d(TAG,"File " + filename+" : " + message);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "I/O error" + e);
            }
        }

}
