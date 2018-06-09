package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.BaseModelResponse;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.User;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.callback.UserCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.SharedPreferenceUtils;
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/7.
 */

public class ProfileFragment extends BaseFragment {
    @Bind(R.id.tv_profile_name)
    TextView uername;
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
    protected int getFragmentLayoutId() {
        return R.layout.frag_profile;
    }


    @Override
    protected void initViews() {
        uername.setText((String) SharedPreferenceUtils.get(getContext(), "userName", "userName"));
        tvTitle.setText("Profile");
        llTitleLeft.setVisibility(View.GONE);
        llTitleRight.setVisibility(View.GONE);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    @Override
    public void onResume() {
        super.onResume();
        uername.setText((String) SharedPreferenceUtils.get(getContext(), "userName", "userName"));

    }

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @OnClick({R.id.rl_profile_update_pwd, R.id.rl_profile_menu_receive_shared_device, R.id.rl_profile_log_out, R.id.rl_profile_change_nickname})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_profile_update_pwd:

                startActivity(new Intent(getActivity(), ChangePwdActivity.class));
                break;
            case R.id.rl_profile_menu_receive_shared_device:
                startActivity(new Intent(getActivity(), SharedDeviceListActivity.class));
                break;

            case R.id.rl_profile_log_out:
                EHomeInterface.getINSTANCE().logOut(mContext,
                        new BaseCallback() {
                            @Override
                            public void onSuccess(Response<BaseResponse> response) {
                                Toast.makeText(mContext, "Logout success!", Toast.LENGTH_SHORT).show();
                                EHome.getInstance().logOut(); // This method must be called when logout success.
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }

                            @Override
                            public void onError(Response<BaseResponse> response) {
                                super.onError(response);
                            }
                        });
                break;
            case R.id.rl_profile_change_nickname:
                startActivity(new Intent(getActivity(), ChangeNickNameActivity.class));
                break;
        }
    }


}
