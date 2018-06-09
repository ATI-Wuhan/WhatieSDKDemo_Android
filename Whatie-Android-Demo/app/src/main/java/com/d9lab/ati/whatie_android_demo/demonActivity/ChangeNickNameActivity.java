package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.BaseModelResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.DeviceVo;
import com.d9lab.ati.whatiesdk.bean.User;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.UserCallback;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.SharedPreferenceUtils;
import com.lzy.okgo.model.Response;

import butterknife.Bind;

/**
 * Created by 神火 on 2018/6/8.
 */

public class ChangeNickNameActivity extends BaseActivity {
    @Bind(R.id.et_change_nick_name)
    EditText etNickName;
    @Bind(R.id.tv_change_nick_name_button)
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


    @Override
    protected int getContentViewId() {
        return R.layout.act_change_nickname;
    }

    @Override
    protected void initViews() {

        etNickName.setText((String) SharedPreferenceUtils.get(mContext, "userName", "0"));
        tvTitle.setText("Change Device Name");

    }


    @Override
    protected void initEvents() {
        tvChangeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etNickName.getText().toString().trim().equals("")) {
                    EHomeInterface.getINSTANCE().modifyNickname(mContext, etNickName.getText().toString().trim(), new UserCallback() {
                        @Override
                        public void onSuccess(Response<BaseModelResponse<User>> response) {
                            if (response.body().isSuccess()) {
                                Toast.makeText(mContext, "Change nickname success!.", Toast.LENGTH_SHORT).show();
                                SharedPreferenceUtils.put(mContext, "userName", etNickName.getText().toString().trim());

                                finish();
                            } else {
                                Toast.makeText(mContext, "Change nickname failed!", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onError(Response<BaseModelResponse<User>> response) {
                            super.onError(response);
                            Toast.makeText(mContext, "Change nickname failed!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    Toast.makeText(mContext, "Device name cannot be empty.", Toast.LENGTH_SHORT).show();
                }
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

    }

}
