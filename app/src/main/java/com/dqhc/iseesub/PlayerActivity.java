package com.dqhc.iseesub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerActivity extends AppCompatActivity {
    private AliPlayer aliPlayer;
    private int mPlayerState = IPlayer.idle;
    private SurfaceView surfaceView;
    private String videoUrl;
    private SeekBar seekBar;
    private TextView tvTime;
    private ImageView ivPlay;
    private TextView tvMessage;
    private int currentPosition;
    private int duration;
    private RelativeLayout controlView;
    private String videoTitle;
    private String username;
    private TextView tvTitle;
    private String campusName;
    private String tel;
    private boolean updateSeek = true;
//    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        surfaceView = findViewById(R.id.surfaceView);
        seekBar = findViewById(R.id.seekBar);
        tvTime = findViewById(R.id.tvTime);
        ivPlay = findViewById(R.id.ivPlay);
        tvTitle = findViewById(R.id.tvTitle);
        tvMessage = findViewById(R.id.tvMessage);
        controlView = findViewById(R.id.controlView);
        ivPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
        initVideoInfo();
        initVideoPlayer();
        initPlayerAction();
    }

    private void initVideoInfo() {
        Intent intent = getIntent();
        String params = intent.getStringExtra("params");
        if (null != params) {
            try {
                JSONObject jsonObject = new JSONObject(params);
                JSONObject video = jsonObject.getJSONObject("video");
                videoUrl = video.getString("videoUrl");
                videoTitle = video.getString("title");
                JSONObject userInformation = jsonObject.getJSONObject("userInformation");
                tel = userInformation.getString("tel");
                username = userInformation.getString("username");
                JSONObject conmenDate = jsonObject.getJSONObject("conmenDate");
                campusName = conmenDate.getString("campusName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String marquee = campusName + "——" + username + "——" + tel;
        initTextMarquee(marquee);
    }


    private void initTextMarquee(String message) {
        tvTitle.setText(videoTitle);
        tvMessage.setText(message);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_slide_in);
        animation.setRepeatCount(Animation.INFINITE);
        tvMessage.setAnimation(animation);
        animation.start();
    }

    private void initVideoPlayer() {
        aliPlayer = AliPlayerFactory.createAliPlayer(this);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (null != aliPlayer) {
                    aliPlayer.setDisplay(surfaceHolder);
                    aliPlayer.redraw();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        UrlSource urlSource = new UrlSource();
        urlSource.setUri(videoUrl);
        aliPlayer.getConfig().mMaxDelayTime = 100;
        aliPlayer.setAutoPlay(true);
        aliPlayer.setDataSource(urlSource);
        aliPlayer.prepare();
        aliPlayer.start();
    }

    private void initPlayerAction() {
        aliPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                releaseAndFinish(true);
            }
        });

        aliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                Toast.makeText(PlayerActivity.this, "播放数据错误", Toast.LENGTH_SHORT).show();
                releaseAndFinish(false);
            }
        });

        aliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                duration = (int) aliPlayer.getDuration();
                seekBar.setMax(duration);
                ivPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
            }
        });


        aliPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int i) {
                mPlayerState = i;
            }
        });

        aliPlayer.setOnSeekCompleteListener(new IPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
//                aliPlayer.pause();
            }
        });

        aliPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
                if (updateSeek) {
                    sourceVideoPlayerInfo(infoBean);
                }
            }
        });
    }

    /**
     * 原视频Info //王琦
     */
    private void sourceVideoPlayerInfo(InfoBean infoBean) {
        if (infoBean.getCode() == InfoCode.AutoPlayStart) {
            //自动播放开始,需要设置播放状态

        } else if (infoBean.getCode() == InfoCode.BufferedPosition) {
            //更新bufferedPosition
//            mVideoBufferedPosition = infoBean.getExtraValue();
//            mControlView.setVideoBufferPosition((int) mVideoBufferedPosition);
        } else if (infoBean.getCode() == InfoCode.CurrentPosition) {
            //更新currentPosition
            currentPosition = (int) infoBean.getExtraValue();
//            Log.e("WQ", "position====" + currentPosition);
            seekBar.setProgress(currentPosition);
            String time = formatTime(infoBean.getExtraValue()) + "/" + formatTime(duration);
            tvTime.setText(time);
        } else if (infoBean.getCode() == InfoCode.AutoPlayStart) {
            //自动播放开始,需要设置播放状态

        } else {

        }
    }


    /**
     * 监听遥控器按键
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:     //确定键enter
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (action == KeyEvent.ACTION_DOWN) {
                    pauseAndRestart();
                }
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
                if (action == KeyEvent.ACTION_DOWN) {
                    updateSeek = false;
                    currentPosition = currentPosition - 1000;
                    seekBar.setProgress(currentPosition);
                }
                if (action == KeyEvent.ACTION_UP) {
                    updateSeek = true;
                    aliPlayer.seekTo(Math.max(currentPosition, 0));
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
                if (currentPosition > 0) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        updateSeek = false;
                        currentPosition = currentPosition + 1000;
                        seekBar.setProgress(currentPosition);
                    } else if (action == KeyEvent.ACTION_UP) {
                        updateSeek = true;
                        aliPlayer.seekTo(Math.min(currentPosition, duration));
                    }

                }

                break;
            case KeyEvent.KEYCODE_BACK:    //返回键
                if (action == KeyEvent.ACTION_DOWN) {
                    releaseAndFinish(true);
                    return true;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层
                }
        }
        return super.dispatchKeyEvent(event);
    }

    private void pauseAndRestart() {
        if (null != aliPlayer) {
            if (mPlayerState == IPlayer.started || mPlayerState == IPlayer.prepared) {
                aliPlayer.pause();
                ivPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
            } else if (mPlayerState == IPlayer.paused) {
                aliPlayer.start();
                ivPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
            }
        }
    }

    /**
     * 释放并回退上一页
     */
    private void releaseAndFinish(boolean played) {
        if (null != aliPlayer) {
            aliPlayer.stop();
            aliPlayer.release();
            aliPlayer = null;
        }
        Intent intent = new Intent();
        intent.putExtra("isPlayed", played);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * formatt时间
     */
    private String formatTime(long time) {
        long min = time / 1000 / 60;
        long sec = time / 1000 % 60;
        return formatNum(min) + ":" + formatNum(sec);
    }

    private String formatNum(long num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return String.valueOf(num);
        }
    }

    /**
     * 播放图标 时间  进度条的隐现
     */
    private void setPlayIconVisible(boolean visible) {
        if (visible) {
            controlView.setVisibility(View.VISIBLE);
        } else {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha_out);
            controlView.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    controlView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }


}
