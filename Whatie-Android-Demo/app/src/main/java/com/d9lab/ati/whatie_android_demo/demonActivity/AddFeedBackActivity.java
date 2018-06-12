package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.demonActivity.widget.Constants;
import com.d9lab.ati.whatie_android_demo.demonActivity.widget.ParseUtil;
import com.d9lab.ati.whatiesdk.bean.BaseResponse;
import com.d9lab.ati.whatiesdk.callback.BaseCallback;
import com.d9lab.ati.whatiesdk.ehome.EHome;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.d9lab.ati.whatiesdk.util.Code;
import com.d9lab.ati.whatiesdk.util.SharedPreferenceUtils;
import com.lzy.okgo.model.Response;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 神火 on 2018/6/12.
 */

public class AddFeedBackActivity extends BaseActivity{
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


    @Bind(R.id.iv_feedback_image)
    ImageView ivFeedbackImage;
    @Bind(R.id.rl_feedback)
    RelativeLayout rlFeedback;
    @Bind(R.id.et_feedback)
    EditText etFeedback;
    @Bind(R.id.iv_feedback_add)
    ImageView ivFeedbackAdd;
    @Bind(R.id.tv_feedback)
    TextView tvFeedback;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int GALLERY = 0xa0;
    public static final int STORAGE = 0;
    private static String FEEDBACK_PIC = "FeedbackCamera.jpg";

    private PopupWindow popupWindow;
    private View view;
    private TextView albumTextv;
    private TextView cameraTextv;
    private TextView cancleTx;
//    private Bitmap photo;
    private boolean hasPhoto = false;

    private File photo=null;
    @Override
    protected int getContentViewId() {
        return R.layout.act_feedback_add;
    }

    @Override
    protected void initViews() {
        tvTitle.setText(getString(R.string.Feedback));
        tvTitleRight.setText(getString(R.string.feedback_submit));
        tvTitleRight.setVisibility(View.VISIBLE);
        if((int) SharedPreferenceUtils.get(mContext, Code.SP_USER_ID, -1) != -1){
            tvFeedback.setText(getString(R.string.feedback_from)+(String ) SharedPreferenceUtils.get(mContext, Code.SP_USER_EMAIL, ""));
        }
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initDatas() {

    }

    @OnClick({R.id.ll_title_left, R.id.rl_feedback, R.id.ll_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_title_left:
                finish();
                break;
            case R.id.rl_feedback:
                if (EHome.getInstance().isLogin()) {
                    showPopWindow();
                }else {
                    startLoginAct();
                }
                break;
            case R.id.ll_title_right:
                if (EHome.getInstance().isLogin()) {
                    if (etFeedback.getText().toString().trim().equals("")) {
                        Toast.makeText(mContext, getString(R.string.feedback_tip), Toast.LENGTH_SHORT).show();
                    }else {
                        addFeedback();
                    }
                }else {
                    startLoginAct();
                }
                break;
        }

    }
    private void startLoginAct(){
        if((int) SharedPreferenceUtils.get(mContext, Code.SP_USER_ID, -1) != -1){
            Toast.makeText(mContext, "Local state. Please login first.", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    private void addFeedback(){
        EHomeInterface.getINSTANCE().addFeedback(mContext,etFeedback.getText().toString().trim(), photo,new BaseCallback() {
            @Override
            public void onSuccess(Response<BaseResponse> response) {
                if (response.body().isSuccess()){
                    Toast.makeText(mContext, "Feedback added success!", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Response<BaseResponse> response) {
                super.onError(response);
                Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case CODE_GALLERY_REQUEST:
                    try {
                        Bitmap b =  BitmapFactory.decodeStream(getContentResolver().openInputStream(intent.getData()));
                        File file = new File(Constants.getAppImageFolder(), FEEDBACK_PIC);
                        if(!file.exists()) file.createNewFile();
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (ParseUtil.hasSdcard()) {
                        File tempFile = new File(Constants.getAppImageFolder(), FEEDBACK_PIC);
                        cropRawPhoto(Uri.fromFile(tempFile));
                    }
                    break;

                case CODE_CAMERA_REQUEST:
                    if (ParseUtil.hasSdcard()) {
                        File tempFile =  new File(Constants.getAppImageFolder(), FEEDBACK_PIC);
                        cropRawPhoto(Uri.fromFile(tempFile));
                    }
                    break;

                case CODE_RESULT_REQUEST:
                    if (intent != null) {
                        Picasso.with(mContext)
                                .load(new File(Constants.getAppImageFolder(), FEEDBACK_PIC))
                                .error(R.mipmap.default_image)//
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//
                                .into(ivFeedbackImage);
                        ivFeedbackImage.setVisibility(View.VISIBLE);
                    }

                    break;
            }

            if (photo != null) {
                hasPhoto = true;
            }
        }
    }

    private void showPopWindow() {
        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.pop_select_photo, null);
            popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        // 设置动画
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimationFromBottom);
        // 聚焦，不然无法监听点击
        popupWindow.setFocusable(true);
        // 设置在外面点击时候，该PopupWindow小弹窗消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(AddFeedBackActivity.this.findViewById(R.id.ll_feedback_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);


        // 监听事件，有两个，一个是触摸外部时关闭的事件，一个触摸小窗口里时的菜单（照片或拍照）的事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                popupWindow.dismiss();
            }
        });

        // 第二个，点击照片或拍照，要发生跳转
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        albumTextv = (TextView) view.findViewById(R.id.tv_select_photo_form_album);
        cameraTextv = (TextView) view.findViewById(R.id.tv_select_photo_form_camera);
        cancleTx = (TextView) view.findViewById(R.id.tv_select_photo_cancel);
        albumTextv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                choseImageFromGallery();
            }
        });
        cameraTextv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                choseImageFromCameraCapture();
            }
        });
        cancleTx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    // 从本地相册选取图片作为头像
    protected void choseImageFromGallery() {
        requestPermission();
    }

    // 启动手机相机拍摄照片作为头像
    private void choseImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (ParseUtil.hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Constants.getAppImageFolder(), FEEDBACK_PIC)));
        }
        try {
            startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
        }catch (SecurityException e){
            Toast.makeText(mContext, "Please accept camera permission first.", Toast.LENGTH_SHORT).show();

        }

    }



    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("scale", true);
        intent.putExtra("crop", true);

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        /* intent.putExtra("return-data", true); */
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE);
            } else {
                Intent intentFromGallery = new Intent("android.intent.action.GET_CONTENT");
                intentFromGallery.setType("image/*");
                try {
                    startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
                }catch (SecurityException e){
                    Toast.makeText(mContext, "Please accept storage permission first.", Toast.LENGTH_SHORT).show();

                }
            }
        } else {
            try {
                Intent intentFromGallery = new Intent("android.intent.action.GET_CONTENT");
                intentFromGallery.setType("image/*");
                startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
            } catch (Exception e) {
                Toast.makeText(mContext, "Please accept storage permission first.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Please accept storage permission first.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
