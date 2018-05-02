package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/4/27.
 */

public class ChangePwdActivity extends BaseActivity {
    @Bind(R.id.et_email)
    EditText etEmail;
    @Bind(R.id.et_old_pwd)
    EditText etOldPwd;
    @Bind(R.id.et_new_pwd)
    EditText etNewPwd;
    @Bind(R.id.button_confirm_change_pwd)
    Button buttonConfirmChangePwd;

    @Override
    protected int getContentViewId() {
        return R.layout.act_change_pwd;
    }

    @Override
    protected void initViews() {
        setTitle("Change Password");
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    @OnClick(R.id.button_confirm_change_pwd)
    public void onViewClicked() {
        if(etEmail.getText().toString().trim().equals("")
                ||etOldPwd.getText().toString().trim().equals("")
                ||etNewPwd.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, "email or password can not be empty.", Toast.LENGTH_SHORT).show();
        } else {
            buttonConfirmChangePwd.setClickable(false);
            EHomeInterface.getINSTANCE().changePwd(mContext, etEmail.getText().toString().trim(),
                    etOldPwd.getText().toString().trim(),
                    etNewPwd.getText().toString().trim(),
                    Constant.ACCESS_ID,
                    Constant.ACCESS_KEY,
                    new BaseCallback() {
                        @Override
                        public void onSuccess(Response<BaseResponse> response) {
                            buttonConfirmChangePwd.setClickable(true);
                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().isSuccess()){
                                finish();
                            }
                        }

                        @Override
                        public void onError(Response<BaseResponse> response) {
                            super.onError(response);
                            buttonConfirmChangePwd.setClickable(true);
                            Toast.makeText(mContext, "Change pwd fail.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
