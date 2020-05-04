package jp.mcinc.imesh.type.ipphone.activity;

import android.Manifest;
import android.app.Activity;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import jp.mcinc.imesh.type.ipphone.BuildConfig;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;

import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.contants.Constants;
import jp.mcinc.imesh.type.ipphone.controller.SoundPoolManager;
import jp.mcinc.imesh.type.ipphone.database.DBManager;
import jp.mcinc.imesh.type.ipphone.notification.IncomingCallNotificationService;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;
import jp.mcinc.imesh.type.ipphone.util.NetworkManager;
import jp.mcinc.imesh.type.ipphone.util.Validation;
//import com.twilio.jwt.accesstoken.AccessToken;
//import com.twilio.jwt.accesstoken.VoiceGrant;
//import com.twilio.jwt.accesstoken.AccessToken;
//import com.twilio.jwt.accesstoken.VoiceGrant;
import com.google.gson.Gson;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.ConnectOptions;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static jp.mcinc.imesh.type.ipphone.contants.Constants.ACCESS_TOKEN;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.CALL_SID_KEY;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.CLIEENT_ID;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.GET_ACCESS_TOKEN_URL;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.ID_TOKEN;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.REFRESH_TOKEN;
import static jp.mcinc.imesh.type.ipphone.contants.Constants.REFRESH_TOKEN_URL;


public class VoiceActivity extends AppCompatActivity {

    private static final String TAG = "VoiceActivity";
    /*
     * You must provide the URL to the publicly accessible Twilio access token server route
     *
     * For example: https://myurl.io/accessToken
     *
     * If your token server is written in PHP, TWILIO_ACCESS_TOKEN_SERVER_URL needs .php extension at the end.
     *
     * For example : https://myurl.io/accessToken.php
     * praveen deshmane added environment 123
     *
     */
    private static final String TWILIO_REGISTRATION_URL = "https://toolbox-gnat-5377.twil.io/register-binding";
    private static final String TWILIO_ACCESS_TOKEN_SERVER_URL = "TWILIO_ACCESS_TOKEN_SERVER_URL";

    private static final int MIC_PERMISSION_REQUEST_CODE = 1;

    private AudioManager audioManager;
    private int savedAudioMode = AudioManager.MODE_INVALID;

    private boolean isReceiverRegistered = false;
    private VoiceBroadcastReceiver voiceBroadcastReceiver;

    HashMap<String, String> params = new HashMap<>();

    private Chronometer chronometer;

    private NotificationManager notificationManager;
    private AlertDialog alertDialog;
    private CallInvite activeCallInvite;
    private Call activeCall;
    private int activeCallNotificationId;

    private SessionManager sessionManager;

    RegistrationListener registrationListener = registrationListener();
    Call.Listener callListener = callListener();
    //    private String mAccessToken;
    private ProgressDialog dialog;
    private RequestQueue queue;

    private RelativeLayout mRelativelayout;
    private EditText mEditNumber;
    private Button mButtonCall, mButtonAccept, mButtonReject;
    private DBManager dbManager;

    private TextView mTextName, mTextNumber, mTextStatus, mTextIncomingNumber, mTextIncomingTitle;
    private LinearLayout mLinearStatus, mLinearDetails, mLinearOutgoing, mLinearIncoming,
            mLinearMenu, mLinearMute, mLinearSpeaker;
    private boolean makePhoneCallNumber = false;
    private CallInvite callInvite;
    private int focus = 0, menuSelection = 1;
    private boolean muteVisible = false, menuVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        sessionManager = new SessionManager(this);
        CALL_SID_KEY = sessionManager.getNumberSid();
        dbManager = new DBManager(this);
        dbManager.open();
        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRelativelayout = findViewById(R.id.relative_dail);
        chronometer = findViewById(R.id.chronometer);
        mButtonCall = findViewById(R.id.button_call);
        mButtonAccept = findViewById(R.id.button_accept);
        mButtonReject = findViewById(R.id.button_reject);
        mEditNumber = findViewById(R.id.edit_number);

