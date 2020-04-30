package jp.mcinc.imesh.type.ipphone.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import jp.mcinc.imesh.type.ipphone.util.NetworkManager;

import static jp.mcinc.imesh.type.ipphone.contants.Constants.*;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.CLIEENT_ID;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.REFRESH_TOKEN;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.REFRESH_TOKEN_URL;

public class PurchaseWaitingSplashActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private SessionManager sessionManager;
    private TextView mTextDeleteInfo;
    private ProgressDialog dialog;
    private RequestQueue queue;
    private String IMEINumber;
    private int REQUEST_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_waiting);
        getSupportActionBar().hide();
        mTextDeleteInfo = findViewById(R.id.text_delete_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager = new SessionManager(this);
        if (NetworkManager.isConnectedToNet(this)) {
            refershToken();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void refershToken() {
        try {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Getting Refresh token, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            queue = Volley.newRequestQueue(this);
            //                HashMap<String, String> jsonObject = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clientId", CLIEENT_ID);
            jsonObject.put("refreshToken", REFRESH_TOKEN);

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, REFRESH_TOKEN_URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e(TAG, "onResponse: " + response.toString());
                                ACCESS_TOKEN = response.getString("accessToken");
                                ID_TOKEN = response.getString("idToken");
                                sessionManager.setAccessToken(ACCESS_TOKEN);
                                sessionManager.setIdToken(ID_TOKEN);
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                callIncomingNumber();
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: " + e.getMessage());
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Snackbar.make(mTextDeleteInfo, "Purchase Failed", Snackbar.LENGTH_SHORT).show();
                            Intent i = new Intent(PurchaseWaitingSplashActivity.this, SplashScreen.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
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

    private void callIncomingNumber() {
        try {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Purchasing Number, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            queue = Volley.newRequestQueue(this);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceId", "" + sessionManager.getDeviceId());

            Log.e(TAG, "callIncomingNumber: " + jsonObject.toString());
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, CREATE_TWILLIO_URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e(TAG, "onResponse: " + response.toString());
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                purchaseNumberSucess(response);
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: " + e.getMessage());
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                Log.e(TAG, "onErrorResponse: " + error.getMessage());
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Snackbar.make(mTextDeleteInfo, "Purchase Failed", Snackbar.LENGTH_SHORT).show();
                                Intent i = new Intent(PurchaseWaitingSplashActivity.this, SplashScreen.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: " + e.getMessage());
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("authorization", sessionManager.getIdToken());
                    params.put("Content-Type", "application/json");
                    params.put("Accept", "application/json");
                    return params;
                }
            };
            queue.add(getRequest);
        } catch (Exception e) {
            Log.e(TAG, "refershToken: " + e.getMessage());
        }
    }

    private void purchaseNumberSucess(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("deviceId"))
            DEVICE_ID = jsonObject.getString("deviceId");
        sessionManager.setDeviceId(jsonObject.getString("deviceId"));
        if (jsonObject.has("phoneNumber"))
            sessionManager.setNumber(jsonObject.getString("phoneNumber"));
        if (jsonObject.has("phoneNumberSid")) {
            sessionManager.setNumberSid(jsonObject.getString("phoneNumberSid"));
            CALL_SID_KEY = sessionManager.getNumberSid();
        }
        Intent i = new Intent(PurchaseWaitingSplashActivity.this, PurchaseSuccessActivity.class);
        startActivity(i);
        finish();
    }
}
