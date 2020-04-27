package jp.mcinc.imesh.type.ipphone.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import jp.mcinc.imesh.type.ipphone.session.SessionManager;

public class TalkStartBroadcast  extends BroadcastReceiver {

    private String TAG= getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.setCallImeshStart(true);
    }
}