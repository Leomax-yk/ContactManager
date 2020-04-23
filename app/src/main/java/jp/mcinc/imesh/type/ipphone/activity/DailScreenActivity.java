package jp.mcinc.imesh.type.ipphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.util.Validation;
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Call;

import androidx.appcompat.app.AppCompatActivity;

public class DailScreenActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private EditText mEditNumber;
    private Button mButtonCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dail_screen);
        getSupportActionBar().hide();

        mButtonCall = findViewById(R.id.button_call);
        mEditNumber = findViewById(R.id.edit_number);

        mEditNumber.setText(getIntent().getExtras().getString("num"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValidateAddContactToList();
            }
        });
    }

    private void isValidateAddContactToList() {
        if (Validation.validateString(mEditNumber.getText().toString()) && Validation.isMobileNumberValid(mEditNumber.getText().toString())) {
            Intent i = new Intent(DailScreenActivity.this, VoiceActivity.class);
            i.putExtra("dailNumber", "" + mEditNumber.getText().toString());
            startActivity(i);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Enter mobile number properly", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //BACK
                finish();
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                //CLEAR

                return true;
            case KeyEvent.KEYCODE_CALL:
                //PICK CALL
                isValidateAddContactToList();
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                finish();
                return true;
            case KeyEvent.KEYCODE_HOME:
                //END CALL

                return true;
            case KeyEvent.KEYCODE_0:
                callDailPad(0);
                return true;
            case KeyEvent.KEYCODE_1:
                callDailPad(1);
                return true;
            case KeyEvent.KEYCODE_2:
                callDailPad(2);
                return true;
            case KeyEvent.KEYCODE_3:
                callDailPad(3);
                return true;
            case KeyEvent.KEYCODE_4:
                callDailPad(4);
                return true;
            case KeyEvent.KEYCODE_5:
                callDailPad(5);
                return true;
            case KeyEvent.KEYCODE_6:
                callDailPad(6);
                return true;
            case KeyEvent.KEYCODE_7:
                callDailPad(7);
                return true;
            case KeyEvent.KEYCODE_8:
                callDailPad(8);
                return true;
            case KeyEvent.KEYCODE_9:
                callDailPad(9);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void callDailPad(int value) {
        if (mEditNumber != null) {
            mEditNumber.setText("" + mEditNumber.getText().toString() + value);
        }
    }

}
