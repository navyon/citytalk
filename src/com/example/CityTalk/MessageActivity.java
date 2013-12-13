package com.example.CityTalk;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

 // The Message Activity
public class MessageActivity extends Activity {
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
           final EditText txtview = (EditText) findViewById(R.id.txtView_msg);

            final Button btnpreview = (Button) findViewById(R.id.btnpreview);




           findViewById(R.id.btnpreview).setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   Intent intent = new Intent(MessageActivity.this, Preview.class);
                   // The Image from the previous Activity is decoded and stored as a bitMap, so it can be sent on to the Next Activity

                   if(getIntent().hasExtra("theimage"));
                   Bitmap b = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("theimage"),0,getIntent().getByteArrayExtra("theimage").length);
                   // The photo is bundled and sent to the message activity
                   if(b!=null) {
                   ByteArrayOutputStream bs = new ByteArrayOutputStream();
                   b.compress(Bitmap.CompressFormat.PNG,50,bs);
                   intent.putExtra("theimage",bs.toByteArray());
                   }
                   startActivity(intent);
               }});


    }





}
