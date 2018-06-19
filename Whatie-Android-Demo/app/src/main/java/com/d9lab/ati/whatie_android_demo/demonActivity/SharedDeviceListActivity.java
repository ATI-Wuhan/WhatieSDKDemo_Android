package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.BaseRecyclerAdapter;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatie_android_demo.application.RecyclerViewHolder;
import com.d9lab.ati.whatie_android_demo.demonActivity.widget.MySwipeRefreshLayout;
import com.d9lab.ati.whatiesdk.bean.BaseListResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.DevicesCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttReceiveLightModeEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveLightModePowerEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveSharedOffEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveSharedOnEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveSharedStatusEvent;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.LogUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/5/15.
 */

public class SharedDeviceListActivity extends BaseActivity {
    private static final String TAG = "SharedDeviceListActivit";

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
    @Bind(R.id.srl_shared_device_list)
    MySwipeRefreshLayout srlSharedDeviceList;
    @Bind(R.id.xrv_shared_device_list)
    XRecyclerView xrvSharedDeviceList;
//    @Bind(R.id.fab_my_device)
//    FloatingActionButton fabMyDevice;
//    @Bind(R.id.content)
//    FrameLayout content;
    @Bind(R.id.tv_share_device_empty)
    TextView tvShareDeviceEmpty;

    private PopupWindow changeDeviceWindow;
    private View changeDeviceView;
    private ArrayList<DeviceVo> mDevices = new ArrayList<>();
    private BaseRecyclerAdapter<DeviceVo> mAdapter;
    private List<DeviceVo> mDeviceVos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(mContext);
        getDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (EHome.getInstance().isLogin()) {
            getDevices();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_shared_device_list;
    }

    @Override
    protected void initViews() {
        tvTitle.setText("Shared Devices");
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        xrvSharedDeviceList.setLayoutManager(layoutManager);
        xrvSharedDeviceList.setPullRefreshEnabled(false);
        xrvSharedDeviceList.setLoadingMoreEnabled(false);
        tvShareDeviceEmpty.setVisibility(View.GONE);
    }

    @Override
    protected void initEvents() {
        srlSharedDeviceList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDevices();
            }
        });
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

                final String productName = item.getProductName();

                holder.setText(R.id.tv_device_name, item.getDevice().getName());
                Log.d(TAG, "bindData:Boolean      " + Boolean.parseBoolean(item.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY)));
                Log.d(TAG, "bindData:             " + item.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY));
                if (Code.PRODUCT_TYPE_RGBLIGHT.equals(productName)) {
                    if ("0".equals(item.getFunctionValuesMap().get(Code.FUNCTION_MAP_COLOR_LIGHT))) {
                        Log.d(TAG, "bindData:setSwitchState      false");

                        holder.setSwitchState(R.id.sw_device, false);
                    } else {
                        Log.d(TAG, "bindData:setSwitchState      true");
                        holder.setSwitchState(R.id.sw_device, true);
                    }
                } else if (Code.PRODUCT_TYPE_PLUG.equals(productName)) {
                    holder.setSwitchState(R.id.sw_device, Boolean.parseBoolean(item.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY)));
                }

                holder.setClickListener(R.id.sw_device, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Code.PRODUCT_TYPE_PLUG.equals(productName)) {
                            Log.d(TAG, "onClick: " + item.getDevice().getStatus());
                            if (item.getDevice().getStatus().equals(Code.DEVICE_STATUS_NORMAL)) {
                                if (Boolean.parseBoolean(item.getFunctionValuesMap().get(Code.FUNCTION_MAP_KEY))) {
                                    EHomeInterface.getINSTANCE().updateOutletsStatus(item.getDevice().getDevId(), false);
                                } else {
                                    EHomeInterface.getINSTANCE().updateOutletsStatus(item.getDevice().getDevId(), true);
                                }
                            } else {
                                Toast.makeText(mContext, "This device is not online.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (Code.PRODUCT_TYPE_RGBLIGHT.equals(productName)) {
                            if ("0".equals(item.getFunctionValuesMap().get(Code.FUNCTION_MAP_COLOR_LIGHT))) {
                                Log.d(TAG, "bindData:ColorLight      item.getDevice().getDevId(), true");
                                sendLightPowerInst(item.getDevice().getDevId(), true);
                            } else {
                                Log.d(TAG, "bindData:ColorLight      item.getDevice().getDevId(), false");
                                sendLightPowerInst(item.getDevice().getDevId(), false);
                            }
                        }

                    }
                });

                holder.setClickListener(R.id.rl_device_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Code.PRODUCT_TYPE_RGBLIGHT.equals(productName)) {
                            Intent deviceControl = new Intent(mContext, LightDetailActivity.class);
                            deviceControl.putExtra(Code.DEVICE, item);
                            startActivity(deviceControl);
                        } else if (Code.PRODUCT_TYPE_PLUG.equals(productName)) {
                            Intent deviceControl = new Intent(mContext, DeviceDetailActivity.class);
                            deviceControl.putExtra(Code.DEVICE, item);
                            startActivity(deviceControl);
                        }
                    }
                });
                holder.setLongClickListener(R.id.rl_device_item, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showChangeDeviceWindow(item);
                        return true;
                    }
                });
            }
        };
        xrvSharedDeviceList.setAdapter(mAdapter);

    }
    private void sendLightPowerInst(String devId, boolean willState) {
        EHomeInterface.getINSTANCE().updateLightPower(devId, willState);
        Log.d(TAG, "bindData:ColorLight      sendLightPowerInst" + willState);

    }
    private void setBackgroundAlpha(float f) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = f;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }
    private void showChangeDeviceWindow(final DeviceVo item) {
        if (changeDeviceWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            changeDeviceView = layoutInflater.inflate(R.layout.pop_change_delete, null);
            changeDeviceWindow = new PopupWindow(changeDeviceView, RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        setBackgroundAlpha(0.5f);
        changeDeviceWindow.setAnimationStyle(R.style.PopupWindowAnimationFromBottom);
        changeDeviceWindow.setFocusable(true);
        changeDeviceWindow.setOutsideTouchable(true);
        changeDeviceWindow.showAtLocation(findViewById(R.id.ll_shared_device_list),
                Gravity.CENTER, 0, 0);
        changeDeviceWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeDeviceWindow.dismiss();
                setBackgroundAlpha(1.0f);
            }
        });
        changeDeviceWindow.setTouchInterceptor(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        TextView changeName = changeDeviceView.findViewById(R.id.tv_select_device_change_name);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChangeDeviceNameActivity.class);
                intent.putExtra(Code.DEVICE, item);
                changeDeviceWindow.dismiss();
                startActivity(intent);
                setBackgroundAlpha(1.0f);

            }
        });

        TextView delete = changeDeviceView.findViewById(R.id.tv_select_device_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDeviceWindow.dismiss();
                EHomeInterface.getINSTANCE().removeSharedDevice(mContext, item.getDevice().getId(),
                        new BaseCallback() {
                            @Override
                            public void onSuccess(Response<BaseResponse> response) {
                                if (response.body().isSuccess()) {
                                    EHome.getInstance().removeDevice(item.getDevice().getDevId());
                                    Toast.makeText(mContext, "This device is deleted.", Toast.LENGTH_SHORT).show();
                                    getDevices();
                                    mAdapter.notifyDataSetChanged();
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
                setBackgroundAlpha(1.0f);

            }
        });
        TextView cancel = changeDeviceView.findViewById(R.id.tv_select_device_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDeviceWindow.dismiss();
                setBackgroundAlpha(1.0f);
            }
        });

        TextView share = changeDeviceView.findViewById(R.id.tv_select_share_device);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDeviceWindow.dismiss();
                Intent intent = new Intent(mContext, ShareActivity.class);
                intent.putExtra(Code.DEVICE_ID, item.getDevice().getId());
                startActivity(intent);
                setBackgroundAlpha(1.0f);
            }
        });


    }
