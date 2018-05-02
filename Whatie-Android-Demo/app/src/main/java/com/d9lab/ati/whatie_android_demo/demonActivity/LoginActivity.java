package com.d9lab.ati.whatie_android_demo.demonActivity;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatiesdk.bean.BaseModelResponse;
import com.d9lab.ati.whatiesdk.bean.User;
import com.d9lab.ati.whatiesdk.callback.UserCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/4/24.
 */

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    @Bind(R.id.et_email)
    EditText etEmail;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.button_login)
    Button buttonLogin;


    @Override
    protected int getContentViewId() {
        return R.layout.act_login;
    }

    @Override
    protected void initViews() {
        setTitle("Login");
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    @OnClick({R.id.button_login, R.id.button_change_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                if(etEmail.getText().toString().trim().equals("") ||etPwd.getText().toString().trim().equals("")) {
                    Toast.makeText(mContext, "email or password can not be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    buttonLogin.setClickable(false);

                    EHomeInterface.getINSTANCE().login(mContext, etEmail.getText().toString().trim(), etPwd.getText().toString().trim(), Constant.ACCESS_ID, Constant.ACCESS_KEY,
                            new UserCallback() {
                                @Override
                                public void onSuccess(Response<BaseModelResponse<User>> response) {
                                    if (response.body().isSuccess()) {
                                        EHome.getInstance().setLogin(true);
                                        EHome.getInstance().setmUser(response.body().getValue());
                                        EHome.getInstance().setToken(response.body().getToken());

                                        startActivity(new Intent(LoginActivity.this, DeviceListActivity.class));
                                        finish();
                                    } else {
                                        buttonLogin.setClickable(true);
                                        Toast.makeText(mContext, "Login fail.", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onError(Response<BaseModelResponse<User>> response) {
                                    super.onError(response);
                                    buttonLogin.setClickable(true);
                                    Toast.makeText(mContext, "Login fail.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                break;
            case R.id.button_change_pwd:
                startActivity(new Intent(LoginActivity.this, ChangePwdActivity.class));
                break;
        }
    }
}
