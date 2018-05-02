package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatiesdk.bean.BaseModelResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.Device;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.DeviceCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttReceiveUnbindEvent;
import com.d9lab.ati.whatiesdk.util.Code;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/4/25.
 */

public class DeviceDetailActivity extends BaseActivity {
    private static final String TAG = "DeviceDetailActivity";
    @Bind(R.id.et_device_name)
    EditText etDeviceName;
    @Bind(R.id.change_device_name)
    Button changeDeviceName;
    @Bind(R.id.bt_unbind)
    Button btUnbind;
    @Bind(R.id.bt_delete_by_owner)
    Button btDeleteByOwner;
    @Bind(R.id.ll_owner)
    LinearLayout llOwner;
    @Bind(R.id.bt_delete_sharing_device)
    Button btDeleteSharingDevice;
    @Bind(R.id.ll_not_owner)
    LinearLayout llNotOwner;
    @Bind(R.id.bt_share_device)
    Button btShareDevice;

    private DeviceVo deviceVo;

    @Override
    protected int getContentViewId() {
        return R.layout.act_device_detail;
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
        setTitle("Device Detail");
        deviceVo = (DeviceVo) getIntent().getSerializableExtra(Code.DEVICE);
        if (deviceVo.isHost()) {
            llOwner.setVisibility(View.VISIBLE);
            llNotOwner.setVisibility(View.GONE);
        } else {
            llNotOwner.setVisibility(View.VISIBLE);
            llOwner.setVisibility(View.GONE);
        }
        etDeviceName.setText(deviceVo.getDevice().getName());
        btUnbind.setClickable(EHome.getInstance().isMqttOn());
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    @OnClick({R.id.bt_unbind, R.id.bt_delete_by_owner, R.id.bt_delete_sharing_device, R.id.bt_share_device, R.id.change_device_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.change_device_name:
                if(!etDeviceName.getText().toString().trim().equals("")){
                    EHomeInterface.getINSTANCE().changeDeviceName(mContext, deviceVo.getDevice().getDevId(), etDeviceName.getText().toString().trim(), Constant.ACCESS_ID, Constant.ACCESS_KEY,
                            new BaseCallback() {
                                @Override
                                public void onSuccess(Response<BaseResponse> response) {
                                    if (response.body().isSuccess()) {
                                        Toast.makeText(mContext, "Change name success.", Toast.LENGTH_SHORT);
                                    } else {
                                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Response<BaseResponse> response) {
                                    super.onError(response);
                                    Toast.makeText(mContext, "Change name fail.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(mContext, "Device name cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_unbind:
                EHomeInterface.getINSTANCE().unbind(deviceVo.getDevice().getDevId());
                break;
            case R.id.bt_delete_by_owner:
                EHomeInterface.getINSTANCE().deleteDevice(mContext, deviceVo.getDevice().getId(), Constant.ACCESS_ID, Constant.ACCESS_KEY, new BaseCallback() {
                    @Override
                    public void onSuccess(Response<BaseResponse> response) {
                        if (response.body().isSuccess()) {
                            EHome.getInstance().removeDevice(deviceVo.getDevice().getDevId());
                            Toast.makeText(mContext, "This device is deleted.", Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            Toast.makeText(mContext, "delete fail.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse> response) {
                        super.onError(response);
                        Toast.makeText(mContext, "delete fail.", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.bt_delete_sharing_device:
                EHomeInterface.getINSTANCE().deleteSharedDevice(mContext, deviceVo.getDevice().getDevId(), Constant.ACCESS_ID, Constant.ACCESS_KEY, new BaseCallback() {
                    @Override
                    public void onSuccess(Response<BaseResponse> response) {
                        if (response.body().isSuccess()) {
                            EHome.getInstance().removeDevice(deviceVo.getDevice().getDevId());
                            Toast.makeText(mContext, "This device is deleted.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(mContext, "delete fail.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse> response) {
                        super.onError(response);
                        Toast.makeText(mContext, "delete fail.", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.bt_share_device:
                Intent intent = new Intent(DeviceDetailActivity.this, ShareActivity.class);
                intent.putExtra(Code.DEVICE_ID, deviceVo.getDevice().getId());
                startActivity(intent);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 3, sticky = true)
    public void onEventMainThread(MqttReceiveUnbindEvent event) {
        if (deviceVo.getDevice().getDevId().equals(event.getDevId())) {
            Toast.makeText(mContext, "This device is unbound.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DeviceDetailActivity.this, DeviceListActivity.class));
        }
    }

}
