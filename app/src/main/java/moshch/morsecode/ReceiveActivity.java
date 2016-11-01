package moshch.morsecode;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ReceiveActivity extends AppCompatActivity implements SensorEventListener {

    TextView textSensorValue;
    TextView textReceivedCode;
    SensorManager mSensorManager;
    Sensor LightSensor;
    Boolean isFirstValue = true;
    Boolean isFlashOn = false;
    float currentLightValue = 0, lastLightValue = 0;
    long timeFlashOn, timeFlashOff;
    String decodedCode = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        textSensorValue = (TextView) findViewById(R.id.textSensorValue);
        textReceivedCode = (TextView) findViewById(R.id.textView_receivedCode);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        LightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentLightValue = event.values[0];

        textSensorValue.setText("Current light value: " + currentLightValue);
        //if(event.sensor.getType() == Sensor.TYPE_LIGHT){}

        //flash turns on
        if (!isFlashOn && currentLightValue >= lastLightValue + 1000.0) {
            isFlashOn = true;
            timeFlashOn = System.currentTimeMillis();

            if (isFirstValue) {
                isFirstValue = false;
            } else {
                timeFlashOff = System.currentTimeMillis() - timeFlashOff;
                textReceivedCode.setText(textReceivedCode.getText() + " -" + timeFlashOff);
                if (timeFlashOff > 800.0 && timeFlashOff <= 1500.0) {
                    decodedCode += "/";
                }
                if (timeFlashOff > 1500) {
                    decodedCode += " ";
                }
            }
        }

        //flash turns off
        if (isFlashOn && currentLightValue <= lastLightValue - 1000.0) {
            isFlashOn = false;
            timeFlashOff = System.currentTimeMillis();
            timeFlashOn = System.currentTimeMillis() - timeFlashOn;
            textReceivedCode.setText(textReceivedCode.getText() + " +" + timeFlashOn);
            if (timeFlashOn >= 500.0) {
                decodedCode += "-";
            } else {
                decodedCode += ".";
            }
        }
        lastLightValue = currentLightValue;
        textReceivedCode.setText(decodedCode);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        if(LightSensor != null){
            //textSensorValue.setText("Sensor.TYPE_LIGHT Available");
            mSensorManager.registerListener(this, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        } else {

        }
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
