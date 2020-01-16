package com.smartmachineindonesia.pos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private WebView view; //membuat variabel view agar bisa akses method
    String url = "https://app.smartcanteenindonesia.com";
    SwipeRefreshLayout swipe;

    Button btnBayar;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extras = getIntent().getExtras();


        if (getIntent().hasExtra("url")) {
            url = extras.getString("url");
            String base_url = "https://app.smartcanteenindonesia.com/api/v1/mobile/scan-qr-code?id="+url;
            Log.v("TAG URL", base_url); // Prints scan results

        }

        btnBayar = findViewById(R.id.btnBayar);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadWeb();
            }
        });

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanQRCodeActivity.class);
                startActivity(intent);
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
                view.loadUrl("http://app.smartcanteenindonesia.com/404");
            }
            public  void  onPageFinished(WebView view, String url){
                //ketika loading selesai, ison loading akan hilang
                swipe.setRefreshing(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent;

                if (url.contains("gojek://")) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                    return true;
                }

                return false;
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
