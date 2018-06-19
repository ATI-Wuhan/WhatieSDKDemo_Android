package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

public class LightFlowFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String TAG = "LightFlowFragment";
    @Bind(R.id.sb_red)
    SeekBar sbRed;
    @Bind(R.id.sb_green)
    SeekBar sbGreen;
    @Bind(R.id.sb_blue)
    SeekBar sbBlue;
    @Bind(R.id.sb_time)
    SeekBar sbTime;
    @Bind(R.id.sb_light)
    SeekBar sbLight;
    @Bind(R.id.btn_set_color1)
    Button btnSetColor1;
    @Bind(R.id.btn_set_color2)
    Button btnSetColor2;
    @Bind(R.id.btn_set_color3)
    Button btnSetColor3;
    @Bind(R.id.btn_set_color4)
    Button btnSetColor4;
    @Bind(R.id.btn_set_flow)
    Button btnSetFlow;

    private static final String ARGS_TAG = "lightDetail";

    private int[] rgb1 = new int[3];
    private int[] rgb2 = new int[3];
    private int[] rgb3 = new int[3];
    private int[] rgb4 = new int[3];

    private int rValue;
    private int gValue;
    private int bValue;
    private int lValue = 1;
    private int tValue = 1000;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            btnSetFlow.setClickable(true);
        }
    };

    private DeviceVo mDeviceVo;

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.frg_light_flow;
    }

    public static LightFlowFragment newInstance() {
        return new LightFlowFragment();
    }

    public static LightFlowFragment newInstance(LightDetail detail) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_TAG, detail);
        LightFlowFragment fragment = new LightFlowFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initViews() {
        sbRed.setOnSeekBarChangeListener(this);
        sbGreen.setOnSeekBarChangeListener(this);
        sbBlue.setOnSeekBarChangeListener(this);
        sbLight.setOnSeekBarChangeListener(this);
        sbTime.setOnSeekBarChangeListener(this);
        btnSetColor1.setOnClickListener(this);
        btnSetColor2.setOnClickListener(this);
        btnSetColor3.setOnClickListener(this);
        btnSetColor4.setOnClickListener(this);
        btnSetFlow.setOnClickListener(this);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {
        mDeviceVo = LightDetailActivity.getItem();
    }

    private void sendFlowInst(String devId, int[] rgb1, int[]  rgb2, int[]  rgb3, int[]  rgb4, int tValue, int lValue) {

        EHomeInterface.getINSTANCE().setLightFlow(devId, rgb1, rgb2, rgb3, rgb4, String.valueOf(tValue), String.valueOf(lValue));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            LightDetail detail = (LightDetail) getArguments().getSerializable(ARGS_TAG);
            if (detail != null) {
                setDetails(detail.getRgb1(), detail.getRgb2(), detail.getRgb3(), detail.getRgb4(), detail.getL(), detail.getT());
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_red:
                rValue = i;
                break;
            case R.id.sb_green:
                gValue = i;
                break;
            case R.id.sb_blue:
                bValue = i;
                break;
            case R.id.sb_light:
                lValue = i + 1;
                break;
            case R.id.sb_time:
                tValue = (i + 1) * 1000;
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_set_color1:
                rgb1[0] = rValue;
                rgb1[1] = gValue;
                rgb1[2] = bValue;
                btnSetColor1.setBackgroundColor(Color.rgb(rValue, gValue, bValue));
                break;
            case R.id.btn_set_color2:
                rgb2[0] = rValue;
                rgb2[1] = gValue;
                rgb2[2] = bValue;
                btnSetColor2.setBackgroundColor(Color.rgb(rValue, gValue, bValue));
                break;
            case R.id.btn_set_color3:
                rgb3[0] = rValue;
                rgb3[1] = gValue;
                rgb3[2] = bValue;
                btnSetColor3.setBackgroundColor(Color.rgb(rValue, gValue, bValue));
                break;
            case R.id.btn_set_color4:
                rgb4[0] = rValue;
                rgb4[1] = gValue;
                rgb4[2] = bValue;

                btnSetColor4.setBackgroundColor(Color.rgb(rValue, gValue, bValue));
                break;
            case R.id.btn_set_flow:
                sendFlowInst(mDeviceVo.getDevice().getDevId(), rgb1, rgb2, rgb3, rgb4, tValue, lValue);
                break;
        }
    }

    public void setDetails(int[] rgb1, int[] rgb2, int[] rgb3, int[] rgb4, int l, int t) {
        this.rgb1 = rgb1;
        this.rgb2 = rgb2;
        this.rgb3 = rgb3;
        this.rgb4 = rgb4;
        tValue = t;
        lValue = l;
        btnSetColor1.setBackgroundColor(Color.rgb(rgb1[0], rgb1[1], rgb1[2]));
        btnSetColor2.setBackgroundColor(Color.rgb(rgb2[0], rgb2[1], rgb2[2]));
        btnSetColor3.setBackgroundColor(Color.rgb(rgb3[0], rgb3[1], rgb3[2]));
        btnSetColor4.setBackgroundColor(Color.rgb(rgb4[0], rgb4[1], rgb4[2]));
        sbLight.setProgress(l - 1);
        sbTime.setProgress(t / 1000 - 1);
    }
}
