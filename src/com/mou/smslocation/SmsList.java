package com.mou.smslocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SmsList extends Activity {
	private String TAG = "SmsLocationMain";
	private ListView smsList;
	private Context context;
	
	public String[] getSmsArray(int select)
	{
		String res[];
		StringBuffer buff = new StringBuffer();
		SQLiteDatabase db;
		Cursor cur;
		
		db = context.openOrCreateDatabase(context.getString(R.string.db_name), Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS sms(number TEXT, data TEXT, id INTEGER);");
		cur = db.rawQuery("SELECT * FROM sms ORDER BY id DESC;", null);
		while (cur.moveToNext())
		{
			//buff.append("id: " + cur.getString(2) + "/" + cur.getString(0) + ":"+ cur.getString(1));
			buff.append(cur.getString(select));
			buff.append("\n");
		}
		db.close();
		res = buff.toString().split("\n");
		return (res);
	}
	private void reloadData()
	{
		String[] res;
		
		res = getSmsArray(0);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(),
				android.R.layout.simple_list_item_1,
				res);
		smsList.setAdapter(adapter);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_list);
		
		context = getApplicationContext();
		smsList = (ListView) findViewById(R.id.smslist);
		
		smsList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String coords;
				
				coords = getSmsArray(1)[(int)id];
				//Toast.makeText(context, coords, Toast.LENGTH_SHORT).show();
				String uri = "geo:" + coords;
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		});
		
		reloadData();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		reloadData();
	}
}
