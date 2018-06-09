package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.QRCodeCons;
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/7.
 */

public class ChangeDeviceNameActivity extends BaseActivity {
    @Bind(R.id.et_change_device_name)
    EditText etDeviceName;
    @Bind(R.id.tv_change_device_name_button)
    TextView tvChangeConfirm;
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
    private DeviceVo deviceVo;

    @Override
    protected int getContentViewId() {
        return R.layout.act_change_device_name;
    }

    @Override
    protected void initViews() {
        deviceVo = (DeviceVo) getIntent().getSerializableExtra(Code.DEVICE);
        etDeviceName.setText(deviceVo.getDevice().getName());
        tvTitle.setText("Change Device Name");
    }

    @Override
    protected void initEvents() {
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvChangeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etDeviceName.getText().toString().trim().equals("")) {
                    EHomeInterface.getINSTANCE().updateDeviceName(mContext, deviceVo.getDevice().getDevId(), etDeviceName.getText().toString().trim(),
                            new BaseCallback() {
                                @Override
                                public void onSuccess(Response<BaseResponse> response) {
                                    if (response.body().isSuccess()) {
                                        Toast.makeText(mContext, "Change name success.", Toast.LENGTH_SHORT);
                                        finish();
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
                } else {
                    Toast.makeText(mContext, "Device name cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void initDatas() {

    }

}
