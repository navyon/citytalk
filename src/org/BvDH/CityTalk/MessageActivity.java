package org.BvDH.CityTalk;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
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
     private ImageView aspectv;
     String msg = null;
     float textsize;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
           txtView_msg = (EditText) findViewById(R.id.txtView_msg);
           aspectv = (ImageView) findViewById(R.id.aspectv);
           setTextSizes(txtView_msg);


           Button btnPrev = (Button) findViewById(R.id.btnpreview);
           final ImageButton  btnhidekeyb = (ImageButton) findViewById(R.id.btnhidekey);

           System.out.println("textsize= "+textsize);
           if(getIntent().hasExtra("msg")){
               msg = getIntent().getStringExtra("msg");
               txtView_msg.setText(msg);
           }
           btnPrev.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String msg = txtView_msg.getText().toString();

                   if (!msg.isEmpty()) {

                       // The ImagePath from the previous Activity sent on to the Next
                       intent = new Intent(MessageActivity.this, Preview.class);

                       //this can probably done simpler..like last line comment
                       if(getIntent().hasExtra("imagePath")){
                           String image_path = getIntent().getStringExtra("imagePath");
                           //intent.putExtra("imagePath", image_path);
                           intent.putExtra("imagePath", getIntent().getStringExtra("imagePath"));
                       }

                       intent.putExtra("msg", msg);

                       startActivity(intent);

                   } else {
                       Toast.makeText(getApplicationContext(), "Het toevoegen van een bericht is verplicht.", Toast.LENGTH_LONG).show();
                   }


               }
           });


           // some online hack that should limit the Edit box, dont think its any better than the first enter hack lets see
           txtView_msg.addTextChangedListener(new TextWatcher() {
               private String text;

               public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
               }

               public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                   text = arg0.toString();
               }

               public void afterTextChanged(Editable arg0) {
                   int lineCount = txtView_msg.getLineCount();
                   if(lineCount > 4){
                       txtView_msg.setText(txtView_msg.getText().delete(txtView_msg.length() - 1, txtView_msg.length()));
                   }
               }
           });

           btnhidekeyb.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   // hide keyboard
                   InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                   inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                   txtView_msg.clearFocus();

               }
           });

           // This on key listener gets the number of lines if larger than 4 it ignore the Enter Key Press
           // SHOULD CHECK LINES, NOT ENTER KEY PRESSES ;)
           txtView_msg.setOnKeyListener(new View.OnKeyListener() {

               @Override
               public boolean onKey(View v, int keyCode, KeyEvent event) {

                   // if enter is pressed start calculating
                   if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                       int editTextLineCount = ((EditText)v).getLineCount();
                       if (editTextLineCount >= 4)
                           return true;
                   }

                   return false;
               }
           });

           txtView_msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
               @Override
               public void onFocusChange(View v, boolean hasFocus) {
                   if(hasFocus){
                       btnhidekeyb.setVisibility(View.VISIBLE);
                   }
                   else btnhidekeyb.setVisibility(View.INVISIBLE);
               }
           });

    }

// function to mimic text size in relation with the Haagse Toren
void setTextSizes(EditText txt){
    //force aspect ratio for txtView
    Bitmap.Config conf = Bitmap.Config.ALPHA_8;
    Bitmap bmp = Bitmap.createBitmap(1024, 776, conf);//create transparent bitmap
    aspectv.setImageBitmap(bmp);
    //get display size
    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);

    Resources r = getResources();
    float marginpx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
    float width = size.x - marginpx; // substract the margins (2x 5dp) from the width in px

    // convert width to textsize (120 at 1024 -> = 1024*0.117
    textsize = (float)(width * 0.1171875);
    int margin = (int)(width * 0.062);
    //set sizes
    txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
    txt.setPadding(margin,margin,margin,margin);
}



}
