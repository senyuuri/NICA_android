package com.dhs.nica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * Created by natsuyuu on 13-7-26.
 */
public class Main_PhoneCall extends Activity{
    static final String TAG = "dhs_nica";
//    private String JSONString;
    private ArrayList<HashMap<String,Object>> arraylist = new ArrayList<HashMap<String, Object>>();
//    static final String filename2 = "JsonCircle";
    private GridView gridview;
    private Integer total;
    DisplayImageOptions options;
    String[] imageUrls=new String[100];
    String[] usernames=new String[100];
    String[] pnlist = new String[100];
    private String circleinfo;
    protected AbsListView listView;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_list);
//        gridview = (GridView) findViewById(R.id.gridview);
        Bundle bundle = this.getIntent().getExtras();
        circleinfo=bundle.getString("user_info");
        total=0;

        //To-do read json from local if present
        //Otherwise fetch from server

//        JSONString = readFileData(filename2);
  //      Log.d(TAG,String.valueOf(JSONString.length()));
        //parseJson(JSONString);
        try{
            JSONObject obj = new JSONObject(circleinfo);
            JSONArray jarry = obj.getJSONArray("user_info");
            total=jarry.length();
            for(int i  = 0; i < jarry.length(); i++){
                JSONObject temp=jarry.getJSONObject(i);
                usernames[i]=temp.getString("username");
                pnlist[i] = temp.getString("phonenumber");
                imageUrls[i] = "http://metallica-nica.appspot.com/getavatar?pn="+temp.getString("phonenumber");
                Log.d(TAG,"Reading Json " +i +"  :"+imageUrls[i]);

            }
        }catch (Exception e){Log.d(TAG,"Expection:"+ e.toString());}
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(com.dhs.nica.R.drawable.ic_empty)
                .showImageOnFail(com.dhs.nica.R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
//                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        listView = (ListView) findViewById(android.R.id.list);
        ((ListView) listView).setAdapter(new ItemAdapter());
//       listView.setOnItemClickListener(new AdapterView.OnitemClickListener() {
     /*       @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
                try{
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+pnlist[position]));
                    startActivity(i);}catch (Exception e){Log.d(TAG,e.toString());}
                }
        });


    }
    @Override
    public void onBackPressed() {
        AnimateFirstDisplayListener.displayedImages.clear();
        super.onBackPressed();
    }
/*
    private void startImagePagerActivity(int position) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        intent.putExtra(Extra.IMAGES, imageUrls);
        intent.putExtra(Extra.IMAGE_POSITION, position);
        startActivity(intent);
    }*/
    class ItemAdapter extends BaseAdapter {

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        private class ViewHolder {
            public TextView text;
            public ImageView image;
        }

        @Override
        public int getCount() {
            return total;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) view.findViewById(R.id.text);
                holder.image = (ImageView) view.findViewById(R.id.image);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.text.setText(usernames[position]);

            imageLoader.displayImage(imageUrls[position], holder.image, options, animateFirstListener);

            return view;
        }
    }
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
/*
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

/*
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
*/