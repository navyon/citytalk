package org.BvDH.CityTalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class SplashActivity extends Activity
{
    // Set the display time, in milliseconds (or extract it out as a configurable parameter)
    private TextView Welcome;
    private TextView IntroText;
    private Button SplBtn;
    private Button SplInfBtn;
    private CheckBox SplChk;
    private RadioGroup LangSelectGroup;
    private RadioButton LangSelectBtn;

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
        SplInfBtn = (Button) findViewById(R.id.SplInfoBtn);
        SplInfBtn.setTypeface(fontLight);
        SplChk = (CheckBox) findViewById(R.id.splashCheck);
        LangSelectGroup = (RadioGroup) findViewById(R.id.LangSelect);
        loadLocale();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
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
            ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            //info alert builder
            AlertDialog.Builder infobuilder		= new AlertDialog.Builder(this);

            infobuilder.setMessage(R.string.SplashTextInfo)
                    .setTitle(R.string.SplashTextInfoBtn)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            final AlertDialog info = infobuilder.create();

            if (!isConnected){
                AlertDialog.Builder builder		= new AlertDialog.Builder(this);
                builder.setMessage(R.string.InternetCheck)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }

            SplInfBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.show();
                }
            });

            LangSelectGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    LangSelectBtn = (RadioButton) findViewById(checkedId);
                    int index = LangSelectGroup.indexOfChild(LangSelectBtn);
                    String languageToLoad = "en";
                    if(index == 0){
                        languageToLoad = "nl";
                        System.out.println(languageToLoad);
                    }
                    else if(index == 1){
                        languageToLoad = "en";
                        System.out.println(languageToLoad);
                    }
                    changeLang(languageToLoad);
                }
            });


            SplBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sp.edit();
                    if(SplChk.isChecked()){
                        editor.putBoolean("isSplashEnabled", false);
                    }

                    finish();
                    editor.commit();
                    Intent mainIntent = new Intent(SplashActivity.this, MyActivity.class);
                    mainIntent.putExtra("check", false);
                    startActivity(mainIntent);
                }
            });
        }

    }



    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }

    public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences sp = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    private void updateTexts(){
        Welcome.setText(R.string.SplashTextWelcome);
        SplChk.setText(R.string.SplashTextCheck);
        SplInfBtn.setText(R.string.SplashTextInfoBtn);
    }
    public void loadLocale()
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }
}