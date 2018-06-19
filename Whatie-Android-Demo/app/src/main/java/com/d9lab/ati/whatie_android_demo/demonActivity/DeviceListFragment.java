package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.AppManager;
import com.d9lab.ati.whatie_android_demo.application.BaseRecyclerAdapter;
import com.d9lab.ati.whatie_android_demo.application.RecyclerViewHolder;
import com.d9lab.ati.whatie_android_demo.demonActivity.widget.MySwipeRefreshLayout;
import com.d9lab.ati.whatiesdk.bean.BaseListResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.DevicesCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttReceiveEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveLightModeEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveLightModePowerEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveOffEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveOnEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveStatusEvent;
import com.d9lab.ati.whatiesdk.event.MqttReceiveUnbindEvent;
import com.d9lab.ati.whatiesdk.event.MqttSendEvent;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.SharedPreferenceUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/7.
 */

public class DeviceListFragment extends BaseFragment {
    private static final String INIT_DEVICE_LIST = "initDeviceList";
    private static final String TAG = "DeviceListFragment";
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
    @Bind(R.id.srl_device_list)
    MySwipeRefreshLayout srlDeviceList;
    @Bind(R.id.xrv_device_list)
    XRecyclerView xrvDeviceList;
    @Bind(R.id.tv_home_device_empty)
    TextView tvDeviceEmpty;

    private ArrayList<DeviceVo> mDevices = new ArrayList<>();
    private BaseRecyclerAdapter<DeviceVo> mAdapter;
    private List<DeviceVo> mDeviceVos = new ArrayList<>();
    private PopupWindow changeDeviceWindow;
    private View changeDeviceView;

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.frag_home;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (EHome.getInstance().isLogin()) {
            Log.d(TAG, "onPause: device list framgent");
            getDevices();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (EHome.getInstance().isLogin()) {
            Log.d(TAG, "onStart: device list framgent");
            getDevices();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (EHome.getInstance().isLogin()) {
            Log.d(TAG, "onResume: device list framgent");
            getDevices();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INIT_DEVICE_LIST, mDevices);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getDevices();
            Log.d(TAG, "getArguments() != null");
        }
        if (savedInstanceState != null) {
            mDevices.clear();
            mDevices.addAll((ArrayList<DeviceVo>) savedInstanceState.getSerializable(INIT_DEVICE_LIST));
        }
        EventBus.getDefault().register(this);

    }


    @Override
    protected void initViews() {
        tvTitle.setText("Home");
//        tvTitleLeft.setVisibility(View.GONE);
//        deviceVo = (DeviceVo) getIntent().getSerializableExtra(Code.DEVICE);
        tvTitleRight.setText(getString(R.string.device_list_add));
        tvTitleRight.setVisibility(View.VISIBLE);
        ivTitleLeft.setVisibility(View.GONE);
//        ivTitleLeft.setImageResource(R.drawable.ic_scan);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        xrvDeviceList.setLayoutManager(layoutManager);
        xrvDeviceList.setPullRefreshEnabled(false);
        xrvDeviceList.setLoadingMoreEnabled(false);
        llTitleLeft.setVisibility(View.GONE);
        tvDeviceEmpty.setVisibility(View.GONE);
    }

