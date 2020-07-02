
package com.dqhc.iseesub;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.net.Uri;
import android.os.Build;

import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dqhc.iseesub.com.dqhc.iseesub.baseActivity.BasePermissionActivity;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.ApiHelper;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.Constant;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.DownloadService;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.JsCallAndroid;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.MyApi;
import com.dqhc.iseesub.com.dqhc.iseesub.tools.ToastUtils;

import com.google.gson.Gson;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.WebCreator;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BasePermissionActivity {

    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    private String versionId;
    private ProgressBar progressBar;
    private AlertDialog dialog;
    private ServiceConnection serviceConnection;
    private String appUrl;
    private TextView tv_start;
    private int screenWidth;
    private WebView webView;
    private JsCallAndroid jc;
    private DisplayMetrics displayMetrics;
    private List<String> deniedPermissions;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private double latitude = 0.00;
    private double longitude = 0.00;
    private int playerACode = 10;
    boolean isDoubleClick;
    CheckForDoublePress mPendingCheckForDoublePress = null;
    Handler mHandler = new Handler();


    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void beforeWebInit() {
//        NetWorkStatus(this); //判断是否有网络链接
        initMapLocation();//初始化经纬度定位信息
        ImageView imageView = (ImageView) findViewById(R.id.splash_img);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        setTheme(R.style.AppTheme_Launcher);
        jc = new JsCallAndroid(this);
//        Toast.makeText(this, AppUtils.getMac(this) + "-------------getMac--------------", Toast.LENGTH_LONG).show();
        isTimego();
        agentWeb = AgentWeb.with(MainActivity.this)
                .setAgentWebParent(relativeLayout, new RelativeLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setMainFrameErrorView(R.layout.error_page, -1)
                .interceptUnkownUrl()
                .setWebChromeClient(null)
                .createAgentWeb()
                .ready()
                .go(Constant.WEBURL);

        WebCreator webCreator = agentWeb.getWebCreator();
        webView = webCreator.getWebView();
        webView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.transparent));
        //注入对象
        agentWeb.getJsInterfaceHolder().addJavaObject("android", new JsCallAndroid(MainActivity.this, webView
        ));
//        webView.setBackgroundResource(R.color.black);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebContentsDebuggingEnabled(true);  // 设置debug模式
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        webView.setOnLongClickListener(v -> true);
        IAgentWebSettings agentWebSettings = agentWeb.getAgentWebSettings();
        WebSettings webSettings = agentWebSettings.getWebSettings();
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString(userAgentString + " ZTGH/Cinderella");
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                Log.e("err", "onReceivedError:" + error.getDescription() + " url:" + request.getUrl());
//            }
//        });


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                relativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                imageView.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animation);
        Thread isCanUseThread = new Thread(new Processor());
        isCanUseThread.start();

    }


    class Processor implements Runnable {
        @Override
        public void run() {
            while (true) {
                isTimego();
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @Override
    protected void afterWebInit() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        deniedPermissions = findDeniedPermissions(needPermissions);
        //请求权限//
        if (!deniedPermissions.isEmpty()) {
            showInstructionDialog();
        } else {
            updateApk(false);
        }
    }

    @Override
    protected boolean initWeb() {
        return false;
    }

    @Override
    protected String getWebLink(Gson gson) {
        return null;
    }

    /**
     * 权限说明的弹出框
     */
    private void showInstructionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.permission_layout, null);
        TextView tv_start = view.findViewById(R.id.tv_start);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        Objects.requireNonNull(window).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        Objects.requireNonNull(window).setLayout(3 * screenWidth / 4, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_start.setOnClickListener(v -> {
            permissionsRequest();
            alertDialog.dismiss();
        });
    }

    /**
     * 获取新版本
     */
    public void updateApk(boolean toast) {
        MyApi api = ApiHelper.getInstance().buildRetrofit(Constant.BASEURL).createService(MyApi.class);
        Call<ResponseBody> call = api.getUptodateAppVersion("", "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() == null) return;
                    String string = response.body().string();
                    JSONObject object = new JSONObject(string);
                    appUrl = object.getString("appUrl");
//                    String remark = object.getString("remark");
                    String version = object.getString("appCode");
                    versionId = object.getString("versionId");
                    float appVersionCode = getAppVersionCode(MainActivity.this);
                    if (TextUtils.isEmpty(version) || TextUtils.isEmpty(appUrl))
                        return;
                    float onlineVersion = Float.parseFloat(version);
                    if (onlineVersion > appVersionCode) {
                        showUpdateDialog();
                    } else {
                        if (toast) {
                            Toast.makeText(MainActivity.this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });

    }

    public void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        tv_start = view.findViewById(R.id.tv_start);
        progressBar = view.findViewById(R.id.update_progress);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        Window window = dialog.getWindow();
        Objects.requireNonNull(window).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Objects.requireNonNull(window).setLayout(3 * screenWidth / 4, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_start.setOnClickListener(v -> {
            if (tv_start.getText().toString().equals("立即更新")) {
                tv_start.setText("正在下载");
                progressBar.setVisibility(View.VISIBLE);
                downloadApk(appUrl);
            }
        });
    }

    /**
     * 下载服务
     */
    private void downloadApk(final String versionURL) {
        if (null == serviceConnection) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
                    DownloadService mService = binder.getService();
                    mService.downApk(versionURL, "颠覆.apk", new DownloadService.DownloadCallback() {
                        @Override
                        public void onPrepare() {

                        }

                        @Override
                        public void onProgress(int progress) {
                            if (null != progressBar) {
                                progressBar.setVisibility(View.VISIBLE);
                                tv_start.setText(MessageFormat.format("正在下载：{0}%", progress));
                                progressBar.setProgress(progress);
                            }
                        }

                        @Override
                        public void onComplete(File file) {
                            if (null != dialog) {
                                dialog.dismiss();
                            }
                            updateAppCount();
                            installAPK(file, MainActivity.this);
                        }

                        @Override
                        public void onFail(String msg) {
                            ToastUtils.showSingleToast(msg);
                        }
                    });
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    ToastUtils.showSingleToast("服务已断开，更新失败");
                }
            };
        } else {
            serviceConnection = null;
            downloadApk(versionURL);
        }
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        bindService(intent, this.serviceConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * 安装apk
     */
    private void installAPK(File file, Context context) {
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //在服务中开启actiivity必须设置flag
            Uri contentUri = FileProvider.getUriForFile(context, "com.dqhc.iseesub", file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Uri uri = Uri.parse("file://" + file.toString());
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 通知下载完成
     */
    private void updateAppCount() {
        MyApi api = ApiHelper.getInstance().buildRetrofit(Constant.BASEURL).createService(MyApi.class);
        Call<ResponseBody> call = api.updateAppCount(versionId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }

    /**
     * 获取当前版本号
     */
    private float getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    @Override
    protected void permissionAllowed() {
        updateApk(false);
    }

    @Override
    protected void permissionDenied() {
        showInstructionDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:    //返回键
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        agentWeb.getJsAccessEntrace().quickCallJs("goBack");
//                        System.out.println("goBack*().....................");
//                    }
//                });
                if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
//                return true;   //这里由于break会退出，所以我们自己要处理掉 不返回上一
            case KeyEvent.KEYCODE_MENU:    // 菜单键
                checkForDoubleClick(event);
                return true;   // 这里由于break会退出，所以我们自己要处理掉 不返回上一
        }

        return super.onKeyDown(keyCode, event);
    }

    private void checkForDoubleClick(KeyEvent event) {


        if (!isDoubleClick) {
            isDoubleClick = true;
            if (mPendingCheckForDoublePress == null) {
                mPendingCheckForDoublePress = new CheckForDoublePress();
            }
            mPendingCheckForDoublePress.setKeycode(event.getKeyCode());
            mHandler.postDelayed(mPendingCheckForDoublePress, 500);
        } else {
            // 500ms内两次单击，触发双击
            isDoubleClick = false;
            webView.loadUrl(Constant.WEBURL);

        }
    }

    class CheckForDoublePress implements Runnable {

        int currentKeycode = 0;

        public void run() {
            if (isDoubleClick) {

            }
            isDoubleClick = false;
        }

        public void setKeycode(int keycode) {
            currentKeycode = keycode;
        }
    }

    /**
     * 这里去掉了  showInstructionDialog
     */
    @Override
    protected void onResume() {
        super.onResume();
//        webView.reload();
        isTimego();
        //请求权限
        if (!deniedPermissions.isEmpty()) {
            updateApk(false);
        }
    }


    /**
     * 定时判断盒子的使用权限
     */
    public void isTimego() {
        MyApi api = ApiHelper.getInstance().buildRetrofit(Constant.BASEURL2).createService(MyApi.class);
        Call<ResponseBody> call = api.getLoadCampusInfo(jc.getSerialNumber(), "latitude=" + latitude + ",longitude" + longitude, jc.getBoxIP());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() == null) return;
                    String string = response.body().string();
                    JSONObject object = new JSONObject(string);
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (TextUtils.isEmpty(message) || TextUtils.isEmpty(message))
                        return;
                    if (!success) {
//                        ToastUtils.showSingleToast(message);
                        showAlert(message);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });

    }


    private void showAlert(String message) {
        //添加"Yes"按钮
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("警告！")
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    Toast.makeText(MainActivity.this, "这是确定按钮", Toast.LENGTH_SHORT).show();
                    isTimego();
                })
                .create();
        alertDialog2.show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }


