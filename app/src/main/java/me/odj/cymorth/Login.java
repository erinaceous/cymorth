package me.odj.cymorth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;


public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setAppLocale(this);
        CookieSyncManager.createInstance(getApplicationContext());
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_login);

        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        final Activity activity = this;

        class LoadListener {
            @JavascriptInterface
            public void finish() {
                activity.setResult(ImportActivity.RESULT_OK, getIntent());
                activity.finish();
            }
        }
        webView.addJavascriptInterface(new LoadListener(), "srweb");

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            private void injectAsset(WebView view, String file, String mimeType) {
                /*
                    Snippet from StackOverflow.
                    Intensely useful for my purposes -- but how easy is it to
                    exploit? Could someone target this app on a rooted phone
                    and replace the JS/CSS asset files with nasty things?
                */
                InputStream input;
                try {
                    input = getAssets().open(file);
                    byte[] buffer = new byte[input.available()];
                    input.read(buffer);
                    input.close();

                    String element = "script";
                    String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
                    if(mimeType == "text/javascript") {
                        element = "script";
                    } else if(mimeType == "text/css") {
                        element = "style";
                    }
                    String url = "javascript:(function() {" +
                            "var parent = document.getElementsByTagName('head').item(0);" +
                            "var asset = document.createElement('" + element + "');" +
                            "asset.type = '" + mimeType + "';" +
                            "asset.innerHTML = window.atob('" + encoded + "');" +
                            "parent.appendChild(asset);" +
                            "})()";
                    view.loadUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                ProgressBar spinner = (ProgressBar) findViewById(R.id.progress_login);
                spinner.setVisibility(ProgressBar.VISIBLE);
                WebView webView = (WebView) findViewById(R.id.webView);
                webView.setVisibility(WebView.INVISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                CookieSyncManager.getInstance().sync();
                activity.setTitle(view.getTitle());
                ProgressBar spinner = (ProgressBar) findViewById(R.id.progress_login);
                spinner.setVisibility(ProgressBar.GONE);
                injectAsset(view, "override.css", "text/css");
                injectAsset(view, "extra.js", "text/javascript");
                WebView webView = (WebView) findViewById(R.id.webView);
                webView.setVisibility(WebView.VISIBLE);
            }
        });

        // If Welsh ever becomes supported as an Android Locale, provide option
        // to load the cy version of the login page.
        String lang = Utils.getCurrentLanguage(getApplicationContext());
        if(lang != "cy") {
            lang = "en";
        }

        webView.loadUrl("https://studentrecord.aber.ac.uk/" + lang + "/login.php");
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