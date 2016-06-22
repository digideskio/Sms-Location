package com.mou.smslocation;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;


public class SmsReceiver extends BroadcastReceiver implements LocationListener {
    private Context context;
    private String TAG = "SmsReceiver";
    LocationManager locationManager;
    int cycles = 0;
    String last_phone;
    SharedPreferences SP;

    static void saveSms(Context context, String num, String message, boolean sent) {
        SQLiteDatabase db;

        db = context.openOrCreateDatabase(context.getString(R.string.db_name), Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sms(number TEXT, data TEXT, date DATETIME, sent INTEGER);");
        if (sent)
            db.execSQL("INSERT INTO sms VALUES('" + num + "','" + message + "', datetime('now', 'localtime'), 1);");
        else
            db.execSQL("INSERT INTO sms VALUES('" + num + "','" + message + "', datetime('now', 'localtime'), 0);");
        //unsecure to sql injection
        db.close();
    }

    private void notifyNewPos(String num, String text) {
        NotificationCompat.Builder builder;
        Intent i;
        PendingIntent pending;

        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(text)
                .setContentText("From : " + MainActivity.getContactName(context, num))
                .setAutoCancel(true);
        i = new Intent(context, SmsList.class);
        pending = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pending);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    public void manageSms(Context context, SmsMessage message) {
        String body;
        String num;

        num = message.getDisplayOriginatingAddress();
        last_phone = num;
        body = message.getDisplayMessageBody();
        if (body.startsWith(context.getString(R.string.prefix))) {
            notifyNewPos(num, "New position!");
            body = body.substring(context.getString(R.string.prefix).length() + 1, body.length());
            saveSms(context, num, body, false);
        } else if (body.startsWith(context.getString(R.string.code)) &&
                SP.getBoolean("position_public", true)) {
            notifyNewPos(num, "Position request!");
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (SendPosition.checkLocationPermission(context))
                return;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            cycles = 0;
        }
    }

    @Override
    public void onReceive(Context context_, Intent intent) {
        context = context_;
        SP = PreferenceManager.getDefaultSharedPreferences(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SmsMessage[] messages;

            messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for (int x = 0; x < messages.length; x += 1) {
                manageSms(context, messages[x]);
            }
        } else {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage message;

            if (pdus == null) {
                return;
            }
            for (int x = 0; x < pdus.length; x += 1) {
                message = SmsMessage.createFromPdu((byte[]) pdus[x]);
                manageSms(context, message);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (Integer.parseInt(SP.getString("cycle_wait", "10")) < cycles) {
            if (SendPosition.checkLocationPermission(context))
                return;
            locationManager.removeUpdates((LocationListener)SmsReceiver.this);
            SendPosition.sendPosition(context, last_phone, location);
            cycles = 0;
        }
        cycles += 1;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
