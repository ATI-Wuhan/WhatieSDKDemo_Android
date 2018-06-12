package com.d9lab.ati.whatie_android_demo.demonActivity.widget;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by 神火 on 2018/6/12.
 */

public class Constants {
    private static final String parentPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private static final String APP_FOLDER_NAME = "eHome";
    private static final String IMAGE_FOLDER_NAME = "image";
    private static final String FILE_FOLDER_NAME = "file";
    private static final String LOG_FOLDER_NAME = "log";
    private static final String DOWNLOAD_FOLDER_NAME = "download";

    // 应用程序文件夹路径
    private static final String APP_FOLDER_PATH = parentPath + File.separator + APP_FOLDER_NAME;
    // 应用程序图片文件夹路径
    private static final String IMAGE_FOLDER_PATH = parentPath + File.separator + APP_FOLDER_NAME + File.separator + IMAGE_FOLDER_NAME;
    // 应用程序文件文件夹路径
    private static final String FILE_FOLDER_PATH = parentPath + File.separator + APP_FOLDER_NAME + File.separator + FILE_FOLDER_NAME;
    // 应用程序文件文件夹路径
    private static final String LOG_FOLDER_PATH = parentPath + File.separator + APP_FOLDER_NAME + File.separator + LOG_FOLDER_NAME;
    // 应用程序下载文件夹路径
    private static final String DOWNLOAD_FOLDER_PATH = parentPath + File.separator + APP_FOLDER_NAME + File.separator + DOWNLOAD_FOLDER_NAME;


    public static String getAppRootFolder() {
        File f = new File(APP_FOLDER_PATH);
        if(!f.exists()){
            f.mkdirs();
        }
        return APP_FOLDER_PATH;
    }

    public static String getAppImageFolder() {
        File f = new File(IMAGE_FOLDER_PATH);
        if(!f.exists()){
            f.mkdirs();
        }
        return IMAGE_FOLDER_PATH;
    }

    public static String getAppLogFolder() {
        File f = new File(LOG_FOLDER_PATH);
        if(!f.exists()){
            f.mkdirs();
        }
        return LOG_FOLDER_PATH;
    }

    public static String getAppDownloadFolder() {
        File f = new File(DOWNLOAD_FOLDER_PATH);
        if(!f.exists()){
            f.mkdirs();
        }
        return DOWNLOAD_FOLDER_PATH;
    }

    public static String getAppFileFolder() {
        File f = new File(FILE_FOLDER_PATH);
        if(!f.exists()){
            f.mkdirs();
        }
        return FILE_FOLDER_PATH;
    }

    public static String getCachePath(Context context){
        return context.getApplicationContext().getCacheDir().getAbsolutePath();
    }
}
