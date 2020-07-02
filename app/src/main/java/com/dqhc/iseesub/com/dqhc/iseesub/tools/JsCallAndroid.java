package com.dqhc.iseesub.com.dqhc.iseesub.tools;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.dqhc.iseesub.PlayerActivity;
import com.dqhc.iseesub.R;
import com.dqhc.iseesub.com.dqhc.iseesub.baseActivity.BaseAppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class JsCallAndroid {

    private BaseAppCompatActivity appCompatActivity;
    private Activity activity;
    private String classNo;
    private String serialNumber;
    private String boxIP;
    private String boxMapAddrass;
    private SharedPreferences sp;
    private static WebView webView;
    private int playerACode = 10;
    private long firstTime;

    public JsCallAndroid(Activity activity){
        this.activity = activity;
    }
    public JsCallAndroid(BaseAppCompatActivity activity, WebView webView){
        this.appCompatActivity = activity;
        this.webView = webView;
        sp  = appCompatActivity.getSharedPreferences("iseeSub", Context.MODE_PRIVATE);

        Log.e("ip地址等等信息：","；ip："+getBoxIP()+"；class："+getClassNo()+"；SerialNumber："+getSerialNumber());

    }

    //存储已选教室编号
    @JavascriptInterface
    public void setClassNo(String mClassNo) {
        classNo = mClassNo;

        SharedPreferences.Editor editor = sp.edit();

        editor.putString("classNo", classNo);
        editor.apply();

    }
    @JavascriptInterface
    public String getClassNo() {
        //获取已选教室编号
        if (null== classNo||classNo.equals("")||classNo.isEmpty()){
            sp = appCompatActivity.getSharedPreferences("iseeSub", Context.MODE_PRIVATE);

            classNo = sp.getString("classNo", "-1");

        }
        return classNo;
    }
    @JavascriptInterface
    public String getSerialNumber() {
        //获取盒子的mac编号

        return "ceshi";
    }
    @JavascriptInterface
    public String getBoxIP() {
        //获取盒子的ip值
        boxIP = getIpAddressString();
//        Toast.makeText(appCompatActivity,boxIP+"-----------------ip----------",Toast.LENGTH_LONG);
        return boxIP;
    }
    @JavascriptInterface
    public String getBoxMapAddrass() {
        //获取盒子的经纬度地址

        return boxMapAddrass;
    }

    public void setActivity(BaseAppCompatActivity activity) {
        this.appCompatActivity = activity;
    }


    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }






    @JavascriptInterface
    public void intermodulation(String params) {
        if (appCompatActivity != null) {
            appCompatActivity.setParams(params);
        }
        if (!params.contains("/login")) {
//            Intent intent = new Intent(context, WebActivity.class);
//            intent.putExtra("params", params);
//            intent.putExtra("isJson", true);
//            context.startActivity(intent);
        }
    }

    @JavascriptInterface
    public void toPlay(String params) {
        long secondTime = System.currentTimeMillis();
        if ((secondTime - firstTime) > 300) {
            firstTime = secondTime;
            if (null != params) {
                Intent intent = new Intent(appCompatActivity, PlayerActivity.class);
                intent.putExtra("params", params);
                appCompatActivity.startActivityForResult(intent, playerACode);
            } else {
                ToastUtils.showSingleToast(appCompatActivity.getResources().getString(R.string.noData));
            }
        }

    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path){

        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.NO_CLOSE);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }




}
