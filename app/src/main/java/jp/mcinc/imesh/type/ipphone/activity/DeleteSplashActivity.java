package jp.mcinc.imesh.type.ipphone.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import jp.mcinc.imesh.type.ipphone.R;

public class DeleteSplashActivity extends AppCompatActivity {
    private TextView mTextDeleteInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        getSupportActionBar().hide();
        mTextDeleteInfo = findViewById(R.id.text_delete_info);
        mTextDeleteInfo.setText(getResources().getString(R.string.deleted_sucessfully));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                finish();
            }
        }, 2000);
    }
}