        mTextName = findViewById(R.id.text_name);
        mTextNumber = findViewById(R.id.text_number);
        mTextStatus = findViewById(R.id.text_status);

        mTextIncomingNumber = findViewById(R.id.text_incoming_number);
        mTextIncomingTitle = findViewById(R.id.text_incoming_title);

        mLinearMenu = findViewById(R.id.linear_menu);
        mLinearMute = findViewById(R.id.linear_mute);
        mLinearSpeaker = findViewById(R.id.linear_speaker);
        mLinearStatus = findViewById(R.id.linear_status);
        mLinearDetails = findViewById(R.id.linear_details);
        mLinearOutgoing = findViewById(R.id.linear_dailing);
        mLinearIncoming = findViewById(R.id.linear_incoming);
        mLinearStatus.setVisibility(View.VISIBLE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi = metrics.densityDpi;
        dpi = dpi - 8;
        dpi = dpi / 2;
        mButtonAccept.setPadding(dpi, 0, 0, 0);
        mButtonReject.setPadding(dpi, 0, 0, 0);

        mLinearOutgoing.setVisibility(View.GONE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /*
         * Setup the broadcast receiver to be notified of FCM Token updates
         * or incoming call invite in this Activity.
         */
//        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
//        registerReceiver();

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
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Snackbar.make(mRelativelayout, "Successfully registered FCM", Snackbar.LENGTH_LONG).show();
                if (makePhoneCallNumber)
                    makeCall("CALL");

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
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Snackbar.make(mRelativelayout, message, Snackbar.LENGTH_LONG).show();
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
                mTextStatus.setText("Dailing");
                mTextName.setText("UNKNOWN");
                muteVisible = false;
                mTextNumber.setText(mEditNumber.getText().toString());
                Snackbar.make(mRelativelayout, "DAILING", Snackbar.LENGTH_LONG).show();
                /*
                 * When [answerOnBridge](https://www.twilio.com/docs/voice/twiml/dial#answeronbridge)
                 * is enabled in the <Dial> TwiML verb, the caller will not hear the ringback while
                 * the call is ringing and awaiting to be accepted on the callee's side. The application
                 * can use the `SoundPoolManager` to play custom audio files between the
                 * `Call.Listener.onRinging()` and the `Call.Listener.onConnected()` callbacks.
                 */
                if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(VoiceActivity.this).playRinging();
                }
            }

