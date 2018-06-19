package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.bean.LightDetail;
import com.d9lab.ati.whatiesdk.bean.MQTTData;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttSendEvent;
import com.d9lab.ati.whatiesdk.util.Code;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by 神火 on 2018/6/15.
 */

public class LightWhiteFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.btn_set_light)
    Button btnSetLight;
    @Bind(R.id.sb_white_light)
    SeekBar sbWhiteLight;

    private static final String ARGS_TAG = "lightDetail";

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            btnSetLight.setClickable(true);
        }
    };
    private DeviceVo mDeviceVo;

    public static LightWhiteFragment newInstance() {
        return new LightWhiteFragment();
    }

    public static LightWhiteFragment newInstance(LightDetail detail) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_TAG, detail);
        LightWhiteFragment fragment = new LightWhiteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.frg_light_white;
    }

    @Override
    protected void initViews() {
        sbWhiteLight.setOnSeekBarChangeListener(this);
        btnSetLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLightInst(mDeviceVo.getDevice().getDevId(),sbWhiteLight.getProgress() + 1);
            }
        });
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {
        mDeviceVo = LightDetailActivity.getItem();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getArguments() != null){
            LightDetail detail = (LightDetail) getArguments().getSerializable(ARGS_TAG);
            if(detail != null){
                setDetails(detail.getL());
            }
        }
    }

    private void sendLightInst(String devId, int lValue){
        EHomeInterface.getINSTANCE().updateLightBrightness(devId,String.valueOf(lValue));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setDetails(int l){
        sbWhiteLight.setProgress(l - 1);
    }

}
