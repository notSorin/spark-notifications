package com.lesorin.sparknotifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lesorin.sparknotifications.helpers.NotificationServiceHelper;

public class UserPresentReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(NotificationServiceHelper.isServiceEnabled(context))
        {
            if(!NotificationServiceHelper.isServiceRunning(context))
            {
                //LoggerFactory.getLogger("UserPresentReceiver").error("Service is enabled, but not running");
            }
        }
    }
}