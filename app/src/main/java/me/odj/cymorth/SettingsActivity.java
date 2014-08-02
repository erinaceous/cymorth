package me.odj.cymorth;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onStop() {
        Log.d("abersistant", "Preferences changed");
        Intent intent = new Intent(getApplicationContext(),
                AlarmService.class);
        intent.setAction(AlarmService.ACTION_RELOAD);
        sendBroadcast(intent);
        super.onStop();
    }
}
