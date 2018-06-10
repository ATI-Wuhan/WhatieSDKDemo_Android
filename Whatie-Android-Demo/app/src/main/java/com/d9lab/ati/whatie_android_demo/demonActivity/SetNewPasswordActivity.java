package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.LogUtil;
import com.d9lab.ati.whatiesdk.util.MD5Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/8.
 *
 * 这里的重置密码我没找到应该用哪个接口，所以先没写
 *
 *
 */

public class SetNewPasswordActivity extends BaseActivity {
    private static final String TAG = "SetNewPasswordActivity";
    @Bind(R.id.et_new_password)
    EditText etNewPassword;
    @Bind(R.id.et_new_password_confirm)
    EditText etNewPasswordConfirm;
    @Bind(R.id.iv_recover_pwd_back)
    ImageView ivRecoverPwdBack;
    @Bind(R.id.tv_set_new_password)
    TextView tvSetNewPassword;

    private String email;

    @Override
    protected int getContentViewId() {
        return R.layout.act_reset_password;
    }



    @Override
    protected void initViews() {
        email = getIntent().getStringExtra("email");
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    public void setNewPassword(final String password) {
        EHomeInterface.getINSTANCE().resetPasswordByEmail(mContext, email, password, new BaseCallback() {
            @Override
            public void onSuccess(Response<BaseResponse> response) {
                Toast.makeText(mContext, "Set new password success.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Response<BaseResponse> response) {
                super.onError(response);
                Toast.makeText(mContext, "Set new password failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(){

    }

    @OnClick({R.id.iv_recover_pwd_back, R.id.tv_set_new_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_recover_pwd_back:
                finish();
                break;
            case R.id.tv_set_new_password:
                String password1 = etNewPassword.getText().toString().trim();
                String Password2 = etNewPasswordConfirm.getText().toString().trim();
                if (password1.equals("") || Password2.equals("")){
                    LogUtil.log(TAG, "new pwd"+password1 +"  confirm:"+Password2);
                    Toast.makeText(SetNewPasswordActivity.this,getString(R.string.change_password_tip),
                            Toast.LENGTH_SHORT).show();
                } else{
                    if (!password1.equals(Password2)) {
                        Toast.makeText(SetNewPasswordActivity.this,getString(R.string.change_password_match),
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        setNewPassword(password1);
                    }
                }
                break;
        }
    }
}

