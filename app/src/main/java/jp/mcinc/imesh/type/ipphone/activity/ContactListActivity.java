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
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.loader.content.CursorLoader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.mcinc.imesh.type.ipphone.BuildConfig;
import jp.mcinc.imesh.type.ipphone.adapter.ContactListItemAdapter;
import jp.mcinc.imesh.type.ipphone.broadcast.BootCompleteBroadCast;
import jp.mcinc.imesh.type.ipphone.contants.Constants;
import jp.mcinc.imesh.type.ipphone.controller.SoundPoolManager;
import jp.mcinc.imesh.type.ipphone.model.ContactListItemModel;

//import com.google.common.collect.Lists;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Say;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.ConnectOptions;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONObject;

import jp.mcinc.imesh.type.ipphone.R;

import jp.mcinc.imesh.type.ipphone.database.DBManager;
import jp.mcinc.imesh.type.ipphone.notification.IncomingCallNotificationService;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;
import jp.mcinc.imesh.type.ipphone.util.NetworkManager;

import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static jp.mcinc.imesh.type.ipphone.contants.Constants.CALL_SID_KEY;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.GET_ACCESS_TOKEN_URL;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.ID_TOKEN;

public class ContactListActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private DBManager dbManager;
    private SessionManager sessionManager;
    private RecyclerView mRecyclerView;
    private TextView mTextNoData, mTextAdd, mTextEdit, mTextDelete, mTextMaintanance, mTextYes, mTextNo;
    private Button mButtonCall, mButtonHistory, mButtonBack;
    private ArrayList<ContactListItemModel> mContactListItemModel;
    private ContactListItemAdapter mContactListItemAdapter;
    private LinearLayout mLinearMenu, mLinearDelete;
    private ImageView mImageMenu, mImageDail;
    private int menuSelection = 0, deleteSelection = 0, pos = 0;
    private int savedAudioMode = AudioManager.MODE_INVALID;
    private boolean deleteVisible = false, menuVisible = false, isReceiverRegistered = false;
    private AudioManager audioManager;
    private VoiceBroadcastReceiver voiceBroadcastReceiver;

    // Empty HashMap, never populated for the Quickstart
    private HashMap<String, String> params = new HashMap<>();
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton callActionFab;
    private FloatingActionButton hangupActionFab;
    private FloatingActionButton holdActionFab;
    private FloatingActionButton muteActionFab;
    private Chronometer chronometer;

    private NotificationManager notificationManager;
    private AlertDialog alertDialog;
    private CallInvite activeCallInvite;
    private Call activeCall;
    private Call.Listener callListener = callListener();
    private int activeCallNotificationId;

    RegistrationListener registrationListener = registrationListener();
    private String mAccessToken;
    private ProgressDialog dialog;
    private RequestQueue queue;

    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    private RecyclerViewClickListener recyclerViewClickListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        CALL_SID_KEY = sessionManager.getNumberSid();

        mRecyclerView = findViewById(R.id.recycler_view_contact);
        mLinearMenu = findViewById(R.id.linear_menu);
        mLinearDelete = findViewById(R.id.linear_delete);

        mImageMenu = findViewById(R.id.image_menu);
        mImageDail = findViewById(R.id.image_dail);

        mTextNoData = findViewById(R.id.text_no_data);
        mTextNoData.setText(getResources().getString(R.string.no_contact_avaiable));
        mTextAdd = findViewById(R.id.text_add);
        mTextEdit = findViewById(R.id.text_edit);
        mTextDelete = findViewById(R.id.text_delete);
        mTextYes = findViewById(R.id.text_yes);
        mTextNo = findViewById(R.id.text_no);
        mTextMaintanance = findViewById(R.id.text_maintanance);

        mButtonCall = findViewById(R.id.button_call);
        mButtonHistory = findViewById(R.id.button_history);
        mButtonBack = findViewById(R.id.button_back);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi = metrics.densityDpi;
        dpi = dpi - 8;
        dpi = dpi / 2;
        mButtonCall.setPadding(dpi, 0, 0, 0);
        mButtonHistory.setPadding(dpi, 0, 0, 0);
        mButtonBack.setPadding(dpi, 0, 0, 0);
        dbManager = new DBManager(this);
        dbManager.open();

        GetAccessToken();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /*
         * Setup the broadcast receiver to be notified of FCM Token updates
         * or incoming call invite in this Activity.
         */
        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        //registerReceiver();

        /*
         * Needed for setting/abandoning audio focus during a call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Setup the UI
         */
        resetUI();

        /*
         * Displays a call dialog if the intent contains a call invite
         */
        handleIncomingCallIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingCallIntent(intent);
    }

    private RegistrationListener registrationListener() {
        return new RegistrationListener() {
            @Override
            public void onRegistered(@NonNull String accessToken, @NonNull String fcmToken) {
                Log.d(TAG, "Successfully registered FCM " + fcmToken);
            }

            @Override
            public void onError(@NonNull RegistrationException error,
                                @NonNull String accessToken,
                                @NonNull String fcmToken) {
                String message = String.format(
                        Locale.getDefault(),
                        "Registration Error: %d, %s",
                        error.getErrorCode(),
                        error.getMessage());
                Log.e(TAG, message);
                Log.e(TAG, "" + accessToken);
                Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
            }
        };
    }

    private Call.Listener callListener() {
        return new Call.Listener() {
            /*
             * This callback is emitted once before the Call.Listener.onConnected() callback when
             * the callee is being alerted of a Call. The behavior of this callback is determined by
             * the answerOnBridge flag provided in the Dial verb of your TwiML application
             * associated with this client. If the answerOnBridge flag is false, which is the
             * default, the Call.Listener.onConnected() callback will be emitted immediately after
             * Call.Listener.onRinging(). If the answerOnBridge flag is true, this will cause the
             * call to emit the onConnected callback only after the call is answered.
             * See answeronbridge for more details on how to use it with the Dial TwiML verb. If the
             * twiML response contains a Say verb, then the call will emit the
             * Call.Listener.onConnected callback immediately after Call.Listener.onRinging() is
             * raised, irrespective of the value of answerOnBridge being set to true or false
             */
            @Override
            public void onRinging(@NonNull Call call) {
                Log.d(TAG, "Ringing");
                /*
                 * When [answerOnBridge](https://www.twilio.com/docs/voice/twiml/dial#answeronbridge)
                 * is enabled in the <Dial> TwiML verb, the caller will not hear the ringback while
                 * the call is ringing and awaiting to be accepted on the callee's side. The application
                 * can use the `SoundPoolManager` to play custom audio files between the
                 * `Call.Listener.onRinging()` and the `Call.Listener.onConnected()` callbacks.
                 */
                if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(ContactListActivity.this).playRinging();
                }
            }

            @Override
            public void onConnectFailure(@NonNull Call call, @NonNull CallException error) {
                setAudioFocus(false);
                if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(ContactListActivity.this).stopRinging();
                }
                Log.d(TAG, "Connect failure");
                String message = String.format(
                        Locale.US,
                        "Call Error: %d, %s",
                        error.getErrorCode(),
                        error.getMessage());
                Log.e(TAG, message);
                Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
                resetUI();
            }

            @Override
            public void onConnected(@NonNull Call call) {
                setAudioFocus(true);
                if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(ContactListActivity.this).stopRinging();
                }
                Log.d(TAG, "Connected");
                activeCall = call;
            }

            @Override
            public void onReconnecting(@NonNull Call call, @NonNull CallException callException) {
                Log.d(TAG, "onReconnecting");
            }

            @Override
            public void onReconnected(@NonNull Call call) {
                Log.d(TAG, "onReconnected");
            }

            @Override
            public void onDisconnected(@NonNull Call call, CallException error) {
                setAudioFocus(false);
                if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(ContactListActivity.this).stopRinging();
                }
                Log.d(TAG, "Disconnected");
                if (error != null) {
                    String message = String.format(
                            Locale.US,
                            "Call Error: %d, %s",
                            error.getErrorCode(),
                            error.getMessage());
                    Log.e(TAG, message);
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
                }
                resetUI();
            }
        };
    }

    /*
     * The UI state when there is an active call
     */
    private void setCallUI() {
        Constants.sendCallStartBroadcast(getApplicationContext());
//        callActionFab.hide();
//        hangupActionFab.show();
//        holdActionFab.show();
//        muteActionFab.show();
//        chronometer.setVisibility(View.VISIBLE);
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.start();
    }

    /*
     * Reset UI elements
     */
    private void resetUI() {
//        callActionFab.show();
//        muteActionFab.setImageDrawable(ContextCompat.getDrawable(ContactListActivity.this, R.drawable.ic_mic_white_24dp));
//        holdActionFab.hide();
//        holdActionFab.setBackgroundTintList(ColorStateList
//                .valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
//        muteActionFab.hide();
//        hangupActionFab.hide();
//        chronometer.setVisibility(View.INVISIBLE);
//        chronometer.stop();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onDestroy() {
        SoundPoolManager.getInstance(this).release();
        super.onDestroy();
    }


    private void handleIncomingCallIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            activeCallInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE);
            activeCallNotificationId = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0);

            switch (action) {
                case Constants.ACTION_INCOMING_CALL:
                    handleIncomingCall();
                    break;
                case Constants.ACTION_INCOMING_CALL_NOTIFICATION:
                    showIncomingCallDialog();
                    break;
                case Constants.ACTION_CANCEL_CALL:
                    handleCancel();
                    break;
                case Constants.ACTION_FCM_TOKEN:
                    GetAccessToken();
                    break;
                case Constants.ACTION_ACCEPT:
                    answer();
                    break;
                default:
                    break;
            }
        }
    }

    private void handleIncomingCall() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showIncomingCallDialog();
        } else {
            if (isAppVisible()) {
                showIncomingCallDialog();
            }
        }
    }

    private void handleCancel() {
        if (alertDialog != null && alertDialog.isShowing()) {
            SoundPoolManager.getInstance(this).stopRinging();
            alertDialog.cancel();
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.ACTION_INCOMING_CALL);
            intentFilter.addAction(Constants.ACTION_CANCEL_CALL);
            intentFilter.addAction(Constants.ACTION_FCM_TOKEN);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
            isReceiverRegistered = false;
        }
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && (action.equals(Constants.ACTION_INCOMING_CALL) || action.equals(Constants.ACTION_CANCEL_CALL))) {
                /*
                 * Handle the incoming or cancelled call invite
                 */
                handleIncomingCallIntent(intent);
            }
        }
    }

    private DialogInterface.OnClickListener answerCallClickListener() {
        return (dialog, which) -> {
            Log.d(TAG, "Clicked accept");
            Intent acceptIntent = new Intent(getApplicationContext(), IncomingCallNotificationService.class);
            acceptIntent.setAction(Constants.ACTION_ACCEPT);
            acceptIntent.putExtra(Constants.INCOMING_CALL_INVITE, activeCallInvite);
            acceptIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, activeCallNotificationId);
            Log.d(TAG, "Clicked accept startService");
            startService(acceptIntent);
        };
    }

    private void makePhoneCall() {
        if(!sessionManager.isCallImeshStart()) {
            params.put("to", "+817021908616");
            ConnectOptions connectOptions = new ConnectOptions.Builder(mAccessToken)
                    .params(params)
                    .build();
            activeCall = Voice.connect(ContactListActivity.this, connectOptions, callListener);
            setCallUI();
            sessionManager.setCallStart(true);
        }
    }

    private void makeEndCall(){
        if(sessionManager.isCallStart()){
            SoundPoolManager.getInstance(ContactListActivity.this).playDisconnect();
            resetUI();
            disconnect();
            Constants.sendCallEndBroadcast(getApplicationContext());
            makeCall("END CALL");
        }
    }
    private DialogInterface.OnClickListener callClickListener() {
        return (dialog, which) -> {
            // Place a call
            EditText contact = ((AlertDialog) dialog).findViewById(R.id.contact);
            params.put("to", "+817021908616");
            ConnectOptions connectOptions = new ConnectOptions.Builder(mAccessToken)
                    .params(params)
                    .build();
            activeCall = Voice.connect(ContactListActivity.this, connectOptions, callListener);
            setCallUI();
            alertDialog.dismiss();
        };
    }

    private DialogInterface.OnClickListener cancelCallClickListener() {
        return (dialogInterface, i) -> {
            SoundPoolManager.getInstance(ContactListActivity.this).stopRinging();
            if (activeCallInvite != null) {
                Intent intent = new Intent(ContactListActivity.this, IncomingCallNotificationService.class);
                intent.setAction(Constants.ACTION_REJECT);
                intent.putExtra(Constants.INCOMING_CALL_INVITE, activeCallInvite);
                startService(intent);
            }
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        };
    }

    public static AlertDialog createIncomingCallDialog(
            Context context,
            CallInvite callInvite,
            DialogInterface.OnClickListener answerCallClickListener,
            DialogInterface.OnClickListener cancelClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);
        alertDialogBuilder.setTitle("Incoming Call");
        alertDialogBuilder.setPositiveButton("Accept", answerCallClickListener);
        alertDialogBuilder.setNegativeButton("Reject", cancelClickListener);
        alertDialogBuilder.setMessage(callInvite.getFrom() + " is calling.");
        return alertDialogBuilder.create();
    }

    /*
     * Register your FCM token with Twilio to receive incoming call invites
     */
    private void registerForCallInvites() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String fcmToken = instanceIdResult.getToken();
            Log.i(TAG, "Registering with FCM");
            Voice.register(mAccessToken, Voice.RegistrationChannel.FCM, fcmToken, registrationListener);
        });
    }

    private View.OnClickListener hangupActionFabClickListener() {
        return v -> {
            SoundPoolManager.getInstance(ContactListActivity.this).playDisconnect();
            resetUI();
            disconnect();

        };
    }

    private View.OnClickListener holdActionFabClickListener() {
        return v -> hold();
    }

    private View.OnClickListener muteActionFabClickListener() {
        return v -> mute();
    }

    /*
     * Accept an incoming Call
     */
    private void answer() {
        SoundPoolManager.getInstance(this).stopRinging();
        activeCallInvite.accept(this, callListener);
        notificationManager.cancel(activeCallNotificationId);
        setCallUI();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    /*
     * Disconnect from Call
     */
    private void disconnect() {
        if (activeCall != null) {
            activeCall.disconnect();
            activeCall = null;
        }
    }

    private void hold() {
        if (activeCall != null) {
            boolean hold = !activeCall.isOnHold();
            activeCall.hold(hold);
            applyFabState(holdActionFab, hold);
        }
    }

    private void mute() {
        if (activeCall != null) {
            boolean mute = !activeCall.isMuted();
            activeCall.mute(mute);
            applyFabState(muteActionFab, mute);
        }
    }

    private void applyFabState(FloatingActionButton button, boolean enabled) {
        // Set fab as pressed when call is on hold
        ColorStateList colorStateList = enabled ?
                ColorStateList.valueOf(ContextCompat.getColor(this,
                        R.color.colorPrimaryDark)) :
                ColorStateList.valueOf(ContextCompat.getColor(this,
                        R.color.colorAccent));
        button.setBackgroundTintList(colorStateList);
    }

    private void setAudioFocus(boolean setFocus) {
        if (audioManager != null) {
            if (setFocus) {
                savedAudioMode = audioManager.getMode();
                // Request audio focus before making any device switch.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();
                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(i -> {
                            })
                            .build();
                    audioManager.requestAudioFocus(focusRequest);
                } else {
                    audioManager.requestAudioFocus(
                            focusChange -> {
                            },
                            AudioManager.STREAM_VOICE_CALL,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                }
                /*
                 * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
                 * required to be in this mode when playout and/or recording starts for
                 * best possible VoIP performance. Some devices have difficulties with speaker mode
                 * if this is not set.
                 */
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(savedAudioMode);
                audioManager.abandonAudioFocus(null);
            }
        }
    }

    private boolean checkPermissionForMicrophone() {
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(coordinatorLayout,
                    "Microphone permissions needed. Please allow in your application settings.",
                    Snackbar.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if microphone permissions is granted
         */
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(coordinatorLayout,
                        "Microphone permissions needed. Please allow in your application settings.",
                        Snackbar.LENGTH_LONG).show();
            } else {
                GetAccessToken();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.speaker_menu_item) {
            if (audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(false);
                item.setIcon(R.drawable.ic_phonelink_ring_white_24dp);
            } else {
                audioManager.setSpeakerphoneOn(true);
                item.setIcon(R.drawable.ic_volume_up_white_24dp);
            }
        }
        return true;
    }


    private void showIncomingCallDialog() {
        SoundPoolManager.getInstance(this).playRinging();
        if (activeCallInvite != null) {
            alertDialog = createIncomingCallDialog(ContactListActivity.this,
                    activeCallInvite,
                    answerCallClickListener(),
                    cancelCallClickListener());
            alertDialog.show();
        }
    }

    private boolean isAppVisible() {
        return ProcessLifecycleOwner
                .get()
                .getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.STARTED);
    }

    private void GetAccessToken() {
        if (NetworkManager.isConnectedToNet(this)) {
            try {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Getting Access token, Please wait...");
                dialog.setCancelable(false);
                dialog.show();
                queue = Volley.newRequestQueue(this);

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, GET_ACCESS_TOKEN_URL + sessionManager.getDeviceId(), null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.e(TAG, "onResponse: " + response.toString());
                                    mAccessToken = response.toString();
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    registerForCallInvites();
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
                                mAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJpc3MiOiJTS2RiZmZiZjU3NjBmODNjZTQ4MmM2ZmUyMzlkZDQxYzBmIiwiZXhwIjoxNTg3NzE2MzczLCJqdGkiOiJTS2RiZmZiZjU3NjBmODNjZTQ4MmM2ZmUyMzlkZDQxYzBmLTE1ODc3MTI3NzMiLCJzdWIiOiJBQ2E3OGFjMjRmNjM4N2QxZjIwOGY2OWYyOGI2ZDg0YzgyIiwiZ3JhbnRzIjp7ImlkZW50aXR5IjoiSU1FSToxMjU5NDU2ODk1NDU0OTciLCJ2b2ljZSI6eyJpbmNvbWluZyI6eyJhbGxvdyI6dHJ1ZX0sIm91dGdvaW5nIjp7ImFwcGxpY2F0aW9uX3NpZCI6IkFQNDM4ODRkODBhNDVmNGVkODA2YjI3YTlhNWJhMWE4OWMifX19fQ.dXp2tbnLXqTH5yvhTiwFKWIE0QkL8niTrwY-RDH-eqY";
                                registerForCallInvites();
                                Log.e(TAG, "onErrorResponse: to get access token ");
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
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
            setMenu();
        }

        // Getting list of contact from the DATABASE
        mContactListItemModel = dbManager.getContactListItem();
        if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
            mContactListItemAdapter = new ContactListItemAdapter(this, mContactListItemModel, recyclerViewClickListener);
            mRecyclerView.setAdapter(mContactListItemAdapter);
            mContactListItemAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
            mButtonHistory.setVisibility(View.VISIBLE);
            mTextNoData.setVisibility(View.GONE);
            mButtonBack.setVisibility(View.GONE);
        } else {
            // No contact available in DATABASE
            mRecyclerView.setVisibility(View.GONE);
            mTextNoData.setVisibility(View.VISIBLE);
            mButtonBack.setVisibility(View.GONE);
            mButtonHistory.setVisibility(View.VISIBLE);
        }

        mButtonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall("History");
                Intent i = new Intent(ContactListActivity.this, HistoryListActivity.class);
                startActivity(i);
            }
        });

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mImageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMenu();
                showMenu();
            }
        });

        mTextAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 0;
                callEvent();
            }
        });

        mTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 1;
                callEvent();
            }
        });

        mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 2;
                callEvent();
            }
        });

        mTextMaintanance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 3;
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

        mButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall("CALL");
            }
        });
    }

    public void makeCall(String str) {
        if (str.equalsIgnoreCase("CALL")) {
            if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                Date date = new Date();
                Log.e(TAG, "makeCall: " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
                Log.e(TAG, "makeCall: " + DateFormat.getTimeInstance().format(date));
                dbManager.insertHistory(3, mContactListItemModel.get(pos).getOwnerName(), mContactListItemModel.get(pos).getOwnerNumber(), "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
                makePhoneCall();
            } else {
                Toast.makeText(getApplicationContext(), "Add Contact to CALL", Toast.LENGTH_SHORT).show();
                callDailPad(0);
            }
        } else
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    private void keyUpDownn(boolean isUp) {
        if (isUp) {
            if (menuSelection == 0)
                menuSelection = 3;
            else
                menuSelection = menuSelection - 1;
        } else {
            if (menuSelection == 3)
                menuSelection = 0;
            else
                menuSelection = menuSelection + 1;
        }
        setMenu();
    }

    private void setMenu() {
        mTextAdd.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextDelete.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextMaintanance.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (menuSelection == 0) {
            mTextAdd.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else if (menuSelection == 1) {
            mTextEdit.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else if (menuSelection == 2) {
            mTextDelete.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else if (menuSelection == 3) {
            mTextMaintanance.setBackgroundColor(Color.parseColor("#CCCCCC"));
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyUp: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                callDailPad(0);
                return true;
            case KeyEvent.KEYCODE_1:
                callDailPad(1);
                return true;
            case KeyEvent.KEYCODE_2:
                callDailPad(2);
                return true;
            case KeyEvent.KEYCODE_3:
                callDailPad(3);
                return true;
            case KeyEvent.KEYCODE_4:
                callDailPad(4);
                return true;
            case KeyEvent.KEYCODE_5:
                callDailPad(5);
                return true;
            case KeyEvent.KEYCODE_6:
                callDailPad(6);
                return true;
            case KeyEvent.KEYCODE_7:
                callDailPad(7);
                return true;
            case KeyEvent.KEYCODE_8:
                callDailPad(8);
                return true;
            case KeyEvent.KEYCODE_9:
                callDailPad(9);
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
                    if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                        mContactListItemModel.get(pos).setCheck(false);
                        if ((mContactListItemModel.size() - 1) == pos) {
                            pos = 0;
                            mContactListItemAdapter.setPos(0);
                            mContactListItemModel.get(pos).setCheck(true);
                        } else {
                            pos = pos + 1;
                            mContactListItemAdapter.setPos((pos + 1));
                            mContactListItemModel.get(pos).setCheck(true);
                        }
                        mContactListItemAdapter = new ContactListItemAdapter(this, mContactListItemModel, recyclerViewClickListener);
                        mRecyclerView.setAdapter(mContactListItemAdapter);
                        mContactListItemAdapter.notifyDataSetChanged();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        makeCall("Add Contact");
                    }
                } else {
                    if (deleteVisible)
                        keyUpDowndelete();
                    else
                        keyUpDownn(false);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                //Up Key
                if (!menuVisible && !deleteVisible) {
                    if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                        mContactListItemModel.get(pos).setCheck(false);
                        if (pos == 0) {
                            pos = mContactListItemModel.size() - 1;
                            mContactListItemAdapter.setPos(pos);
                            mContactListItemModel.get(pos).setCheck(true);
                        } else {
                            pos = pos - 1;
                            mContactListItemAdapter.setPos((pos));
                            mContactListItemModel.get(pos).setCheck(true);
                        }
                        mContactListItemAdapter = new ContactListItemAdapter(this, mContactListItemModel, recyclerViewClickListener);
                        mRecyclerView.setAdapter(mContactListItemAdapter);
                        mContactListItemAdapter.notifyDataSetChanged();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        makeCall("Add Contact");
                    }
                } else {
                    if (deleteVisible)
                        keyUpDowndelete();
                    else
                        keyUpDownn(true);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
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
            case KeyEvent.KEYCODE_MENU: {
                setMenu();
                showMenu();
            }
            return true;
            case KeyEvent.KEYCODE_BACK:
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
                //BACK
                if (menuVisible) {
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
                makeCall("HOME");
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                makeEndCall();
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void callDailPad(int value) {
        startActivity(new Intent(ContactListActivity.this, VoiceActivity.class).putExtra("num", "" + value).setAction(Constants.OUTGOING_CALL_INVITE));
    }

    private void iJustWantToScroll() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(pos);
            }
        }, 500);
    }

    private void callDeleteMethod() {
        if (deleteSelection == 0) {
            if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                dbManager.updateHistory(mContactListItemModel.get(pos).getOwnerName(), "UNKNOWN");
                dbManager.delete(mContactListItemModel.get(pos).getId());
                Intent i = new Intent(ContactListActivity.this, DeleteSplashActivity.class);
                startActivity(i);
            } else {
                makeCall("Add Contact to delete");
            }
        }
        showDeleteMenu();
        deleteVisible = !deleteVisible;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void callEvent() {
        showMenu();
        if (menuSelection == 0) {
            Intent i = new Intent(ContactListActivity.this, AddContactActivity.class);
            startActivity(i);
        } else if (menuSelection == 1) {
            menuSelection = 0;
            if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                Gson gson = new Gson();
                Log.e(TAG, "callEvent: " + mContactListItemModel.get(pos).getOwnerName());
                String myJson = gson.toJson(mContactListItemModel.get(pos));
                Log.e(TAG, "callEvent: " + myJson);
                Intent i = new Intent(ContactListActivity.this, EditContactActivity.class);
                i.putExtra("user", myJson);
                startActivity(i);
            } else {
                makeCall("Add Contact to edit");
            }
        } else if (menuSelection == 2) {
            menuSelection = 0;
            if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                showDeleteMenu();
                setMenuDelete();
                deleteVisible = !deleteVisible;
            } else {
                makeCall("Add Contact to delete");
            }
        } else if (menuSelection == 3) {
            menuSelection = 0;
            showMenu();
            setMenu();
            Intent i = new Intent(ContactListActivity.this, CancelConfirmActivity.class);
            startActivity(i);
        }
    }
}
