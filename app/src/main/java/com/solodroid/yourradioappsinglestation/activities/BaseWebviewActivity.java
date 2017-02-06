package com.solodroid.yourradioappsinglestation.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.solodroid.yourradioappsinglestation.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseWebviewActivity extends AppCompatActivity {

    public static final String URL_TO_RENDER_TAG = "Url_to_render";
    public static final String TITLE = "Title";
    public static String url = "https://www.google.com";
    public static String title = "";

    @BindView(R.id.webview1)
    WebView web;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        handleExtras();
        web.setWebViewClient(new WebViewClient());


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        loadWebView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }


    private void handleExtras() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString(URL_TO_RENDER_TAG);
            title = extras.getString(TITLE);
        }


    }

    private void loadWebView() {
        setTitle(title);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setPluginState(WebSettings.PluginState.ON);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.getSettings().setSupportMultipleWindows(false);
        web.getSettings().setSupportZoom(true);
        web.setVerticalScrollBarEnabled(true);
        web.setHorizontalScrollBarEnabled(true);
        web.loadUrl(url);
    }

}
