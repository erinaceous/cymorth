package me.odj.cymorth;

import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;

import java.util.Date;

/**
 * Created by owain on 9/19/13.
 */
public class Slot {
    public Date time = null;
    public String type = null;
    public String module = null;
    public String room = null;
    public String readableTime = null;
    public int missed = 0;
    public long id = -1;

    public static Slot fromSQLiteCursor(SQLiteCursor cur) {
        if(cur == null || cur.getCount() < 1) return null;
        cur.moveToFirst();
        Slot s = new Slot();
        s.id = cur.getLong(cur.getColumnIndex(DBHelper.colId));
        s.time = new Date(cur.getLong(cur.getColumnIndex(DBHelper.colDate)));
        s.module = cur.getString(cur.getColumnIndex(DBHelper.colModule));
        s.room = cur.getString(cur.getColumnIndex(DBHelper.colRoom));
        s.missed = cur.getInt(cur.getColumnIndex(DBHelper.colMissed));
        s.readableTime = cur.getString(cur.getColumnIndex(DBHelper
                .colReadableTime));

        return s;
    }

    @Override
    public String toString() {
        String out = "";
        out += kv("id", this.id);
        out += kv("time", this.time);
        out += kv("module", this.module);
        out += kv("room", this.room);
        out += kv("missed", this.missed);
        out += kv("readableTime", this.readableTime);
        return out;
    }

    public static String kv(String key, Object val) {
        if(val != null) {
            return key + "=" + String.format("%s", val) + ", ";
        }
        return "";
    }

    public static Bundle toBundle(Slot slot) {
        Bundle bundle = new Bundle();
        bundle.putLong("time", slot.time.getTime());
        bundle.putString("type", slot.type);
        bundle.putString("module", slot.module);
        bundle.putString("room", slot.room);
        bundle.putInt("missed", slot.missed);
        bundle.putLong("id", slot.id);
        bundle.putString("readableTime", slot.readableTime);

        return bundle;
    }

    public static Slot fromBundle(Bundle bundle) {
        Slot slot = new Slot();
        slot.time = new Date(bundle.getLong("time"));
        slot.type = bundle.getString("type");
        slot.module = bundle.getString("module");
        slot.room = bundle.getString("room");
        slot.missed = bundle.getInt("missed");
        slot.id = bundle.getLong("id");
        slot.readableTime = bundle.getString("readableTime");

        return slot;
    }
}
