package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.bean.LightDetail;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttReceiveLightModeEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveLightModePowerEvent;

import com.d9lab.ati.whatiesdk.util.Code;

import com.d9lab.ati.whatiesdk.util.ParseUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/15.
 */

public class LightDetailActivity extends BaseActivity {

    private String TAG = "LightDetailActivity";
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_title_left)
    ImageView ivTitleLeft;
    @Bind(R.id.ll_title_left)
    LinearLayout llTitleLeft;
    @Bind(R.id.iv_title_right)
    ImageView ivTitleRight;
    @Bind(R.id.tv_title_right)
    TextView tvTitleRight;
    @Bind(R.id.ll_title_right)
    LinearLayout llTitleRight;
    @Bind(R.id.iv_light_rgbl)
    ImageView ivLightRGBL;
    @Bind(R.id.iv_light_flow)
    ImageView ivLightFlow;
    @Bind(R.id.iv_light_white)
    ImageView ivLightWhite;
    @Bind(R.id.tv_light_rgbl)
    TextView tvLightRGBL;
    @Bind(R.id.tv_light_flow)
    TextView tvLightFlow;
    @Bind(R.id.tv_light_white)
    TextView tvLightWhite;
    @Bind(R.id.ll_rgbl_button)
    LinearLayout llRGBLButton;
    @Bind(R.id.ll_flow_button)
    LinearLayout llFlowButton;
    @Bind(R.id.ll_white_button)
    LinearLayout llWhiteButton;
    @Bind(R.id.fl_content)
    FrameLayout flContent;
    @Bind(R.id.iv_light_detail_switch)
    ImageView ivLightDetailSwitch;

    private RelativeLayout rlLightDetailSwitch;
    private TextView tvLightDetailSwitch;
    private View viewShelter;

    private Fragment mCurrentFragment = null;
    FragmentManager mFragmentManager = getSupportFragmentManager();
    private LightRGBFragment mLightRGBFragment;
    private LightFlowFragment mLightFlowFragment;
    private LightWhiteFragment mLightWhiteFragment;
    private boolean powerState = false;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            rlLightDetailSwitch.setClickable(true);
        }
    };

    private static DeviceVo mDeviceVo;

    @Override
    protected int getContentViewId() {
        return R.layout.act_light_detail;

    }

    @Override
    protected void initViews() {
        rlLightDetailSwitch = (RelativeLayout) findViewById(R.id.rl_light_detail_switch);
        tvLightDetailSwitch = (TextView) findViewById(R.id.tv_light_detail_switch);
        viewShelter = findViewById(R.id.view_shelter);
        ivLightRGBL.setImageResource(R.drawable.ic_light_rgbl);
        ivLightFlow.setImageResource(R.drawable.ic_light_flow);
        ivLightWhite.setImageResource(R.drawable.ic_light_white);
        tvTitle.setText("Light Detail");

    }

    @Override
    protected void initEvents() {
        EventBus.getDefault().register(mContext);
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initDatas() {
        mDeviceVo = (DeviceVo) getIntent().getSerializableExtra(Code.DEVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EHomeInterface.getINSTANCE().reunsubscribeDeviceTopic(mDeviceVo.getDevice().getDevId());

    }

    @OnClick({R.id.ll_rgbl_button, R.id.ll_flow_button, R.id.ll_white_button, R.id.ll_title_left, R.id.rl_light_detail_switch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_rgbl_button:
                showRGBLControl();
                break;
            case R.id.ll_flow_button:
                showFlowControl();
                break;
            case R.id.ll_white_button:
                showWhiteControl();
                break;
            case R.id.ll_title_left:
                finish();
                break;
            case R.id.rl_light_detail_switch:
                EHomeInterface.getINSTANCE().updateLightPower(mDeviceVo.getDevice().getDevId(),!powerState);
                break;
        }
    }



    private void showRGBLControl() {
        if (mLightRGBFragment == null) {
            mLightRGBFragment = LightRGBFragment.newInstance();
        }
        addOrShowFragment(mLightRGBFragment);
        rgblSelected();
    }

    private void showRGBLControl(LightDetail detail) {
        if (mLightRGBFragment == null) {
            mLightRGBFragment = LightRGBFragment.newInstance(detail);
        } else {
            mLightRGBFragment.setDetails(detail.getRgb(), detail.getL());
        }
        addOrShowFragment(mLightRGBFragment);
        rgblSelected();
    }

    private void showFlowControl() {
        if (mLightFlowFragment == null) {
            mLightFlowFragment = LightFlowFragment.newInstance();
        }
        addOrShowFragment(mLightFlowFragment);
        flowSelected();
    }

    private void showFlowControl(LightDetail detail) {
        if (mLightFlowFragment == null) {
            mLightFlowFragment = LightFlowFragment.newInstance(detail);
        } else {
            mLightFlowFragment.setDetails(detail.getRgb1(), detail.getRgb2(), detail.getRgb3(), detail.getRgb4(), detail.getL(), detail.getT());
        }
        addOrShowFragment(mLightFlowFragment);
        flowSelected();
    }

    private void showWhiteControl() {
        if (mLightWhiteFragment == null) {
            mLightWhiteFragment = LightWhiteFragment.newInstance();
        }
        addOrShowFragment(mLightWhiteFragment);
        whiteSelected();
    }

    private void showWhiteControl(LightDetail detail) {
        if (mLightWhiteFragment == null) {
            mLightWhiteFragment = LightWhiteFragment.newInstance(detail);
        } else {
            mLightWhiteFragment.setDetails(detail.getL());
        }
        addOrShowFragment(mLightWhiteFragment);
        whiteSelected();
    }

    private void rgblSelected() {
        tvLightRGBL.setTextColor(getResources().getColor(R.color.colorAccent));
        ivLightRGBL.setSelected(true);
        tvLightFlow.setTextColor(getResources().getColor(R.color.navibar_text_color));
        ivLightFlow.setSelected(false);
        tvLightWhite.setTextColor(getResources().getColor(R.color.navibar_text_color));
        ivLightWhite.setSelected(false);
    }

    private void flowSelected() {
        tvLightRGBL.setTextColor(getResources().getColor(R.color.navibar_text_color));
        ivLightRGBL.setSelected(false);
        tvLightFlow.setTextColor(getResources().getColor(R.color.colorAccent));
        ivLightFlow.setSelected(true);
        tvLightWhite.setTextColor(getResources().getColor(R.color.navibar_text_color));
        ivLightWhite.setSelected(false);
    }

    private void whiteSelected() {
        tvLightRGBL.setTextColor(getResources().getColor(R.color.navibar_text_color));
        ivLightRGBL.setSelected(false);
        tvLightFlow.setTextColor(getResources().getColor(R.color.navibar_text_color));
        ivLightFlow.setSelected(false);
        tvLightWhite.setTextColor(getResources().getColor(R.color.colorAccent));
        ivLightWhite.setSelected(true);
    }

    private void addOrShowFragment(android.support.v4.app.Fragment fragment) {   //???

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (mCurrentFragment != null) {
            if (mCurrentFragment == fragment) return;
            if (!fragment.isAdded()) {
                // 如果当前fragment未被添加，则添加到Fragment管理器中
                transaction.hide(mCurrentFragment)
                        .add(R.id.fl_content, fragment)
                        .commitAllowingStateLoss();
            } else {
                transaction.hide(mCurrentFragment).show(fragment).commitAllowingStateLoss();
            }
        } else {
            if (!fragment.isAdded()) {
                mFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.fl_content, fragment).commitAllowingStateLoss();
            } else {
                mFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .hide(mCurrentFragment).show(fragment).commitAllowingStateLoss();
            }
        }
        mCurrentFragment = fragment;
    }

    private void toggleState(boolean state) {
        Log.d(TAG, "toggleState() " + state);
        if (state) {
            rlLightDetailSwitch.setBackgroundResource(R.color.device_detail_black);
            tvLightDetailSwitch.setText(getString(R.string.device_switch_close));
            viewShelter.setVisibility(View.GONE);
            powerState = true;
        } else {
            rlLightDetailSwitch.setBackgroundResource(R.color.main_theme_blue);
            tvLightDetailSwitch.setText(getString(R.string.device_switch_open));
            viewShelter.setVisibility(View.VISIBLE);
            viewShelter.setOnClickListener(null);
            powerState = false;
        }
    }

    public static DeviceVo getItem() {
        return mDeviceVo;
    }



    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveLightModeEvent event) {
        mHandler.removeCallbacks(mRunnable);
        rlLightDetailSwitch.setClickable(true);
        switch (event.getLightMode()){
            case Code.FLOW_MODE_CONTROL:
                Log.d(TAG, "onEventMainThread: MqttReceiveLightModeFlowEvent");
                toggleState(true);
                int[] rgb1 = event.getRgb1();
                int[] rgb2 = event.getRgb2();
                int[] rgb3 = event.getRgb3();
                int[] rgb4 = event.getRgb4();
                int t =event.gettValue();
                int l = event.getlValue();
                showFlowControl(new LightDetail(rgb1, rgb2, rgb3, rgb4, t, l));
                break;
            case Code.LIGHT_MODE_L:
                Log.d(TAG, "onEventMainThread: MqttReceiveLightModeLEvent");
                toggleState(true);
                int wl = event.getlValue();
                showWhiteControl(new LightDetail(wl));
                break;
            case Code.LIGHT_MODE_RGBL:
                Log.d(TAG, "onEventMainThread: MqttReceiveLightModeRGBLEvent");
                toggleState(true);

                int[] rgb = event.getRgb();
                int lValue = event.getlValue();
                showRGBLControl(new LightDetail(rgb, lValue));
                break;
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveLightModePowerEvent event) {
        Log.d(TAG, "onEventMainThread: MqttReceiveLightModePowerEvent");
        mHandler.removeCallbacks(mRunnable);
        rlLightDetailSwitch.setClickable(true);
        toggleState(false);
    }

}
