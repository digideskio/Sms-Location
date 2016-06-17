package com.mou.smslocation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SmsList extends AppCompatActivity {

    private String TAG = "SmsLocationMain";
    private ListView smsList;
    private Context context;

    public static String[] getSmsArray(Context cont, int select)
    {
        String res[];
        StringBuffer buff = new StringBuffer();
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
        db.close();
        res = buff.toString().split("\n");
        return (res);
    }
    private void reloadData()
    {
        String[] res;

        res = getSmsArray(context, 0);
        res = MainActivity.phoneArrayToName(context, res);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context,
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
        smsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String coords;
                String name;

                name = getSmsArray(context, 0)[(int)id];
                coords = getSmsArray(context, 1)[(int)id];
                //Toast.makeText(context, coords, Toast.LENGTH_SHORT).show();
                String uri = "geo:" + coords + "?q=" + coords + "(" + MainActivity.getContactName(context, name)+")";
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
