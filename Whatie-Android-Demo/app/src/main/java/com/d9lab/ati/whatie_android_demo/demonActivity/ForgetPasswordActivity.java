package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.demonActivity.widget.TimerTextView;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/8.
 */

public class ForgetPasswordActivity extends BaseActivity {
    @Bind(R.id.et_recover_password_email)
    EditText etRecoverPasswordEmail;
    @Bind(R.id.iv_recover_pwd_back)
    ImageView ivRecoverPwdBack;
    @Bind(R.id.cdtv_send_pin)
    TimerTextView cdtvSendPin;
    @Bind(R.id.et_change_pwd_pin)
    EditText etChangePwdPin;
    @Bind(R.id.tv_recover_password)
    TextView tvRecoverPassword;

    private static final String TAG = "ForgetPasswordActivity";


    @Override
    protected int getContentViewId() {
        return R.layout.act_forget_login_password;
    }


    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {
//        EHomeInterface.getINSTANCE().sendVerifyCodeByEmail(mContext, );
    }


    public void getPin(String email) {
        /**
         *      获取验证码
         *
         */
        EHomeInterface.getINSTANCE().sendVerifyCodeByEmail(mContext, email, new BaseCallback() {
            @Override
            public void onSuccess(Response<BaseResponse> response) {
                if (response.body().isSuccess()) {
                    Toast.makeText(mContext, "Send Pin success.", Toast.LENGTH_SHORT).show();
                    cdtvSendPin.countdownStart();

                } else {
                    Toast.makeText(mContext, "Send Pin failed.", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onError(Response<BaseResponse> response) {
                super.onError(response);
                Toast.makeText(mContext, "Send Pin failed.", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void verifyPin(final String email, String pin) {
        /**
         *      确认验证码
         *
         */

        EHomeInterface.getINSTANCE().checkVerifyCode(mContext,
                etRecoverPasswordEmail.getText().toString().trim(),
                etChangePwdPin.getText().toString().trim(),
                new BaseCallback() {
                    @Override
                    public void onSuccess(Response<BaseResponse> response) {
                        if (response.body().isSuccess()) {
                            Toast.makeText(mContext, "Verify Pin success.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgetPasswordActivity.this, SetNewPasswordActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(mContext, "Verify Pin failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    @OnClick({R.id.tv_recover_password, R.id.iv_recover_pwd_back, R.id.cdtv_send_pin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_recover_password:
                String email = etRecoverPasswordEmail.getText().toString().trim();
                String pin = etChangePwdPin.getText().toString().trim();
                if (!email.equals("") && !pin.equals("")) {
                    verifyPin(email, pin);
                } else {
                    Toast.makeText(mContext, getString(R.string.forget_password_tip), Toast.LENGTH_SHORT).show();

//                    ToastUtils.show(mContext, getString(R.string.forget_password_tip), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.iv_recover_pwd_back:
                finish();
                break;
            case R.id.cdtv_send_pin:
                String email1 = etRecoverPasswordEmail.getText().toString().trim();
                if (!email1.equals("")) {
                    Log.d(TAG, email1);
                    getPin(email1);
                } else {
                    Toast.makeText(mContext, getString(R.string.forget_password_email), Toast.LENGTH_SHORT).show();

//                    ToastUtils.show(mContext,  getString(R.string.forget_password_email), Toast.LENGTH_SHORT);
                }
                break;

        }

    }
}







