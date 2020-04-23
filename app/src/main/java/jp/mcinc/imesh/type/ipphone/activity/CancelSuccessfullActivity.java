package jp.mcinc.imesh.type.ipphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;

import androidx.appcompat.app.AppCompatActivity;

public class CancelSuccessfullActivity extends AppCompatActivity {
    private TextView mTextDeleteInfo;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_sucess);
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        mTextDeleteInfo = findViewById(R.id.text_delete_info);
        mTextDeleteInfo.setText(sessionManager.getNumber() + " " + getResources().getString(R.string.cancelled_sucessfully));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                sessionManager.setPurchase(false);
                Intent i = new Intent(CancelSuccessfullActivity.this, SplashScreen.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}