            @Override
            public void onConnectFailure(@NonNull Call call, @NonNull CallException error) {
                Constants.sendCallEndBroadcast(getApplicationContext());
                setAudioFocus(false);
                Snackbar.make(mRelativelayout, "CONNECTION FAILED", Snackbar.LENGTH_LONG).show();
                mLinearOutgoing.setVisibility(View.GONE);
                mLinearIncoming.setVisibility(View.GONE);
                mRelativelayout.setVisibility(View.VISIBLE);
                muteVisible = false;
                if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(VoiceActivity.this).stopRinging();
                }
                Log.d(TAG, "Connect failure");
                String message = String.format(
                        Locale.US,
                        "Call Error: %d, %s",
                        error.getErrorCode(),
                        error.getMessage());
                Log.e(TAG, message);
                Snackbar.make(mRelativelayout, message, Snackbar.LENGTH_LONG).show();
                resetUI();
            }

            @Override
            public void onConnected(@NonNull Call call) {
                Constants.sendCallStartBroadcast(getApplicationContext());
                Snackbar.make(mRelativelayout, "CONNECTED", Snackbar.LENGTH_LONG).show();
                mTextStatus.setText("Connected");
                mTextName.setText("UNKNOWN");
                mLinearStatus.setVisibility(View.GONE);
                mLinearIncoming.setVisibility(View.GONE);
                mRelativelayout.setVisibility(View.GONE);
                muteVisible = true;
                mTextNumber.setText(mEditNumber.getText().toString());
                setAudioFocus(true);
                if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(VoiceActivity.this).stopRinging();
                }
                Log.d(TAG, "Connected");
                activeCall = call;
            }

            @Override
            public void onReconnecting(@NonNull Call call, @NonNull CallException callException) {
                Snackbar.make(mRelativelayout, "RECONNECTING", Snackbar.LENGTH_LONG).show();
                Log.d(TAG, "onReconnecting");
                mTextStatus.setText("Reconnecting");
                mTextName.setText("UNKNOWN");
                muteVisible = false;
                //  mTextNumber.setText("+817021908616");
                mTextNumber.setText(mEditNumber.getText().toString());
            }

            @Override
            public void onReconnected(@NonNull Call call) {
                Constants.sendCallStartBroadcast(getApplicationContext());
                Snackbar.make(mRelativelayout, "RECONNECTED", Snackbar.LENGTH_LONG).show();
                Log.d(TAG, "onReconnected");
                mTextStatus.setText("Reconnected");
                mTextName.setText("UNKNOWN");
                muteVisible = true;
                // mTextNumber.setText("+817021908616");
                mTextNumber.setText(mEditNumber.getText().toString());
            }

            @Override
            public void onDisconnected(@NonNull Call call, CallException error) {
                Constants.sendCallEndBroadcast(getApplicationContext());
                Snackbar.make(mRelativelayout, "DISCONNECTED", Snackbar.LENGTH_LONG).show();
                mTextStatus.setText("Disconnected");
                mTextName.setText("UNKNOWN");
                mTextNumber.setText(mEditNumber.getText().toString());
                mLinearStatus.setVisibility(View.VISIBLE);
                mLinearIncoming.setVisibility(View.GONE);
                mRelativelayout.setVisibility(View.VISIBLE);
                muteVisible = false;
                setAudioFocus(false);
                if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(VoiceActivity.this).stopRinging();
                }
                Log.d(TAG, "Disconnected");
                if (error != null) {
                    String message = String.format(
                            Locale.US,
                            "Call Error: %d, %s",
                            error.getErrorCode(),
                            error.getMessage());
                    Log.e(TAG, message);
                    Snackbar.make(mRelativelayout, message, Snackbar.LENGTH_LONG).show();
                }
                resetUI();
            }
        };
    }

    private void makePhoneCall() {
        if (!sessionManager.isCallImeshStart()) {
            params.put("to", mEditNumber.getText().toString());
            ConnectOptions connectOptions = new ConnectOptions.Builder(sessionManager.getAccessToken())
                    .params(params)
                    .build();
            activeCall = Voice.connect(VoiceActivity.this, connectOptions, callListener);
            setCallUI();
            sessionManager.setCallStart(true);
        } else {
            showToast("Cannot make a Call");
        }
    }

    private void makeEndCall() {
        if (sessionManager.isCallStart()) {
            SoundPoolManager.getInstance(VoiceActivity.this).playDisconnect();
            resetUI();
            disconnect();
            showToast("END CALL");
        }
    }

    /*
     * The UI state when there is an active call
     */
    private void setCallUI() {
        mLinearOutgoing.setVisibility(View.VISIBLE);
        mRelativelayout.setVisibility(View.GONE);
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    /*
     * Reset UI elements
     */
    private void resetUI() {
        mLinearOutgoing.setVisibility(View.GONE);
        mLinearStatus.setVisibility(View.VISIBLE);
        chronometer.setVisibility(View.INVISIBLE);
        chronometer.stop();
        mRelativelayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEditNumber.post(new Runnable() {
            @Override
            public void run() {
                mEditNumber.setSelection(mEditNumber.getText().length());
                mEditNumber.setInputType(InputType.TYPE_CLASS_PHONE);
                mEditNumber.requestFocus();
            }
        });

        mButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValidateAddContactToList();
            }
        });

        mButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focus = 1;
                acceptIncomingCall();
            }
        });
        mButtonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focus = 2;
                rejectIncomingCall();
            }
        });
        mLinearMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 0;
                callEvent();
            }
        });
        mLinearSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 1;
                callEvent();
            }
        });

        /*
         * Ensure the microphone permission is enabled
         */
        if (!checkPermissionForMicrophone()) {
            requestPermissionForMicrophone();
        }

        if(sessionManager.isCallImeshStart()){
            rejectIncomingCall();
        }
    }

    private void acceptIncomingCall() {
        Intent acceptIntent = new Intent(getApplicationContext(), IncomingCallNotificationService.class);
        acceptIntent.setAction(Constants.ACTION_ACCEPT);
        acceptIntent.putExtra(Constants.INCOMING_CALL_INVITE, activeCallInvite);
        acceptIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, activeCallNotificationId);
        Log.d(TAG, "Clicked accept startService");
        startService(acceptIntent);
    }

    private void rejectIncomingCall() {
        SoundPoolManager.getInstance(VoiceActivity.this).stopRinging();
        if (activeCallInvite != null) {
            Date date = new Date();
            dbManager.insertHistory(1, "UNKNOWN", activeCallInvite.getFrom(), "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
            mLinearOutgoing.setVisibility(View.GONE);
            mLinearIncoming.setVisibility(View.GONE);
            mRelativelayout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(VoiceActivity.this, IncomingCallNotificationService.class);
            intent.setAction(Constants.ACTION_REJECT);
            intent.putExtra(Constants.INCOMING_CALL_INVITE, activeCallInvite);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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
//                    retrieveAccessToken();
                    break;
                case Constants.ACTION_ACCEPT:
                    answer();
                    break;
                default:
                    break;
            }

            if (intent.hasExtra("num")) {
                mEditNumber.setText("" + intent.getExtras().getString("num"));
                if (intent.hasExtra("call")) {
                    makePhoneCallNumber = intent.getExtras().getBoolean("call");
                    if (makePhoneCallNumber) {
                        makeCall("CALL");
                    }
                }
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

//    private void registerReceiver() {
//        if (!isReceiverRegistered) {
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(Constants.ACTION_INCOMING_CALL);
//            intentFilter.addAction(Constants.ACTION_CANCEL_CALL);
//            intentFilter.addAction(Constants.ACTION_FCM_TOKEN);
//            LocalBroadcastManager.getInstance(this).registerReceiver(
//                    voiceBroadcastReceiver, intentFilter);
//            isReceiverRegistered = true;
//        }
//    }

//    private void unregisterReceiver() {
//        if (isReceiverRegistered) {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
//            isReceiverRegistered = false;
//        }
//    }

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

//    private DialogInterface.OnClickListener answerCallClickListener() {
//        return (dialog, which) -> {
//            Log.d(TAG, "Clicked accept");
//            acceptIncomingCall();
//        };
//    }

//    private DialogInterface.OnClickListener callClickListener() {
//        return (dialog, which) -> {
//            // Place a call
//            EditText contact = ((AlertDialog) dialog).findViewById(R.id.contact);
//    //        params.put("to", "+817021908616");
//            params.put("to",mEditNumber.getText().toString());
//            ConnectOptions connectOptions = new ConnectOptions.Builder(mAccessToken)
//                    .params(params)
//                    .build();
//            activeCall = Voice.connect(VoiceActivity.this, connectOptions, callListener);
//            setCallUI();
//            alertDialog.dismiss();
//        };
//    }

//    private DialogInterface.OnClickListener cancelCallClickListener() {
//        return (dialogInterface, i) -> {
//            rejectIncomingCall();
//            if (alertDialog != null && alertDialog.isShowing()) {
//                alertDialog.dismiss();
//            }
//        };
//    }

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
            Voice.register(sessionManager.getAccessToken(), Voice.RegistrationChannel.FCM, fcmToken, registrationListener);
        });
    }

    /*
     * Accept an incoming Call
     */
    private void answer() {
        SoundPoolManager.getInstance(this).stopRinging();
        activeCallInvite.accept(this, callListener);
        Date date = new Date();
        dbManager.insertHistory(2, "UNKNOWN", activeCallInvite.getFrom(), "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
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
        }
    }

    private void mute() {
        if (activeCall != null) {
            boolean mute = !activeCall.isMuted();
            activeCall.mute(mute);
        }
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
            Snackbar.make(mRelativelayout,
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
                Snackbar.make(mRelativelayout,
                        "Microphone permissions needed. Please allow in your application settings.",
                        Snackbar.LENGTH_LONG).show();
            } else {
                //retrieveAccessToken();
            }
        }
    }

    private static AlertDialog createCallDialog(final DialogInterface.OnClickListener callClickListener,
                                                final DialogInterface.OnClickListener cancelClickListener,
                                                final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);
        alertDialogBuilder.setTitle("Call");
        alertDialogBuilder.setPositiveButton("Call", callClickListener);
        alertDialogBuilder.setNegativeButton("Cancel", cancelClickListener);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater li = LayoutInflater.from(activity);
        View dialogView = li.inflate(
                R.layout.dialog_call,
                activity.findViewById(android.R.id.content),
                false);
        final EditText contact = dialogView.findViewById(R.id.contact);
        contact.setHint(R.string.callee);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();

    }

    private void showIncomingCallDialog() {
        SoundPoolManager.getInstance(this).playRinging();
        if (activeCallInvite != null) {
            focus = 1;
            mButtonAccept.requestFocus();
            mLinearOutgoing.setVisibility(View.GONE);
            mRelativelayout.setVisibility(View.GONE);
            mLinearIncoming.setVisibility(View.VISIBLE);
            mTextIncomingTitle.setText("Incoming Call");
            mTextIncomingNumber.setText(activeCallInvite.getFrom() + " is calling.");
        }
    }

    private boolean isAppVisible() {
        return ProcessLifecycleOwner
                .get()
                .getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.STARTED);
    }

