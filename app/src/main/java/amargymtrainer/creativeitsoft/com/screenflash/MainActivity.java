package amargymtrainer.creativeitsoft.com.screenflash;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private Camera camera;
    private boolean isFlashOn;
    private FrameLayout flashEmulator;
    private float lastScreenBrightness;
    private CameraPreview cameraPreview;
    Boolean mSwap=true;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int fullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(fullScreen, fullScreen);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        flashEmulator = (FrameLayout) findViewById(R.id.flashEmulator);
        cameraPreview = (CameraPreview)findViewById(R.id.cameraPreview);

        startCamera();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click event for clicking the hidden button
                CountDownTimer mCountDownTimer = new CountDownTimer(1000000, 1000) {

                    public void onTick(long remainingMilis) {
                        // Update your ImageView here
                        button.setEnabled(false);
                        if (mSwap) {
                            turnOffFlash();
                            mSwap = false;

                        } else {
                            turnOnFlash();
                            mSwap = true;
                        }
                    }

                    public void onFinish() {
                        // Start the timer again from the code.
                    }

                }.start();

            }
        });




    }

    private void startCamera() {
        //Connecting and opening the camera of surfaceview from CameraPreview
        if (camera == null) {
            try {
                   camera = Camera.open(1);
                Camera.Parameters params = camera.getParameters();
                params.setPreviewSize(1280, 720);
                camera.setDisplayOrientation(90);
                camera.setParameters(params);
                cameraPreview.connectCamera(camera);
                cameraPreview.startPreview();

            } catch (Exception e) {
            }
        }
    }

    private void turnOnFlash() {
        //Making the flash on
        if (!isFlashOn) {
            int color = Color.WHITE;
            float[] hsv = new float[3];
            final Window window = getWindow();
            final WindowManager.LayoutParams params = window.getAttributes();

            lastScreenBrightness = params.screenBrightness;
            params.screenBrightness = 5F;
            window.setAttributes(params);
            flashEmulator.setVisibility(View.VISIBLE);
            Color.colorToHSV(color, hsv);
            hsv[2] *= 5f;
            color = Color.HSVToColor(hsv);
            flashEmulator.setBackgroundColor(color);
            isFlashOn = true;
        }
    }

    private void turnOffFlash() {
        //Making the flash off
        if (isFlashOn) {
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();

            params.screenBrightness = lastScreenBrightness;
            window.setAttributes(params);
            flashEmulator.setVisibility(View.GONE);
            isFlashOn = false;
        }
    }


    public  boolean isCameraPermissionGranted() {
        //For taking the camera permission from the users
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("SS","Permission is granted");
                cameraPreview.connectCamera(Camera.open());
                return true;
            } else {

                Log.v("SS","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
                return false;
            }
        }
        else {
            cameraPreview.connectCamera(Camera.open());
            //permission is automatically granted on sdk<23 upon installation
            Log.v("SS","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //requesting the permission
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            cameraPreview.connectCamera(Camera.open());
            Log.v("SS","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //activity life cycle method turning the flash off when app is minimized
        super.onPause();
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        //this is required when the activity starts
        super.onStart();
        if (isCameraPermissionGranted()) {
            startCamera();
        }
    }

    @Override
    protected void onStop() {
        //when activity stops
        super.onStop();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
