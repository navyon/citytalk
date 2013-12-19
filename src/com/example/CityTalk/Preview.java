
package com.example.CityTalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Preview extends Activity {
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    public ImageView imagev;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    boolean hasphoto = false;
    boolean hasmessage = false;
    String msg =null;
    Button btnChangePreviewPhoto;
    Button btnChangePreviewMessage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewtxt);
        imagev = (ImageView)findViewById(R.id.imageViewPreview);
        btnChangePreviewPhoto =(Button) findViewById(R.id.btnChangePreviewPhoto);
        btnChangePreviewMessage =(Button) findViewById(R.id.btnchangePreviewText);
        // These Methods check whether photos or a message was added
        CheckMessageExists();
        CheckPhotoExist();


        final String [] items			= new String [] {"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) { //pick from camera
                if (item == 0) {
                    Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

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





        findViewById(R.id.btnSubmitmsgtxt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Preview.this, ConfirmActivity.class);
                Preview.this.startActivity(intent);

            }});


       // final AlertDialog dialog = builder.create();
        findViewById(R.id.btnChangePreviewPhoto).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               dialog.show();
               CheckPhotoExist();
            }
        });

        findViewById(R.id.btnchangePreviewText).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();


            }
        });




     }
    void CheckMessageExists()
    {
        if(getIntent().hasExtra("msg")){
            msg = getIntent().getStringExtra("msg");
            hasmessage =true;
        }
        if(!hasmessage)
        {
            String btnaddmsgtxtt=  getString(R.string.PreviewbtnAddTxt);
            btnChangePreviewMessage.setText(btnaddmsgtxtt);

        }
        else
        {
            String btnChangetxt=  getString(R.string.PreviewbtnChangeTxt);
            btnChangePreviewMessage.setText(btnChangetxt);

        }



    }
    // This Method checks if a photo was added and updates the UI
    void CheckPhotoExist()
    {
        if(getIntent().hasExtra("theimage")){
            Bitmap b = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("theimage"),0,getIntent().getByteArrayExtra("theimage").length);

            if(b!=null)
                imagev.setImageBitmap(b);
                hasphoto =true;
        }
        // checks whether user added a photo, then changes button text accordingly
        if(!hasphoto)
        {
            String btnaddphototxt=  getString(R.string.PreviewbtnAddPhoto);
            btnChangePreviewPhoto.setText(btnaddphototxt);

        }
        else{
            String btnaddchangetxt=  getString(R.string.PreviewbtnChangePhoto);
            btnChangePreviewPhoto.setText(btnaddchangetxt);
        }


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

                Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                     if(photo!=null)
                        imagev.setImageBitmap(photo);
                    // The photo is bundled and sent to the message activity
                    Intent intent = getIntent();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    intent.putExtra("theimage",bs.toByteArray());

                }
                CheckPhotoExist();
                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) f.delete();

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
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

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
                builder.setTitle("Choose Crop App");
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
