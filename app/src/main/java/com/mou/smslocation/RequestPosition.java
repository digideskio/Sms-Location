package com.mou.smslocation;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class RequestPosition extends AppCompatActivity {

    final int PHONE_PICK = 42;
    private String TAG = "PositionSender";
    private Context context;
    private EditText phone;
    private ListView sms_list;
    private ImageButton send;
    private String[] recent_phones;

    private void reloadData() {
        String res[];

        recent_phones = Database.getSmsArray(context, 0, false);
        recent_phones = SendPosition.removeDup(recent_phones);
        res = MainActivity.phoneArrayToName(context, recent_phones);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                res);
        sms_list.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_position);
        context = RequestPosition.this;

        send = (ImageButton) findViewById(R.id.r_send);
        phone = (EditText) findViewById(R.id.r_phone);
        sms_list = (ListView) findViewById(R.id.r_recent_list);
        ImageButton pick_contact = (ImageButton) findViewById(R.id.r_pick_contact);

        if (sms_list == null) throw new AssertionError("Object cannot be null");
        sms_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String num;

                num = recent_phones[(int) id];
                phone.setText(num);
            }
        });

        if (send == null) throw new AssertionError("Object cannot be null");
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                String message;

                message = getResources().getString(R.string.code);
                SmsManager smsManager = SmsManager.getDefault();
                try {
                    smsManager.sendTextMessage(phone.getText().toString(), null, message, null, null);
                    Toast.makeText(context, getString(R.string.sent), Toast.LENGTH_SHORT).show();
                    Database.saveSms(context, phone.getText().toString(), message, true);
                } catch (Exception e) {
                    Toast.makeText(context,
                            getString(R.string.not_sent) + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

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
}
