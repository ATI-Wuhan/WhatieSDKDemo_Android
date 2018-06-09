package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatie_android_demo.application.TimeTypeUtil;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.ClockVo;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttCancelTimerSuccessEvent;
import com.d9lab.ati.whatiesdk.event.MqttSetTimerSuccessEvent;
import com.d9lab.ati.whatiesdk.util.Code;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/5/15.
 */

public class SetTimerActivity extends BaseActivity {
    private static final String TAG = "SetTimerActivity";
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_repeat_day)
    TextView tvRepeatDay;
    @Bind(R.id.tv_device_state)
    TextView tvDeviceState;
    @Bind(R.id.bt_timer_save)
    Button btTimerSave;
    @Bind(R.id.bt_timer_delete)
    Button btTimerDelete;

    private DeviceVo deviceVo;
    private ClockVo clockVo;
    private String timetype = "0000000";
    private boolean deviceState = true;
    private String finishHour = "00";
    private String finishMin = "00";
    private TimePickerView timerView;

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
    protected int getContentViewId() {
        return R.layout.act_set_timer;
    }

    @Override
    protected void initViews() {

        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        selectedDate.set(0,0,0,0,0,0);
        startDate.set(0,0,0,0,0,0);
        endDate.set(0,0,0,23,59,0);
        timerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
                String d = sDateFormat.format(date);
                tvTime.setText(d);
                finishHour = d.substring(0,2);
                finishMin = d.substring(3,5);
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

        if (getIntent().getAction().equals(TimerActivity.FROM_ADD_TIMER)) {
            setTitle("Add Timer");
            deviceVo = (DeviceVo) getIntent().getSerializableExtra(Code.DEVICE);
            btTimerDelete.setVisibility(View.GONE);
        } else if (getIntent().getAction().equals(TimerActivity.FROM_EDIT_TIMER)) {
            setTitle("Edit Timer");
            clockVo = (ClockVo) getIntent().getSerializableExtra(Code.ALARM);
            timetype = clockVo.getDeviceClock().getTimerType();
            String[] dps = clockVo.getDeviceClock().getDps().split("_");
            deviceState = (dps[0].equals(Code.TRUE));
            Calendar time = Calendar.getInstance();
            time.set(0,0,0,Integer.valueOf(clockVo.getFinishTimeApp().substring(0,2)),Integer.valueOf(clockVo.getFinishTimeApp().substring(2,4)),0);
            timerView.setDate(time);
            tvDeviceState.setText(deviceState?"ON":"OFF");
            tvRepeatDay.setText(TimeTypeUtil.getRepeatDay(timetype));
            tvTime.setText(clockVo.getFinishTimeApp().substring(0,2)+":"+clockVo.getFinishTimeApp().substring(2,4));
        }
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }


    @OnClick({R.id.tv_time, R.id.tv_repeat_day, R.id.tv_device_state, R.id.bt_timer_save, R.id.bt_timer_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_time:
                timerView.show();
                break;
            case R.id.tv_repeat_day:
                dialogMoreChoice();
                break;
            case R.id.tv_device_state:
                dialogChoice();
                break;
            case R.id.bt_timer_save:
                btTimerSave.setClickable(false);
                if(getIntent().getAction().equals(TimerActivity.FROM_EDIT_TIMER)){
                    EHomeInterface.getINSTANCE().editTimer(mContext, clockVo.getDeviceClock().getId(), timetype, finishHour, finishMin, deviceState,
                            new BaseCallback() {
                                @Override
                                public void onSuccess(Response<BaseResponse> response) {
                                    btTimerSave.setClickable(true);
                                }

                                @Override
                                public void onError(Response<BaseResponse> response) {
                                    btTimerSave.setClickable(true);
                                    super.onError(response);
                                }
                            });

                }else {
                    EHomeInterface.getINSTANCE().addTimer(mContext, deviceVo.getDevice().getId(), timetype, finishHour, finishMin, deviceState,
                            new BaseCallback() {
                                @Override
                                public void onSuccess(Response<BaseResponse> response) {
                                    btTimerSave.setClickable(true);
                                }

                                @Override
                                public void onError(Response<BaseResponse> response) {
                                    btTimerSave.setClickable(true);
                                    super.onError(response);
                                }
                            });
                }

                break;
            case R.id.bt_timer_delete:
                EHomeInterface.getINSTANCE().removeTimer(mContext, clockVo.getDeviceClock().getId(),
                        new BaseCallback() {
                            @Override
                            public void onSuccess(Response<BaseResponse> response) {

                            }

                            @Override
                            public void onError(Response<BaseResponse> response) {
                                super.onError(response);
                            }
                        });

                break;
        }
    }

    private void dialogChoice() {
        final String items[] = {"ON", "OFF"};
        final int checkedItem = deviceState?0:1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Device State");
        builder.setSingleChoiceItems(items, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvDeviceState.setText(items[which]);
                        deviceState = items[which].equals("ON");
                    }
                });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void dialogMoreChoice() {
        final String items[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        final boolean selected[] = {
                timetype.charAt(6) == '1',
                timetype.charAt(5) == '1',
                timetype.charAt(4) == '1',
                timetype.charAt(3) == '1',
                timetype.charAt(2) == '1',
                timetype.charAt(1) == '1',
                timetype.charAt(0) == '1'
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Repeat Day");
        builder.setMultiChoiceItems(items, selected,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                    }
                });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder tt = new StringBuilder();
                StringBuilder rd = new StringBuilder();

                for (int i = selected.length-1; i >=0 ; i--) {
                    if(selected[i]){
                        tt.append(1);
                    }else {
                        tt.append(0);
                    }
                }
                timetype = tt.toString();
                Log.d(TAG, "onClick: " + timetype);
                if(tt.toString().equals("0000000")){
                    tvRepeatDay.setText("Only Once");
                }else if(tt.toString().equals("1111111")){
                    tvRepeatDay.setText("Everyday");
                }else {
                    for (int i = 0; i <selected.length ; i++) {
                        if(selected[i]){
                            rd.append(items[i] + " ");
                        }
                    }
                    tvRepeatDay.setText(rd.toString());
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttSetTimerSuccessEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttCancelTimerSuccessEvent event) {
        finish();
    }

}
