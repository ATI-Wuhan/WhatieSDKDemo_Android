package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.graphics.Bitmap;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.SharedInfo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.FastjsonUtils;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.lzy.okgo.model.Response;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;

import butterknife.Bind;

/**
 * Created by liz on 2018/4/25.
 */

public class ScanActivity extends BaseActivity {
    private static final String TAG = "ScanActivity";
    @Bind(R.id.sv_scan_qrcode)
    ScannerView svScanQrcode;


    @Override
    protected void onResume() {
        svScanQrcode.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        svScanQrcode.onPause();
        super.onPause();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_scan;
    }

    @Override
    protected void initViews() {
        setTitle("Scan QRcode");
        svScanQrcode.setDrawText(getString(R.string.scanQR_code), true);
    }

    @Override
    protected void initEvents() {
        svScanQrcode.setOnScannerCompletionListener(new OnScannerCompletionListener() {
            @Override
            public void OnScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
                if (rawResult.getText() != null) {
                    if (EHome.getInstance().isLogin()) {
                        try {
                            svScanQrcode.onPause();
                            SharedInfo s = FastjsonUtils.deserialize(rawResult.getText(), SharedInfo.class);
                            EHomeInterface.getINSTANCE().addSharedDevice(mContext, s.getAdminId(), s.getDeviceId(), s.getTimestamp(), Constant.ACCESS_ID, Constant.ACCESS_KEY,new BaseCallback() {
                                @Override
                                public void onSuccess(Response<BaseResponse> response) {
                                    if(response.body().isSuccess()){
                                        startActivity(new Intent(ScanActivity.this, DeviceListActivity.class));
                                    }else {
                                        svScanQrcode.onResume();
                                    }
                                }

                                @Override
                                public void onError(Response<BaseResponse> response) {
                                    super.onError(response);
                                    svScanQrcode.onResume();
                                }
                            });
                        }catch (Exception e){
                            svScanQrcode.onResume();
                        }
                    } else {
                        startActivity(new Intent(ScanActivity.this, LoginActivity.class));
                    }
                }
            }
        });
    }

    @Override
    protected void initDatas() {

    }

}
