package com.dqhc.iseesub.com.dqhc.iseesub.baseActivity;

import android.annotation.SuppressLint;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.http.SslError;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;


import com.dqhc.iseesub.R;
import com.google.gson.Gson;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.WebCreator;


public abstract class BaseWebActivity extends BaseAppCompatActivity {
//    @BindView(R.id.bar_message)
//    public ImageView barMessageButton;
//    @BindView(R.id.barHead)
//    public RelativeLayout barHead;
//    @BindView(R.id.barTitle)
//    public TextView barTitle;
//    @BindView(R.id.barCommit)
//    public TextView barCommit;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    protected AgentWeb agentWeb;

    protected boolean barCommmitVisible = false;
    private RelativeLayout relativeLayout;
    private WebView webView;

    @Override
    protected void initViewsAndEnvents() {

        Gson gson = new Gson();
        String link = getWebLink(gson);
        beforeWebInit();
        if (initWeb() && (link != null) && (!TextUtils.isEmpty(link))) {
            initAgent(link);
        }
        initToolbar();
        afterWebInit();
    }

    protected abstract void beforeWebInit();

    protected abstract void afterWebInit();

    protected void initToolbar() {
        if (null == toolbar) return;
//        setSupportActionBar(toolbar);
//        ActionBar supportActionBar = getSupportActionBar();
//        if (supportActionBar == null) return;
//        supportActionBar.setHomeButtonEnabled(true);
//        supportActionBar.setDisplayShowTitleEnabled(false);
//        if (!(this instanceof HomeActivity)) {
//            supportActionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    protected abstract boolean initWeb();

    protected abstract String getWebLink(Gson gson);


    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initAgent(String link) {
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(relativeLayout, new RelativeLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setMainFrameErrorView(R.layout.error_page, -1)
                .interceptUnkownUrl()
                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(webChromeClient)
                .createAgentWeb()
                .ready()
                .go(link);
        WebCreator webCreator = agentWeb.getWebCreator();
        webView = webCreator.getWebView();
        webView.setVerticalScrollBarEnabled(false);
        webView.setOnLongClickListener(v -> true);
        IAgentWebSettings agentWebSettings = agentWeb.getAgentWebSettings();
        WebSettings webSettings = agentWebSettings.getWebSettings();
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + " ZTGH/Cinderella");
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        //注入对象
//        JsCallAndroid jsCallAndroid = new JsCallAndroid(this, agentWeb);
//        agentWeb.getJsInterfaceHolder().addJavaObject("android", jsCallAndroid);
//        jsCallAndroid.setActivity(this);
    }

    private WebChromeClient webChromeClient = new WebChromeClient() {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);

//            setDarkStatusBar();
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            mCustomView.setBackgroundColor(getResources().getColor(android.R.color.black));
            relativeLayout.addView(mCustomView);
            mCustomViewCallback = callback;
            webView.setVisibility(View.GONE);
//            toolbar.setVisibility(View.GONE);

        }

        @Override
        public void onHideCustomView() {
            webView.setVisibility(View.VISIBLE);
            if (mCustomView == null) {
                return;
            }
            mCustomView.setVisibility(View.GONE);
            relativeLayout.removeView(mCustomView);
            mCustomViewCallback.onCustomViewHidden();
            mCustomView = null;
//            toolbar.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            super.onHideCustomView();
        }
    };

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    /*设置加载首页的loading效果*/

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view,
                                       SslErrorHandler handler, SslError error) {
            handler.proceed(); // 接受所有证书
        }

        //这个方法会走两次，10%走一次，100%走一次
        public void onPageFinished(WebView view, String url) {
            //因为页面加载完成之前，点击右上角按钮不能与web交互而不能跳转，所以等加载完成之后再显示按钮
            }


    };


}