//    private void refershToken() {
//        try {
//            dialog = new ProgressDialog(this);
//            dialog.setMessage("Getting Access token, Please wait...");
//            dialog.setCancelable(false);
//            dialog.show();
//            queue = Volley.newRequestQueue(this);
//            //                HashMap<String, String> jsonObject = new HashMap<String, String>();
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("clientId", CLIEENT_ID);
//            jsonObject.put("refreshToken", sessionManager.getRefreshToken());
//
//            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, REFRESH_TOKEN_URL, jsonObject,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                Log.e(TAG, "onResponse: " + response.toString());
//                                ACCESS_TOKEN = response.getString("accessToken");
//                                ID_TOKEN = response.getString("idToken");
//                                sessionManager.setAccessToken(ACCESS_TOKEN);
//                                sessionManager.setIdToken(ID_TOKEN);
//                                if (dialog.isShowing()) {
//                                    dialog.dismiss();
//                                }
//                                retrieveAccessToken();
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
//                            Snackbar.make(mRelativelayout, "Purchase Failed", Snackbar.LENGTH_SHORT).show();
//                            Intent i = new Intent(VoiceActivity.this, SplashScreen.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(i);
//                            finish();
//                        }
//                    }) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    HashMap<String, String> params = new HashMap<String, String>();
//                    params.put("Authorization", sessionManager.getRefreshToken());
//                    return params;
//                }
//            };
//            queue.add(getRequest);
//        } catch (Exception e) {
//            Log.e(TAG, "refreshToken: " + e.getMessage());
//        }
//    }

    /*
     * Get an access token from your Twilio access token server
     */
