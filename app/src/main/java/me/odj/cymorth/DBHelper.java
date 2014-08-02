package me.odj.cymorth;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

/**
 * Created by owain on 9/18/13.
 */
public class DBHelper extends SQLiteOpenHelper {
    static final int dbVersion = 1;
    static final String dbName = "abersistant";
    static final String dbTable = "slots";
    static final String colId = "_id";
    static final String colDate = "Date";
    static final String colType = "Type";
    static final String colModule = "Module";
    static final String colRoom = "Room";
    static final String colMissed = "Missed";
    static final String colReadableTime = "ReadableTime";

    public DBHelper(Context context) {
        super(context, dbName, null, dbVersion);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, " +
                "%s LONG, %s TEXT, %s TEXT, %s TEXT NOT NULL, %s INTEGER, " +
                "%s INTEGER);",
                dbTable, colId, colDate, colType, colModule, colRoom,
                colMissed,
                colReadableTime));
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + dbTable);
            onCreate(db);
        }
    }

    public SQLiteCursor getLecture(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor cur = (SQLiteCursor) db.query(dbTable, null,
                String.format("%s=%d", colId, id), null, null, null, colDate,
                null);
        return cur;
    }

    public SQLiteCursor getLectures() {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor cur = (SQLiteCursor) db.query(dbTable, null, null, null,
                null,
                colDate, null);
        return cur;
    }

    public boolean lectureExists(Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor cur = (SQLiteCursor) db.query(dbTable, null,
                String.format("%s=%d",
                colDate, date.getTime()), null, null, null, colDate, null);
        return cur.getCount() > 0;
    }

    public SQLiteCursor getWeeksLectures(Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.Calendar[] range = UniCalendar.getWeekRange(date);
        long start = range[0].getTime().getTime();
        long end = range[0].getTime().getTime();
        SQLiteCursor cur = (SQLiteCursor) db.query(dbTable, null,
                String.format("%s BETWEEN %d AND %d", colDate, start, end),
                null, null, colDate, null);
        return cur;
    }

    public SQLiteCursor getNextLecture(Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor cur = (SQLiteCursor) db.query(dbTable, null,
                String.format("%s >= %d", colDate, date.getTime()), null,
                null, null,
                colDate, null);
        return cur;
    }

    public SQLiteCursor getLastLecture(Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor cur = (SQLiteCursor) db.query(dbTable, null,
                String.format("%s <= %d", colDate, date.getTime()), null, null,
                null, colDate + " DESC", null);
        return cur;
    }

    public SQLiteCursor getDaysLectures(Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        long start = UniCalendar.getStartOfUniDay(date).getTime().getTime() - 1;
        long end = UniCalendar.getEndOfUniDay(date).getTime().getTime() + 1;
        SQLiteCursor cur = (SQLiteCursor) db.query(dbTable, null,
                String.format("%s BETWEEN %d AND %d", colDate, start,
                        end),
                null, null, null, colDate, null);
        return cur;
    }

    public boolean insertLecture(Slot slot, boolean replace) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Long id = null;
        cv.put(colDate, slot.time.getTime());
        cv.put(colType, slot.type);
        cv.put(colModule, slot.module);
        cv.put(colRoom, slot.room);
        cv.put(colReadableTime, slot.readableTime);
        if(lectureExists(slot.time)) {
            if(replace == true) {
                id = (long) db.update(dbTable, cv, String.format("%s=%d",
                        colDate, slot.time.getTime()), null);
            }
        } else {
            id = db.insert(dbTable, null, cv);
        }
        if(id != null) {
            Log.d("abersistant", String.format("New row added, id: %d", id));
        }
        return id != null;
    }

    public int insertLecture(Slot[] slots, boolean replace) {
        int count = 0;
        boolean inserted;
        for(Slot slot : slots) {
            if(slot != null) {
                inserted = this.insertLecture(slot, replace);
                if(inserted == true) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean insertLecture(Slot slot) {
        return insertLecture(slot, false);
    }

    public int insertLecture(Slot[] slots) {
        return insertLecture(slots, false);
    }
}
