package com.mou.smslocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SendPosition extends AppCompatActivity implements LocationListener {

    final int PHONE_PICK = 42;
    private String TAG = "PositionSender";
    private Context context;
    private LocationManager locationManager;
    private TextView tv_last_position;
    private EditText phone;
    private Location last_position;
    private ListView sms_list;
    private ImageButton send;

    private void reloadData() {
        String[] res;

        res = SmsList.getSmsArray(context, 0);
        res = MainActivity.phoneArrayToName(context, res);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                res);
        sms_list.setAdapter(adapter);
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission error.", Toast.LENGTH_LONG).show();
            return (true);
        }
        return (false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_position);
        context = SendPosition.this;

        tv_last_position = (TextView) findViewById(R.id.lastposition);
        send = (ImageButton) findViewById(R.id.send);
        phone = (EditText) findViewById(R.id.phone);
        sms_list = (ListView) findViewById(R.id.recentlist);
        ImageButton pick_contact = (ImageButton) findViewById(R.id.pickcontact);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission())
            finish();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        if (sms_list == null) throw new AssertionError("Object cannot be null");
        sms_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String num;

                num = SmsList.getSmsArray(context, 0)[(int) id];
                phone.setText(num);
            }
        });

        if (send == null) throw new AssertionError("Object cannot be null");
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                String message;

                if (last_position == null) {
                    Toast.makeText(context, "No position", Toast.LENGTH_SHORT).show();
                    return;
                }
                message = ((String) context.getText(R.string.prefix)) + "&" +
                        last_position.getLatitude() + "," + last_position.getLongitude();
                SmsManager smsManager = SmsManager.getDefault();
                try {
                    smsManager.sendTextMessage(phone.getText().toString(), null, message, null, null);
                    Toast.makeText(context, "Sms sent!", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Toast.makeText(context,
                            "Did not send sms:\n" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        send.setClickable(false);

        if (pick_contact == null) throw new AssertionError("Object cannot be null");
        pick_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                Intent i;

                i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PHONE_PICK);
            }
        });

        reloadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkLocationPermission())
            finish();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (checkLocationPermission())
            finish();
        locationManager.removeUpdates((LocationListener) this);
    }

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        if (req == PHONE_PICK && res == RESULT_OK) {
            Uri contact = data.getData();
            Cursor cursor;
            int col;
            String res_data;

            cursor = getContentResolver().query(contact, null, null, null, null);
            if (cursor == null)
                return;
            cursor.moveToFirst();
            col = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            res_data = cursor.getString(col);
            if (phone != null) {
                phone.setText(res_data);
                cursor.close();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        last_position = location;
        send.setClickable(true);
        tv_last_position.setText("Getting position:\n" + location.getLatitude() + "," + location.getLongitude());
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
