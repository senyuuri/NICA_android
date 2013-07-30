package com.dhs.nica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by natsuyuu on 13-7-20.
 */
public class User_GetPhoto extends Activity {
    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    private ImageView imageView;
    private Button button1, button2;
    /**
     * Tag for log output
     */
    static final String TAG = "dhs_nica";

    String FileName = "PN";

    private String lineStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_getphoto);
        button2 = (Button) findViewById(R.id.getphoto_button2);
        button2.setEnabled(false);

    }

    public void onclick(View view){
        imageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, REQUEST_CODE);
    }


    public void upload(View view){
        new Thread(runnable).start();

    }

    android.os.Handler h = new android.os.Handler(){
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {
                //"y" Upload succeed
                case 1:
                    Intent intent = new Intent(getApplicationContext(), PreMain.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(getApplicationContext(),"Avatar upload successed",Toast.LENGTH_LONG);
                    toast.show();
                    break;

                // Server Error
                case 2:

                    break;

                // Exception encountered
                case 3:
                    //do somthing here to show error
                    break;

              }
        }
    };

    private Message msg = new Message();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            BufferedReader in = null;
            try{
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(Constant.SERVER_IMAGE_UPLOAD);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

                String phoneNumber = readFileData(FileName);
                String base64_encode = bitmap_to_base64();
                //Add parameter to POST
                postParameters.add(new BasicNameValuePair("pn", phoneNumber));
                postParameters.add(new BasicNameValuePair("avatar","1"));
                postParameters.add(new BasicNameValuePair("imgdata",base64_encode));
                Log.d(TAG, "Avatar parameter added"+base64_encode);

                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                        postParameters);
                request.setEntity(formEntity);
                HttpResponse response = client.execute(request);
                in = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent()));
                lineStr = in.readLine();
                Log.d(TAG, "Avatarupload LineStr:" + lineStr);
                in.close();

                //Check response status
                msg = h.obtainMessage();
                if(lineStr.equals("y") ){
                    msg.what = 1;
                    Log.d(TAG,"msg.what:1");}
                else{
                    msg.what = 2;
                    Log.d(TAG,"msg.what:2");
                }
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


    public String bitmap_to_base64(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
        byte[] outbyte = out.toByteArray();
        String outbyte1 = Base64.encodeToString(outbyte, Base64.DEFAULT);
        return outbyte1;
    }


     @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         InputStream stream = null;
         if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
             try {
                 // Recyle unused bitmaps
                 if (bitmap != null) {
                     bitmap.recycle();
                 }
                 //stream = getContentResolver().openInputStream(data.getData());
                 Bundle extras = data.getExtras();

                 bitmap = (Bitmap) extras.get("data");

                 imageView.setImageBitmap(bitmap);
                 button2.setEnabled(true);
             } catch (Exception e) {
                 e.printStackTrace();
             } finally {
                 if (stream != null)
                     try {
                         stream.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
             }
     }


    public void writeFileData(String filename, String message){
        try {
            FileOutputStream fout = openFileOutput(filename, MODE_PRIVATE);
            byte[]  bytes = message.getBytes();
            fout.write(bytes);//
            fout.close();//
            Log.d(TAG,filename+" : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "I/O error" + e);
        }
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



