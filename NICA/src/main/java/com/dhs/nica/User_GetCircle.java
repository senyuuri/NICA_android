package com.dhs.nica;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by natsuyuu on 13-7-31.
 */
public class User_GetCircle extends Activity {
    static final String TAG = "dhs_nica";


    /**
     * Result for user verification
     */


    String phoneNumber;
    String username;

    private EditText editText;
    private Button button1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_getcircle);
        Log.d(TAG, "getcircle1");
        Bundle b = this.getIntent().getExtras();
        phoneNumber = b.getString("pn");
        username = b.getString("username");
        Log.d(TAG, "getcircle2");
        button1 = (Button) findViewById(R.id.user_circle_button);
        Log.d(TAG, "getcircle3");
        //editText = (EditText)findViewById(R.id.user_circle_edittext);
        //editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Log.d(TAG, "getcircle4");

    }

    public void onclick(View view){
        Log.d(TAG, "getcircle5");
        editText = (EditText)findViewById(R.id.user_circle_edittext);
        Log.d(TAG, "getcircle6"+phoneNumber+" " +username);
        try{
        Intent intent = new Intent(this, User_GetPhoto.class);
        intent.putExtra("pn",phoneNumber);
        intent.putExtra("username",username);
        intent.putExtra("circle",String.valueOf(editText.getText()));
        Log.d(TAG, "getcircle7");
        startActivity(intent);}catch(Exception e){Log.e(TAG,e.toString());}
        Log.d(TAG, "NameTyped:" + "y");


    }

}
