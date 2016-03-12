package com.mou.smslocation;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private String TAG = "SmsLocationMain";
	private ListView smsList;
	private Context context;
	
	private String[] getSmsArray()
	{
		String res[];
		StringBuffer buff = new StringBuffer();
		SQLiteDatabase db;
		Cursor cur;
		
		db = context.openOrCreateDatabase(context.getString(R.string.db_name), Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS sms(number VARCHAR, data VARCHAR);");
		cur = db.rawQuery("SELECT * FROM sms;", null);
		while (cur.moveToNext())
		{
			buff.append(cur.getString(0) + ":"+ cur.getString(1));
			buff.append("\n");
		}
		db.close();
		res = buff.toString().split("\n");
		return (res);
	}
	private void reloadData()
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MainActivity.this,
				android.R.layout.simple_list_item_1,
				getSmsArray());
		smsList.setAdapter(adapter);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = getApplicationContext();
		smsList = (ListView) findViewById(R.id.smslist);
		
		reloadData();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		reloadData();
	}
}
