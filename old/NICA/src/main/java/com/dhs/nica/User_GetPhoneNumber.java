package com.dhs.nica;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by natsuyuu on 13-7-20.
 */
public class User_GetPhoneNumber extends Activity{
    EditText editText;
    static final String TAG = "dhs_nica";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_getphonenumber);
        editText = (EditText) findViewById(R.id.editText);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public void confirm(View view){
        finish();
    }

    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }



    @Override
    public void finish() {

        String pn = String.valueOf(editText.getText());

        if(isNumeric(pn)&& pn.length() == 8){
            Log.d(TAG,"RE: Good result "+ pn);
            Intent data = new Intent();
            data.putExtra("returnNumber", pn);
            setResult(RESULT_OK,data);
            super.finish();
        }else{
        Log.d(TAG,"RE: Bad result " + pn);
        Toast toast = Toast.makeText(getApplicationContext(),"Sorry, Invalid phone number",Toast.LENGTH_LONG);
        toast.show();
        }
    }
}
