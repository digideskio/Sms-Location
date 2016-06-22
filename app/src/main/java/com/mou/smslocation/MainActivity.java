package com.mou.smslocation;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "SmsLocationMain";
    private Context context;

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("This app is using SMS to communicate," +
                            "\nWith the current settings anyone can access your position by SMS" +
                            "\nYou can turn off sharing position in the options to prevent unwanted cost" +
                            "\nand only use this application to send your position")
                    .setNeutralButton("OK", null)
                    .show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public static String getContactName(Context context, String phoneNumber) {
        String contactName = phoneNumber;
        Cursor cursor = null;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (cursor == null) {
            return (phoneNumber);
        }
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        cursor.close();
        return (contactName);
    }

    public static String[] phoneArrayToName(Context context, final String[] numbers) {
        String[] result = new String[numbers.length];
        int x = 0;
        while (x < numbers.length) {
            result[x] = getContactName(context, numbers[x]);
            x += 1;
        }
        return (result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        Button to_list = (Button) findViewById(R.id.to_list);
        Button to_send = (Button) findViewById(R.id.to_send);
        Button to_settings = (Button) findViewById(R.id.to_settings);
        Button to_info = (Button) findViewById(R.id.to_info);
        Button to_request = (Button) findViewById(R.id.to_request);

        if (to_list == null) throw new AssertionError("Object cannot be null");
        to_list.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                Intent i = new Intent(context, SmsList.class);
                startActivity(i);
            }
        });

        if (to_send == null) throw new AssertionError("Object cannot be null");
        to_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                Intent i = new Intent(context, SendPosition.class);
                startActivity(i);
            }
        });

        if (to_request == null) throw new AssertionError("Object cannot be null");
        to_request.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                Intent i = new Intent(context, RequestPosition.class);
                startActivity(i);
            }
        });

        if (to_info == null) throw new AssertionError("Object cannot be null");
        to_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                Intent i = new Intent(context, Information.class);
                startActivity(i);
            }
        });

        if (to_settings == null) throw new AssertionError("Object cannot be null");
        to_settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                Intent i = new Intent(context, Settings.class);
                startActivity(i);
            }
        });

        Button[] Buttons = {to_list, to_send, to_info, to_settings, to_request};
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        for (int i = 0; i < Buttons.length; i++) {
            Buttons[i].setWidth(size.x / 2 - 40);
        }
        checkFirstRun();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
