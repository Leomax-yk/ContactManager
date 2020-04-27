package jp.mcinc.imesh.type.ipphone.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import jp.mcinc.imesh.type.ipphone.contants.Constants;

public class BootCompleteBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Constants.sendBootCompletedStartBroadcast(context.getApplicationContext());
    }
}
