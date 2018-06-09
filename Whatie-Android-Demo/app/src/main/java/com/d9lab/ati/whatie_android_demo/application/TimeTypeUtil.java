package com.d9lab.ati.whatie_android_demo.application;

/**
 * Created by liz on 2018/5/16.
 */

public class TimeTypeUtil {
    private static final String TAG = "TimeTypeUtil";


    public static String getRepeatDay(String timeType){
        String strt = "";
        if (timeType != null && !"".equals(timeType)) {
            if (!"111111".equals(timeType)) {
                if (timeType.charAt(0) == '1') {
                    strt = "Sunday" + " " + strt;
                }
                if (timeType.charAt(1) == '1') {
                    strt = "Saturday" + " " + strt;
                }
                if (timeType.charAt(2) == '1') {
                    strt = "Friday" + " " + strt;
                }
                if (timeType.charAt(3) == '1') {
                    strt = "Thursday" + " " + strt;
                }
                if (timeType.charAt(4) == '1') {
                    strt = "Wednesday" + " " + strt;
                }
                if (timeType.charAt(5) == '1') {
                    strt = "Tuesday" + " " + strt;
                }
                if (timeType.charAt(6) == '1') {
                    strt = "Monday" + " " + strt;
                }
            }
            if (timeType.equals("1111111")) {
                strt = "Everyday";
            }
            if (timeType.equals("0000000")) {
                strt = "Only once";
            }
        }
        return strt;
    }
}
