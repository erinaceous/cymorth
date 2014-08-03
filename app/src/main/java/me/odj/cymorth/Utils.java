package me.odj.cymorth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by owain on 02/08/14.
 */
public class Utils {
    public static void setCurrentLanguage(Context ctx, String language) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("app_language", language);
        edit.commit();
    }

    public static String getCurrentLanguage(Context ctx) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        return sharedPref.getString("app_language", "gb");
    }

    public static Locale getCurrentLocale(Context ctx) {
        Resources res = ctx.getApplicationContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        return conf.locale;
    }

    public static void setAppLocale(Context ctx) {
        Resources res = ctx.getApplicationContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(getCurrentLanguage(ctx).toLowerCase());
        res.updateConfiguration(conf, dm);
    }

    public static String getLocalDay(Context ctx, int weekDay) {
        Context app = ctx.getApplicationContext();
        switch(weekDay) {
            case 2: // monday
                return app.getString(R.string.monday);
            case 3: // tuesday
                return app.getString(R.string.tuesday);
            case 4: // wednesday
                return app.getString(R.string.wednesday);
            case 5: // thursday
                return app.getString(R.string.thursday);
            case 6: // friday
                return app.getString(R.string.friday);
            case 0: // saturday
                return app.getString(R.string.saturday);
            case 1: // sunday
                return app.getString(R.string.sunday);
            default:
                return "";
        }
    };

    public static String getLocalDayShort(Context ctx, int weekDay) {
        Context app = ctx.getApplicationContext();
        switch(weekDay) {
            case 2: // mon
                return app.getString(R.string.mon);
            case 3: // tue
                return app.getString(R.string.tue);
            case 4: // wed
                return app.getString(R.string.wed);
            case 5: // thu
                return app.getString(R.string.thu);
            case 6: // fri
                return app.getString(R.string.fri);
            case 0: // sat
                return app.getString(R.string.sat);
            case 1: // sun
                return app.getString(R.string.sun);
            default:
                return "";
        }
    }

    public static String getLocalMonth(Context ctx, int month) {
        Context app = ctx.getApplicationContext();
        switch(month) {
            case 1: // january
                return app.getString(R.string.january);
            case 2: // february
                return app.getString(R.string.february);
            case 3: // march
                return app.getString(R.string.march);
            case 4: // april
                return app.getString(R.string.april);
            case 5: // may
                return app.getString(R.string.may);
            case 6: // june
                return app.getString(R.string.june);
            case 7: // july
                return app.getString(R.string.july);
            case 8: // august
                return app.getString(R.string.august);
            case 9: // september
                return app.getString(R.string.september);
            case 10: // october
                return app.getString(R.string.october);
            case 11: // november
                return app.getString(R.string.november);
            case 12: // december
                return app.getString(R.string.december);
            default:
                return "";
        }
    }

    public static String getLocalDate(Context ctx, Calendar c) {
        return String.format("%s %d %s",
                getLocalDay(ctx, c.get(c.DAY_OF_WEEK)),
                c.get(c.DAY_OF_MONTH),
                getLocalMonth(ctx, c.get(c.MONTH)));
    }

    public static String getLocalDateShort(Context ctx, Calendar c) {
        return String.format("%s %02d",
                             getLocalDayShort(ctx, c.get(c.DAY_OF_WEEK)),
                             c.get(c.DAY_OF_MONTH));
    }
}