//    @OnClick(R.id.fab_my_device)
//    public void onViewClicked() {
//        startActivity(new Intent(SharedDeviceListActivity.this, MainActivity.class));
//    }

    private void getDevices() {
        EHomeInterface.getINSTANCE().querySharedDevices(mContext, new DevicesCallback() {
            @Override
            public void onSuccess(Response<BaseListResponse<DeviceVo>> response) {
                if (response.body().isSuccess()) {
                    mDeviceVos.clear();
                    if (response.body().getList()==null) {
                        xrvSharedDeviceList.refreshComplete();
                        srlSharedDeviceList.setRefreshing(false);
//                                Toast.makeText(mContext, "Device list is empty.", Toast.LENGTH_SHORT).show();
                        tvShareDeviceEmpty.setVisibility(View.VISIBLE);
                    } else {
                        EHomeInterface.getINSTANCE().saveDevices(response.body().getList());

                        mDeviceVos.addAll(response.body().getList());
                        xrvSharedDeviceList.refreshComplete();
                        srlSharedDeviceList.setRefreshing(false);
                        mAdapter.replaceAll(response.body().getList());
                        tvShareDeviceEmpty.setVisibility(View.GONE);
                    }

//                            mAdapter.notifyDataSetChanged();

                } else {
                    tvShareDeviceEmpty.setVisibility(View.VISIBLE);
                    xrvSharedDeviceList.refreshComplete();
                    srlSharedDeviceList.setRefreshing(false);
                    Toast.makeText(mContext, "Get devices fail.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Response<BaseListResponse<DeviceVo>> response) {
                super.onError(response);
                xrvSharedDeviceList.refreshComplete();
                srlSharedDeviceList.setRefreshing(false);
                Toast.makeText(mContext, "Get devices fail.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2, sticky = true)
    public void onEventMainThread(MqttReceiveSharedOnEvent event) {
        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_KEY, String.valueOf(true));
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(Code.DEVICE_STATUS_NORMAL);
        mAdapter.notifyItemChanged(event.getIndex() + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2, sticky = true)
    public void onEventMainThread(MqttReceiveSharedOffEvent event) {
        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_KEY, String.valueOf(false));
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(Code.DEVICE_STATUS_NORMAL);
        mAdapter.notifyItemChanged(event.getIndex() + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2, sticky = true)
    public void onEventMainThread(MqttReceiveSharedStatusEvent event) {
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(event.getStatus());
        mAdapter.notifyItemChanged(event.getIndex() + 1);
    }




    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2, sticky = true)
    public void onEventMainThread(MqttReceiveLightModeEvent event) {
        EHome.getInstance().getmDeviceVos().get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, String.valueOf(event.getlValue()));

        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, String.valueOf(event.getlValue()));
        mDevices.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, String.valueOf(event.getlValue()));
        mAdapter.notifyItemChanged(event.getIndex() + 1);

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2, sticky = true)
    public void onEventMainThread(MqttReceiveLightModePowerEvent event) {
        Log.d(TAG, "onEventMainThread: MqttReceiveLightModePowerEvent" + event.getIndex());
        EHome.getInstance().getmDeviceVos().get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, "0");
        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, "0");
        mDevices.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, "0");
        mAdapter.notifyItemChanged(event.getIndex() + 1);

    }
}
