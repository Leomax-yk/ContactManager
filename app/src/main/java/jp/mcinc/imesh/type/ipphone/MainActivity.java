package jp.mcinc.imesh.type.ipphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import jp.mcinc.imesh.type.ipphone.activity.ContactListActivity;
import jp.mcinc.imesh.type.ipphone.activity.PurchaseWaitingSplashActivity;
import jp.mcinc.imesh.type.ipphone.services.BackgroundService;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private int REQUEST_CODE = 1001;
//    private BootCompleteBroadCast MyReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        if (intent.hasExtra("device_id")) {
            sessionManager.setDeviceId(intent.getStringExtra("device_id"));
            sessionManager.setOpen(true);
            if (intent.hasExtra("id_token")) {
                sessionManager.setIdToken(intent.getStringExtra("id_token"));
                sessionManager.setOpen(true);
                if (intent.hasExtra("refresh_token")) {
                    sessionManager.setRefreshToken(intent.getStringExtra("refresh_token"));
                    sessionManager.setOpen(true);
                    Toast.makeText(this, "Got Token", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        }
      /*  //For testing data purpose
        DEVICE_ID = "IMEI:125945689545497";
        sessionManager.setDeviceId(DEVICE_ID);
        sessionManager.setRefreshToken(REFRESH_TOKEN);
        */
        if(sessionManager.isPurchase()) {
            Intent backgroundService = new Intent(MainActivity.this, BackgroundService.class);
            startService(backgroundService);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);

        } else  if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        } else {
            //Dynamic passing of IMEI NUmber
            TelephonyManager telMgr = (TelephonyManager)
                    getSystemService(this.TELEPHONY_SERVICE);
            String deviceId;
            if (Build.VERSION.SDK_INT >= 26) {
                if (telMgr.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                    deviceId = telMgr.getMeid();
                } else if (telMgr.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                    deviceId = telMgr.getImei();
                } else {
                    deviceId = ""; // default!!!
                }
            } else {
                deviceId = telMgr.getDeviceId();
            }
//            sessionManager.setDeviceId("IMEI:" + deviceId);
            processfurther();
        }
    }

    private void setLanguage() {
        sessionManager.setLanguageSet(true);
        sessionManager.setLanguage("ja");
        String languageToLoad = "ja"; // your language
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
                    Intent i = new Intent(MainActivity.this, ContactListActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(MainActivity.this, PurchaseWaitingSplashActivity.class);
                    startActivity(i);
                    finish();
                }
                finish();
            }
        }, 6000);


    }
}
