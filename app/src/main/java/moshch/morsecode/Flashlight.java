package moshch.morsecode;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;


public class Flashlight {
    Context mContext;
    Intent mIntent;
    private CameraManager mCameraManager;
    private String mCameraId;
    private Boolean isFlashlightOn;

    public Flashlight(Context mContext) {
        this.mContext = mContext;
        mIntent = new Intent(mContext, SendActivity.class);
        isFlashlightOn = false;

        if (!checkFlashlightIsAvailable()) { return; }

        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void makeMorseWithFlashlight(final String messageString) {
        //Morse rules:
        //The length of a dot is one unit.
        //A dash is three units.
        //The space between parts of the same letter is one unit.
        //The space between letters is three units.
        //The space between words is seven units.
        try {
            if (isOn()) {
                turnOff();
            }

            Thread t = new Thread() {
                public void run() {
                    long unit = 300; //Delay in ms
                    try {
                        for (int i = 0; i < messageString.length(); i++) {
                            switch (messageString.charAt(i)) {
                                case '.':  //dot
                                    turnOn();
                                    sleep(unit);
                                    turnOff();
                                    sleep(unit);
                                    break;
                                case '-':  //dash
                                    turnOn();
                                    sleep(3*unit);
                                    turnOff();
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
            if (isOn()) {
                turnOff();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean checkFlashlightIsAvailable() {
        Boolean isFlashAvailable = mContext.getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {

            AlertDialog alert = new AlertDialog.Builder(mContext)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Your device doesn't support flashlight!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    ((Activity)mContext).finish();
                    System.exit(0);
                }
            });
            alert.show();
            return false;
        } else {
            return true;
        }
    }

    public void turnOn() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOff() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean isOn() {
        return isFlashlightOn;
    }



}
