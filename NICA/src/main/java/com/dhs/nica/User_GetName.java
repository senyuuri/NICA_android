package com.dhs.nica;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by natsuyuu on 13-7-31.
 */
public class User_GetName extends BaseActivity{
    static final String TAG = "dhs_nica";


    /**
     * Result for user verification
     */

    String phoneNumber;
    private EditText editText;
    private Button button1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_getname);
        Bundle b = this.getIntent().getExtras();
        phoneNumber = b.getString("pn");

        button1 = (Button) findViewById(R.id.user_name_button);

    }

    public void onclick(View view){
        editText = (EditText)findViewById(R.id.user_name_edittext);
        Log.d(TAG,phoneNumber);
        Log.d(TAG,String.valueOf(editText.getText()));

        Intent intent = new Intent(this, User_GetCircle.class);
        intent.putExtra("pn",phoneNumber);
        intent.putExtra("username",String.valueOf(editText.getText()));
        startActivity(intent);


    }

}
