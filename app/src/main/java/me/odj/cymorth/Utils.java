package me.odj.cymorth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by owain on 02/08/14.
 */
public class Utils {
    public static void setCurrentLanguage(Context ctx, String language) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("app_language", language);
        edit.commit();
    }

    public static String getCurrentLanguage(Context ctx) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getString("app_language", "gb");
    }

    public static void setAppLocale(Context ctx) {
        Resources res = ctx.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(getCurrentLanguage(ctx).toLowerCase());
        res.updateConfiguration(conf, dm);
    }
}
