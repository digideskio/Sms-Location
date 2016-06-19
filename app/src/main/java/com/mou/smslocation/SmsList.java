package com.mou.smslocation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SmsList extends AppCompatActivity {

    private String TAG = "SmsLocationList";
    private ListView sms_list;
    private Context context;
    private Handler handler;

    public static String[] getSmsArray(Context cont, int select)
    {
        String res[];
        StringBuilder buff = new StringBuilder();
        SQLiteDatabase db;
        Cursor cur;

        db = cont.openOrCreateDatabase(cont.getString(R.string.db_name), Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sms(number TEXT, data TEXT, date DATETIME);");
        cur = db.rawQuery("SELECT * FROM sms ORDER BY date DESC;", null);
        while (cur.moveToNext())
        {
            buff.append(cur.getString(select));
            buff.append("\n");
        }
        cur.close();
        db.close();
        if (buff.toString().length() == 0) {
            return (new String[0]);
        }
        res = buff.toString().split("\n");
        return (res);
    }
    private void reloadData()
    {
        String[] res;

        res = getSmsArray(context, 0);
        res = MainActivity.phoneArrayToName(context, res);
        sms_list.setAdapter(new SmsListAdapter(context));
        sms_list.setEmptyView(findViewById(R.id.empty));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);

        context = SmsList.this;

        sms_list = (ListView) findViewById(R.id.smslist);
        if (sms_list == null) throw new AssertionError("Object cannot be null");
        sms_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String coords;
                String name;

                name = getSmsArray(context, 0)[(int)id];
                coords = getSmsArray(context, 1)[(int)id];
                if (coords.length() > 0) {
                    //Toast.makeText(context, coords, Toast.LENGTH_SHORT).show();
                    String uri = "geo:" + coords + "?q=" + coords + "(" + MainActivity.getContactName(context, name) + ")";
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        });
        reloadData();
        Runnable runnable = new Runnable() {
            //auto reload data
            @Override
            public void run() {
                reloadData();
                handler.postDelayed(this, 1000);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        reloadData();
    }
}
