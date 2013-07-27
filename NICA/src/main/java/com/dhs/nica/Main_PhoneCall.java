package com.dhs.nica;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by natsuyuu on 13-7-26.
 */
public class Main_PhoneCall extends Activity{
    static final String TAG = "dhs_nica";
    private String JSONString;
    private ArrayList<HashMap<String,Object>> arraylist;
    static final String filename2 = "JsonCircle";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_phonecall);
        JSONString = readFileData(filename2);
        Log.d(TAG,String.valueOf(JSONString.length()));
        parseJson(JSONString);
        /**
        Bundle extras = getIntent().getExtras();
        if(extras == null){
            Log.e(TAG, "Main_PhoneCall:null extra");
            return;
        }
        JSONString = extras.getString("JSONString");
        Log.d(TAG,String.valueOf(JSONString.length()));
        //arraylist = parseJson(JSONString);
        try{
            JSONArray jsonArray = new JSONObject(JSONString).getJSONArray("user_info");


            for(int i  = 0; i < jsonArray.length(); i++){

                JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                String jsonpn = jsonObject2.getString("phonenumber");
                String jsonun = jsonObject2.getString("username");
                Log.d(TAG,"Json:" +jsonpn + "  " +"jsonun");
            }

           \
        }catch(Exception e){
            Log.e(TAG,e.toString());
        }
         **/
    }

    private static ArrayList<HashMap<String, Object>> parseJson(String jsonString){
        ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
        try{
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("user_info");

            for(int i  = 0; i < jsonArray.length(); i++){

                JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                String jsonpn = jsonObject2.getString("phonenumber");
                String jsonun = jsonObject2.getString("username");
                Log.d(TAG,"Json:" +jsonpn + "  " +"jsonun");
            }


        }catch(Exception e){
            Log.e(TAG,e.toString());
        }
        return list;
    }

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
