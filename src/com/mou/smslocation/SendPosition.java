package com.mou.smslocation;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendPosition extends Activity implements LocationListener {
	private String TAG = "PositionSender";
	private Context context;
	private LocationManager locationManager;
	private TextView tvposition;
	private EditText phone;
	private Button send;
	private Location lastposition;
	
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
		
		send.setOnClickListener(new OnClickListener(){
			public void onClick(View p)
			{
				String message;
				
				message = ((String) context.getText(R.string.prefix))+"/" +
						lastposition.getLatitude() + ":" + lastposition.getLongitude();
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
