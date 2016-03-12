package com.mou.smslocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
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
			if (body.startsWith("smslocation:"))
			{
				Log.d("GOT MESSAGE", num + " : " + body);
			}
		}
	}
}
