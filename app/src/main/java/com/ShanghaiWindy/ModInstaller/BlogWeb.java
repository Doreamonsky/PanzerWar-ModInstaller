package com.ShanghaiWindy.ModInstaller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BlogWeb extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_web);

        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient() {
            // Load opened URL in the application instead of standard browser
            // application
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings websettings = webView.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(false);

        SetUrl("https://blog.waroftanks.cn/");
    }

    public void SetUrl(String url) {
        webView.loadUrl(url);
    }
}
