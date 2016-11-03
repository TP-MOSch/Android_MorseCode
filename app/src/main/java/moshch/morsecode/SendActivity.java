package moshch.morsecode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class SendActivity extends AppCompatActivity {
    Flashlight flashlight;
    SeekBar seekBarUnit;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FlashlightActivity", "onCreate()");
        setContentView(R.layout.activity_send);

        final Button buttonSendMessage = (Button) findViewById(R.id.button_sendMessage);
        final Button buttonStopSending = (Button) findViewById(R.id.button_stopSending);

        editText = (EditText) findViewById(R.id.sendingText);
        final TextView textViewUnitValue = (TextView) findViewById(R.id.textView_unitValue);

        seekBarUnit = (SeekBar) findViewById(R.id.seekBar_unit);

        flashlight = new Flashlight(this);

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashlight.setUnitValue(seekBarUnit.getProgress());
                flashlight.makeMorseCode(MorseConverter.textToMorse(editText.getText().toString()));
            }
        });

        buttonStopSending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashlight.stopSendingCode();
            }
        });

        seekBarUnit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewUnitValue.setText(String.valueOf(seekBarUnit.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewUnitValue.setText(String.valueOf(seekBarUnit.getProgress()));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        flashlight.stopSendingCode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        flashlight.stopSendingCode();
        SharedPreferences prefs = getSharedPreferences("mySettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("unitTimeSend", seekBarUnit.getProgress());
        editor.putString("messageSend", editText.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        flashlight.stopSendingCode();
        SharedPreferences prefs = getSharedPreferences("mySettings", MODE_PRIVATE);
        seekBarUnit.setProgress(prefs.getInt("unitTimeSend", 500));
        editText.setText(prefs.getString("messageSend", ""));
    }
}
