package jp.mcinc.imesh.type.ipphone.fcm;

import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import jp.mcinc.imesh.type.ipphone.contants.Constants;
import jp.mcinc.imesh.type.ipphone.model.CreateBindingResponse;
import jp.mcinc.imesh.type.ipphone.notification.IncomingCallNotificationService;
import retrofit2.Call;
import retrofit2.Response;

import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.CancelledCallInvite;
import com.twilio.voice.MessageListener;
import com.twilio.voice.Voice;

import java.io.IOException;

import static android.provider.ContactsContract.CommonDataKinds.Identity.IDENTITY;

public class VoiceFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "VoiceFCMService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Received onMessageReceived()");
        Log.d(TAG, "Bundle data: " + remoteMessage.getData());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            boolean valid = Voice.handleMessage(this, remoteMessage.getData(), new MessageListener() {
                @Override
                public void onCallInvite(@NonNull CallInvite callInvite) {
                    final int notificationId = (int) System.currentTimeMillis();
                    handleInvite(callInvite, notificationId);
                }

                @Override
                public void onCancelledCallInvite(@NonNull CancelledCallInvite cancelledCallInvite, @Nullable CallException callException) {
                    handleCanceledCallInvite(cancelledCallInvite);
                }
            });

            if (!valid) {
                Log.e(TAG, "The message was not a valid Twilio Voice SDK payload: " +
                        remoteMessage.getData());
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Intent intent = new Intent(Constants.ACTION_FCM_TOKEN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        String identity =  intent.getStringExtra(IDENTITY);
//        String storedIdentity = sharedPreferences.getString(IDENTITY, null);
//        if (newIdentity == null) {
//            // If no identity was provided to us then we use the identity stored in shared preferences.
//            // This can occur when the registration token changes.
//            identity = storedIdentity;
//        } else {
//            // Otherwise we save the new identity in the shared preferences for future use.
//            sharedPreferences.edit().putString(IDENTITY, binding.identity).commit();
//        }
//        sendRegistrationToServer(identity, token);

    }
//    private CreateBindingResponse sendRegistrationToServer(String identity, String token) throws IOException {
//        String endpoint = sharedPreferences.getString(ENDPOINT + newIdentity, null);
//        ScriptGroup.Binding binding = new ScriptGroup.Binding(identity, endpoint, token, "fcm");
//        Call<CreateBindingResponse> call = bindingResource.createBinding(binding);
//        Response<CreateBindingResponse> response = call.execute();
//        sharedPreferences.edit().putString(ENDPOINT + binding.identity, response.body().endpoint).commit();
//    }

    private void handleInvite(CallInvite callInvite, int notificationId) {
        Intent intent = new Intent(this, IncomingCallNotificationService.class);
        intent.setAction(Constants.ACTION_INCOMING_CALL);
        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);

        startService(intent);
    }

    private void handleCanceledCallInvite(CancelledCallInvite cancelledCallInvite) {
        Intent intent = new Intent(this, IncomingCallNotificationService.class);
        intent.setAction(Constants.ACTION_CANCEL_CALL);
        intent.putExtra(Constants.CANCELLED_CALL_INVITE, cancelledCallInvite);
        startService(intent);
    }
}
