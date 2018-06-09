package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.SharedInfo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.FastjsonUtils;
import com.d9lab.ati.whatiesdk.util.SharedPreferenceUtils;
import com.lzy.okgo.model.Response;
import com.mylhyl.zxing.scanner.encode.QREncode;

import butterknife.Bind;

/**
 * Created by liz on 2018/4/26.
 */

public class ShareActivity extends BaseActivity {
    @Bind(R.id.et_shared_email)
    EditText etSharedEmail;
    @Bind(R.id.tv_share_button)
    TextView confirm;
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
    private int deviceId;

    @Override
    protected int getContentViewId() {
        return R.layout.act_share_with_email;
    }

    @Override
    protected void initViews() {
        tvTitle.setText("Share Device");
        this.deviceId = getIntent().getIntExtra(Code.DEVICE_ID, -1);

    }

    @Override
    protected void initEvents() {
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etSharedEmail.getText().toString().trim().equals("")) {
                    EHomeInterface.getINSTANCE().addShare(mContext, (int) SharedPreferenceUtils.get(mContext, "userId", -1), etSharedEmail.getText().toString().trim(), deviceId, new BaseCallback() {
                        @Override
                        public void onSuccess(Response<BaseResponse> response) {
                            if(response.body().isSuccess()){
                                if (((int)SharedPreferenceUtils.get(mContext, "userId", -1) )!= -1) {
                                    Toast.makeText(mContext, "Share success.", Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {
                                    Toast.makeText(mContext, "Share failed.", Toast.LENGTH_SHORT).show();

                                }
                            }
                            else {
                                Toast.makeText(mContext,response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            }


                        }

                        @Override
                        public void onError(Response<BaseResponse> response) {
                            super.onError(response);
                            Toast.makeText(mContext, "Share failed.", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void initDatas() {

    }

}
