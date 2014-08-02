package me.odj.cymorth;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by owain on 9/22/13.
 */
public class AlarmService extends BroadcastReceiver {
    /** Register a broadcast receiver that runs on phone startup to
     * re-register any AlarmManager things due in the future.
     */

    public static final int ID = 0;
    public static final String ACTION_ALARM = "AbersistantAlarm";
    public static final String ACTION_RELOAD = "AbersistantTimetableChanged";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)
                || intent.getAction().equalsIgnoreCase(ACTION_RELOAD)) {
            onSystemStartup(context, intent);
        } else if(intent.getAction().equalsIgnoreCase(ACTION_ALARM)) {
            onAlarmReceived(context, intent);
        }
    }

    public void onSystemStartup(Context context, Intent intent) {
        Log.d("abersistant", "AlarmService caught startup broadcast");
        setAlarm(context, intent, true);
    }

    public void setAlarm(Context context, Intent intent, boolean now) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences
                (context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context
                .ALARM_SERVICE);
        int notify_when_lecture_soon = Integer.parseInt(sp.getString
                ("notify_when_lecture_soon", "-1"));
        int notify_alarm_morning = Integer.parseInt(sp.getString
                ("notify_alarm_morning", "-1"));

        if(notify_when_lecture_soon != -1) {
            Date d =  new Date();
            DBHelper dbhelper = new DBHelper(context);
            Slot slot = Slot.fromSQLiteCursor(dbhelper.getNextLecture(d));
            if(slot != null) {
                long time = (long) slot.time.getTime() -
                        (notify_when_lecture_soon * 60000);
                if(notify_when_lecture_soon == 0) {
                    Slot last_slot = Slot.fromSQLiteCursor(dbhelper
                            .getLastLecture(d));
                    if(last_slot != null) {
                        time = (long) last_slot.time.getTime() + (UniCalendar
                                .SLOT_LENGTH_MIN * 60000);
                    } else if(now == true) {
                        time = d.getTime();
                    }
                }
                Intent newIntent = new Intent(context, AlarmService.class);
                newIntent.setAction(ACTION_ALARM);
                Bundle info = Slot.toBundle(slot);
                info.putBoolean("morning", false);
                newIntent.putExtras(info);
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                Log.d("abersistant", String.format("Registered notification " +
                        "for %s (for lecture: %s)",
                        new Date(time), slot));
            }
        }

        if(notify_alarm_morning != -1) {
            Date d = UniCalendar.getStartOfUniDay(new Date()).getTime();
            if(intent.getBooleanExtra("morning", false) == true) {
                Calendar c = UniCalendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, 1);
                d = UniCalendar.getStartOfUniDay(c.getTime()).getTime();
            }
            DBHelper dbhelper = new DBHelper(context);
            Slot slot = Slot.fromSQLiteCursor(dbhelper.getNextLecture(d));
            if(slot != null) {
                long time = (long) slot.time.getTime() -
                        (notify_alarm_morning * 60000);
                Intent newIntent = new Intent(context, AlarmService.class);
                newIntent.setAction(ACTION_ALARM);
                Bundle info = Slot.toBundle(slot);
                info.putBoolean("morning", true);
                newIntent.putExtras(info);
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (context, 0, newIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                Log.d("abersistant", String.format("Registered alarm for %s " +
                        "(for lecture: %s", new Date(time), slot));
            }
        }
    }

    public void onAlarmReceived(Context context, Intent intent) {
        setAlarm(context, intent, false);
        PendingIntent newIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, TimetableView.class), 0);
        intent.setClass(context, TimetableView.class);
        NotificationManager nm = (NotificationManager) context
                .getSystemService(context.NOTIFICATION_SERVICE);
        Slot slot = Slot.fromBundle(intent.getExtras());
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager
                .TYPE_NOTIFICATION);
        if(intent.getBooleanExtra("morning", false) == true) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager
                    .TYPE_ALARM);
        }
        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.lecture_soon))
                .setContentText(Html.fromHtml(String.format(context.getString(R
                        .string
                        .lecture_soon_detailed), slot.room,
                        slot.readableTime))).setSmallIcon(R.drawable
                        .ic_launcher_grey)
                .setSound(alarmSound).setContentIntent(newIntent).build();
        nm.notify(ID, notification);
    }
}
