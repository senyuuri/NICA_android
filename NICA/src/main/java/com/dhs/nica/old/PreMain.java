package com.dhs.nica;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by natsuyuu on 13-7-29.
 */
public class PreMain extends BaseActivity {
    private ProgressDialog progressDialog;
    static final String TAG = "dhs_nica";
    private String lineStr;
    private String lineStr2;
    static final String filename = "PN";
    String[] imageUrls = new String[100];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true, false);
        new Thread(){
            @Override
            public void run(){
                BufferedReader in = null;
                try{
                    //Get circle contact info
                    HttpClient client = new DefaultHttpClient();
                    String urlnew = (String) Constant.SERVER_CIRCLE_INFO+"?pn="+readFileData(filename);
                    Log.d(TAG, urlnew);
                    HttpGet request = new HttpGet(urlnew);
                    HttpResponse response = client.execute(request);
                    if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                        HttpEntity resEntity = response.getEntity();
                        lineStr = EntityUtils.toString(resEntity);
                    }
                    Log.d(TAG, "Circlrinfo fetched: " +lineStr);


                    //get image url list
                    HttpClient client2 = new DefaultHttpClient();
                    HttpPost request2 = new HttpPost(Constant.SERVER_IMAGE_LIST_GENERATE);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

                    //Add parameter to POST
                    postParameters.add(new BasicNameValuePair("phonenum", readFileData(filename)));
                    Log.d(TAG,"Parameter added");
                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                            postParameters);
                    request2.setEntity(formEntity);
                    HttpResponse response2 = client2.execute(request2);
                    if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                        HttpEntity resEntity = response2.getEntity();
                        lineStr2 = EntityUtils.toString(resEntity);
                    }
                    Log.d(TAG, "ImgList fetched: " +lineStr2);

                    //Convert JsonArray to String[]

                    JSONObject obj = new JSONObject(lineStr2);
                    JSONArray jarry = obj.getJSONArray("image_ids");
                    Log.d(TAG,String.valueOf(jarry.length()));
                    for(int i  = 0; i < jarry.length(); i++){

                        imageUrls[i] = Constant.SERVER_GET_IMAGE+jarry.getString(i);
                        Log.d(TAG,"Reading Json " +i +"  :"+imageUrls[i]);

                    }



                    Message msg = new Message();
                    msg.what = 1;
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
        }.start();

    }

    android.os.Handler h = new android.os.Handler(){
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {

                case 1:
                    progressDialog.dismiss();
                    Intent i = new Intent(getApplicationContext(),Main.class);
                    i.putExtra("imageurl",imageUrls);
                    i.putExtra("circleinfo",lineStr);
                    startActivity(i);
                    break;
            }
        }
    };

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

}