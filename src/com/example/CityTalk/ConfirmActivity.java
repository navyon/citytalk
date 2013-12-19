
package com.example.CityTalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class ConfirmActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.confirm_screen);


        findViewById(R.id.btnfinalsubmit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(ConfirmActivity.this, FinalActivity.class);
                startActivity(intent);

            }
        });

    }
 }
