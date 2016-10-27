package moshch.morsecode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendActivity extends AppCompatActivity {
    Flashlight flashlight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FlashlightActivity", "onCreate()");
        setContentView(R.layout.activity_send);

        Button buttonSendMessage = (Button) findViewById(R.id.button_sendMessage);
        final EditText editText = (EditText) findViewById(R.id.sendingText);
        flashlight = new Flashlight(this);

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sosString = "...*---*...";
                flashlight.makeMorseCode(editText.getText().toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(flashlight.isOn()){
            flashlight.turnOff();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(flashlight.isOn()){
            flashlight.turnOff();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flashlight.isOn()){
            flashlight.turnOff();
        }
    }
}
