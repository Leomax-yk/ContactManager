package jp.mcinc.imesh.type.ipphone.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twilio.voice.CallInvite;

import jp.mcinc.imesh.type.ipphone.activity.SplashScreen;
import jp.mcinc.imesh.type.ipphone.activity.VoiceActivity;
import jp.mcinc.imesh.type.ipphone.contants.Constants;

public class VoiceIncomingBroadCast extends BroadcastReceiver {

    private String TAG= getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, VoiceActivity.class);
        if (intent != null && intent.getAction() != null) {
            i.setAction(intent.getAction());
            CallInvite callInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE);
            i.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
            int activeCallNotificationId = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0);
            i.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, activeCallNotificationId);
        }
        context.startActivity(i);
    }
}
