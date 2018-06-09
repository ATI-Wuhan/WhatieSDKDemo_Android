package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.Constant;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.bean.InviteMemberInfo;
import com.d9lab.ati.whatiesdk.bean.QRCodeInfo;
import com.d9lab.ati.whatiesdk.bean.SharedDeviceInfo;
import com.d9lab.ati.whatiesdk.bean.SharedInfo;
import com.d9lab.ati.whatiesdk.bean.TransferHomeInfo;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.FastjsonUtils;
import com.d9lab.ati.whatiesdk.util.QRCodeCons;
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
                performAction(rawResult);
            }
        });
    }

    @Override
    protected void initDatas() {

    }

    private void performAction(Result rawResult) {
        if ((rawResult != null) && (rawResult.getText() != null)) {
            if (EHome.getInstance().isLogin()) {
                try {
                    int usage = FastjsonUtils.deserialize(rawResult.getText(), QRCodeInfo.class).getUsage();
                    switch (usage) {
                        case QRCodeCons.USAGE.INVITE_MEMBER:
                            QRCodeInfo<InviteMemberInfo> info1 = JSON.parseObject(rawResult.getText(), new TypeReference<QRCodeInfo<InviteMemberInfo>>() {
                            });
                            InviteMemberInfo imInfo = info1.getInfoObj();
                            onInviteMember(imInfo.getHomeId(), imInfo.getTimeStamp());
                            break;
                        case QRCodeCons.USAGE.SHARE_DEVICE:
                            QRCodeInfo<SharedDeviceInfo> info2 = JSON.parseObject(rawResult.getText(), new TypeReference<QRCodeInfo<SharedDeviceInfo>>() {
                            });
                            SharedDeviceInfo sdInfo = info2.getInfoObj();
                            onAddSharedDevice(sdInfo.getAdminId(), sdInfo.getDeviceId(), sdInfo.getTimestamp());
                            break;
                        case QRCodeCons.USAGE.TRANSFER_HOME:
                            QRCodeInfo<TransferHomeInfo> info3 = JSON.parseObject(rawResult.getText(), new TypeReference<QRCodeInfo<TransferHomeInfo>>() {
                            });
                            TransferHomeInfo thInfo = info3.getInfoObj();
                            onTransferHome(thInfo.getHomeId(), thInfo.getHostId(), thInfo.getTimestamp());
                            break;
                    }
                    svScanQrcode.onPause();
                } catch (Exception e) {
                    svScanQrcode.onResume();
                }
            } else {
                startActivity(new Intent(ScanActivity.this, LoginActivity.class));
            }
        }
    }

    private void onInviteMember(int homeId, long timeStamp) {
    }

    private void onAddSharedDevice(int adminId, int deviceId, long timestamp) {
    }

    private void onTransferHome(int homeId, int hostId, long timestamp) {
    }

}
