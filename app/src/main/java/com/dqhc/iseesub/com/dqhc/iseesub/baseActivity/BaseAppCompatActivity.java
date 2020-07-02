package com.dqhc.iseesub.com.dqhc.iseesub.baseActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toolbar;


import com.dqhc.iseesub.MainActivity;
import com.dqhc.iseesub.R;
import com.dqhc.iseesub.SplashActivity;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.AppUtils;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.BaseAppManager;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.JsCallAndroid;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.StatusBarCompat;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public abstract class BaseAppCompatActivity extends SwipeBackActivity {

    //    protected AlertDialog alertDialog;
    protected Unbinder bind;
    private long firstTime;
    protected BaseAppManager appManager;
    protected Toolbar toolbar;
    protected String nextPageParams = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        appManager = BaseAppManager.getInstance();
//        appManager.addActivity(this);
        setTheme(R.style.AppTheme_Launcher);

//        AppUtils.getLocation(this);

        setSwipeBack();
        setWhiteStatusBar();
        transparent19and20();
        setContentView(getLayoutID());
        bind = ButterKnife.bind(this);
        initViewsAndEnvents();
    }

    protected void setWhiteStatusBar() {
        int statusBarColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statusBarColor = ContextCompat.getColor(this, R.color.white);
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            statusBarColor = ContextCompat.getColor(this, R.color.colorStatusBar);
        }
        StatusBarCompat.compat(this, statusBarColor);
    }

//    protected void setDarkStatusBar() {
//        int statusBarColor;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            statusBarColor = ContextCompat.getColor(this, R.color.black);
//            View decor = getWindow().getDecorView();
//            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        } else {
//            statusBarColor = ContextCompat.getColor(this, R.color.colorStatusBar);
//        }
//        StatusBarCompat.compat(this, statusBarColor);
//    }

    private void setSwipeBack() {
        if (this instanceof MainActivity) {
            setSwipeBackEnable(false);
        } else {
            setSwipeBackEnable(true);
        }
    }

    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        if (this.toolbar != null) {
//            setSupportActionBar(this.toolbar);
            Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    protected void transparent19and20() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    protected abstract int getLayoutID();

    protected abstract void initViewsAndEnvents();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @Override
    protected void onResume(){
        super.onResume();
//        AppUtils.getLocation(this);
    }

    // 启动应用的设置
    protected static final String PACKAGE_URL_SCHEME = "package:";

    // 显示缺失权限提示
    protected void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_help);
        builder.setMessage(R.string.tips_permissions);
        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, (dialog, which) -> {
            setResult(-100);
            finish();
        });
        builder.setPositiveButton(R.string.label_setting, (dialog, which) -> startAppSettings());
        builder.setCancelable(false);
        builder.show();
    }

    protected void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }




    public void setParams(String params) {
        nextPageParams = params;
    }


}
