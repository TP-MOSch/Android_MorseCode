package moshch.morsecode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendActivity extends AppCompatActivity {

    private CameraManager mCameraManager;
    private String mCameraId;
    private Boolean isFlashlightOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FlashlightActivity", "onCreate()");
        setContentView(R.layout.activity_send);

        Button buttonSendMessage = (Button) findViewById(R.id.button_sendMessage);
        final EditText editText = (EditText) findViewById(R.id.sendingText);
        isFlashlightOn = false;

        if (!checkFlashlightIsAvailable()) {
            return;
        }

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isFlashlightOn) {
                        turnOffFlashlight();
                        isFlashlightOn = false;
                    }
                    //Morse rules:
                    //The length of a dot is one unit.
                    //A dash is three units.
                    //The space between parts of the same letter is one unit.
                    //The space between letters is three units.
                    //The space between words is seven units.
                    Thread t = new Thread() {
                        public void run() {
                            long unit = 300; //Delay in ms
                            //String messageString = "...*---*...";
                            String messageString = editText.getText().toString();
                            try {
                                for (int i = 0; i < messageString.length(); i++) {
                                    switch (messageString.charAt(i)) {
                                        case '.':  //dot
                                            turnOnFlashlight();
                                            sleep(unit);
                                            turnOffFlashlight();
                                            sleep(unit);
                                            break;
                                        case '-':  //dash
                                            turnOnFlashlight();
                                            sleep(3*unit);
                                            turnOffFlashlight();
                                            sleep(unit);
                                            break;
                                        case '*':  //space between letters
                                            sleep(3*unit);
                                            break;
                                        case ' ':  //space between words
                                            sleep(7*unit);
                                            break;
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                    if (isFlashlightOn) {
                        turnOffFlashlight();
                        isFlashlightOn = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Boolean checkFlashlightIsAvailable() {
        Boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {

            AlertDialog alert = new AlertDialog.Builder(SendActivity.this)
                    .create();
            alert.setTitle("Error !!");
            alert.setMessage("Your device doesn't support flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                    System.exit(0);
                }
            });
            alert.show();
            return false;
        } else {
            return true;
        }
    }
    public void turnOnFlashlight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOffFlashlight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFlashlightOn){
            turnOffFlashlight();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFlashlightOn){
            turnOffFlashlight();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFlashlightOn){
            turnOnFlashlight();
        }
    }
}
