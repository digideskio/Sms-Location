package com.mou.smslocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by mou on 18/06/2016.
 */

public class SmsListAdapter extends BaseAdapter {
    String[] phones = null;
    String[] dates = null;
    Context context = null;

    public SmsListAdapter(Context context_)
    {
        context = context_;
        phones = SmsList.getSmsArray(context, 0);
        dates = SmsList.getSmsArray(context, 2);
    }
    public int getCount() {
        return phones.length;
    }

    public Object getItem(int position) {
        return phones[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        TextView date;
        TextView name;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sms_list_item, parent, false);
        }
        date = (TextView) convertView.findViewById(R.id.item_date);
        name = (TextView) convertView.findViewById(R.id.item_name);
        if (phones[position].length() > 0) {
            name.setText(MainActivity.getContactName(context, phones[position]));
            date.setText(dates[position]);
        }
        return (convertView);
    }
}
