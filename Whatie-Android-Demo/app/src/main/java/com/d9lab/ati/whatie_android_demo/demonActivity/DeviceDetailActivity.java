package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatie_android_demo.demonActivity.widget.CountdownTextView;
import com.d9lab.ati.whatiesdk.bean.BaseListResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.ClockVo;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.ClockCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttCancelCdSuccessEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveOffEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveOnEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveStatusEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveUnbindEvent;
import com.d9lab.ati.whatiesdk.event.MqttSetCdSuccessEvent;
import com.d9lab.ati.whatiesdk.event.TcpReceiveInstEvent;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.LogUtil;
import com.d9lab.ati.whatiesdk.util.QRCodeCons;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/4/25.
 */

public class DeviceDetailActivity extends BaseActivity {
    private static final String TAG = "DeviceDetailActivity";
    @Bind(R.id.iv_title_left)
    ImageView ivTitleLeft;
    //    @Bind(R.id.tv_title_left)
//    TextView tvTitleLeft;
    @Bind(R.id.ll_title_left)
    LinearLayout llTitleLeft;
    @Bind(R.id.iv_title_right)
    ImageView ivTitleRight;
    @Bind(R.id.tv_title_right)
    TextView tvTitleRight;
    @Bind(R.id.ll_title_right)
    LinearLayout llTitleRight;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_device_control_name)
    TextView tvDeviceControlName;
    @Bind(R.id.tv_device_control_state)
    TextView tvDeviceControlState;
    @Bind(R.id.ctv_device_control_countdown_time)
    CountdownTextView ctvDeviceControlCountdownTime;
    @Bind(R.id.iv_device_detail_switch)
    ImageView ivDeviceDetailSwitch;
    @Bind(R.id.iv_device_detail_countdown)
    ImageView ivDeviceDetailCountdown;
    @Bind(R.id.iv_device_detail_alarm)
    ImageView ivDeviceDetailAlarm;
    @Bind(R.id.rl_device_detail_bg)
    LinearLayout rlDeviceDetailBg;
    @Bind(R.id.ll_device_detail_bgC)
    LinearLayout llDeviceDetailBgC;
    @Bind(R.id.rl_device_detail_switch)
    RelativeLayout rlDeviceDetailSwitch;
    @Bind(R.id.tv_device_detail_switch)
    TextView tvDeviceDetailSwitch;

    private DeviceVo deviceVo;
    private boolean state;
    private ClockVo countdownClock;
    private int countdownClockId = -1;

    private TimePickerView countdownTimeView;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            rlDeviceDetailSwitch.setEnabled(true);
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.act_device_detail_e;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(mContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }

    @Override
    protected void initViews() {
        tvTitle.setText(getString(R.string.device_control_title));
        deviceVo = (DeviceVo) getIntent().getSerializableExtra(Code.DEVICE);
        tvDeviceControlName.setText(deviceVo.getDevice().getProduct().getName());
        state = Boolean.parseBoolean(deviceVo.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY));
        toggleState(state);
        getCountdownTime();

        if (deviceVo.isHost()){
            ivTitleRight.setImageResource(R.drawable.ic_device_setting);
        }else {
            ivTitleRight.setImageResource(R.drawable.ic_device_delete);
        }

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
                LogUtil.log(TAG, countdownTime+"");
                if(countdownTime > 0){
                    setCountdownTime(deviceVo.getDevice().getDevId(), !state, countdownTime);
                }
            }
        }).setType(new boolean[]{false, false, false, true, true, false})
                .setCancelText(getString(R.string.dialog_cancel))
                .setSubmitText(getString(R.string.dialog_ok))
                .isCenterLabel(false)
                .isCyclic(true)
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("","","","hours","minutes","")
                .build();

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    private void setCountdownTime(String devId, boolean state, int duration){
        EHomeInterface.getINSTANCE().addTimerClockWithDeviceModel(devId, state, duration);
    }

    private void cancelCountdownTime(String devId, boolean state){
        EHomeInterface.getINSTANCE().removeTimerClockWithDeviceModel(devId);
    }

    private void getCountdownTime(){
        EHomeInterface.getINSTANCE().getTimerClockWithDeviceModel(mContext, deviceVo.getDevice().getId(), new ClockCallback() {
            @Override
            public void onSuccess(Response<BaseListResponse<ClockVo>> response) {
                if(response.body().isSuccess() && response.body().getList()!=null && (!response.body().getList().isEmpty())){
                    countdownClock = response.body().getList().get(0);
                    countdownClockId = countdownClock.getDeviceClock().getClockId();
                    ctvDeviceControlCountdownTime.setVisibility(View.VISIBLE);
                    ctvDeviceControlCountdownTime.setCountdownConfigAndStart(countdownClock.getDurationTime()*1000, 1000, Boolean.valueOf(countdownClock.getDeviceClock().getDps()));
                }
            }

            @Override
            public void onError(Response<BaseListResponse<ClockVo>> response) {
                super.onError(response);
            }
        });

    }

    @OnClick({R.id.ll_title_left, R.id.ll_title_right, R.id.rl_device_detail_switch, R.id.iv_device_detail_countdown, R.id.iv_device_detail_alarm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_title_left:
                finish();
                break;
            case R.id.rl_device_detail_switch:
                rlDeviceDetailSwitch.setEnabled(false);
                mHandler.postDelayed(mRunnable, 1500);
                EHomeInterface.getINSTANCE().updateOutletsStatus(deviceVo.getDevice().getDevId(), !state);
                if(countdownClockId != -1 && EHome.getInstance().isMqttOn()) cancelCountdownTime(deviceVo.getDevice().getDevId(), state);
                break;
            case R.id.iv_device_detail_countdown:
                if(EHome.getInstance().isMqttOn()){
                    countdownTimeView.show();
                }else {
                    Toast.makeText(mContext, getString(R.string.countdown_without_network), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_device_detail_alarm:
                if(EHome.getInstance().isMqttOn()){
                    Intent alarmIntent = new Intent(this, TimerActivity.class);
                    alarmIntent.putExtra(Code.DEVICE, deviceVo);
                    startActivity(alarmIntent);
                }else {
                    Toast.makeText(mContext, "Can not set alarms at local state.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void toggleState(boolean state){
        if(state){
            llDeviceDetailBgC.setBackgroundResource(R.color.main_theme_blue);
            rlDeviceDetailSwitch.setBackgroundResource(R.color.device_detail_black);
            tvDeviceControlState.setText(getString(R.string.device_detail_on));
            tvDeviceDetailSwitch.setText("Close");

        }else {
            llDeviceDetailBgC.setBackgroundResource(R.color.device_detail_black);
            rlDeviceDetailSwitch.setBackgroundResource(R.color.main_theme_blue);
            tvDeviceControlState.setText(getString(R.string.device_detail_off));
            tvDeviceDetailSwitch.setText("Open");

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 3, sticky = true)
    public void onEventMainThread(MqttReceiveEvent event) {
        switch (event.getProtocol()){
            case Code.DATA_SUBMIT:
                if(deviceVo.getDevice().getDevId().equals(event.getData().getDevId()) && (state != (boolean)event.getData().getDps().get("1"))){
                    rlDeviceDetailSwitch.setEnabled(true);
                    mHandler.removeCallbacks(mRunnable);
                    state = (boolean)event.getData().getDps().get("1");
                    toggleState(state);
                }
                break;
            case Code.UNBIND_CONFIRM:
            case Code.RESET:
            case Code.OUT_LINE:
                break;
            case Code.COUNT_DOWN_FEEDBACK:
                if(deviceVo.getDevice().getDevId().equals(event.getData().getDevId())){
                    switch (event.getData().getExecutionType()){
                        case Code.MQTT_COUNT_DOWN_SUCCESS:
                            countdownClockId = event.getData().getClockId();
                            ctvDeviceControlCountdownTime.setVisibility(View.VISIBLE);
                            ctvDeviceControlCountdownTime.setCountdownConfigAndStart(event.getData().getDuration()*1000, 1000, state);
                            break;
                        case Code.MQTT_COUNT_DOWN_CANCEL_SUCCESS:
                            ctvDeviceControlCountdownTime.cancelCountdown();
                            ctvDeviceControlCountdownTime.setVisibility(View.INVISIBLE);
                            countdownClock = null;
                            countdownClockId = -1;
                            break;
                    }
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 4, sticky = true)
    public void onEventMainThread(MqttSetCdSuccessEvent event) {
        if (event.getDevId().equals(deviceVo.getDevice().getDevId())) {
            countdownClock = new ClockVo();
            ctvDeviceControlCountdownTime.setVisibility(View.VISIBLE);
            ctvDeviceControlCountdownTime.setCountdownConfigAndStart(event.getDuration()*1000, 1000, state);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 4, sticky = true)
    public void onEventMainThread(MqttCancelCdSuccessEvent event) {
        if (event.getDevId().equals(deviceVo.getDevice().getDevId())) {
            ctvDeviceControlCountdownTime.cancelCountdown();
            ctvDeviceControlCountdownTime.setVisibility(View.INVISIBLE);
            countdownClock = null;
            countdownClockId = -1;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2, sticky = true)
    public void onEventMainThread(TcpReceiveInstEvent event) {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 800);
        String devId = event.getDeviceTcp().getDevId();
        boolean state = (boolean)event.getDeviceTcp().getDps().get("1");
        if(devId.equals(deviceVo.getDevice().getDevId()) && (state != this.state)){
            this.state = state;
            toggleState(state);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveOnEvent event) {
        deviceVo.getFunctionValuesMap().put(Code.FUNCTION_MAP_KEY, String.valueOf(true));
        deviceVo.getDevice().setStatus(Code.DEVICE_STATUS_NORMAL);
        state = true;
        toggleState(true);
        rlDeviceDetailSwitch.setEnabled(true);
        mHandler.removeCallbacks(mRunnable);
        Log.d(TAG, "onEventMainThread: MqttReceiveOnEvent" + event.getIndex());
//        mAdapter.notifyDataSetChanged();
        removeCountDown();
    }

    private void removeCountDown() {
        EHomeInterface.getINSTANCE().getTimerClockWithDeviceModel(mContext, deviceVo.getDevice().getId(), new ClockCallback() {
            @Override
            public void onSuccess(Response<BaseListResponse<ClockVo>> response) {
                if (response.body().isSuccess()){
                    if (response.body().getList()!=null){
                        cancelCountdownTime(deviceVo.getDevice().getDevId(),state);
                    }
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveOffEvent event) {
        deviceVo.getFunctionValuesMap().put(Code.FUNCTION_MAP_KEY, String.valueOf(false));
        deviceVo.getDevice().setStatus(Code.DEVICE_STATUS_NORMAL);
        state = false;
        toggleState(false);
        rlDeviceDetailSwitch.setEnabled(true);
        mHandler.removeCallbacks(mRunnable);
        Log.d(TAG, "onEventMainThread: MqttReceiveOffEvent" + event.getIndex());
        removeCountDown();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveStatusEvent event) {
        deviceVo.getDevice().setStatus(event.getStatus());
//        mAdapter.notifyDataSetChanged();
        Log.d(TAG, "onEventMainThread: MqttReceiveStatusEvent" + event.getIndex());
    }

}
