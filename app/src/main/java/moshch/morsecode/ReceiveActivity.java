package moshch.morsecode;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReceiveActivity extends AppCompatActivity implements SensorEventListener {

    TextView textCurrentSensorValue;
    TextView textReceivedCode;
    Button buttonCalibrate;
    SensorManager mSensorManager;
    Sensor lightSensor;
    long unit = 300;
    long luxValueChangesOnFlash = 1000;
    MorseLightSensor morseLightSensor;
    Boolean isCalibrating = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        textCurrentSensorValue = (TextView) findViewById(R.id.textView_lightSensorValue);
        textReceivedCode = (TextView) findViewById(R.id.textView_receivedMessageValue);
        buttonCalibrate = (Button) findViewById(R.id.button_calibrateUnit);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        morseLightSensor = new MorseLightSensor(unit, luxValueChangesOnFlash);

        buttonCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalibrating) {
                    isCalibrating = true;
                    morseLightSensor.startCalibrating();
                    buttonCalibrate.setText("Stop");
                } else {
                    isCalibrating = false;
                    morseLightSensor.stopCalibrating();
                    buttonCalibrate.setText(getResources().getString(R.string.button_receive_calibrate));
                    textReceivedCode.setText("Calibrated. Unit=" + morseLightSensor.getUnitValue() + " lux=" + morseLightSensor.getFlashBrightness());
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        textCurrentSensorValue.setText(String.valueOf(event.values[0]));
        morseLightSensor.addValue(event.values[0]);
        textReceivedCode.setText(MorseConverter.morseToText(morseLightSensor.getDecodedString()));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        if (lightSensor != null) {
            //textCurrentSensorValue.setText("Sensor.TYPE_LIGHT Available");
            mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
