package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.bean.LightDetail;
import com.d9lab.ati.whatiesdk.bean.MQTTData;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttReceiveEvent;
import com.d9lab.ati.whatiesdk.event.MqttSendEvent;
import com.d9lab.ati.whatiesdk.util.Code;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by 神火 on 2018/6/15.
 */

public class LightRGBFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.btn_set_rgbl)
    Button btnSetRgbl;
    @Bind(R.id.sb_red)
    SeekBar sbRed;
    @Bind(R.id.sb_green)
    SeekBar sbGreen;
    @Bind(R.id.sb_blue)
    SeekBar sbBlue;
    @Bind(R.id.sb_light)
    SeekBar sbLight;
    @Bind(R.id.tv_red_value)
    TextView tvRedValue;
    @Bind(R.id.tv_green_value)
    TextView tvGreenValue;
    @Bind(R.id.tv_blue_value)
    TextView tvBlueValue;
    @Bind(R.id.tv_light_value)
    TextView tvLightValue;

    private static final String ARGS_TAG = "lightDetail";

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            btnSetRgbl.setClickable(true);
        }
    };
    private DeviceVo mDeviceVo;

    public static LightRGBFragment newInstance() {
        return new LightRGBFragment();
    }

    public static LightRGBFragment newInstance(LightDetail detail) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_TAG, detail);
        LightRGBFragment fragment = new LightRGBFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.frg_light_rgbl;
    }


    @Override
    protected void initViews() {
        sbRed.setOnSeekBarChangeListener(this);
        sbGreen.setOnSeekBarChangeListener(this);
        sbBlue.setOnSeekBarChangeListener(this);
        sbLight.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void initEvents() {
        btnSetRgbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRGBLInst(mDeviceVo.getDevice().getDevId(), sbRed.getProgress(),
                        sbGreen.getProgress(), sbBlue.getProgress(), sbLight.getProgress() + 1);
            }
        });
    }

    @Override
    protected void initDatas() {
        mDeviceVo = LightDetailActivity.getItem();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            LightDetail detail = (LightDetail) getArguments().getSerializable(ARGS_TAG);
            if (detail != null) {
                setDetails(detail.getRgb(), detail.getL());
            }
        }
    }

    private void sendRGBLInst(String devId, int rValue, int gValue, int bValue, int lValue) {
        int[] rgb = {rValue,gValue,bValue};
        EHomeInterface.getINSTANCE().updateLightRGBL(devId, rgb, String.valueOf(lValue));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_red:
                tvRedValue.setText(String.valueOf(i));
                break;
            case R.id.sb_green:
                tvGreenValue.setText(String.valueOf(i));
                break;
            case R.id.sb_blue:
                tvBlueValue.setText(String.valueOf(i));
                break;
            case R.id.sb_light:
                tvLightValue.setText(String.valueOf(i + 1));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 3, sticky = true)
    public void onEventMainThread(MqttReceiveEvent event) {

    }

    public void setDetails(int[] rgb, int l) {
        sbRed.setProgress(rgb[0]);
        sbGreen.setProgress(rgb[1]);
        sbBlue.setProgress(rgb[2]);
        sbLight.setProgress(l - 1);
    }

}
