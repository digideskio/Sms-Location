package com.mou.smslocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class SmsReceiver extends BroadcastReceiver {
	private Context context;
	private String TAG = "SmsReceiver";
	
	private void saveSms(String num, String message)
	{
		SQLiteDatabase db;
		
		db = context.openOrCreateDatabase(context.getString(R.string.db_name), Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS sms(number VARCHAR, data VARCHAR);");
		db.execSQL("INSERT INTO sms VALUES('" + num + "','" + message + "');");
		db.close();
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
				//only saving useful messages
				saveSms(num, body);
			}
		}
	}
}
