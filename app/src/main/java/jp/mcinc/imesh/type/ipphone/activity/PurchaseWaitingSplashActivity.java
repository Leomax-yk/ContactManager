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
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(PurchaseWaitingSplashActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(PurchaseWaitingSplashActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
//            return;
//        }
        sessionManager = new SessionManager(this);
//        IMEINumber = telephonyManager.getDeviceId();
//        sessionManager.setImenumber("IMEI:"+IMEINumber);

        if (NetworkManager.isConnectedToNet(this)) {
//            refershToken();
            DEVICE_ID = sessionManager.getDeviceId();
            ID_TOKEN = sessionManager.getIdToken();
            callIncomingNumber();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


//    private void refershToken() {
//        try {
//            dialog = new ProgressDialog(this);
//            dialog.setMessage("Getting Refresh token, Please wait...");
//            dialog.setCancelable(false);
//            dialog.show();
//            queue = Volley.newRequestQueue(this);
//            //                HashMap<String, String> jsonObject = new HashMap<String, String>();
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("clientId", CLIEENT_ID);
//            jsonObject.put("refreshToken", REFRESH_TOKEN);
//
//            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, REFRESH_TOKEN_URL, jsonObject,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                Log.e(TAG, "onResponse: " + response.toString());
//                                ACCESS_TOKEN = response.getString("accessToken");
//                                ID_TOKEN = response.getString("idToken");
//                                if (dialog.isShowing()) {
//                                    dialog.dismiss();
//                                }
//                                callIncomingNumber();
//                            } catch (Exception e) {
//                                Log.e(TAG, "onResponse: " + e.getMessage());
//                                if (dialog.isShowing()) {
//                                    dialog.dismiss();
//                                }
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            if (dialog.isShowing()) {
//                                dialog.dismiss();
//                            }
//                        }
//                    }) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    HashMap<String, String> params = new HashMap<String, String>();
//                    params.put("Authorization", REFRESH_TOKEN);
//                    return params;
//                }
//            };
//            queue.add(getRequest);
//        } catch (Exception e) {
//            Log.e(TAG, "refershToken: " + e.getMessage());
//        }
//    }

    private void callIncomingNumber() {
        try {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Purchasing Number, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            queue = Volley.newRequestQueue(this);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceId", "IMEI:" + IMEINumber);
            Log.e(TAG, "callIncomingNumber: " + jsonObject.toString());

            Log.e(TAG, "callIncomingNumber: " + ID_TOKEN);
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
                                JSONObject response = new JSONObject("{\"deviceId\": \"IMEI:125945689545497\",\"phoneNumberSid\": \"PN6e9a2f80849375ec3a4bfc25b874babf\",\"phoneNumber\": \"+815036270524\"}");
                                purchaseNumberSucess(response);
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
                    params.put("Authorization", ID_TOKEN);
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            queue.add(getRequest);
        } catch (Exception e) {
            Log.e(TAG, "refershToken: " + e.getMessage());
        }
    }

    private void purchaseNumberSucess(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("customerCd"))
            sessionManager.setCustomerCd(jsonObject.getString("customerCd"));
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

//    public class OkHttpHandler extends AsyncTask<String, String, String> {
//        OkHttpClient client = new OkHttpClient();
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                HashMap<String, String> jsonObject = new HashMap<String, String>();
//                jsonObject.put("clientId", CLIEENT_ID);
//                jsonObject.put("refreshToken", REFRESH_TOKEN);
//
//                Log.e(TAG, "doInBackground:2 " + jsonObject);
//
//                okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
//                builder.addHeader("Authorization", REFRESH_TOKEN);
//                builder.url(REFRESH_TOKEN_URL);
//
//                HttpUrl.Builder httpBuilder = HttpUrl.parse(REFRESH_TOKEN_URL).newBuilder();
//                for (Map.Entry<String, String> param : jsonObject.entrySet()) {
//                    httpBuilder.addQueryParameter(param.getKey(), param.getValue());
//                }
//
//                okhttp3.Request request = builder.url(httpBuilder.build()).build();
//                okhttp3.Response response = client.newCall(request).execute();
//
//                Log.e(TAG, "doInBackground:3 " + response);
//
//                return response.body().string();
//            } catch (Exception e) {
//                Log.e(TAG, "doInBackground:4 " + e.getMessage());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//            super.onPostExecute(response);
//            try {
//                Log.e("Responce in Purhcase : ", response);
//                JSONObject jsonObject = new JSONObject(response);
//
//
//
//            } catch (Exception e) {
//                Log.e(TAG, "onPostExecute: " + e.getMessage());
//            }
//        }
//    }


//    private void callAddressesApi() {
//        try {
//            if (NetworkManager.isConnectedToNet(this)) {
//                dialog = new ProgressDialog(this);
//                dialog.setMessage("Please wait...");
//                dialog.setCancelable(false);
//                dialog.show();
//                queue = Volley.newRequestQueue(this);
//                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, INCOMINGPHONENUMBER_LOCAL, null,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    Log.e(TAG, "onResponse: " + response.toString());
//                                    //Available available = PurchaseController.fromJsonString(response.toString());
//
//                                    if (dialog.isShowing()) {
//                                        dialog.dismiss();
//                                    }
//                                } catch (Exception e) {
//                                    Log.e(TAG, "onResponse: " + e.getMessage());
//                                    if (dialog.isShowing()) {
//                                        dialog.dismiss();
//                                    }
//                                }
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.e(TAG, "onErrorResponse: " + error.getMessage());
//                                if (dialog.isShowing()) {
//                                    dialog.dismiss();
//                                }
//                            }
//                        }) {
//                    @Override
//                    public Map<String, String> getHeaders() {
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        String credentials = Credentials.basic(ACCOUNT_SID, AUTH_TOKEN);
//                        params.put("Authorization", credentials);
//
//                        params.put("CustomerName", "MC user");
//                        params.put("Street", "Higashiomichi");
//                        params.put("City", "Oita-shi");
//                        params.put("Region", "Oita");
//                        params.put("PostalCode", "8700823");
//                        params.put("IsoCountry", "JPN");
//                        return params;
//                    }
//                };
//
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_ADDRESSES, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "onResponse E:  " + error.getMessage());
//                    }
//                }) {
//                    @Override
//                    public Map<String, String> getHeaders() {
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        String credentials = Credentials.basic(ACCOUNT_SID, AUTH_TOKEN);
//                        params.put("Authorization", credentials);
//                        return params;
//                    }
//
//                    @Override
//                    public String getBodyContentType() {
//                        return "application/json";
//                    }
//                };
//                queue.add(getRequest);
//            } else {
//                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "onResponse: " + e.getMessage());
//        }
//    }
//    public void postIncomingRequest(){
//        try{
//            MediaType MEDIA_TYPE = MediaType.parse("application/json");
//            OkHttpClient client = new OkHttpClient();
//            JSONObject jsonObject = new JSONObject();
//            try {
//                String number = sessionManager.getNumber();
//                number = number.substring(2);
//                Log.e(TAG, "postIncomingRequest: " + number);
//                jsonObject.put("accountSid", ACCOUNT_SID);
//                jsonObject.put("addressSid", ADDRESS_SID);
//                jsonObject.put("bundleSid", BUNDLE_SID);
//                jsonObject.put("phoneNumber", "" + sessionManager.getNumber());
////                jsonObject.put("areaCode", "+81");
//
////                jsonObject.put("PhoneNumber", "" + sessionManager.getNumber());
////                jsonObject.put("AddressSid", ADDRESS_SID);
////                jsonObject.put("BundleSid", BUNDLE_SID);
//
//                Log.e(TAG, "postIncomingRequest: " + jsonObject);
//            } catch(JSONException e){
//                Log.e(TAG, "postIncomingRequest: "+e.getMessage() );
//            }
//
//            RequestBody body = RequestBody.create(MEDIA_TYPE, jsonObject.toString());
//
//            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
//            String credentials = Credentials.basic(ACCOUNT_SID, AUTH_TOKEN);
//
//            Request request = new Request.Builder()
//                    .url(INCOMINGPHONENUMBER_LOCAL)
//                    .post(body)
//                    .addHeader("Authorization", credentials)
//                    .header("Accept", "application/json")
//                    .header("Content-Type", "application/json")
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    String mMessage = e.getMessage().toString();
//                    Log.w("failure Response", mMessage);
//                    //call.cancel();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String mMessage = response.body().string();
//                    Log.e(TAG, mMessage);
//                    try{
//
//                        Log.e("Responce in Purhcase : ", mMessage);
//                        JSONObject jsonObject = new JSONObject(mMessage);
//
//                        if(jsonObject.getString("status").equals("in-use")){
//                            sessionManager.setPurchaseResponse(mMessage);
//                            Intent i = new Intent(PurchaseWaitingSplashActivity.this, PurchaseSuccessActivity.class);
//                            startActivity(i);
//                            finish();
//                        }
//                    }catch (Exception e){
//                        Log.e(TAG, "onPostExecute: " + e.getMessage());
//                    }
//                }
//            });
//        }catch(Exception e){
//            Log.e(TAG, "postIncomingRequest: "+e.getMessage() );
//        }
//    }

}
