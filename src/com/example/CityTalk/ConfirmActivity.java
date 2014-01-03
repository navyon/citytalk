
package com.example.CityTalk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import it.sauronsoftware.ftp4j.FTPClient;

import java.io.File;
import java.util.regex.Pattern;


public class ConfirmActivity extends Activity {
    //
    static final String FTP_HOST= "195.211.74.189";

    /*********  FTP USERNAME ***********/
    static final String FTP_USER = "bvdh@daankrn.nl";

    /*********  FTP PASSWORD ***********/
    static final String FTP_PASS  ="hci2013";  //
    String image_path;

    EditText edittx_email;
    CheckBox chkbox;
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.confirm_screen);
        chkbox = (CheckBox)findViewById(R.id.checkBox);
        if(getIntent().hasExtra("imagePath")){
             image_path = getIntent().getStringExtra("imagePath");
        }
        findViewById(R.id.btnfinalsubmit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Call the ftp upload method which starts a new thread
                StartNewThreadUpload();

                edittx_email = (EditText) findViewById(R.id.editText_email);
                edittx_email.setTextColor(Color.BLACK);
                String email = edittx_email.getText().toString();
                if(checkEmail(email))
                {
                    if(chkbox.isChecked())
                    {
                    Intent intent = new Intent(ConfirmActivity.this, FinalActivity.class);
                    startActivity(intent);
                    }
                    else
                    {
                        String chkboxerror =  getString(R.string.ConfirmCheckboxError);
                        Toast.makeText(getApplicationContext(), chkboxerror, Toast.LENGTH_LONG).show();

                    }
                }
                else
                {

                    edittx_email.setTextColor(Color.RED);
                    String emailerror = getString(R.string.ConfirmEmailError);
                    Toast.makeText(getApplicationContext(), emailerror, Toast.LENGTH_LONG).show();


                }


            }
        });



    }

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    // Starte a New thread for the network activity
    public void StartNewThreadUpload()
    {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {

                    /********** Pick file from sdcard *******/
                // we can add a check for null here maybe
                    File f = new File(image_path);

                    // Upload sdcard file
                    uploadFile(f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    // Creates a ftp client to send the file
    public void uploadFile(File fileName){

        FTPClient client = new FTPClient();

        try {

            client.connect(FTP_HOST,21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.changeDirectory("/uploads/cropped/");

            client.upload(fileName);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

}
