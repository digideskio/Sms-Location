package com.mou.smslocation;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;


public class SmsReceiver extends BroadcastReceiver {
    private Context context;
    private String TAG = "SmsReceiver";

    private void saveSms(String num, String message)
    {
        SQLiteDatabase db;

        db = context.openOrCreateDatabase(context.getString(R.string.db_name), Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sms(number TEXT, data TEXT, date DATETIME);");
        db.execSQL("INSERT INTO sms VALUES('" + num + "','" + message + "',datetime());");
        //unsecure to sql injection
        db.close();
    }
    private void notifyNewPos(String num)
    {
        NotificationCompat.Builder builder;
        Intent i;
        PendingIntent pending;

        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("New position!")
                .setContentText(num + " sent you his position.")
                .setAutoCancel(true);
        i = new Intent(context, SmsList.class);
        pending = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pending);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
    @Override
    public void onReceive(Context context_, Intent intent) {
        context = context_;
        SmsMessage message;
        String body;
        String num;
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");

        for (int x = 0; x < pdus.length; x += 1)
        {
            message = SmsMessage.createFromPdu((byte[]) pdus[x]);
            num = message.getDisplayOriginatingAddress();
            body = message.getDisplayMessageBody();
            if (body.startsWith(context.getString(R.string.prefix)))
            {
                body = body.substring(context.getString(R.string.prefix).length() + 1, body.length());
                saveSms(num, body);
                notifyNewPos(num);
                //abortBroadcast();
            }
        }
    }
}
