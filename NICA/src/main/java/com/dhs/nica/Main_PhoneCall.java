package com.dhs.nica;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    private ArrayList<HashMap<String,Object>> arraylist = new ArrayList<HashMap<String, Object>>();
    static final String filename2 = "JsonCircle";
    private GridView gridview;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_phonecall);
        gridview = (GridView) findViewById(R.id.gridview);

        //To-do read json from local if present
        //Otherwise fetch from server

        JSONString = readFileData(filename2);
        Log.d(TAG,String.valueOf(JSONString.length()));
        //parseJson(JSONString);


        SimpleAdapter saItem = new SimpleAdapter(this,
                arraylist, //data source
                R.layout.main_phonecall_relative, //xml implementation
                new String[]{"ItemImage","ItemText"}, //corresponding map key
                new int[]{R.id.ItemImage,R.id.ItemText});  //corresponding R id


        for(int i = 1;i < 10;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.placehold200);
            map.put("ItemText", ""+i);
            arraylist.add(map);
        }

        gridview.setAdapter(saItem);

        gridview.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
                    {
                        int index=arg2+1;//id start from 0 so need + 1
                        Toast.makeText(getApplicationContext(), "You have pressed " + index, 0).show();

                    }
                }
        );
    }

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
