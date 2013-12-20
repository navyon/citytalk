
package com.example.CityTalk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;


public class ConfirmActivity extends Activity {
     EditText edittx_email;
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


        findViewById(R.id.btnfinalsubmit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                edittx_email = (EditText) findViewById(R.id.editText_email);
                String email = edittx_email.getText().toString();
                if(checkEmail(email))
                {
                    edittx_email.setTextColor(Color.BLACK);
                    Intent intent = new Intent(ConfirmActivity.this, FinalActivity.class);
                    startActivity(intent);
                }
                else
                {

                    edittx_email.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Please check your email address.", Toast.LENGTH_LONG).show();


                }

            }
        });



    }
    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
 }
