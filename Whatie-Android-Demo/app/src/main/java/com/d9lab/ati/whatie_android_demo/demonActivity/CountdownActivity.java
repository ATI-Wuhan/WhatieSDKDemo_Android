package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatiesdk.bean.BaseListResponse;
import com.d9lab.ati.whatiesdk.bean.ClockVo;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.ClockCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttCancelCdSuccessEvent;
import com.d9lab.ati.whatiesdk.event.MqttSetCdSuccessEvent;
import com.d9lab.ati.whatiesdk.util.Code;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/5/15.
 */

public class CountdownActivity extends BaseActivity {
    private static final String TAG = "CountdownActivity";
    @Bind(R.id.tv_countdown)
    TextView tvCountdown;
    @Bind(R.id.bt_cancel_countdown)
    Button btCancelCountdown;
    @Bind(R.id.bt_set_countdown)
    Button btSetCountdown;

    private DeviceVo mDevice;
    private CountDownTimer mCountDown;
    private ClockVo clockVo;
    private TimePickerView countdownTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(mContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
        if (mCountDown != null) mCountDown.cancel();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_countdown;
    }

    @Override
    protected void initViews() {
        setTitle("Countdown");
        mDevice = (DeviceVo) getIntent().getSerializableExtra(Code.DEVICE);
        getCountdown();

        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        selectedDate.set(0,0,0,0,0,0);
        startDate.set(0,0,0,0,0,0);
        endDate.set(0,0,0,23,59,0);
        countdownTimeView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date, View v) {
                int countdownTime = date.getHours()*60*60+date.getMinutes()*60;
                if(countdownTime > 0){
                    setCountdownTime(countdownTime);
                }
            }
        }).setType(new boolean[]{false, false, false, true, true, false})
                .setCancelText(getString(R.string.dialog_cancel))
                .setSubmitText(getString(R.string.dialog_ok))
                .isCenterLabel(false)
                .isCyclic(true)
                .setOutSideCancelable(false)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLabel("","","","H","M","")
                .build();
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    private void getCountdown() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 4, sticky = true)
    public void onEventMainThread(MqttSetCdSuccessEvent event) {
        if (event.getDevId().equals(mDevice.getDevice().getDevId())) {
            clockVo = new ClockVo();
            setCountdownTimer(event.getDuration(), event.isState());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 4, sticky = true)
    public void onEventMainThread(MqttCancelCdSuccessEvent event) {
        if (event.getDevId().equals(mDevice.getDevice().getDevId())) {
            clockVo = null;
            if (mCountDown != null) mCountDown.cancel();
            tvCountdown.setText("No CountDown.");
        }
    }

    private void setCountdownTimer(int duration, boolean willState) {
        final String state = (willState) ? "ON" : "OFF";
        if (mCountDown != null) mCountDown.cancel();
        mCountDown = new CountDownTimer(duration * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = (millisUntilFinished % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;
                if (millisUntilFinished / 1000 / 60 / 60 < 1) {
                    tvCountdown.setText("device will turn" + state + " after " + minutes + ":" + seconds);
                } else {
                    tvCountdown.setText("device will turn" + state + " after " + hours + ":" + minutes + ":" + seconds);
                }
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("device is " + state);
            }
        }.start();
    }





    private void setCountdownTime(int duration){
        boolean willstate = !Boolean.parseBoolean(mDevice.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY));
        Log.d(TAG, "setCountdownTime: " +mDevice.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY) + willstate);

    }
}
