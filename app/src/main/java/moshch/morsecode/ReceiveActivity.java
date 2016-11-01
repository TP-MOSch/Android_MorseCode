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
    Sensor lightSensor;
    long unit = 300;
    long luxValueChangesOnFlash = 1000;
    MorseLightSensor morseLightSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        textSensorValue = (TextView) findViewById(R.id.textSensorValue);
        textReceivedCode = (TextView) findViewById(R.id.textView_receivedCode);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        morseLightSensor = new MorseLightSensor(unit, luxValueChangesOnFlash);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        morseLightSensor.newLuxValue(event.values[0]);

        textSensorValue.setText("Current light value: " + event.values[0]);

        textReceivedCode.setText(morseLightSensor.getDecodedString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        if(lightSensor != null){
            //textSensorValue.setText("Sensor.TYPE_LIGHT Available");
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