//    private boolean NetWorkStatus(Context context) {
//        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        // cwjManager.getActiveNetworkInfo();
//        boolean netSataus = true;
//        if (cwjManager.getActiveNetworkInfo() != null) {
//            netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
//            Toast.makeText(context, "网络已经打开", Toast.LENGTH_LONG).show();
//        } else {
//
//            AlertDialog.Builder b = new AlertDialog.Builder(context).setTitle("没有可用的网络")
//                    .setMessage("是否对网络进行设置？");
//            b.setPositiveButton("是", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    Intent mIntent = new Intent("/");
//                    ComponentName comp = new ComponentName(
//                            "com.android.settings",
//                            "com.android.settings.WirelessSettings");
//                    mIntent.setComponent(comp);
//                    mIntent.setAction("android.intent.action.VIEW");
//                    // 如果在设置完成后需要再次进行操作，可以重写操作代码，在这里不再重写
//                    startActivityForResult(mIntent, 0);
//                }
//            }).show();
//        }
//
//        return netSataus;
//    }

    private void initMapLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认GCJ02
//GCJ02：国测局坐标；
//BD09ll：百度经纬度坐标；
//BD09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(100000);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
//可选，V7.2版本新增能力
//如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        mLocationClient.start();
        //mLocationClient为第二步初始化过的LocationClient对象
//调用LocationClient的start()方法，便可发起定位请求
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            latitude = location.getLatitude();    //获取纬度信息
            longitude = location.getLongitude();    //获取经度信息

            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

//            Toast.makeText(MainActivity.this, latitude + "......................" + longitude, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == playerACode) && (resultCode == RESULT_OK) && (null != data)) {
            boolean isPlayed = data.getBooleanExtra("isPlayed", false);
            if (isPlayed) {
                agentWeb.getJsAccessEntrace().quickCallJs("vidFinished");
            }

        }
    }
}


