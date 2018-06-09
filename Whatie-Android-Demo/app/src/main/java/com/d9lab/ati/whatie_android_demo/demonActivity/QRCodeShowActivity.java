package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.InviteMemberInfo;
import com.d9lab.ati.whatiesdk.bean.QRCodeInfo;
import com.d9lab.ati.whatiesdk.bean.SharedDeviceInfo;
import com.d9lab.ati.whatiesdk.bean.TransferHomeInfo;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.FastjsonUtils;
import com.d9lab.ati.whatiesdk.util.QRCodeCons;
import com.d9lab.ati.whatiesdk.util.SharedPreferenceUtils;
import com.mylhyl.zxing.scanner.encode.QREncode;

import butterknife.Bind;

public class QRCodeShowActivity extends BaseActivity {

    @Bind(R.id.iv_qrcode_show)
    ImageView ivQRCodeShow;

    @Override
    protected int getContentViewId() {
        return R.layout.act_qrcode_show;
    }

    @Override
    protected void initViews() {
        Intent it = getIntent();
        switch (it.getIntExtra(QRCodeCons.USAGE_INTENT, 1)) {
            case QRCodeCons.USAGE.INVITE_MEMBER:
                InviteMemberInfo imInfo = new InviteMemberInfo(it.getIntExtra(QRCodeCons.PARAMS.HOME_ID, -1),
                        System.currentTimeMillis());
                QRCodeInfo<InviteMemberInfo> qrCodeInfo1= new QRCodeInfo<>(QRCodeCons.USAGE.INVITE_MEMBER, imInfo);
                generateQRCode(qrCodeInfo1);
                break;
            case QRCodeCons.USAGE.SHARE_DEVICE:
                SharedDeviceInfo sdInfo = new SharedDeviceInfo((int) SharedPreferenceUtils.
                        get(mContext, Code.SP_USER_ID, -1),
                        it.getIntExtra(QRCodeCons.PARAMS.DEVICE_ID, -1),
                        System.currentTimeMillis());
                QRCodeInfo<SharedDeviceInfo> qrCodeInfo2 = new QRCodeInfo<>(QRCodeCons.USAGE.SHARE_DEVICE, sdInfo);
                generateQRCode(qrCodeInfo2);
                break;
            case QRCodeCons.USAGE.TRANSFER_HOME:
                TransferHomeInfo thInfo = new TransferHomeInfo(it.getIntExtra(QRCodeCons.PARAMS.HOME_ID, -1),
                        it.getIntExtra(QRCodeCons.PARAMS.HOST_ID, -1),
                        System.currentTimeMillis());
                QRCodeInfo<TransferHomeInfo> qrCodeInfo3 = new QRCodeInfo<>(QRCodeCons.USAGE.TRANSFER_HOME, thInfo);
                generateQRCode(qrCodeInfo3);
                break;
        }
    }

    private void generateQRCode(QRCodeInfo<?> info) {
        Bitmap bitmap = new QREncode.Builder(mContext)
                .setContents(FastjsonUtils.serialize(info))
                .build().encodeAsBitmap();
        ivQRCodeShow.setImageBitmap(bitmap);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

}
