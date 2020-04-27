package jp.mcinc.imesh.type.ipphone.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import jp.mcinc.imesh.type.ipphone.activity.SplashScreen;

public class RefreshBroadCast  extends BroadcastReceiver {

    private String TAG= getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Intent i = new Intent(context, SplashScreen.class);
            i.putExtra("isOpen",true);
            i.putExtra("device_id","" + bundle.getString("device_id"));
            i.putExtra("refresh_token","" + bundle.getString("refresh_token"));
            i.putExtra("id_token","" + bundle.getString("id_token"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
