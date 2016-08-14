package com.cheah.david.flashlight;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button btnSwitch;
    private boolean isFlashOn;
    private boolean hasFlash;
    private boolean hasPermission;
    private RelativeLayout mainLayout;
    private TextView about;
    private String TAG = "FlashLight";
    private Camera camera;
    private Camera.Parameters params;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //App in fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        mainLayout.setBackgroundColor(Color.BLACK);
        about = (TextView)findViewById(R.id.aboutTextView);
        about.setTextColor(Color.WHITE);
        btnSwitch = (Button)findViewById(R.id.onOffButton);
        btnSwitch.setText(R.string.onString);
        btnSwitch.setBackgroundColor(Color.BLACK);
        btnSwitch.setTextColor(Color.WHITE);
        hasPermission = false;

        //Ask user permission at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkselfpermission");
            hasPermission = false;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                Log.d(TAG, "requestPermission");
            }
        } else {
            Log.d(TAG,"permission granted");
            hasPermission = true;
        }

        if (hasPermission){
            registerButton();
        }
    }

    private void registerButton(){
        checkForFlash();
        getCamera();
        isFlashOn = false;
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"button triggered");
                if (!isFlashOn){
                    btnSwitch.setText(R.string.offString);
                    setDayMode();
                    onFlash();
                } else {
                    btnSwitch.setText(R.string.onString);
                    setNightMode();
                    offFlash();
                }
            }
        });
    }
    private void checkForFlash(){
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                    Log.d(TAG, "permission granted");
                    registerButton();
                    return;
                } else {
                    // Permission Denied
                    hasPermission = false;
                    Toast.makeText(MainActivity.this, "CAMERA permission Denied", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setDayMode()
    {
        mainLayout.setBackgroundColor(Color.WHITE);
        about.setTextColor(Color.BLACK);
        btnSwitch.setBackgroundColor(Color.WHITE);
        btnSwitch.setTextColor(Color.BLACK);
    }

    private void setNightMode()
    {
        mainLayout.setBackgroundColor(Color.BLACK);
        about.setTextColor(Color.WHITE);
        btnSwitch.setBackgroundColor(Color.BLACK);
        btnSwitch.setTextColor(Color.WHITE);
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void onFlash()
    {
        try{
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
            Log.d(TAG, "onFlash");
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception onFlash",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void offFlash()
    {
        try{
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            Log.d(TAG, "offFlash");
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception offFlash",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Log.d(TAG, "isFlashOn:" + isFlashOn);
        if (isFlashOn) {
            getCamera();
            onFlash();
        } else {
            if (hasPermission) registerButton();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        // on starting the app get the camera params
        //getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
