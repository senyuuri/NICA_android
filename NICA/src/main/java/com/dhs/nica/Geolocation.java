package com.dhs.nica;

/**
 * Created by natsuyuu on 13-8-1.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by natsuyuu on 13-7-31.
 */
public class Geolocation extends Activity implements LocationListener {

    private LocationManager locationManager;
    private String provider;
    private GoogleMap map;

    private String lineStr2;

    static final String TAG = "dhs_nica";

    private int lat = 0;
    private int lng = 0;


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geolocation);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        // Get the location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            Log.d(TAG, "Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }

        LatLng current = new LatLng(location.getLatitude(),location.getAltitude());
        Marker current_u = map.addMarker(new MarkerOptions()
                .position(current)
                .title("Me")
                .snippet("I'm here")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = (int) (location.getLatitude());
        lng = (int) (location.getLongitude());
        Log.d(TAG,"Location changed:" + lat +"  "+lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    private Runnable upload_location = new Runnable() {
        @Override
        public void run() {

            BufferedReader in = null;
            try{

                HttpClient client2 = new DefaultHttpClient();
                HttpPost request2 = new HttpPost(Constant.SERVER_IMAGE_LIST_GENERATE);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

                //Add parameter to POST
                postParameters.add(new BasicNameValuePair("phonenum", readFileData("pn")));
                Log.d(TAG,"Parameter added");
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                        postParameters);
                request2.setEntity(formEntity);
                HttpResponse response2 = client2.execute(request2);
                if(response2.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    HttpEntity resEntity = response2.getEntity();
                    lineStr2 = EntityUtils.toString(resEntity);
                }
                Log.d(TAG, "ImgList fetched: " +lineStr2);
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
