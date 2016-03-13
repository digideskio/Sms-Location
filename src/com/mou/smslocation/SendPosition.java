package com.mou.smslocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SendPosition extends Activity implements LocationListener {
	private String TAG = "PositionSender";
	private Context context;
	private LocationManager locationManager;
	private TextView tvposition;
	private EditText phone;
	private Button send;
	private Location lastposition;
	private ListView smsList;
	
	private void reloadData()
	{
		String[] res;
		
		res = SmsList.getSmsArray(context, 0);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				context,
				android.R.layout.simple_list_item_1,
				res);
		smsList.setAdapter(adapter);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_position);
		context = getApplicationContext();
		
		tvposition = (TextView) findViewById(R.id.lastposition);
		send = (Button) findViewById(R.id.send);
		phone = (EditText) findViewById(R.id.phone);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		smsList = (ListView) findViewById(R.id.recentlist);
		
		smsList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String num;
				
				num = SmsList.getSmsArray(context, 0)[(int)id];
				phone.setText(num);
			}
		});
		
		send.setOnClickListener(new OnClickListener(){
			public void onClick(View p)
			{
				String message;
				
				if (lastposition == null)
				{
					Toast.makeText(context, "No position", Toast.LENGTH_SHORT).show();
					return ;
				}
				message = ((String) context.getText(R.string.prefix))+"/" +
						lastposition.getLatitude() + "," + lastposition.getLongitude();
				SmsManager smsManager = SmsManager.getDefault();
				try
				{
					smsManager.sendTextMessage(phone.getText().toString(), null, message, null, null);
					Toast.makeText(context, "Sms sent!", Toast.LENGTH_SHORT).show();
				}
				catch (Exception e)
				{
					Log.e(TAG, "Did not send sms:\n"+ e.getMessage());
				}
			}
		});
		reloadData();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	@Override
	public void onPause()
	{
		super.onPause();
		locationManager.removeUpdates(this);
	}
	

	@Override
	public void onLocationChanged(Location location) {
		lastposition = location;
		tvposition.setText("Getting position:\n" + location.getLatitude() + "$" + location.getLongitude());
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
