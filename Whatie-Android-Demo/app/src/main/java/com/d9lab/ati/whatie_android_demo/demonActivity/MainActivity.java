package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.BaseRecyclerAdapter;
import com.d9lab.ati.whatie_android_demo.application.RecyclerViewHolder;
import com.d9lab.ati.whatiesdk.bean.BaseListResponse;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.DevicesCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.FastjsonUtils;
import com.d9lab.ati.whatiesdk.util.LogUtil;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/7.
 */

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    @Bind(R.id.ll_home_button)
    LinearLayout llHomeButton;
    @Bind(R.id.iv_main_profile)
    ImageView ivMainProfile;
    @Bind(R.id.tv_main_profile)
    TextView tvMainProfile;
    @Bind(R.id.ll_profile_button)
    LinearLayout llProfileButton;
    @Bind(R.id.rl_navigation_bar)
    LinearLayout rlNavigationBar;
    @Bind(R.id.fl_content)
    FrameLayout flContent;
    @Bind(R.id.tv_title_right)
    TextView tvTitleRight;
    @Bind(R.id.tv_title_left)
    TextView tvTitleLeft;
    @Bind(R.id.iv_title_right)
    ImageView ivTitleRight;
    @Bind(R.id.iv_title_left)
    ImageView ivTitleLeft;
    @Bind(R.id.iv_main_home)
    ImageView ivMainHome;
    @Bind(R.id.tv_main_home)
    TextView tvMainHome;

    private Fragment mCurrentFragment = null;
    FragmentManager mFragmentManager = getSupportFragmentManager();
    private DeviceListFragment mDeviceListFragment;
    private ProfileFragment mProfileFragment;
    private boolean resumeRefresh = false;

    //    private MaterialDialog mUpdateDialog;
    private Object mDownloadTag = new Object();
    public static final int STORAGE = 0;
    private static final int CAMERA = 1;


    private BaseRecyclerAdapter<DeviceVo> mAdapter;
    private List<DeviceVo> mDeviceVos = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.act_main;
    }

    @Override
    protected void initViews() {
        homePressed();
        showHome();

    }

    private void homePressed() {
        tvMainHome.setTextColor(getResources().getColor(R.color.colorAccent));
        ivMainHome.setImageResource(R.drawable.ic_main_home_pressed);
        tvMainProfile.setTextColor(getResources().getColor(R.color.navibar_text_color));
        ivMainProfile.setImageResource(R.drawable.ic_main_profile_normal);

    }

    @Override
    protected void initEvents() {

    }


    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void initDatas() {

    }

    @OnClick({R.id.ll_home_button, R.id.ll_profile_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_home_button:
                homePressed();
                showHome();
                break;
            case R.id.ll_profile_button:
                profilePressed();
                showProfile();
                break;

        }
    }

    private void showHome() {
        if (mDeviceListFragment == null) {
            mDeviceListFragment = DeviceListFragment.newInstance();
        }
        addOrShowFragment(mDeviceListFragment);

    }

    private void profilePressed() {
        tvMainProfile.setTextColor(getResources().getColor(R.color.colorAccent));
        ivMainProfile.setImageResource(R.drawable.ic_main_profile_pressed);
        tvMainHome.setTextColor(getResources().getColor(R.color.navibar_text_color));
        ivMainHome.setImageResource(R.drawable.ic_main_home_normal);

    }

    private void showProfile() {
        if (mProfileFragment == null) {
            mProfileFragment = ProfileFragment.newInstance();
        }
        addOrShowFragment(mProfileFragment);
    }

    private void addOrShowFragment(Fragment fragment) {   //???

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

}
