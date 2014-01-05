
package com.example.CityTalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import it.sauronsoftware.ftp4j.FTPClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class ConfirmActivity extends Activity {

    JSONParser jsonParser = new JSONParser();

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    // url to call php script
    private static String url_create_message = "http://beeldvandenhaag.daankrn.nl/android_api/create_message.php";
    //
    static final String FTP_HOST= "195.211.74.189";

    /*********  FTP USERNAME ***********/
    static final String FTP_USER = "bvdh@daankrn.nl";

    /*********  FTP PASSWORD ***********/
    static final String FTP_PASS  ="hci2013";  //
    String image_path;

    EditText edittx_email;
    CheckBox chkbox;
    // Progress Dialog
    private ProgressDialog pDialog;


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
    protected void onPause() {
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        super.onPause();
    }
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


                edittx_email = (EditText) findViewById(R.id.editText_email);
                edittx_email.setTextColor(Color.BLACK);
                String email = edittx_email.getText().toString();
                if(checkEmail(email))
                {
                    if(chkbox.isChecked())
                    {
                        // Call the ftp upload method which starts a new thread
                        StartNewThreadUpload();

                        // creating new message in background thread
                        new CreateNewMessage().execute();

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

    /**
     * Background Async Task to Create new Message
     * */
    class CreateNewMessage extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    pDialog = new ProgressDialog(ConfirmActivity.this);
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                }
            });

        }
       // Create the Message
        protected String doInBackground(String... args) {
            String email = edittx_email.getText().toString();
            File f = new File(image_path);
            String foto = f.getName();
            String bericht = getIntent().getStringExtra("msg");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("bericht", bericht));
            params.add(new BasicNameValuePair("foto", foto));


            // getting JSON Object

            JSONObject json = jsonParser.makeHttpRequest(url_create_message,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created message  can update UI here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {



                        }
                    });
                } else {
                    // failed to create message
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    // Start a New thread for the network activity
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
            //client.changeDirectory("/uploads/cropped/");

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