    @Override
    protected void initEvents() {


        srlDeviceList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EHomeInterface.getINSTANCE().getMyDevices(mContext, new DevicesCallback() {
                    @Override
                    public void onSuccess(Response<BaseListResponse<DeviceVo>> response) {
                        if (response.body().isSuccess()) {
                            EHomeInterface.getINSTANCE().saveDevices(response.body().getList());
                            mDeviceVos.clear();
                            if (response.body().getList().isEmpty()) {
                                xrvDeviceList.refreshComplete();
                                srlDeviceList.setRefreshing(false);
//                                Toast.makeText(mContext, "Device list is empty.", Toast.LENGTH_SHORT).show();
                                tvDeviceEmpty.setVisibility(View.VISIBLE);
                            } else {
                                mDeviceVos.addAll(response.body().getList());
                                xrvDeviceList.refreshComplete();
                                srlDeviceList.setRefreshing(false);
                                mAdapter.replaceAll(response.body().getList());
                                tvDeviceEmpty.setVisibility(View.GONE);
                            }

//                            mAdapter.notifyDataSetChanged();

                        } else {
                            tvDeviceEmpty.setVisibility(View.VISIBLE);
                            xrvDeviceList.refreshComplete();
                            srlDeviceList.setRefreshing(false);
                            Toast.makeText(mContext, "Get devices fail.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Response<BaseListResponse<DeviceVo>> response) {
                        super.onError(response);
                        xrvDeviceList.refreshComplete();
                        srlDeviceList.setRefreshing(false);
                        Toast.makeText(mContext, "Get devices fail.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                            Intent deviceControl = new Intent(getActivity(), LightDetailActivity.class);
                            deviceControl.putExtra(Code.DEVICE, item);
                            startActivity(deviceControl);
                        } else if (Code.PRODUCT_TYPE_PLUG.equals(productName)) {
                            Intent deviceControl = new Intent(getActivity(), DeviceDetailActivity.class);
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
        xrvDeviceList.setAdapter(mAdapter);
        getDevices();
    }

    private void sendLightPowerInst(String devId, boolean willState) {
        EHomeInterface.getINSTANCE().updateLightPower(devId, willState);
        Log.d(TAG, "bindData:ColorLight      sendLightPowerInst" + willState);

    }

    public static DeviceListFragment newInstance() {
        Bundle args = new Bundle();

        DeviceListFragment fragment = new DeviceListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getDevices() {
        EHomeInterface.getINSTANCE().getMyDevices(mContext, new DevicesCallback() {
            @Override
            public void onSuccess(Response<BaseListResponse<DeviceVo>> response) {
                if (response.body().isSuccess()) {
                    if (response.body().getList().isEmpty()) {
                        xrvDeviceList.refreshComplete();
                        srlDeviceList.setRefreshing(false);
                        tvDeviceEmpty.setVisibility(View.VISIBLE);
//                        Toast.makeText(mContext, "Device list is empty.", Toast.LENGTH_SHORT).show();
                        mAdapter.replaceAll(response.body().getList());
                    } else {
                        xrvDeviceList.refreshComplete();
                        srlDeviceList.setRefreshing(false);
                        tvDeviceEmpty.setVisibility(View.GONE);
                        EHomeInterface.getINSTANCE().saveDevices(response.body().getList());
                        mDeviceVos.clear();
                        mDeviceVos.addAll(response.body().getList());
//                    mAdapter.notifyDataSetChanged();
                        mAdapter.replaceAll(response.body().getList());
                    }

                } else {
                    xrvDeviceList.refreshComplete();
                    srlDeviceList.setRefreshing(false);
                    Toast.makeText(mContext, "Get devices fail.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Response<BaseListResponse<DeviceVo>> response) {
                super.onError(response);
                xrvDeviceList.refreshComplete();
                srlDeviceList.setRefreshing(false);
                Toast.makeText(mContext, "Get devices fail.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveOnEvent event) {
        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_KEY, String.valueOf(true));
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(Code.DEVICE_STATUS_NORMAL);
        Log.d(TAG, "onEventMainThread: MqttReceiveOnEvent    " + event.getIndex());
        mAdapter.notifyItemChanged(event.getIndex() + 1);
//        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveOffEvent event) {
        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_KEY, String.valueOf(false));
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(Code.DEVICE_STATUS_NORMAL);
        mAdapter.notifyItemChanged(event.getIndex() + 1);
//        mAdapter.notifyDataSetChanged();
        Log.d(TAG, "onEventMainThread: MqttReceiveOffEvent    " + event.getIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveUnbindEvent event) {
        mDeviceVos.remove(event.getIndex());
        Log.d(TAG, "onEventMainThread: MqttReceiveUnbindEvent" + event.getIndex());
        mAdapter.notifyItemRemoved(event.getIndex() + 1);
//        mAdapter.notifyDataSetChanged();
    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveStatusEvent event) {
        mDeviceVos.get(event.getIndex()).getDevice().setStatus(event.getStatus());
        mAdapter.notifyItemChanged(event.getIndex() + 1);
//        mAdapter.notifyDataSetChanged();
        Log.d(TAG, "onEventMainThread: MqttReceiveStatusEvent" + event.getIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveLightModeEvent event) {
        EHome.getInstance().getmDeviceVos().get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, String.valueOf(event.getlValue()));

        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, String.valueOf(event.getlValue()));
        mDevices.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, String.valueOf(event.getlValue()));
        mAdapter.notifyItemChanged(event.getIndex() + 1);

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttReceiveLightModePowerEvent event) {
        Log.d(TAG, "onEventMainThread: MqttReceiveLightModePowerEvent" + event.getIndex());
        EHome.getInstance().getmDeviceVos().get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, "0");
        mDeviceVos.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, "0");
        mDevices.get(event.getIndex()).getFunctionValuesMap().put(Code.FUNCTION_MAP_COLOR_LIGHT, "0");
        mAdapter.notifyItemChanged(event.getIndex() + 1);

    }


    private void setBackgroundAlpha(float f) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = f;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    private void showChangeDeviceWindow(final DeviceVo item) {
        if (changeDeviceWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            changeDeviceView = layoutInflater.inflate(R.layout.pop_change_delete, null);
            changeDeviceWindow = new PopupWindow(changeDeviceView, RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        setBackgroundAlpha(0.5f);
        changeDeviceWindow.setAnimationStyle(R.style.PopupWindowAnimationFromBottom);
        changeDeviceWindow.setFocusable(true);
        changeDeviceWindow.setOutsideTouchable(true);
        changeDeviceWindow.showAtLocation(getActivity().findViewById(R.id.rl_main_act),
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
                Intent intent = new Intent(getActivity(), ChangeDeviceNameActivity.class);
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
                EHomeInterface.getINSTANCE().removeDevice(mContext, item.getDevice().getId(),
                        new BaseCallback() {
                            @Override
                            public void onSuccess(Response<BaseResponse> response) {
                                if (response.body().isSuccess()) {
                                    EHome.getInstance().removeDevice(item.getDevice().getDevId());
                                    Toast.makeText(mContext, "This device is deleted.", Toast.LENGTH_SHORT);
                                    getDevices();
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
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.putExtra(Code.DEVICE_ID, item.getDevice().getId());
                startActivity(intent);
                setBackgroundAlpha(1.0f);
            }
        });


    }

    @OnClick({R.id.tv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title_right:
                startActivity(new Intent(getContext(), ProductionListActivity.class));
                break;

        }
    }

}
