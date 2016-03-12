package com.mou.smslocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private String TAG = "SmsLocationMain";
	private Context context;
	private Button tosmslist;
	private Button tosend;
	
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
