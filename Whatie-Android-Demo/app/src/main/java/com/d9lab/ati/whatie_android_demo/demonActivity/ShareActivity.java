package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatiesdk.bean.SharedInfo;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.FastjsonUtils;
import com.mylhyl.zxing.scanner.encode.QREncode;

import butterknife.Bind;

/**
 * Created by liz on 2018/4/26.
 */

public class ShareActivity extends BaseActivity {
    @Bind(R.id.iv_device_qrcode)
    ImageView ivDeviceQrcode;

    private int deviceId;

    @Override
    protected int getContentViewId() {
        return R.layout.act_share;
    }

    @Override
    protected void initViews() {
        setTitle("Share Device");
        deviceId = getIntent().getIntExtra(Code.DEVICE_ID, -1);
        SharedInfo sharedInfo = new SharedInfo(EHome.getInstance().getmUser().getId(), deviceId, System.currentTimeMillis());
        Bitmap bitmap = new QREncode.Builder(mContext)
                .setContents(FastjsonUtils.serialize(sharedInfo))
                .build().encodeAsBitmap();
        ivDeviceQrcode.setImageBitmap(bitmap);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

}
