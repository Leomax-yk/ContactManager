package jp.mcinc.imesh.type.ipphone.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.activity.SplashScreen;
import jp.mcinc.imesh.type.ipphone.activity.VoiceActivity;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;
import jp.mcinc.imesh.type.ipphone.util.NetworkManager;

import static jp.mcinc.imesh.type.ipphone.contants.Constants.ACCESS_TOKEN;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.CLIEENT_ID;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.GET_ACCESS_TOKEN_URL;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.ID_TOKEN;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.REFRESH_TOKEN_URL;

public class BackgroundService extends Service implements RegistrationListener {
    private String TAG = getClass().getSimpleName();
    private RequestQueue queue;
    private SessionManager sessionManager;
    private static Timer timer = new Timer ();
    private static TimerTask hourlyTask;
    public static final String CHANNEL_ID = "IPPhone Background Service";
    private String mAccessToken;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("IPPhone is running...")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_contact)
                .setContentIntent(null)
                .build();
        startForeground(1, notification);

         hourlyTask = new TimerTask () {
            @Override
            public void run () {
                // your code here...
                Log.e(TAG, "run: call timer ");
                refershToken();
            }
        };
        // schedule the task to run starting now and then every hour 6000000 milliseconds
        timer.schedule (hourlyTask, 0l, 1000 * 60 * 6);
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Service is running", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void refershToken() {
        try {
            Log.e(TAG, "refershToken: ");
            sessionManager = new SessionManager(this);
            queue = Volley.newRequestQueue(this);
            //                HashMap<String, String> jsonObject = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clientId", CLIEENT_ID);
            jsonObject.put("refreshToken", sessionManager.getRefreshToken());
            Log.e(TAG, "refershToken: "+jsonObject.toString());
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, REFRESH_TOKEN_URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e(TAG, "onResponse: "+response.toString());
                                ACCESS_TOKEN = response.getString("accessToken");
                                ID_TOKEN = response.getString("idToken");
                                sessionManager.setAccessToken(ACCESS_TOKEN);
                                sessionManager.setIdToken(ID_TOKEN);
                                retrieveAccessToken();
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "onErrorResponse: "+error.getMessage());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", sessionManager.getRefreshToken());
                    return params;
                }
            };
            queue.add(getRequest);
        } catch (Exception e) {
            Log.e(TAG, "refershToken: " + e.getMessage());
        }
    }
    /*
     * Get an access token from your Twilio access token server
     */
    private void retrieveAccessToken() {
        if (NetworkManager.isConnectedToNet(this)) {
            try {
                queue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_ACCESS_TOKEN_URL + sessionManager.getDeviceId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG, "onResponse: Access token" + response.toString());
                            mAccessToken = response.toString();
                            sessionManager.setAccessToken(mAccessToken);
                            registerForCallInvites();
                        } catch (Exception e) {
                            Log.e(TAG, "onResponse: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mAccessToken = sessionManager.getAccessToken();
                        Log.e(TAG, "Failed to get Access token: " + mAccessToken);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", sessionManager.getIdToken());
                        params.put("Content-Type", "application/json");
                        params.put("accept", "text/html");
                        return params;
                    }
                };
                queue.add(stringRequest);
            } catch (Exception e) {
                Log.e(TAG, "retrieveAccessToken: " + e.getMessage());
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Register your FCM token with Twilio to receive incoming call invites
     */
    private void registerForCallInvites() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String fcmToken = instanceIdResult.getToken();
            Log.i(TAG, "Registering with FCM");
            Voice.register(mAccessToken, Voice.RegistrationChannel.FCM, fcmToken, BackgroundService.this);
        });
    }

    @Override
    public void onRegistered(@NonNull String accessToken, @NonNull String fcmToken) {
        Log.e(TAG, "onRegistered: sucessfull");
    }

    @Override
    public void onError(@NonNull RegistrationException registrationException, @NonNull String accessToken, @NonNull String fcmToken) {
        Log.e(TAG, "onError: FCM Registeration");
    }
}
