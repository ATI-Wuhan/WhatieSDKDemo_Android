package com.d9lab.ati.whatie_android_demo.demonActivity.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by liz on 2018/4/23.
 */

public class CountdownTextView extends AppCompatTextView {


    private static final String TAG = "CountdownTextView";

    private CountDownTimer mCountDownTimer;
    private long mMillisInFuture;
    private long mCountDownInterval;

    private String mFinishTips;

    private CountdownTextView.CountdownTimerFinishListener mListener;

    public CountdownTextView(Context context) {
        super(context);
    }

    public CountdownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStringsInfo(String finishTips) {
        this.mFinishTips = finishTips;
    }

    public void setCountdownConfigAndStart(final long millisInFuture, long countDownInterval, final boolean state) {

        this.mMillisInFuture = millisInFuture;
        this.mCountDownInterval = countDownInterval;

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        mCountDownTimer = new CountDownTimer(mMillisInFuture, mCountDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = (millisUntilFinished % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;
                if(millisUntilFinished/1000/60/60 < 1) {
                    if(state){
                        if(getResources().getConfiguration().locale.getCountry().equals("CN")){
                            setText( minutes +":" + seconds + "后关闭设备");
                        }else {
                            setText("Device will turn off after " + minutes +":" + seconds);
                        }

                    }else {
                        if(getResources().getConfiguration().locale.getCountry().equals("CN")){
                            setText( minutes +":" + seconds + "后开启设备");
                        }else {
                            setText("Device will turn on after " + minutes + ":" + seconds);
                        }
                    }
                } else {
                    if(state){
                        if(getResources().getConfiguration().locale.getCountry().equals("CN")){
                            setText( hours + ":" + minutes +":" + seconds + "后关闭设备");
                        }else {
                            setText("Device will turn off after " + hours + ":" + minutes + ":" + seconds);
                        }
                    }else {
                        if(getResources().getConfiguration().locale.getCountry().equals("CN")){
                            setText( hours + ":" + minutes +":" + seconds + "后开启设备");
                        }else {
                            setText("Device will turn on after " + hours + ":" + minutes + ":" + seconds);
                        }
                    }
                }
            }

            @Override
            public void onFinish() {

                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                setText("");
                if (mListener != null) {
                    mListener.onCountdownFinish();
                }
            }
        };

        mCountDownTimer.start();
    }

    public void cancelCountdown(){
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        setText("");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setCountDownFinishListener(CountdownTimerFinishListener listener) {
        this.mListener = listener;
    }

    public interface CountdownTimerFinishListener {
        void onCountdownFinish();
    }
}
