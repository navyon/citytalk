
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Preview  extends Activity implements Animation.AnimationListener {
    private Uri mImageCaptureUri;
    private TextView txtview;
    private Uri tempURI;
    public ImageView imagev;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    boolean hasphoto = false;
    boolean hasmessage = false;
    String msg =null;
    Button btnChangePreviewPhoto;
    Button btnChangePreviewMessage;
    ImageButton btnRestartAnim;
    View thislayout;
    // Animation
    Animation animSideDown, animSlideUp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.previewtxt);
        imagev = (ImageView)findViewById(R.id.ImageViewPreview);
        txtview = (TextView)findViewById(R.id.TextViewPreview);
        btnChangePreviewPhoto =(Button) findViewById(R.id.btnChangePreviewPhoto);
        btnChangePreviewMessage =(Button) findViewById(R.id.btnchangePreviewText);
        btnRestartAnim =(ImageButton) findViewById(R.id.btnRestartAnim);

        // load the animation
        animSideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);


        // set animation listener
        animSideDown.setAnimationListener(this);
        animSlideUp.setAnimationListener(this);
        // These Methods check whether photos or a message was added

        //load message and image, check if image exists
        LoadMsgImg();

        CheckPhotoExist();



        if(hasmessage)
        {
            txtview.setText(msg);

            StartTextAnimation();
        }
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





        findViewById(R.id.btnSubmitmsgtxt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Preview.this, ConfirmActivity.class);
                intent.putExtra("imagePath", getIntent().getStringExtra("imagePath"));
                intent.putExtra("msg",msg);
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

                imagev.setDrawingCacheEnabled(true);
                CheckPhotoExist();

                Intent i = new Intent(Preview.this, MessageActivity.class);
                if(hasphoto){
                    if(tempURI!=null){
                        i.putExtra("imagePath", tempURI.getPath());
                    }
                    else i.putExtra("imagePath", getIntent().getStringExtra("imagePath"));
                    i.putExtra("msg",msg);
                    startActivity(i);
                }
                //finish(); // this removes image added in this activity.


            }
        });

        btnRestartAnim.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                StartTextAnimation();
            }
        });






     }
    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        super.onPause();
    }
    //this can be DELETED i think
    void LoadMsgImg()
    {
        if(getIntent().hasExtra("msg")){
            msg = getIntent().getStringExtra("msg");
            hasmessage =true;
        }
        if(getIntent().hasExtra("imagePath")){
            String image_path = getIntent().getStringExtra("imagePath");
            Bitmap b = BitmapFactory.decodeFile(image_path);

            if(b!=null){
                imagev.setImageBitmap(b);
            }
        }

    }

    void StartTextAnimation()
    {
        btnRestartAnim.setVisibility(View.INVISIBLE);
        txtview.setVisibility(View.VISIBLE);
        txtview.startAnimation(animSideDown);

    }
    void StartImageAnimation()
    {
        imagev.setVisibility(View.VISIBLE);
        imagev.startAnimation(animSlideUp);
    }
    // This Method checks if a photo was added

    void CheckPhotoExist(){
        if(imagev.getDrawable()!=null){
            hasphoto = true;
        }
        else hasphoto = false;
        ChangeButtons();
    }

    //this method changes the buttons text according to context
    void ChangeButtons()
    {
        String btnChangetxt=  getString(R.string.PreviewbtnChangeTxt);
        btnChangePreviewMessage.setText(btnChangetxt);
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
                    String imagePath = tempURI.getPath();
                    Bitmap photo = BitmapFactory.decodeFile(imagePath);
                     if(photo!=null){
                        imagev.setImageBitmap(photo);
                        hasphoto = true; //force set photo true because CheckPhotoExist() doesn't work..
                     }
                    CheckPhotoExist(); //this re-adds the picture from the previous activity!
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) f.delete();
                StartTextAnimation();
                break;




        }
    }
    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation



        // check for zoom in animation
        if (animation == animSideDown && hasphoto) { //only start image animation if there is one
            StartImageAnimation();
        }
        else if (animation == animSideDown && !hasphoto){
            btnRestartAnim.setVisibility(View.VISIBLE); //else show restart button
        }
        else{
            btnRestartAnim.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }
    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Geen app beschikbaar voor formaat aanpassen", Toast.LENGTH_SHORT).show();

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
