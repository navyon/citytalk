
package com.example.CityTalk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class FinalActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landingpage);

        findViewById(R.id.btnlike).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/BeeldVanDenHaag"));
                startActivity(open);
            }
        });
    }


    // call onStop() to start initial activity
    protected void onStop(){
        super.onStop();

        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
