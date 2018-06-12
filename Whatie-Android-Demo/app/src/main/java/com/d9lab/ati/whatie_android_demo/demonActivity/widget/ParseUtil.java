package com.d9lab.ati.whatie_android_demo.demonActivity.widget;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 神火 on 2018/6/12.
 */

public class ParseUtil {
    public static String dateToString(Date date, String type) {
        String str = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (type.equals("SHORT")) {
            // 07-1-18
            format = DateFormat.getDateInstance(DateFormat.SHORT);
            str = format.format(date);
        } else if (type.equals("MEDIUM")) {
            // 2007-1-18
            //  format = DateFormat.getDateInstance(DateFormat.MEDIUM);
            str = format.format(date);
        } else if (type.equals("MEDIUMTX")) {
            // 2007-1-18
            format = new SimpleDateFormat("yyyy年MM月dd日");
            str = format.format(date);
        } else if (type.equals("FULL")) {
            // 2007年1月18日 周四
            format = DateFormat.getDateInstance(DateFormat.FULL);
            str = format.format(date);
        } else if (type.equals("FULLHm")) {
            // yyyy-MM-dd HH:mm
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            str = format.format(date);
        } else if (type.equals("FULLHmTx")) {
            // yyyy-MM-dd HH:mm
            format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            str = format.format(date);
        } else if (type.equals("FULLHms")) {
            // yyyy-MM-dd HH:mm:ss
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = format.format(date);
        } else if (type.equals("Hm")) {
            // HH:mm
            format = new SimpleDateFormat("HH:mm");
            str = format.format(date);
        } else if (type.equals("ms")) {
            // mm:ss
            format = new SimpleDateFormat("mm:ss");
            str = format.format(date);
        }else if (type.equals("Hms")) {
            // HH:mm:ss
            format = new SimpleDateFormat("HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));
            str = format.format(date);
        }
        return str;

    }

    /**
     * 获取指定日期的偏移日期
     *
     * @param date     Date类型
     * @param dayCount 负：前多少天，正：后多少天
     * @return Date类型
     * * 并将获取的时间偏移结果格式化
     */
    public static String getDayBefore(Date date, int dayCount) {
        if (dayCount == 0) {
            return "今日方案";
        }
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        c.setTime(date);
        c.set(Calendar.DATE, c.get(Calendar.DATE) + dayCount);
        int day = c.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }

    public static Date stringToDate(String str, String type) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        if (type.equals("Hm")) {
            format = new SimpleDateFormat("HH:mm");
        }
        try {
            // Fri Feb 24 00:00:00 CST 2012
            date = format.parse(str);

        } catch (ParseException e) {
            e.printStackTrace();
        }
//	        // 2012-02-24
//	        date = java.sql.Date.valueOf(str);

        return date;
    }

    /**
     * 获取指定index位置的时间区间数组
     *
     * @param period
     * @param index
     * @return
     */
    public static String[] splitPeriod(String period, int index) {
        String[] str1 = period.split(",");
        return str1[index].split("-");

    }

    /**
     * 获取方案开始时间的时分
     *
     * @param timezone
     * @return
     */
    public static String[] splitTimeZone(String timezone) {
        String[] str1 = timezone.split(":");

        return str1;

    }

    public static String updatePlanTimeZone(String planTimeZone, String customerTime) {
        String[] str = customerTime.split(",");
        String[] str0 = splitPeriod(planTimeZone, 0);
        String[] str1 = splitPeriod(planTimeZone, 1);
        str0[0] = str[0];
        str1[0] = str[1];
        return str0[0] + "-" + str0[1] + "," + str1[0] + "-" + str1[1];

    }

    public static List<String> splitStringToList(String string) {
        List<String> list = new ArrayList<>();
        if (string.contains(",")) {
            String[] str1 = string.split(",");
            for (int i = 0; i < str1.length; i++) {
                list.add(str1[i]);
            }
        } else {
            list.add(string);
        }
        return list;
    }

//    public static void setImageHeight(View view, double proportion) {
//
//        int mHeight = (int) (Constant.SCREEN_WIDTH * proportion);
//        LayoutParams lp = view.getLayoutParams();
//        lp.height = mHeight;
//        view.setLayoutParams(lp);
//    }

