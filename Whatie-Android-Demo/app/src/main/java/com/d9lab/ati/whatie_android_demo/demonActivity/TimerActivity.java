package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.BaseRecyclerAdapter;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatie_android_demo.application.RecyclerViewHolder;
import com.d9lab.ati.whatie_android_demo.application.TimeTypeUtil;
import com.d9lab.ati.whatiesdk.bean.BaseListResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.ClockVo;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.ClockCallback;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.event.MqttCancelTimerSuccessEvent;
import com.d9lab.ati.whatiesdk.event.MqttSetTimerSuccessEvent;
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
 * Created by liz on 2018/5/15.
 */

public class TimerActivity extends BaseActivity {
    private static final String TAG = "TimerActivity";
    @Bind(R.id.rv_timer_list)
    RecyclerView rvTimerList;
    @Bind(R.id.fab_add_timer)
    FloatingActionButton fabAddTimer;
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
    private DeviceVo mDevice;
    private BaseRecyclerAdapter<ClockVo> mAdapter;
    private List<ClockVo> mClockVos = new ArrayList<>();

    public static final String FROM_ADD_TIMER = "addTimer";
    public static final String FROM_EDIT_TIMER = "editTimer";

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
        getTimers();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_timer_list;
    }

    @Override
    protected void initViews() {
        setTitle("Timer List");
        mDevice = (DeviceVo) getIntent().getSerializableExtra(Code.DEVICE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvTimerList.setLayoutManager(layoutManager);
        tvTitle.setText("Timer List");
        llTitleRight.setVisibility(View.GONE);
        ivTitleLeft.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initEvents() {
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initDatas() {
        mAdapter = new BaseRecyclerAdapter<ClockVo>(mContext, mClockVos) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_timer;
            }

            @Override
            public void bindData(RecyclerViewHolder holder, int position, final ClockVo item) {
                holder.setText(R.id.tv_timer_time, item.getFinishTimeApp().substring(0, 2) + ":" + item.getFinishTimeApp().substring(2, item.getFinishTimeApp().length()));
                String[] dps = item.getDeviceClock().getDps().split("_");
                holder.setText(R.id.tv_timer_will_state, (dps[0].equals(Code.TRUE))?"Turn ON":"Turn OFF");
                holder.setText(R.id.tv_timer_repeat, TimeTypeUtil.getRepeatDay(item.getDeviceClock().getTimerType()));
                holder.setClickListener(R.id.sw_timer, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editTimerState(item);
                    }
                });

                holder.setSwitchState(R.id.sw_timer, item.getDeviceClock().getClockStatus());
                holder.setClickListener(R.id.rl_timer_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editIntent = new Intent(TimerActivity.this, SetTimerActivity.class);
                        editIntent.setAction(FROM_EDIT_TIMER);
                        editIntent.putExtra(Code.ALARM, item);
                        startActivity(editIntent);
                    }
                });
            }
        };
        rvTimerList.setAdapter(mAdapter);
    }

    private void editTimerState(ClockVo clockVo) {
        EHomeInterface.getINSTANCE().updateTimerStatus(mContext,
                clockVo.getDeviceClock().getId(),
                !clockVo.getDeviceClock().getClockStatus(),
                new BaseCallback() {
                    @Override
                    public void onSuccess(Response<BaseResponse> response) {

                    }

                    @Override
                    public void onError(Response<BaseResponse> response) {
                        super.onError(response);
                    }
                });
    }

    private void getTimers(){
        EHomeInterface.getINSTANCE().getTimerList(mContext, mDevice.getDevice().getId(),
                new ClockCallback() {
                    @Override
                    public void onSuccess(Response<BaseListResponse<ClockVo>> response) {
                        mClockVos.clear();
                        if(response.body().getList() != null && response.body().getList().size()>0){
                            mClockVos.addAll(response.body().getList());
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Response<BaseListResponse<ClockVo>> response) {
                        super.onError(response);
                    }
                });
    }

    @OnClick(R.id.fab_add_timer)
    public void onViewClicked() {
        Intent addIntent = new Intent(TimerActivity.this, SetTimerActivity.class);
        addIntent.setAction(FROM_ADD_TIMER);
        addIntent.putExtra(Code.DEVICE, mDevice);
        startActivity(addIntent);
    }
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttSetTimerSuccessEvent event) {
        getTimers();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEventMainThread(MqttCancelTimerSuccessEvent event) {
        getTimers();
    }




}
