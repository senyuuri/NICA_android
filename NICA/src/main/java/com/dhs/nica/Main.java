package com.dhs.nica;

import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by natsuyuu on 13-7-20.
 */


public class Main extends BaseActivity {


    private static final String STATE_POSITION = "STATE_POSITION";

    DisplayImageOptions options;

    ViewPager pager;
    //DisplayImageOptions options;


    static final String TAG = "dhs_nica";
    private String lineStr;
    private String lineStr2;
    //private String[] imglist =new String[]{};
    static final String filename = "PN";
    static final String filename2 = "JsonCircle";
    private Button button1,button2;
    private File cache;

    String[] imageUrls = new String[]{};

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(com.dhs.nica.R.layout.main);
        setContentView(com.dhs.nica.R.layout.main);
        //button1 = (Button) findViewById(com.dhs.nica.R.id.main_uploadbutton);
        //button2 = (Button) findViewById(com.dhs.nica.R.id.main_photocancel);
        //new Thread(runnable).start();

        /*Create new cache folder
        cache = new File(Environment.getExternalStorageDirectory(), "cache");

        if(!cache.exists()){
            cache.mkdirs();
        }*/




        //String[] imageUrls = Constant.IMAGES;

        int pagerPosition = 0;

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(com.dhs.nica.R.drawable.ic_empty)
                .showImageOnFail(com.dhs.nica.R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        pager = (ViewPager) findViewById(com.dhs.nica.R.id.pager);
        final ImagePagerAdapter adapter = new ImagePagerAdapter(imageUrls);
        pager.setAdapter(adapter);
        pager.setCurrentItem(pagerPosition);


        progressDialog = ProgressDialog.show(Main.this, "Loading", "Please wait...", true, false);
        //DownloadService ds = new DownloadService();
        //String phonenumber = readFileData(filename);
        //String rawcontact = ds.circleupdate(phonenumber);
        //writeFileData(filename2,rawcontact);

        //new Thread(runnable).start();

        new Thread(){
            @Override
            public void run(){
                BufferedReader in = null;
                try{
                    //Get circle contact info
                    HttpClient client = new DefaultHttpClient();
                    String urlnew = (String) Constant.SERVER_CIRCLE_INFO+"?pn="+readFileData(filename);
                    Log.d(TAG,urlnew);
                    HttpGet request = new HttpGet(urlnew);
                    HttpResponse response = client.execute(request);
                    if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                        HttpEntity resEntity = response.getEntity();
                        lineStr = EntityUtils.toString(resEntity);
                    }
                    Log.d(TAG, "Circlrinfo fetched: " +lineStr);


                    //get image url list
                    HttpClient client2 = new DefaultHttpClient();
                    String urlnew2 = (String) Constant.SERVER_IMAGE_LIST_GENERATE+"?pn="+readFileData(filename);
                    Log.d(TAG,urlnew2);
                    HttpGet request2 = new HttpGet(urlnew2);
                    HttpResponse response2 = client2.execute(request2);
                    if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                        HttpEntity resEntity = response2.getEntity();
                        lineStr2 = EntityUtils.toString(resEntity);
                    }
                    Log.d(TAG, "ImgList fetched: " +lineStr2);

                    //Convert JsonArray to String[]
                    JSONArray jsonArray = new JSONObject(lineStr2).getJSONArray("imglist");
                    for(int i  = 0; i < jsonArray.length(); i++){

                        JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                        imageUrls[i] = jsonObject2.toString();
                        Log.d(TAG,"Reading Json " +i +"  :"+imageUrls[i]);

                    }
                    adapter.notifyDataSetChanged();
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

    /*

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };*/

    android.os.Handler h = new android.os.Handler(){
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {

                case 1:
                    progressDialog.dismiss();

                    break;
            }
        }
    };


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

    /*============================================*/










    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private String[] images;
        private LayoutInflater inflater;

        ImagePagerAdapter(String[] images) {
            this.images = images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(com.dhs.nica.R.layout.item_pager_image, view, false);
            ImageView imageView = (ImageView) imageLayout.findViewById(com.dhs.nica.R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(com.dhs.nica.R.id.loading);

            imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                }
            });

            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }

}
