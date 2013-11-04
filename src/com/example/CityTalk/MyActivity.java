package com.example.CityTalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button btntakephoto =(Button)findViewById(R.id.btnTakePhoto);
        Button btnuploadphoto =(Button)findViewById(R.id.btn_UploadPhoto);
        Button btncontinue =(Button)findViewById(R.id.btn_Continue);
        Typeface typeFace= Typeface.createFromAsset(getAssets(), "fonts/RobotoBold.ttf");
        btntakephoto.setTypeface(typeFace);
        btnuploadphoto.setTypeface(typeFace);
        btncontinue.setTypeface(typeFace);
    }

}
