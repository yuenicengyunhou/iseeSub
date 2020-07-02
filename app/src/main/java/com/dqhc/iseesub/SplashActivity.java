package com.dqhc.iseesub;


import android.content.Intent;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dqhc.iseesub.com.dqhc.iseesub.baseActivity.BaseFullScreenActivity;


public class SplashActivity extends BaseFullScreenActivity {



    @Override
    protected void initViewsAndEnvents() {
        ImageView imageView = findViewById(R.id.splash_img);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        setTheme(R.style.AppTheme_Launcher);

//        JsCallAndroid jc = new JsCallAndroid(this);
//        Toast.makeText(this,jc.getSerialNumber()+"-------------serialNumber--------------",Toast.LENGTH_LONG).show();
//        Toast.makeText(this,jc.getBoxIP()+"-------------serialNumber--------------",Toast.LENGTH_LONG).show();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animation);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected boolean initWeb() {
        return false;
    }



}
