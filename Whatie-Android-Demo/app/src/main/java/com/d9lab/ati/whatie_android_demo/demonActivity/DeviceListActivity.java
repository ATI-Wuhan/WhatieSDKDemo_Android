package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.AppManager;
import com.d9lab.ati.whatie_android_demo.application.BaseRecyclerAdapter;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatie_android_demo.application.RecyclerViewHolder;
import com.d9lab.ati.whatiesdk.bean.BaseListResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.DevicesCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttReceiveOffEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveOnEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveStatusEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveUnbindEvent;
import com.d9lab.ati.whatiesdk.util.Code;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/4/24.
 */

public class DeviceListActivity extends BaseActivity {
    private static final String TAG = "DeviceListActivity";
    @Bind(R.id.rv_device_list)
    RecyclerView rvDeviceList;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private BaseRecyclerAdapter<DeviceVo> mAdapter;
    private List<DeviceVo> mDeviceVos = new ArrayList<>();

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
        if(EHome.getInstance().isLogin()){
            getDevices();
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.act_device_list;
    }

    @Override
    protected void initViews() {
        setTitle("My Devices");
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvDeviceList.setLayoutManager(layoutManager);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {
        mAdapter = new BaseRecyclerAdapter<DeviceVo>(mContext, mDeviceVos) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_device;
            }

            @Override
            public void bindData(RecyclerViewHolder holder, int position, final DeviceVo item) {
                holder.setText(R.id.tv_device_name, item.getDevice().getName());
                holder.setSwitchState(R.id.sw_device, item.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY));
                holder.setClickListener(R.id.sw_device, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.getDevice().getStatus().equals(Code.DEVICE_STATUS_NORMAL)){
                            if(item.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY)){
                                EHomeInterface.getINSTANCE().turnOff(item.getDevice().getDevId());
                            }else {
                                EHomeInterface.getINSTANCE().turnOn(item.getDevice().getDevId());
                            }
                        }else {
                            Toast.makeText(mContext, "This device is not online.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.setClickListener(R.id.rl_device_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DeviceListActivity.this, DeviceDetailActivity.class);
                        intent.putExtra(Code.DEVICE, item);
                        startActivity(intent);
                    }
                });
            }
        };
        rvDeviceList.setAdapter(mAdapter);
    }

    private void getDevices(){
        EHomeInterface.getINSTANCE().getMyDevices(mContext, Constant.ACCESS_ID, Constant.ACCESS_KEY, new DevicesCallback() {
            @Override
            public void onSuccess(Response<BaseListResponse<DeviceVo>> response) {
                if (response.body().isSuccess()){
                    EHome.getInstance().saveDevices(response.body().getList());
                    mDeviceVos.clear();
                    mDeviceVos.addAll(response.body().getList());
                    mAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(mContext,"Get devices fail.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Response<BaseListResponse<DeviceVo>> response) {
                super.onError(response);
                Toast.makeText(mContext,"Get devices fail.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        EHomeInterface.getINSTANCE().logOut(mContext, Constant.ACCESS_ID, Constant.ACCESS_KEY,new BaseCallback() {
            @Override
            public void onSuccess(Response<BaseResponse> response) {
                if(response.body().isSuccess()){
                    EHome.getInstance().logOut();
                    AppManager.getInstance().appExit(getApplicationContext());
                }
            }
        });
    }


    @OnClick(R.id.fab)
    public void onViewClicked() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Add Device");
        dialog.setContentView(R.layout.dialog_add_device);
        Window window = dialog.getWindow();
        TextView smartconfig = (TextView) window.findViewById(R.id.tv_add_device_smartconfig);
        TextView scanQRcode = (TextView) window.findViewById(R.id.tv_add_device_scan_qrcode);

        smartconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceListActivity.this, ProductionListActivity.class));
                dialog.dismiss();
            }
        });

        scanQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceListActivity.this, ScanActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveOnEvent event) {
        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_KEY, true);
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(Code.DEVICE_STATUS_NORMAL);
        mAdapter.notifyItemChanged(event.getIndex() + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveOffEvent event) {
        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_KEY, false);
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(Code.DEVICE_STATUS_NORMAL);
        mAdapter.notifyItemChanged(event.getIndex() + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveUnbindEvent event) {
        mDeviceVos.remove(event.getIndex());
        mAdapter.notifyItemRemoved(event.getIndex() + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveStatusEvent event) {
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(event.getStatus());
        mAdapter.notifyItemChanged(event.getIndex() + 1);
    }

}
