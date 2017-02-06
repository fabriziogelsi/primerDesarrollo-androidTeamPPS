package com.solodroid.yourradioappsinglestation.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.solodroid.yourradioappsinglestation.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseWebviewActivity extends AppCompatActivity {

    public static final String URL_TO_RENDER_TAG = "Url_to_render";
    public static  String url = "https://www.google.com.ar";

    @BindView(R.id.webview1)
    WebView web;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        handleExtras();
        loadWebView();
    }


    private void handleExtras() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString(URL_TO_RENDER_TAG);
        }


    }

    private void loadWebView() {
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
