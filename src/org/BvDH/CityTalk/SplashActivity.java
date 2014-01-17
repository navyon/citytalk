package org.BvDH.CityTalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SplashActivity extends Activity
{
    // Set the display time, in milliseconds (or extract it out as a configurable parameter)
    private TextView Welcome;
    private TextView IntroText;
    private Button SplBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface fontLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Welcome = (TextView) findViewById(R.id.welkom_text);
        IntroText = (TextView) findViewById(R.id.introText);
        Welcome.setTypeface(fontRegular);
        IntroText.setTypeface(fontLight);
        SplBtn = (Button) findViewById(R.id.SplBtn);
        SplBtn.setTypeface(fontLight);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        // Obtain the sharedPreference, default to true if not available
        boolean isSplashEnabled = sp.getBoolean("isSplashEnabled", true);

        if (!isSplashEnabled)
        {
            // if the splash is not enabled, then finish the activity immediately and go to main.
            finish();
            Intent mainIntent = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(mainIntent);
        }


        else
        {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isSplashEnabled", false);
            editor.commit();

            ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (!isConnected){
                AlertDialog.Builder builder		= new AlertDialog.Builder(this);
                builder.setMessage("Er is geen internet verbinding gevonden, het versturen van een bericht is niet mogelijk")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
            SplBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent mainIntent = new Intent(SplashActivity.this, MyActivity.class);
                    mainIntent.putExtra("check", false);
                    startActivity(mainIntent);
                }
            });
        }

    }
}