package moshch.morsecode;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;


public class Flashlight {
    private Context mContext;
    private Intent mIntent;
    private CameraManager mCameraManager;
    private Camera camera;
    Camera.Parameters params;
    private String mCameraId;
    private Boolean isFlashlightOn;
    private Thread threadSendingCode;
    private int unit;

    public Flashlight(Context mContext) {
        this.mContext = mContext;
        mIntent = new Intent(mContext, SendActivity.class);
        isFlashlightOn = false;
        if (!checkFlashlightIsAvailable()) { return; }
    }

    //Morse rules:
    //The length of a dot is one unit.
    //A dash is three units.
    //The space between parts of the same letter is one unit.
    //The space between letters is three units.
    //The space between words is seven units.
    public void makeMorseCode(final String messageString) {
        getCamera();
        try {
            turnFlashOff();
            flashThread thread = new flashThread(messageString);
            thread.start();
            turnFlashOff();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class flashThread extends Thread {
        private String message;
        flashThread(String message) {
            this.message = message;
        }
        public void run() {
            try {
                for (int i = 0; i < message.length(); i++) {
                    switch (message.charAt(i)) {
                        case '.':  //dot
                            turnFlashOn();
                            sleep(unit);
                            turnFlashOff();
                            sleep(unit);
                            break;
                        case '-':  //dash
                            turnFlashOn();
                            sleep(3*unit);
                            turnFlashOff();
                            sleep(unit);
                            break;
                        case '/':  //space between letters
                            sleep(3*unit);
                            break;
                        case ' ':  //space between words
                            sleep(7*unit);
                            break;
                    }
                    if (Thread.interrupted()) {
                        return;
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Boolean checkFlashlightIsAvailable() {
        Boolean isFlashAvailable = mContext.getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {

            AlertDialog alert = new AlertDialog.Builder(mContext)
                    .create();
            alert.setTitle("Error with flashlight!");
            alert.setMessage("Your device doesn't support flashlight, so you can't send a message with flashlight.");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the activity
                    ((Activity)mContext).finish();
                }
            });
            alert.show();
            return false;
        } else {
            return true;
        }
    }

    @TargetApi(21)
    private void getCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                mCameraId = mCameraManager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            if (camera == null) {
                try {
                    camera = Camera.open();
                    params = camera.getParameters();
                    camera.startPreview();
                } catch (RuntimeException e) {
                    Log.e("Camera Error: ", e.getMessage());
                }
            }
        }
    }

    public void turnFlashOn() {
        if (!isFlashlightOn) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCameraManager.setTorchMode(mCameraId, true);
                    isFlashlightOn = true;
                } else {
                    if (camera == null || params == null) {
                        return;
                    }
                    //params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    //params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                    params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview();
                    isFlashlightOn = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void turnFlashOff() {
        if(isFlashlightOn)
        {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCameraManager.setTorchMode(mCameraId, false);
                    isFlashlightOn = false;
                } else {
                    if (camera == null || params == null) {
                        return;
                    }
                    params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    camera.startPreview();
                    isFlashlightOn = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopSendingCode() {
        if (threadSendingCode != null) {
            if (threadSendingCode.isAlive()) {
                threadSendingCode.interrupt();
            }
            threadSendingCode = null;
            turnFlashOff();
            camera.setParameters(params);
            camera.startPreview();
        }
    }

    public void setUnitValue(int unitValue) {
        this.unit = unitValue;
    }

}
