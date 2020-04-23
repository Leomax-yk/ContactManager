package jp.mcinc.imesh.type.ipphone.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;
import jp.mcinc.imesh.type.ipphone.R;

public class CallContactActivity extends AppCompatActivity {
    private ImageView mImageCall, mImageHistory;
    private TextView mTextSelectContact;
    private TextInputEditText mEditDail;
    private int MIC_PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_contact);
        getSupportActionBar().hide();
        mEditDail = findViewById(R.id.text_input_dail);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_REQUEST_CODE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_0:
                //BACK
                keyNumber("0");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_1:
                //BACK
                keyNumber("1");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
                //BACK
                keyNumber("2");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
                //BACK
                keyNumber("3");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_4:
                //BACK
                keyNumber("4");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_5:
                //BACK
                keyNumber("5");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
                //BACK
                keyNumber("6");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
                //BACK
                keyNumber("7");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
                //BACK
                keyNumber("8");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
                //BACK
                keyNumber("9");
                return true;
            case KeyEvent.KEYCODE_BACK:
                //BACK
                finish();
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                //CLEAR

                return true;
            case KeyEvent.KEYCODE_CALL:
                //PICK CALL
                Toast.makeText(getApplicationContext(), "Call keypad", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                Toast.makeText(getApplicationContext(), "End Call keypad", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void keyNumber(String s) {
        if (mEditDail.getText().length() < 11) {
            mEditDail.setText(mEditDail.getText().toString() + s);
        }
    }

    private void clearText(){
        int length = mEditDail.getText().length();
        if (length > 0) {
            mEditDail.getText().delete(length - 1, length);
        }
    }
}
