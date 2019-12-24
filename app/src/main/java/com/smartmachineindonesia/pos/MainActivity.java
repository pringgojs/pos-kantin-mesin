package com.smartmachineindonesia.pos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView view; //membuat variabel view agar bisa akses method
    String url = "http://app.smartcanteenindonesia.com/";
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadWeb();
            }
        });

        LoadWeb();
    }

    private void LoadWeb() {
        view = (WebView) this.findViewById(R.id.web_view);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new MyBrowser());
        view.loadUrl(url);
        swipe.setRefreshing(true);
        view.setWebViewClient(new WebViewClient(){
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                view.loadUrl("file:///android_asset/error.html");
            }
            public  void  onPageFinished(WebView view, String url){
                //ketika loading selesai, ison loading akan hilang
                swipe.setRefreshing(false);
            }
        });

        view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //loading akan jalan lagi ketika masuk link lain
                // dan akan berhenti saat loading selesai
                if(view.getProgress()== 100){
                    swipe.setRefreshing(false);
                } else {
                    swipe.setRefreshing(true);
                }
            }
        });
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed(){

        if (view.canGoBack()){
            view.goBack();
        }else {
            finish();
        }
    }
}
