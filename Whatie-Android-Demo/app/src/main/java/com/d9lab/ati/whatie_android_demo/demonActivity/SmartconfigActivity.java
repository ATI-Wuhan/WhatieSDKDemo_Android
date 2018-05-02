package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatiesdk.bean.BaseModelResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.Device;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.BaseStringCallback;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttAlreadyBindEvent;
import com.d9lab.ati.whatiesdk.event.MqttBindSuccessEvent;
import com.d9lab.ati.whatiesdk.smartconfig.EspWifiAdminSimple;
import com.d9lab.ati.whatiesdk.smartconfig.EsptouchTask;
import com.d9lab.ati.whatiesdk.smartconfig.IEsptouchResult;
import com.d9lab.ati.whatiesdk.smartconfig.IEsptouchTask;
import com.d9lab.ati.whatiesdk.smartconfig.task.__IEsptouchTask;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/4/24.
 */

public class SmartconfigActivity extends BaseActivity {
    private static final String TAG = "SmartconfigActivity";
    @Bind(R.id.tv_ssid)
    TextView tvSsid;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.button_config)
    Button buttonConfig;

    private EspWifiAdminSimple mWifiAdmin;
    private IEsptouchTask mEsptouchTask;
    private ProgressDialog mProgressDialog;
    private final Object mLock = new Object();

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(true);
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                    "Confirm");
            mProgressDialog.setMessage("Config failed.");
        }
    };


    @Override
    protected int getContentViewId() {
        return R.layout.act_smartconfig;
    }

    @Override
    protected void initViews() {
        setTitle("SmartConfig");
        mWifiAdmin = new EspWifiAdminSimple(this);

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
    protected void onResume() {
        super.onResume();
        String apSsid = mWifiAdmin.getWifiConnectedSsid();
        if (apSsid != null) {
            tvSsid.setText(apSsid);
        } else {
            tvSsid.setText("");
        }
        // check whether the wifi is connected
        boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
        buttonConfig.setEnabled(!isApSsidEmpty);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    @OnClick(R.id.button_config)
    public void onViewClicked() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("EHome is configuring, please wait for a moment...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                synchronized (mLock) {
                    if (__IEsptouchTask.DEBUG) {
                        Log.i(TAG, "progress dialog is canceled");
                    }
                    if (mEsptouchTask != null) {
                        mEsptouchTask.interrupt();
                    }
                }
            }
        });
        mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "Waiting...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        mProgressDialog.show();
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setEnabled(false);
        mProgressDialog.setCancelable(false);

        EHomeInterface.getINSTANCE().getNetToken(mContext, Constant.ACCESS_ID, Constant.ACCESS_KEY, new BaseStringCallback() {
            @Override
            public void onSuccess(Response<BaseModelResponse<String>> response) {
                if (response.body().isSuccess()){
                    String apSsid = mWifiAdmin.getWifiConnectedSsid();
                    String apPassword = etPwd.getText().toString().trim();
                    String apBssid = mWifiAdmin.getWifiConnectedBssid();
                    new WhatieAsyncTask().execute(apSsid, apBssid, response.body().getValue()+ apPassword , "1");
                }else {
                    mProgressDialog.setMessage("Config failed.");
                    mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            .setEnabled(true);
                    mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                            "Confirm");
                }
            }

            @Override
            public void onError(Response<BaseModelResponse<String>> response) {
                super.onError(response);
                mProgressDialog.setMessage("Config failed.");
                mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setEnabled(true);
                mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                        "Confirm");
            }
        });


    }

    private class WhatieAsyncTask extends AsyncTask<String, Void, List<IEsptouchResult>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                String apSsid = mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
                String apBssid = params[1];
                String apPassword = params[2];
                String taskResultCountStr = params[3];
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, mContext);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            IEsptouchResult firstResult = result.get(0);
            if (!firstResult.isCancelled()) {
                if (firstResult.isSuc()) {
                    mHandler.postDelayed(mRunnable, 30000);
                } else {
                    mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            .setEnabled(true);
                    mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                            "Confirm");
                    mProgressDialog.setMessage("Config failed.");
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttAlreadyBindEvent event) {
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setEnabled(true);
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                "Confirm");
        mProgressDialog.setMessage("The device has been bound by" + event.getEmail());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(final MqttBindSuccessEvent event) {
        mHandler.removeCallbacks(mRunnable);
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setEnabled(true);
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                "Confirm");
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EHomeInterface.getINSTANCE().getStarted(mContext, event.getDevId(), event.getName(), Constant.ACCESS_ID, Constant.ACCESS_KEY,
                        new BaseCallback() {
                            @Override
                            public void onSuccess(Response<BaseResponse> response) {
                                if(response.body().isSuccess()){
                                    Toast.makeText(mContext, "Device initialize success.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SmartconfigActivity.this, DeviceListActivity.class));
                                }else {
                                    Toast.makeText(mContext, "Device initialize fail.", Toast.LENGTH_SHORT).show();
                                }
                                mProgressDialog.dismiss();
                            }

                            @Override
                            public void onError(Response<BaseResponse> response) {
                                super.onError(response);
                                Toast.makeText(mContext, "Device initialize fail.", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        });
            }
        });
        mProgressDialog.setMessage("Config success.");
    }


}