    /**
     * 判断时间字符串大小
     *
     * @param startdate
     * @param enddate
     * @return
     */
    public static boolean compareDate(String startdate, String enddate) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        boolean isOver = false;
        System.out.println("dt1/dt2 " + startdate + "/" + enddate);
        try {
            Date dt1 = df.parse(startdate);
            Date dt2 = df.parse(enddate);
            if (dt1.getTime() > dt2.getTime()) {
                isOver = true;
            } else if (dt1.getTime() < dt2.getTime()) {
                isOver = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isOver;
    }



    /**
     * 加密算法，上传密码时使用
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString().toLowerCase();
    }

    public static void fixTextSize(TextView textView, String tagName) {
        if (tagName.length() == 3) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setText(tagName);
        } else if (tagName.length() > 3) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            textView.setText(tagName);
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(tagName);
        }

    }

    public static boolean isNotEmpty(String... s) {
        for (String string : s) {
            if (string != null && !string.isEmpty()) continue;
            return false;
        }
        return true;
    }


    /**
     * 剔除数字
     *
     * @param value
     */
    public static String removeDigital(String value) {

        Pattern p = Pattern.compile("[\\d]");
        Matcher matcher = p.matcher(value);
        String result = matcher.replaceAll("");
        return result;
    }

    /**
     * 剔除字母
     *
     * @param value
     */
    public static String removeLetter(String value) {
        Pattern p = Pattern.compile("[a-zA-z]");
        Matcher matcher = p.matcher(value);
        String result = matcher.replaceAll("");
        return result;
    }

    /**
     * 格式化百分数
     *
     * @param x
     * @param y
     * @return
     */
    public static String getPercent(int x, int y) {
        double percentX = x * 1.0;
        double percentY = y * 1.0;
        double percent = percentX / percentY;
        //  DecimalFormat df = new DecimalFormat("##.00%");
        DecimalFormat df = new DecimalFormat("##%");
        return df.format(percent);
    }

    public static String getPercentWithdecimal(int x, int y) {
        double percentX = x * 1.0;
        double percentY = y * 1.0;
        double percent = percentX / percentY;
        DecimalFormat df = new DecimalFormat("##.00%");
        return df.format(percent);
    }

    /**
     * 格式化一位小数
     *
     * @param x
     * @param y
     * @return
     */
    public static String getDecimalForOne(int x, int y) {
        double decimalX = x * 1.0;
        double decimalY = y * 1.0;
        double decimal = decimalX / decimalY;
        NumberFormat df = new DecimalFormat("#.0");
        return df.format(decimal);
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);

        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); //以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

        return output;
    }
    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
    //获取屏幕的高度
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }
    public static int dip2px(Context context, int dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, int pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        // 有存储的SDCard
        return state.equals(Environment.MEDIA_MOUNTED);
    }

//    /**
//     * 保存指定搜索记录
//     * @param context
//     * @param text
//     * @param keyPath
//     */
//    public static void saveHistory(Context context, String text, String keyPath) {
//        String oldText = (String) SharedPrefUtils.get(context,keyPath, "");
//        StringBuilder builder = new StringBuilder(text);
//        if (!TextUtils.isEmpty(oldText)) {
//            builder.append("," + oldText);
//        }
//        if (!TextUtils.isEmpty(text) && !oldText.contains(text)) {
//            SharedPrefUtils.put(context, keyPath, builder.toString());
//        }
//        Logs.i("ParseUtil", "searchhistory:" + SharedPrefUtils.get(context,keyPath, ""));
//    }
//
//    public static void removeHistory(Context context, String string, String keyPath) {
//        String oldText = (String) SharedPrefUtils.get(context,keyPath, "");
//        StringBuilder builder = new StringBuilder(oldText);
//        if (oldText.contains(string)) {
//            builder.delete(oldText.indexOf(string) == 0 ? oldText.indexOf(string) : (oldText.indexOf(string) - 1), oldText.indexOf(string) == 0 ? (oldText.indexOf(string) + string.length() + 1) : (oldText.indexOf(string) + string.length()));
//            SharedPrefUtils.put(context, keyPath, builder.toString());
//        }
//        Logs.i("ParseUtil", "searchhistory:" + SharedPrefUtils.get(context,keyPath, ""));
//    }
//    public static void initLocTx(final Context context, final TextView locationTx) {
//        locationTx.setText(SharedPrefUtils
//                .get(context, Constant.CHOOSE_CITY, SharedPrefUtils.get(context, Constant.LOC_CITY, "武汉市"))
//                .toString());
//        if (!SharedPrefUtils
//                .get(context, Constant.CHOOSE_CITY, SharedPrefUtils.get(context, Constant.LOC_CITY, "武汉市"))
//                .equals(SharedPrefUtils.get(context, Constant.LOC_CITY, "武汉市"))) {
//            new AlertDialog.Builder(context).setTitle("提示").setMessage("您选择的城市与定位城市不同，是否切换为定位城市？")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            locationTx.setText(SharedPrefUtils.get(context, Constant.LOC_CITY, "武汉市").toString());
//                            SharedPrefUtils.put(context, Constant.CHOOSE_CITY,
//                                    SharedPrefUtils.get(context, Constant.LOC_CITY, "武汉市"));
//                            SharedPrefUtils.put(context, Constant.CHOOSE_LAT,
//                                    SharedPrefUtils.get(context, Constant.LOC_LAT, "30.54"));
//                            SharedPrefUtils.put(context, Constant.CHOOSE_LNG,
//                                    SharedPrefUtils.get(context, Constant.LOC_LNG, "114.24"));
//                        }
//                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    return;
//                }
//            }).show();
//        }
//    }
//
//    public static void judgeLoction(Context context, Info info) {
//        //如果已安装,
//        if(isAvilible(context,"com.baidu.BaiduMap")) {//传入指定应用包名
//            //MyToast.showToast(this,"即将用百度地图打开导航");
//            Uri mUri = Uri.parse("geo:"+info.getLatitude() + "," +info.getLongitude()+"?q="+info.getName());
//            Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
//            context.startActivity(mIntent);
//        }else if(isAvilible(context,"com.autonavi.minimap")){
//            // MyToast.showToast(this,"即将用高德地图打开导航");
//            Uri mUri = Uri.parse("geo:"+info.getLatitude()+","+info.getLongitude()+"?q="+info.getName());
//            Intent intent = new Intent("android.intent.action.VIEW",mUri);
//            context.startActivity(intent);
//        }else {
//            MyToast.showToast(context,"请安装第三方地图方可导航");
//            return;
//        }
//    }

    /**
     * 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName：应用包名
     * @return
     */
    private static boolean isAvilible(Context context, String packageName){
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
}
