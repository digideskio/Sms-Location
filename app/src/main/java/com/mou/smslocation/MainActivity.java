package com.mou.smslocation;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "SmsLocationMain";
    private Context context;
    private Button to_list;
    private Button to_send;

    public static String getContactName(Context context, String phoneNumber) {
        String contactName = phoneNumber;
        Cursor cursor = null;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (cursor == null) {
            return (phoneNumber);
        }
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        cursor.close();
        return (contactName);
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
        return (result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        to_list = (Button) findViewById(R.id.tosmslist);
        to_send = (Button) findViewById(R.id.tosend);
        to_list.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p)
            {
                Intent i = new Intent(context, SmsList.class);
                startActivity(i);
            }
        });
        to_send.setOnClickListener(new View.OnClickListener() {
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
