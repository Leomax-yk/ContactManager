package jp.mcinc.imesh.type.ipphone.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;

import jp.mcinc.imesh.type.ipphone.R;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    private SessionManager sessionManager;
    private int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashScreen.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
            return;
        }else{
            processfurther();
        }
//        if(sessionManager.isLanguageSet())
//        else
//            setLanguage();
    }

    private void setLanguage() {
        sessionManager.setLanguageSet(true);
        sessionManager.setLanguage("ja");
        String languageToLoad  = "ja"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


    }

    private void processfurther() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after
                if (sessionManager.isPurchase()) {
                    Intent i = new Intent(SplashScreen.this, ContactListActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, PurchaseWaitingSplashActivity.class);
                    startActivity(i);
                    finish();
                }
                finish();
            }
        }, 2000);


    }
}
