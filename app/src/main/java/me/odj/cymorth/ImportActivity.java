package me.odj.cymorth;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class ImportActivity extends Activity {

    public static final int FILE_SELECT_CODE = 0;
    public static final int FILE_DOWNLOAD_CODE = 1;
    public static final int LOGIN_CODE = 2;
    private HTMLImporter importer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setAppLocale(this);
        setContentView(R.layout.activity_import);
    }

    public void set_status(Spanned text) {
        TextView tv = (TextView) this.findViewById(R.id.import_info);
        if(tv != null) {
            tv.setText(text);
        } else {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }
    }

    public void set_status(String text) {
        Spanned s = Html.fromHtml(text);
        set_status(s);
    }

    public void set_status(int id) {
        set_status(getString(id));
    }

    public void btn_download(View view) {
        Intent intent = new Intent(this, DownloadFromWeb.class);
        startActivityForResult(intent, FILE_DOWNLOAD_CODE);
    }

    public void btn_browse(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/html");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent,
                            getString(R.string.select_file_to_import)),
                    FILE_SELECT_CODE
            );
        } catch (ActivityNotFoundException e) {
            set_status(R.string.please_install_file_manager);
            e.printStackTrace();
        }
    }

    public void btn_import(View view) {
        if(this.importer != null) {
            try {
                set_status(R.string.importing);
                boolean replace = ((CheckBox) findViewById(R.id
                        .check_overwrite)).isChecked();
                int count = this.importer.addToDatabase(this, replace);
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(this);
                sp.edit().putBoolean("new_data", true).commit();
                set_status(String.format(getString(R.string.added_X_lectures),
                        count));
                Intent intent = new Intent(this, AlarmService.class);
                intent.setAction(AlarmService.ACTION_RELOAD);
                sendBroadcast(intent);
            } catch(Exception e) {
                set_status(R.string.couldnt_add_database);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch(requestCode) {
            case FILE_SELECT_CODE:
            case FILE_DOWNLOAD_CODE:
                if(resultCode == RESULT_OK) {
                    String info = "";
                    Uri uri = data.getData();
                    info += uri.getLastPathSegment() + "<br /><br />";
                    TextView tv = (TextView) this.findViewById(R.id.import_info);
                    ContentResolver cr = this.getContentResolver();
                    try {
                        InputStream in = cr.openInputStream(uri);
                        this.importer = HTMLImporter.fromFile(in);
                        Date[] dates = this.importer.getDates();
                        Calendar c = UniCalendar.getInstance();
                        c.setTime(dates[0]);
                        info += String.format(getString(R.string
                                .timetable_starting_on), Utils.getLocalDate(this, c));
                        Button button = (Button) this.findViewById(R.id
                                .button_import);
                        button.setEnabled(true);
                        tv.setText(Html.fromHtml(info));
                    } catch (Exception e) {
                        set_status(R.string.couldnt_open_file);
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
