package jp.mcinc.imesh.type.ipphone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jp.mcinc.imesh.type.ipphone.R;

import androidx.appcompat.app.AppCompatActivity;

public class CancelConfirmActivity extends AppCompatActivity {

    private Button mButtonOk, mButtonCancel;
    private boolean cancelVisible = false;
    private LinearLayout mLinearDelete;
    private TextView mTextYes, mTextNo;
    private int deleteSelection = 0;
    private ImageView mImageMenu;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_confirmation);
        getSupportActionBar().hide();
        mLinearDelete = findViewById(R.id.linear_delete);

        mButtonOk = findViewById(R.id.button_ok);
        mButtonCancel = findViewById(R.id.button_cancel);

        mTextYes = findViewById(R.id.text_yes);
        mTextNo = findViewById(R.id.text_no);

        mImageMenu = findViewById(R.id.image_menu);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi = metrics.densityDpi;
        dpi = dpi - 8;
        dpi = dpi / 3;
        mButtonOk.setPadding(dpi, 0, 0, 0);
        mButtonCancel.setPadding(dpi, 0, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancel();
            }
        });

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancel();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTextYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelection = 0;
                callDeleteMethod();
            }
        });

        mTextNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelection = 1;
                callDeleteMethod();
            }
        });
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyUp: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //Left Key

                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //Right Key

                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //Down Key
                if (cancelVisible)
                    keyUpDowndelete();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                //Up Key
                if (cancelVisible)
                    keyUpDowndelete();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //Up Key
                if (cancelVisible) {
                    Log.e(TAG, "onKeyUp: VISIBLE MENU");
                    deleteSelection = 0;
                    callDeleteMethod();
                } else {
                    showCancel();
                }
                return true;
            case KeyEvent.KEYCODE_MENU: {
                showCancel();
            }
            return true;
            case KeyEvent.KEYCODE_BACK:
                //BACK
                if (cancelVisible) {
                    showCancel();
                } else
                    finish();
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                //CLEAR
                //BACK
                if (cancelVisible) {
                    showCancel();
                }
                return true;
            case KeyEvent.KEYCODE_CALL:
                //PICK CONTACT AND CALL
                return true;
            case KeyEvent.KEYCODE_HOME:
                //PICK CONTACT AND CALL
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                finish();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    private void keyUpDowndelete() {
        if (deleteSelection == 0)
            deleteSelection = 1;
        else
            deleteSelection = 0;
        setMenuDelete();
    }

    private void setMenuDelete() {
        mTextYes.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextNo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (deleteSelection == 0) {
            mTextYes.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else {
            mTextNo.setBackgroundColor(Color.parseColor("#CCCCCC"));
        }
    }

    private void callDeleteMethod() {
        if (deleteSelection == 0) {
            Intent i = new Intent(CancelConfirmActivity.this, CancelWaitingSplashScreen.class);
            startActivity(i);
            finish();
        } else {
            showCancel();
        }
    }

    private void showCancel() {
        if (cancelVisible) {
            mLinearDelete.setVisibility(View.GONE);
            deleteSelection = 0;
        } else {
            mLinearDelete.setVisibility(View.VISIBLE);
        }
        cancelVisible = !cancelVisible;
    }
}
