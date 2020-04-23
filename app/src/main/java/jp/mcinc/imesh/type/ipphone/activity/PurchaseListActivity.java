package jp.mcinc.imesh.type.ipphone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.mcinc.imesh.type.ipphone.controller.PurchaseController;
import jp.mcinc.imesh.type.ipphone.model.Available;
import jp.mcinc.imesh.type.ipphone.model.PurchaseListItemModel;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;
import jp.mcinc.imesh.type.ipphone.util.NetworkManager;
import okhttp3.Credentials;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.adapter.PurchaseListItemAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static jp.mcinc.imesh.type.ipphone.contants.Constants.*;

public class PurchaseListActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private RecyclerView mRecyclerView;
    private TextView mTextNoData, mTextYes, mTextNo, mTextPopupMessage;
    private int saveSelection = 0;
    private SessionManager sessionManager;
    private ArrayList<PurchaseListItemModel> mPurchaseListItemModels;
    private PurchaseListItemAdapter mPurchaseListItemAdapter;
    private PurchaseListItemModel mPurchaseListItemModel;
    private boolean saveVisible = false;
    private int pos = 0;
    private LinearLayout mLinearDelete;
    private ProgressDialog dialog;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_purchase_list);
        sessionManager = new SessionManager(this);

        mLinearDelete = findViewById(R.id.linear_delete);

        mRecyclerView = findViewById(R.id.recycler_view_number);
        mTextNoData = findViewById(R.id.text_no_data);
        mTextYes = findViewById(R.id.text_yes);
        mTextNo = findViewById(R.id.text_no);
        mTextPopupMessage = findViewById(R.id.text_popup_message);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        callAvailableNumberApi();
        //new GetAvailablePhoneNumber().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSelection = 0;
                saveNumber(mPurchaseListItemModel);
            }
        });

        mTextNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSelection = 1;
                saveNumber(mPurchaseListItemModel);
            }
        });

        mTextNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAvailableNumberApi();
            }
        });

    }

    private void dummyData() {
        try {
            JSONObject response = new JSONObject("{\"available_phone_numbers\": [{\"friendly_name\": \"+815030335849\", \"phone_number\": \"+815030335849\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044354\", \"phone_number\": \"+815032044354\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044346\", \"phone_number\": \"+815032044346\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815030856917\", \"phone_number\": \"+815030856917\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044683\", \"phone_number\": \"+815032044683\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044693\", \"phone_number\": \"+815032044693\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031162872\", \"phone_number\": \"+815031162872\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031600280\", \"phone_number\": \"+815031600280\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044801\", \"phone_number\": \"+815032044801\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815030338241\", \"phone_number\": \"+815030338241\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031609559\", \"phone_number\": \"+815031609559\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044360\", \"phone_number\": \"+815032044360\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031162845\", \"phone_number\": \"+815031162845\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815030335861\", \"phone_number\": \"+815030335861\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815030856908\", \"phone_number\": \"+815030856908\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031374632\", \"phone_number\": \"+815031374632\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044436\", \"phone_number\": \"+815032044436\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044762\", \"phone_number\": \"+815032044762\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044522\", \"phone_number\": \"+815032044522\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044446\", \"phone_number\": \"+815032044446\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031374681\", \"phone_number\": \"+815031374681\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044572\", \"phone_number\": \"+815032044572\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044333\", \"phone_number\": \"+815032044333\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815030338211\", \"phone_number\": \"+815030338211\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044782\", \"phone_number\": \"+815032044782\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031555647\", \"phone_number\": \"+815031555647\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031162834\", \"phone_number\": \"+815031162834\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031600295\", \"phone_number\": \"+815031600295\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815031555641\", \"phone_number\": \"+815031555641\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } }, {\"friendly_name\": \"+815032044467\", \"phone_number\": \"+815032044467\", \"lata\": null, \"rate_center\": null, \"latitude\": null, \"longitude\": null, \"locality\": \"Japan Proper\", \"region\": null, \"postal_code\": null, \"iso_country\": \"JP\", \"address_requirements\": \"any\", \"beta\": false, \"capabilities\": {\"voice\": true, \"SMS\": false, \"MMS\": false, \"fax\": true } } ], \"uri\": \"/2010-04-01/Accounts/ACfec7df38e00a510a01919088b484bf3c/AvailablePhoneNumbers/JP/Local.json?Format=json&CountryCode=JP&VoiceEnabled=true&__referrer=runtime\"}");
            Log.e(TAG, "onResponse: " + response.toString());
            Available available = PurchaseController.fromJsonString(response.toString());
            mPurchaseListItemModels = new ArrayList<>();
            if (available != null && available.getAvailablePhoneNumbers() != null && available.getAvailablePhoneNumbers().length > 0) {
                for (int i = 0; i < available.getAvailablePhoneNumbers().length; i++) {
                    PurchaseListItemModel purchaseListItemModel = new PurchaseListItemModel();
                    purchaseListItemModel.setId(i);
                    purchaseListItemModel.setCheck(false);
                    purchaseListItemModel.setOwnerNumber("" + available.getAvailablePhoneNumbers()[i].getPhoneNumber());
                    mPurchaseListItemModels.add(purchaseListItemModel);
                }
            }
            if (mPurchaseListItemModels != null && mPurchaseListItemModels.size() > 0) {
                mPurchaseListItemModels.get(0).setCheck(true);
                setAdapterList();
            } else {
                // No contact available in DATABASE
                mRecyclerView.setVisibility(View.GONE);
                mTextNoData.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse: " + e.getMessage());
        }
    }

    private void callAvailableNumberApi() {
        try {
            if (NetworkManager.isConnectedToNet(this)) {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.show();

                queue = Volley.newRequestQueue(this);
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, AVAILABLE_JP_LOCAL_VOICE, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.e(TAG, "onResponse: " + response.toString());
                                    Available available = PurchaseController.fromJsonString(response.toString());
                                    mPurchaseListItemModels = new ArrayList<>();
                                    if (available != null && available.getAvailablePhoneNumbers() != null && available.getAvailablePhoneNumbers().length > 0) {
                                        for (int i = 0; i < available.getAvailablePhoneNumbers().length; i++) {
                                            PurchaseListItemModel purchaseListItemModel = new PurchaseListItemModel();
                                            purchaseListItemModel.setId(i);
                                            purchaseListItemModel.setCheck(false);
                                            purchaseListItemModel.setOwnerNumber("" + available.getAvailablePhoneNumbers()[i].getPhoneNumber());
                                            mPurchaseListItemModels.add(purchaseListItemModel);
                                        }
                                    }
                                    if (mPurchaseListItemModels != null && mPurchaseListItemModels.size() > 0) {
                                        mPurchaseListItemModels.get(0).setCheck(true);
                                        setAdapterList();
                                    } else {
                                        // No contact available in DATABASE
                                        mRecyclerView.setVisibility(View.GONE);
                                        mTextNoData.setVisibility(View.VISIBLE);
                                        //dummyData();
                                    }
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
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        String credentials = Credentials.basic(ACCOUNT_SID, AUTH_TOKEN);
                        params.put("Authorization", credentials);
                        return params;
                    }
                };
                queue.add(getRequest);
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse: " + e.getMessage());
        }
    }

    private void saveNumber(PurchaseListItemModel purchaseListItemModel) {
        if (saveSelection == 0) {
            Log.e(TAG, "saveNumber: "+purchaseListItemModel.getOwnerNumber());
            sessionManager.setNumber(purchaseListItemModel.getOwnerNumber());
            Intent i = new Intent(PurchaseListActivity.this, PurchaseWaitingSplashActivity.class);
            startActivity(i);
            finish();
        }
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
                if (!saveVisible) {
                    if (mPurchaseListItemModels != null && mPurchaseListItemModels.size() > 0) {
                        mPurchaseListItemModels.get(pos).setCheck(false);
                        if ((mPurchaseListItemModels.size() - 1) == pos) {
                            pos = 0;
                            mPurchaseListItemAdapter.setPos(0);
                            mPurchaseListItemModels.get(pos).setCheck(true);
                        } else {
                            pos = pos + 1;
                            mPurchaseListItemAdapter.setPos((pos + 1));
                            mPurchaseListItemModels.get(pos).setCheck(true);
                        }
                        setAdapterList();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mTextNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    keyUpDowndelete();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                //Up Key
                if (!saveVisible) {
                    if (mPurchaseListItemModels != null && mPurchaseListItemModels.size() > 0) {
                        mPurchaseListItemModels.get(pos).setCheck(false);
                        if (pos == 0) {
                            pos = mPurchaseListItemModels.size() - 1;
                            mPurchaseListItemAdapter.setPos(pos);
                            mPurchaseListItemModels.get(pos).setCheck(true);
                        } else {
                            pos = pos - 1;
                            mPurchaseListItemAdapter.setPos((pos));
                            mPurchaseListItemModels.get(pos).setCheck(true);
                        }
                        setAdapterList();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mTextNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    keyUpDowndelete();

                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //Up Key
                if (saveVisible) {
                    saveNumber(mPurchaseListItemModel);
                } else {
                    if (mPurchaseListItemModels != null && mPurchaseListItemModels.size() > 0) {
                        showSaveMenu();
                    }else {
                        callAvailableNumberApi();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_MENU: {

            }
            return true;
            case KeyEvent.KEYCODE_BACK:
                //BACK
                if (saveVisible) {
                    showSaveMenu();
                } else
                    finish();
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                //CLEAR
                //BACK
                if (saveVisible) {
                    showSaveMenu();
                }
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                finish();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    private void showSaveMenu() {
        if (saveVisible) {
            mLinearDelete.setVisibility(View.GONE);
            saveSelection = 0;
        } else {
            mLinearDelete.setVisibility(View.VISIBLE);
            mTextPopupMessage.setText(getResources().getString(R.string.do_you_want_to_puchase_this_number) + " \n" + mPurchaseListItemModel.getOwnerNumber());
        }
        saveVisible = !saveVisible;
    }

    private void keyUpDowndelete() {
        if (saveSelection == 0)
            saveSelection = 1;
        else
            saveSelection = 0;
        setMenuDelete();
    }

    private void setMenuDelete() {
        mTextYes.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextNo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (saveSelection == 0) {
            mTextYes.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else {
            mTextNo.setBackgroundColor(Color.parseColor("#CCCCCC"));
        }
    }

    private void iJustWantToScroll() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(pos);
            }
        }, 500);
    }

    private void saveMenuOperate() {

    }

    private void setAdapterList() {
        mPurchaseListItemAdapter = new PurchaseListItemAdapter(PurchaseListActivity.this, mPurchaseListItemModels, new PurchaseListItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PurchaseListItemModel item) {
                Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                mPurchaseListItemModel = item;
                saveNumber(mPurchaseListItemModel);
            }
        });
        mRecyclerView.setAdapter(mPurchaseListItemAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        mTextNoData.setVisibility(View.GONE);
    }

    class GetAvailablePhoneNumber extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                /**
                 * Initialize the Twilio environment.
                 *
                 * @param username account to use
                 * @param password auth token for the account
                 * @param accountSid account sid to use
                //                 */
//                Twilio.init(USER, AUTH_TOKEN, ACCOUNT_SID);
//
//                AvailablePhoneNumberCountry availablePhoneNumberCountry =
//                        AvailablePhoneNumberCountry.fetcher("IN")
//                                .fetch();

                mPurchaseListItemModels = new ArrayList<>();

                for (int i = 0; i < 50; i++) {
                    PurchaseListItemModel purchaseListItemModel = new PurchaseListItemModel();
                    purchaseListItemModel.setId(i);
                    purchaseListItemModel.setCheck(false);
                    purchaseListItemModel.setOwnerNumber("+91000000000" + i);
                    mPurchaseListItemModels.add(purchaseListItemModel);
                }
//                Log.e(TAG, "doInBackground: " + availablePhoneNumberCountry.getCountry());
            } catch (Exception e1) {
                Log.e(TAG, "doInBackground: " + e1.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mPurchaseListItemModels != null && mPurchaseListItemModels.size() > 0) {
                mPurchaseListItemModels.get(0).setCheck(true);
                setAdapterList();
            } else {
                // No contact available in DATABASE
                mRecyclerView.setVisibility(View.GONE);
                mTextNoData.setVisibility(View.VISIBLE);
            }
        }
    }

//    private void getPhoneNumberList() {
//        try {
//            // new GetAvailablePhoneNumber().execute();
//            String URL = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/AvailablePhoneNumbers/IN/Mobile.json";
//            StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.e(TAG, "onResponse: " + response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d("error", error.toString());
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    //headers.put("Content-Type", "application/json");
//                    headers.put("token", AUTH_TOKEN);
//                    return headers;
//                }
//            };
//            AppController.getInstance().addToRequestQueue(request);
//        } catch (Exception e) {
//            Log.e(TAG, "onResume: " + e.getMessage());
//        }
//    }

//    private void getAvailablePhoneNumber() {
//        String URL = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/AvailablePhoneNumbers/IN/Mobile.json";
//        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL,
//                null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.e(TAG, "onResponse: " + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "onResponse: " + error.getMessage());
//            }
//        });
//        AppController.getInstance().addToRequestQueue(req);
//    }

//    private void getKeySID() {
//        String URL = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Keys.json";
//        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL,
//                null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.e(TAG, "onResponse: " + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "onResponse: " + error.getMessage());
//            }
//        });
//        AppController.getInstance().addToRequestQueue(req);
//    }

}
