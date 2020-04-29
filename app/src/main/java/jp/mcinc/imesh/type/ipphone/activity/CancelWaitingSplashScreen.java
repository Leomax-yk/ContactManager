package jp.mcinc.imesh.type.ipphone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.mcinc.imesh.type.ipphone.R;

import androidx.appcompat.app.AppCompatActivity;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;
import jp.mcinc.imesh.type.ipphone.util.NetworkManager;

import static jp.mcinc.imesh.type.ipphone.contants.Constants.ACCESS_TOKEN;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.CLIEENT_ID;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.CREATE_TWILLIO_URL;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.DELETE_TWILLIO_URL;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.ID_TOKEN;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.REFRESH_TOKEN;

public class CancelWaitingSplashScreen extends AppCompatActivity {
    private TextView mTextDeleteInfo;
    private String TAG = getClass().getSimpleName();
    private SessionManager sessionManager;
    private ProgressDialog dialog;
    private RequestQueue queue;
    private String IMEINumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_waiting);
        getSupportActionBar().hide();
        mTextDeleteInfo = findViewById(R.id.text_delete_info);
        mTextDeleteInfo.setText(getResources().getString(R.string.cancel_waiting_message));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkManager.isConnectedToNet(this)) {
            deleteIncomingNumber();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteIncomingNumber() {
        try {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Purchasing Number, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            queue = Volley.newRequestQueue(this);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceId", "" + sessionManager.getDeviceId());
            Log.e(TAG, "callIncomingNumber: " + jsonObject);

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.DELETE, DELETE_TWILLIO_URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e(TAG, "onResponse: " + response.toString());
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Intent i = new Intent(CancelWaitingSplashScreen.this, CancelSuccessfullActivity.class);
                                startActivity(i);
                                finish();
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
                    return params;
                }
            };
            queue.add(getRequest);
        } catch (Exception e) {
            Log.e(TAG, "refershToken: " + e.getMessage());
        }
    }
}
