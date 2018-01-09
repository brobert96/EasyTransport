package com.example.robert.easytransport.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.services.GPSTracker;

/**
 * Created by Robert on 11/22/2017.
 */

public class ArrivedReceiver extends BroadcastReceiver {

    private Double latitude;
    private Double longitude;

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);


        Uri myNavigationUri = Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude+"&mode=w");
        Intent intent1 = new Intent(Intent.ACTION_VIEW, myNavigationUri);


        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent1,
                0));
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(context.getString(R.string.notification_text));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
        Intent intent2 = new Intent(context, GPSTracker.class);
        context.unregisterReceiver(this);
        context.stopService(intent2);
    }
}
