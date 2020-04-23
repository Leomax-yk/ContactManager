package jp.mcinc.imesh.type.ipphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;

import androidx.appcompat.app.AppCompatActivity;

public class PurchaseSuccessActivity extends AppCompatActivity {
    private TextView mTextDeleteInfo;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_success);
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        mTextDeleteInfo = findViewById(R.id.text_delete_info);
        mTextDeleteInfo.setText("\n" + sessionManager.getNumber() + "\n" + getResources().getString(R.string.successfully_purchase_this_number));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                sessionManager.setPurchase(true);
                Intent i = new Intent(PurchaseSuccessActivity.this, ContactListActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}