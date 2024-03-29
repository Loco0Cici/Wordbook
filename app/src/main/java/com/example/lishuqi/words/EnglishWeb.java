package com.example.lishuqi.words;

import android.content.ClipboardManager;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class EnglishWeb extends AppCompatActivity {
    WebView webView;
    private CharSequence addedText;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_web);

        webView=(WebView) findViewById(R.id.webId);
        webView.getSettings().setJavaScriptEnabled(true);

        //自适应手机屏幕
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        //手势缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);

        //其他细节操作
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAllowFileAccess(true); //设置可以访问文件
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        //网页接入
        webView.loadUrl("https://www.chinadaily.com.cn/");

        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            // 每一次请求资源，用这个函数来回调
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!url.startsWith("http://")) {
                    return false;
                }
                view.loadUrl(url);
                return true;
            }
            // 重写此方法可以让webview处理https请求
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });
        registerClipEvents();


    }

    private void registerClipEvents() {

        final ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {

                if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {

                    addedText = manager.getPrimaryClip().getItemAt(0).getText();

                    if (addedText != null) {
                        Intent intent=new Intent(EnglishWeb.this,Main2Activity.class);

                        intent.putExtra("get_English",addedText.toString());
                        EnglishWeb.this.startActivity(intent);
                        Toast.makeText(EnglishWeb.this,addedText , Toast.LENGTH_SHORT).show();


                    }
                }
            }


        });
    }

    //将项目添加到操作栏
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//引用xml中的菜单
        return true;
    }

    //处理菜单被选中运行后的事件处理
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //用于决定启动哪个Activity
        switch (id) {
            case R.id.action_news:
                Intent intent2 = new Intent(EnglishWeb.this,Main2Activity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
