package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.BaseModelResponse;
import com.d9lab.ati.whatiesdk.bean.User;
import com.d9lab.ati.whatiesdk.callback.UserCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/7.
 */

public class SignUpActivity extends BaseActivity {
    @Bind(R.id.et_signUp_email)
    EditText etEmail;
    @Bind(R.id.et_signUp)
    EditText etPwd;
    @Bind(R.id.tv_signUp_signUp)
    TextView buttonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
    @Override
    protected int getContentViewId() {
        return R.layout.act_sign_up;
    }

    @Override
    protected void initViews() {
        setTitle("Sign Up");
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    @OnClick({R.id.tv_signUp_signUp,R.id.iv_signUp_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_signUp_back:
                finish();
                break;
            case R.id.tv_signUp_signUp:
                if(etEmail.getText().toString().trim().equals("") ||etPwd.getText().toString().trim().equals("")) {
                    Toast.makeText(mContext, "email or password can not be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    buttonLogin.setClickable(false);

                    EHomeInterface.getINSTANCE().registerAccountWithEmail(mContext, etEmail.getText().toString().trim(), etPwd.getText().toString().trim(),
                            new UserCallback() {
                                @Override
                                public void onSuccess(Response<BaseModelResponse<User>> response) {
                                    if (response.body().isSuccess()) {
                                        Toast.makeText(mContext, "Sign up success.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        buttonLogin.setClickable(true);
                                        Toast.makeText(mContext, "Sign up fail.", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onError(Response<BaseModelResponse<User>> response) {
                                    super.onError(response);
                                    buttonLogin.setClickable(true);
                                    Toast.makeText(mContext, "Sign up fail.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                break;

        }
    }

}
