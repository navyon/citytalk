
package com.example.CityTalk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;


public class Preview extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewtxt);
        final ImageView imagev = (ImageView)findViewById(R.id.imageViewPreview);
        if(getIntent().hasExtra("theimage"));
        Bitmap b = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("theimage"),0,getIntent().getByteArrayExtra("theimage").length);
         if(b!=null)
        imagev.setImageBitmap(b);


        findViewById(R.id.btnSubmitmsgtxt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Preview.this, ConfirmActivity.class);
                Preview.this.startActivity(intent);

            }});


    }

   }
