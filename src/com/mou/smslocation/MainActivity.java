package com.mou.smslocation;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private String TAG = "SmsLocationMain";
	private Context context;
	private Button tosmslist;
	private Button tosend;
	
	public static String getContactName(Context context, String phoneNumber) {
		String contactName = phoneNumber;
		ContentResolver cr = context.getContentResolver();
	    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
	    Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
	    if (cursor == null) {
	        return phoneNumber;
	    }
	    if(cursor.moveToFirst()) {
	        contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
	    }
	    return contactName;
	}
	public static String[] phoneArrayToName(Context context, final String[] numbers)
	{
		String[] result = new String[numbers.length];
		int x = 0;
		while (x < numbers.length)
		{
			result[x] = getContactName(context, numbers[x]);
			x += 1;
		}
		return result;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = getApplicationContext();
		tosmslist = (Button) findViewById(R.id.tosmslist);
		tosend = (Button) findViewById(R.id.tosend);
		
		tosmslist.setOnClickListener(new OnClickListener() {
			public void onClick(View p)
			{
				Intent i = new Intent(context, SmsList.class);
				startActivity(i);
			}
		});
		tosend.setOnClickListener(new OnClickListener() {
			public void onClick(View p)
			{
				Intent i = new Intent(context, SendPosition.class);
				startActivity(i);
			}
		});
	}
	@Override
	public void onResume()
	{
		super.onResume();
	}
}
