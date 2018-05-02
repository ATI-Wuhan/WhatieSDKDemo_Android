package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.d9lab.ati.whatie_android_demo.application.AppManager;
import com.d9lab.ati.whatiesdk.ehome.EHome;

import butterknife.ButterKnife;


/**
 * Created by liz on 2018/2/26.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AppManager.getInstance().addActivity(this);

        if (getContentViewId() != 0) {
            setContentView(getContentViewId());
        }
        ButterKnife.bind(this);
        mContext = this;
        initViews();
        initEvents();
        initDatas();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AppManager.getInstance().addActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EHome.getInstance().cancelTag(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(EHome.getInstance().isLogin() && !EHome.getInstance().isMqttOn()){
            EHome.getInstance().startMqttService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        AppManager.getInstance().finishActivity(this);
    }

    /**
     * 绑定布局文件
     */
    protected abstract int getContentViewId();

    /**
     * 初始化控件
     */
    protected abstract void initViews();

    /**
     * 事件绑定
     */
    protected abstract void initEvents();

    /**
     * 初始化数据
     */
    protected abstract void initDatas();

}
