package com.d9lab.ati.whatie_android_demo.demonActivity.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.d9lab.ati.whatie_android_demo.R;

/**
 * Created by 神火 on 2018/6/8.
 */

public class TimerTextView extends AppCompatTextView {

    private CountDownTimer mCountDownTimer;
    private long oneMinute = 60000;
    private long oneSecond = 1000;
    private TimerTextView mTextView;


    public TimerTextView(Context context) {
        super(context);
        mTextView = this;
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTextView = this;
    }

    public void countdownStart(){
        mTextView.setClickable(false);
        mCountDownTimer = new CountDownTimer(oneMinute, oneSecond) {
            @Override
            public void onTick(long millisUntilFinished) {
                setText(millisUntilFinished/1000+"s");
                setBackground(getResources().getDrawable(R.drawable.shape_countdown_bg));
            }

            @Override
            public void onFinish() {
                mTextView.setClickable(true);
                setText(R.string.sign_up_send);
                setBackground(getResources().getDrawable(R.drawable.shape_confirm_button));
            }
        };
        mCountDownTimer.start();
    }


}
