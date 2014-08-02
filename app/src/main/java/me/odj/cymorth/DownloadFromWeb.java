package me.odj.cymorth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;


public class DownloadFromWeb extends Activity {
    public static final String URL = "https://studentrecord.aber.ac.uk/en/" +
                                     "timetable.php?day=%d&month=%d&year=%d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setAppLocale(this);
        setContentView(R.layout.activity_download_from_web);

        CookieSyncManager.createInstance(getApplicationContext());
        String cookie = android.webkit.CookieManager.getInstance().getCookie("studentlive");
        if(cookie == null || cookie == "") {
            startActivityForResult(new Intent(this, Login.class),
                    ImportActivity.LOGIN_CODE);
        } else {
            Log.d("srmain", "Already logged in");
        }
    }

    // Snippet from stackoverflow:
    // http://stackoverflow.com/a/17553052
    public static String getCookieFromAppCookieManager(String url) throws MalformedURLException {
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager == null)
            return null;
        String rawCookieHeader = null;
        java.net.URL parsedURL = new URL(url);

        // Extract Set-Cookie header value from Android app CookieManager for this URL
        rawCookieHeader = cookieManager.getCookie(parsedURL.getHost());
        if (rawCookieHeader == null)
            return null;
        return rawCookieHeader;
    }

    public void btn_download(View view) {
        DatePicker dp = (DatePicker) findViewById(R.id.datePicker);
        Date d = new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth());
        Calendar c = UniCalendar.getStartOfWeek(d);
        String url = String.format(URL, c.get(Calendar.DAY_OF_MONTH),
                                        1 + c.get(Calendar.MONTH),
                                        c.get(Calendar.YEAR));
        Log.d("srweb", url);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_download_timetable);
        final Button downloadButton = (Button) findViewById(R.id.button_download_timetable);
        final Activity activity = this;
        progressBar.setVisibility(ProgressBar.VISIBLE);
        downloadButton.setVisibility(Button.GONE);

        AsyncHttpClient client = new AsyncHttpClient();
        try {
            String cookies = getCookieFromAppCookieManager("https://studentrecord.aber.ac.uk/");
            client.addHeader("Cookie", cookies);
        } catch(MalformedURLException e) {
        }
        client.get(url, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Log.d("srweb", response.getAbsolutePath());
                Intent intent = activity.getIntent();
                Uri uri = Uri.fromFile(response);
                intent.setData(uri);
                activity.setResult(ImportActivity.RESULT_OK, intent);
                activity.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File response) {
                progressBar.setVisibility(ProgressBar.GONE);
                downloadButton.setVisibility(Button.VISIBLE);
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
        CookieSyncManager.getInstance().sync();
    }


    @Override
    public void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }
}
