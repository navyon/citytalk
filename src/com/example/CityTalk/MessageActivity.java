package com.example.CityTalk;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.io.ByteArrayOutputStream;

 // The Message Activity
public class MessageActivity extends Activity {
     Intent intent;
     private EditText  txtView_msg;
     String msg = null;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

           txtView_msg = (EditText) findViewById(R.id.txtView_msg);
           Button btnPrev = (Button) findViewById(R.id.btnpreview);

           btnPrev.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   String defaultmsg = "Voer hier direct jouw bericht in.";
                   if(getIntent().hasExtra("msg")){
                       msg = getIntent().getStringExtra("msg");
                       txtView_msg.setText(msg);
                   }
                   String msg = txtView_msg.getText().toString();

                   if (!msg.isEmpty()) {

                       // The ImagePath from the previous Activity sent on to the Next
                       intent = new Intent(MessageActivity.this, Preview.class);

                       //this can probably done simpler..like last line comment
                       if(getIntent().hasExtra("imagePath")){
                           String image_path = getIntent().getStringExtra("imagePath");
                           intent.putExtra("imagePath", image_path);
                           //intent.putExtra("imagePath", getIntent().getStringExtra("imagePath"));
                       }

                       intent.putExtra("msg", msg);

                       startActivity(intent);

                   } else {
                       Toast.makeText(getApplicationContext(), "Het toevoegen van een bericht is verplicht.", Toast.LENGTH_LONG).show();
                   }


               }
           });





           // This on key listener gets the number of lines if larger than 4 it ignore the Enter Key Press
           // SHOULD CHECK LINES, NOT ENTER KEY PRESSES ;)
           txtView_msg.setOnKeyListener(new View.OnKeyListener() {

               @Override
               public boolean onKey(View v, int keyCode, KeyEvent event) {

                   if (keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == KeyEvent.ACTION_DOWN) {

                       if ( ((EditText)v).getLineCount() >= 4 )
                           return true;
                   }

                   return false;
               }
           });

    }




}
