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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SmsList extends AppCompatActivity {

    private String TAG = "SmsLocationList";
    private ListView sms_list;
    private Context context;


    private void reloadData()
    {
        String[] res;

        res = Database.getSmsArray(context, 0, true);
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

                name = Database.getSmsArray(context, 0, true)[(int)id];
                coords = Database.getSmsArray(context, 1, true)[(int)id];
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reload_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                reloadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        reloadData();
    }
}
