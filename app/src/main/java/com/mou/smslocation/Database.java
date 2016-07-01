package com.mou.smslocation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chetyre on 01/07/2016.
 */
public class Database {
    static void saveSms(Context context, String num, String message, boolean sent) {
        SQLiteDatabase db;

        db = context.openOrCreateDatabase(context.getString(R.string.db_name), Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sms(number TEXT, data TEXT, date DATETIME, sent INTEGER);");
        if (sent)
            db.execSQL("INSERT INTO sms VALUES('" + num + "','" + message + "', datetime('now', 'localtime'), 1);");
        else
            db.execSQL("INSERT INTO sms VALUES('" + num + "','" + message + "', datetime('now', 'localtime'), 0);");
        db.close();
    }

    public static String[] getSmsArray(Context cont, int select, boolean filter) {
        List<String> buffer = new ArrayList<String>();
        String res[];
        SQLiteDatabase db;
        Cursor cur;

        db = cont.openOrCreateDatabase(cont.getString(R.string.db_name), Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sms(number TEXT, data TEXT, date DATETIME, sent INTEGER);");
        if (filter)
            cur = db.rawQuery("SELECT * FROM sms WHERE sms.sent == 0 ORDER BY date DESC;", null);
        else
            cur = db.rawQuery("SELECT * FROM sms ORDER BY date DESC;", null);
        while (cur.moveToNext())
            buffer.add(cur.getString(select));
        cur.close();
        db.close();
        res = new String[buffer.size()];
        buffer.toArray(res);
        return (res);
    }
}
