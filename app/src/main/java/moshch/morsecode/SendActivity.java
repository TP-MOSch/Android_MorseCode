package moshch.morsecode;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FlashlightActivity", "onCreate()");
        setContentView(R.layout.activity_send);

        final Button buttonSendMessage = (Button) findViewById(R.id.button_sendMessage);
        final Button buttonStopSending = (Button) findViewById(R.id.button_stopSending);

        final EditText editText = (EditText) findViewById(R.id.sendingText);
        final TextView textViewUnitValue = (TextView) findViewById(R.id.textView_unitValue);

        final SeekBar seekBarUnit = (SeekBar) findViewById(R.id.seekBar_unit);

        flashlight = new Flashlight(this);

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sosString = "...*---*...";
                flashlight.setUnitValue(seekBarUnit.getProgress());
                flashlight.makeMorseCode(editText.getText().toString());
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        flashlight.stopSendingCode();
    }
}
