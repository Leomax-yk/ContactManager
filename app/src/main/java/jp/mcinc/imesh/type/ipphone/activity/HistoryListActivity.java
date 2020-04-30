package jp.mcinc.imesh.type.ipphone.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.ConnectOptions;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.mcinc.imesh.type.ipphone.BuildConfig;
import jp.mcinc.imesh.type.ipphone.adapter.HistoryListItemAdapter;
import jp.mcinc.imesh.type.ipphone.contants.Constants;
import jp.mcinc.imesh.type.ipphone.controller.SoundPoolManager;
import jp.mcinc.imesh.type.ipphone.model.HistroyListItemModel;

import jp.mcinc.imesh.type.ipphone.R;

import jp.mcinc.imesh.type.ipphone.database.DBManager;
import jp.mcinc.imesh.type.ipphone.notification.IncomingCallNotificationService;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;
import jp.mcinc.imesh.type.ipphone.util.NetworkManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static jp.mcinc.imesh.type.ipphone.contants.Constants.GET_ACCESS_TOKEN_URL;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.ID_TOKEN;

public class HistoryListActivity extends AppCompatActivity {
    private DBManager dbManager;
    private RecyclerView mRecyclerView;
    private TextView mTextNoData, mTextDelete, mTextYes, mTextNo;
    private ArrayList<HistroyListItemModel> mHistroyListItemModels;
    private HistoryListItemAdapter mHistoryListItemAdapter;
    private String TAG = getClass().getSimpleName();
    private LinearLayout mLinearMenu, mLinearDelete;
    private int menuSelection = 0, deleteSelection = 0;
    private boolean deleteVisible = false, menuVisible = false;
    private int pos = 0;

    private RecyclerViewClickListener recyclerViewClickListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
    private int MIC_PERMISSION_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        getSupportActionBar().hide();
        mLinearMenu = findViewById(R.id.linear_menu);
        mLinearDelete = findViewById(R.id.linear_delete);
        mRecyclerView = findViewById(R.id.recycler_view_contact);
        mTextNoData = findViewById(R.id.text_no_data);
        mTextNoData.setText(getResources().getString(R.string.no_history_avaiable));
        mTextDelete = findViewById(R.id.text_delete);
        mTextYes = findViewById(R.id.text_yes);
        mTextNo = findViewById(R.id.text_no);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dbManager = new DBManager(this);
        dbManager.open();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if microphone permissions is granted
         */
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mRecyclerView,
                        "Microphone permissions needed. Please allow in your application settings.",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewClickListener = (view, position) -> {
            pos = position;
            makeCall("CALL");
        };

        if (mLinearMenu.getVisibility() != View.VISIBLE) {
            menuVisible = false;
            menuSelection = 0;
        }
        setHistoryData();

        mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 0;
                callEvent();
            }
        });
        mTextYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelection = 0;
                callDeleteMethod();
            }
        });

        mTextNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelection = 1;
                callDeleteMethod();
            }
        });
    }

    private void setHistoryData() {
        // Getting list of contact from the DATABASE
        mHistroyListItemModels = dbManager.getHistoryListItem();
        if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
            mHistoryListItemAdapter = new HistoryListItemAdapter(this, mHistroyListItemModels, recyclerViewClickListener);
            mRecyclerView.setAdapter(mHistoryListItemAdapter);
            mHistoryListItemAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextNoData.setVisibility(View.GONE);
        } else {
            // No contact available in DATABASE
            mRecyclerView.setVisibility(View.GONE);
            mTextNoData.setVisibility(View.VISIBLE);
        }
    }

    private void callDeleteMethod() {
        if (deleteSelection == 0) {
            if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
                dbManager.deleteHistory(mHistroyListItemModels.get(pos).getId());
                Intent i = new Intent(HistoryListActivity.this, DeleteSplashActivity.class);
                startActivity(i);
            } else {
                showToast("No Contact to delete");
            }
        }
        showDeleteMenu();
        deleteVisible = !deleteVisible;
    }

    private void showDeleteMenu() {
        if (menuVisible) {
            showMenu();
        }
        if (deleteVisible) {
            mLinearDelete.setVisibility(View.GONE);
            deleteSelection = 0;
        } else {
            mLinearDelete.setVisibility(View.VISIBLE);
        }
    }

    private void keyUpDowndelete() {
        if (deleteSelection == 0)
            deleteSelection = 1;
        else
            deleteSelection = 0;
        setMenuDelete();
    }

    private void setMenuDelete() {
        mTextYes.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextNo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (deleteSelection == 0) {
            mTextYes.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else {
            mTextNo.setBackgroundColor(Color.parseColor("#CCCCCC"));
        }
    }

    private void callEvent() {
        showMenu();
        menuSelection = 0;
        if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
            showDeleteMenu();
            setMenuDelete();
            deleteVisible = !deleteVisible;
        } else {
            showToast("Add Contact to delete");
        }
    }

    private void showMenu() {
        if (menuVisible) {
            mLinearMenu.setVisibility(View.GONE);
        } else {
            mLinearMenu.setVisibility(View.VISIBLE);
        }
        menuVisible = !menuVisible;
    }

    private void callNumber(String value) {
        startActivity(new Intent(HistoryListActivity.this, VoiceActivity.class).putExtra("num", "" + value).putExtra("call",true).setAction(Constants.OUTGOING_CALL_INVITE));
    }

    private void makeCall(String number) {
        Date date = new Date();
        dbManager.insertHistory(3, mHistroyListItemModels.get(mHistoryListItemAdapter.getPos()).getOwnerName(), mHistroyListItemModels.get(mHistoryListItemAdapter.getPos()).getOwnerNumber(), "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
        setHistoryData();
        callNumber(mHistroyListItemModels.get(mHistoryListItemAdapter.getPos()).getOwnerNumber());
    }

    private void showToast(String message){
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void iJustWantToScroll() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(pos);
            }
        }, 100);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //BACK
                //BACK
                if (menuVisible) {
                    showMenu();
                } else if (deleteVisible) {
                    showDeleteMenu();
                    deleteVisible = !deleteVisible;
                } else
                    finish();
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                //CLEAR
                if (mLinearMenu.getVisibility() == View.VISIBLE) {
                    showMenu();
                } else if (deleteVisible) {
                    showDeleteMenu();
                    deleteVisible = !deleteVisible;
                }
                return true;
            case KeyEvent.KEYCODE_CALL:
                //PICK CONTACT AND CALL
                makeCall("CALL");
                return true;
            case KeyEvent.KEYCODE_HOME:
                //PICK CONTACT AND CALL

                return true;
            case KeyEvent.KEYCODE_MENU: {
                //PICK CONTACT AND CALL
                showMenu();
            }
            return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //Left Key

                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //Right Key

                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //Down Key
                if (!menuVisible && !deleteVisible) {
                    if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
                        mHistroyListItemModels.get(pos).setCheck(false);
                        if ((mHistroyListItemModels.size() - 1) == pos) {
                            pos = 0;
                            mHistoryListItemAdapter.setPos(0);
                            mHistroyListItemModels.get(pos).setCheck(true);
                        } else {
                            pos = pos + 1;
                            mHistoryListItemAdapter.setPos((pos + 1));
                            mHistroyListItemModels.get(pos).setCheck(true);
                        }
                        mHistoryListItemAdapter = new HistoryListItemAdapter(this, mHistroyListItemModels, recyclerViewClickListener);
                        mRecyclerView.setAdapter(mHistoryListItemAdapter);
                        mHistoryListItemAdapter.notifyDataSetChanged();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        showToast("No call history");
                    }
                } else {
                    if (deleteVisible)
                        keyUpDowndelete();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                //Up Key
                if (!menuVisible && !deleteVisible) {
                    if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
                        mHistroyListItemModels.get(pos).setCheck(false);
                        if (pos == 0) {
                            pos = mHistroyListItemModels.size() - 1;
                            mHistoryListItemAdapter.setPos(pos);
                            mHistroyListItemModels.get(pos).setCheck(true);
                        } else {
                            pos = pos - 1;
                            mHistoryListItemAdapter.setPos((pos));
                            mHistroyListItemModels.get(pos).setCheck(true);
                        }
                        mHistoryListItemAdapter = new HistoryListItemAdapter(this, mHistroyListItemModels, recyclerViewClickListener);
                        mRecyclerView.setAdapter(mHistoryListItemAdapter);
                        mHistoryListItemAdapter.notifyDataSetChanged();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        showToast("No call history");
                    }
                } else {
                    if (deleteVisible)
                        keyUpDowndelete();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //Up Key
                //Up Key
                if (menuVisible) {
                    Log.e(TAG, "onKeyUp: VISIBLE MENU");
                    callEvent();
                } else if (deleteVisible) {
                    callDeleteMethod();
                } else {
                    Log.e(TAG, "onKeyUp: NOTVISIBLE MENU");
                    makeCall("CALL");
                }
                return true;

            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
//                makeEndCall();
                finish();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
