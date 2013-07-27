package com.dhs.nica;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_phonecall);
        Bundle extras = getIntent().getExtras();
        if(extras == null){
            Log.e(TAG, "Main_PhoneCall:null extra");
            return;
        }
        JSONString = extras.getString("JSONString");
    }

    private static List<Map<String, Object>> parseJson(String jsonString){
        try{
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("user_info");
            JSONObject jsonObject;
            for(int i  = 0; i < jsonArray.length(); i++){

            }


        }catch(Exception e){
            Log.e(TAG,e.toString());
        }
    }
    return list;
}
