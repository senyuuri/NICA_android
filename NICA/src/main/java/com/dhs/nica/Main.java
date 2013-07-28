package com.dhs.nica;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    static final String filename = "PN";
    static final String filename2 = "JsonCircle";
    private Button button1,button2;
    private File cache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button1 = (Button) findViewById(R.id.main_uploadbutton);
        button2 = (Button) findViewById(R.id.main_photocancel);
        //new Thread(runnable).start();

        /*Create new cache folder
        cache = new File(Environment.getExternalStorageDirectory(), "cache");

        if(!cache.exists()){
            cache.mkdirs();
        }*/

        DownloadService ds = new DownloadService();
        String phonenumber = readFileData(filename);
        String rawcontact = ds.circleupdate(phonenumber);
        writeFileData(filename2,rawcontact);
    }



    public void call(View view){
        Intent icall = new Intent(getApplicationContext(),Main_PhoneCall.class);
        startActivity(icall);

    }

    public void photoupload(View view){
        Intent iphoto = new Intent(getApplicationContext(),Main_PhotoUpload_new.class);
        startActivity(iphoto);

    }

    /**
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Delete cache when exit
        File[] files = cache.listFiles();
        for(File file :files){
            file.delete();
        }
        cache.delete();
    }
    android.os.Handler h = new android.os.Handler(){
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {
                //"yy" Valid number and valid avatar
                case 1:
                Intent i = new Intent(getApplicationContext(), Main_PhoneCall.class);
                writeFileData(filename2,lineStr);
                startActivity(i);
                break;
            }
        }
    };






    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
        }
    };


     **/
    public String readFileData(String fileName){
        String result="";
        try {
            FileInputStream fin = openFileInput(fileName);
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

}
