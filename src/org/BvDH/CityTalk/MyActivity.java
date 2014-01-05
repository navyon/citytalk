package org.BvDH.CityTalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity {
    private Uri mImageCaptureUri;
    private Uri tempURI;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.main);
        Button button 	= (Button) findViewById(R.id.btn_crop);
        Button btnskip 	= (Button) findViewById(R.id.btn_Skip);



        final String [] items			= new String [] {"Maak een foto", "Selecteer een bestaande foto"};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        builder.setTitle("Selecteer afbeelding");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) { //pick from camera
                if (item == 0) {
                    Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            String.valueOf(System.currentTimeMillis()) + "_app_upload.jpg"));

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { //pick from file
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        btnskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, MessageActivity.class);
                startActivity(intent);
            }});
        checkDir();

    }


    void checkDir(){
        File dir = new File(Environment.getExternalStorageDirectory()+"/bvdh");
        if(!dir.exists()){
            boolean result = dir.mkdir();
            if(result){
                System.out.println("created a DIR");
            }
        }
    }
    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        super.onPause();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                doCrop();

                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:

                Intent intent = new Intent(MyActivity.this, MessageActivity.class);
                if (tempURI!= null) {

                    // The photo path is sent to the message activity
                    intent.putExtra("imagePath", tempURI.getPath());

                    startActivity(intent);
                }

                File temp = new File(mImageCaptureUri.getPath());

                if (temp.exists()) temp.delete();

                break;

        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            //cropped picture is saved at tempURI location
            tempURI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "bvdh" + String.valueOf(System.currentTimeMillis()) + "_app_upload.jpg"));

            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 1024);
            intent.putExtra("outputY", 776);
            intent.putExtra("aspectX", 1024);
            intent.putExtra("aspectY", 776);
            intent.putExtra("crop", true);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false); //don't send data back to prevent transactionTooLarge
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempURI); //save to file!

            if (size == 1) {
                Intent i 		= new Intent(intent);
                ResolveInfo res	= list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);

                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pas formaat aan");
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });
                // Cancels the Image Capture
                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }


}