//    private void retrieveAccessToken() {
//        if (NetworkManager.isConnectedToNet(this)) {
//            try {
//                queue = Volley.newRequestQueue(this);
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_ACCESS_TOKEN_URL + sessionManager.getDeviceId(), new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            Log.e(TAG, "onResponse: Access token" + response.toString());
//                            mAccessToken = response.toString();
//                            registerForCallInvites();
//                        } catch (Exception e) {
//                            if (dialog.isShowing()) {
//                                dialog.dismiss();
//                            }
//                            Log.e(TAG, "onResponse: " + e.getMessage());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//                        mAccessToken = sessionManager.getAccessToken();
//                        Log.e(TAG, "Failed to get Access token: " + mAccessToken);
//                    }
//                }) {
//                    @Override
//                    public Map<String, String> getHeaders() {
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        params.put("Authorization", sessionManager.getIdToken());
//                        params.put("Content-Type", "application/json");
//                        params.put("accept", "text/html");
//                        return params;
//                    }
//                };
//                queue.add(stringRequest);
//            } catch (Exception e) {
//                Log.e(TAG, "retrieveAccessToken: " + e.getMessage());
//            }
//        } else {
//            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void isValidateAddContactToList() {
        if (Validation.validateString(mEditNumber.getText().toString()) && Validation.isMobileNumberValid(mEditNumber.getText().toString())) {
            mLinearOutgoing.setVisibility(View.VISIBLE);
            makeCall("CALL");
        } else {
            Toast.makeText(getApplicationContext(), "Enter mobile number properly", Toast.LENGTH_SHORT).show();
        }
    }

    public void makeCall(String number) {
        if (!makePhoneCallNumber) {
            Date date = new Date();
            // dbManager.insertHistory(3, "UNKNOWN", "+817021908616", "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
            dbManager.insertHistory(3, "UNKNOWN", mEditNumber.getText().toString(), "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
        }
        makePhoneCall();
    }

    private void showToast(String message) {
        Snackbar.make(mRelativelayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showMenu() {
        if (menuVisible) {
            mLinearMenu.setVisibility(View.GONE);
        } else {
            mLinearMenu.setVisibility(View.VISIBLE);
        }
        menuVisible = !menuVisible;
    }

    private void setMenu() {
        if (!muteVisible) {
            mLinearMute.setVisibility(View.GONE);
        } else {
            mLinearMute.setVisibility(View.VISIBLE);
        }
        mLinearMute.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mLinearSpeaker.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (menuSelection == 0) {
            mLinearMute.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else if (menuSelection == 1) {
            mLinearSpeaker.setBackgroundColor(Color.parseColor("#CCCCCC"));
        }
    }

    private void callEvent() {
        showMenu();
        if (menuSelection == 0) {
            mute();
        } else if (menuSelection == 1) {
            if (audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(false);
            } else {
                audioManager.setSpeakerphoneOn(true);
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU: {
                setMenu();
                showMenu();
            }
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (menuVisible) {
                    callEvent();
                } else {
                    if (focus == 1) {
                        acceptIncomingCall();
                    } else if (focus == 2) {
                        rejectIncomingCall();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (menuVisible) {
                    if (muteVisible && menuSelection == 0) {
                        menuSelection = 1;
                    } else if (muteVisible && menuSelection == 1) {
                        menuSelection = 0;
                    }
                    setMenu();
                } else {
                    if (focus == 1) {
                        focus = 2;
                        mButtonReject.requestFocus();
                    } else if (focus == 2) {
                        focus = 1;
                        mButtonAccept.requestFocus();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (menuVisible) {
                    callEvent();
                } else {
                    if (focus == 1) {
                        focus = 2;
                        mButtonReject.requestFocus();
                    } else if (focus == 2) {
                        focus = 1;
                        mButtonAccept.requestFocus();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_CALL:
                //PICK CALL
                isValidateAddContactToList();
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                makeEndCall();
                return true;
            case KeyEvent.KEYCODE_HOME:
                //END CALL
                return true;
            case KeyEvent.KEYCODE_NUMPAD_0:
                callDailPad(0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_1:
                callDailPad(1);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
                callDailPad(2);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
                callDailPad(3);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_4:
                callDailPad(4);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_5:
                callDailPad(5);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
                callDailPad(6);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
                callDailPad(7);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
                callDailPad(8);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
                callDailPad(9);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void callDailPad(int value) {
        if (mEditNumber != null) {
            mEditNumber.setText("" + mEditNumber.getText().toString() + value);
        }
        mEditNumber.post(new Runnable() {
            @Override
            public void run() {
                mEditNumber.setSelection(mEditNumber.getText().length());
                mEditNumber.setInputType(InputType.TYPE_CLASS_PHONE);
                mEditNumber.requestFocus();
            }
        });
    }
}
