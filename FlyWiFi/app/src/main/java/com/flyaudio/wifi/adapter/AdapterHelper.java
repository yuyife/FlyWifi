package com.flyaudio.wifi.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;

import com.flyaudio.wifi.R;

/**
 * @author yuyife
 *         Created by Administrator on 2016/7/4.
 */
public class AdapterHelper {
    public static boolean isLock = false;//标记当前网络是否有密码
    public static String levelString = null;//标记当前网络的信号强弱
    private final static String GOOD = "强";
    private final static String GOOD_1 = "较强";
    private final static String GOOD_2 = "一般";
    private final static String GOOD_3 = "差";

    /**
     * 返回当前信号对应的图片
     */
    public static Drawable getSignalDrawable(Context context, boolean isLock, int level) {
        if (isLock) {
            // 有锁
            if (level <= 0 && level >= -10) {
                // 0 - -10
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4);
            } else if (level < -10 && level >= -20) {
                // -11 - -20
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4);
            } else if (level < -20 && level >= -30) {
                // -21 - -30
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4);
            } else if (level < -30 && level >= -40) {
                // -11 - -20
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4);
            } else if (level < -40 && level >= -50) {
                // -11 - -20
                levelString = GOOD_1;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_3);
            } else if (level < -50 && level >= -60) {
                // -11 - -20
                levelString = GOOD_1;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_3);
            } else if (level < -60 && level >= -70) {
                // -11 - -20
                levelString = GOOD_2;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_2);
            } else if (level < -70 && level >= -80) {
                // -11 - -20
                levelString = GOOD_2;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_1);
            } else if (level < -80 && level >= -90) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -90 && level >= -100) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -100 && level >= -110) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -110 && level >= -120) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -120 && level >= -130) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -130 && level >= -140) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -140 && level >= -150) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            }
        } else {
            // 无锁
            if (level <= 0 && level >= -10) {
                // 0 - -10
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_4);
            } else if (level < -10 && level >= -20) {
                // -11 - -20
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_4);
            } else if (level < -20 && level >= -30) {
                // -21 - -30
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_4);
            } else if (level < -30 && level >= -40) {
                // -11 - -20
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_4);
            } else if (level < -40 && level >= -50) {
                // -11 - -20
                levelString = GOOD_1;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_3);
            } else if (level < -50 && level >= -60) {
                // -11 - -20
                levelString = GOOD_1;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_3);
            } else if (level < -60 && level >= -70) {
                // -11 - -20
                levelString = GOOD_2;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_2);
            } else if (level < -70 && level >= -80) {
                // -11 - -20
                levelString = GOOD_2;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_1);
            } else if (level < -80 && level >= -90) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -90 && level >= -100) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -100 && level >= -110) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -110 && level >= -120) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -120 && level >= -130) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -130 && level >= -140) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -140 && level >= -150) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            }
        }
        return null;
    }


    private static final String WPA_TEXT = "通过WPA进行保护";
    private static final String WPA2_TEXT = "通过WPA2进行保护";
    private static final String WPA_WPA2_TEXT = "通过WPA/WPA2进行保护";
    private static final String ESS_TEXT = "开放网络";

    private static final String WPA_PSK = "WPA";
    private static final String WPA2_PSK = "WPA2";

    /**
     * 返回当前ScanResult 所对应的 保护状态
     */
    public static String getWifiTypeText(ScanResult scanResult) {
        String stateText;
        if (isContainXXX(scanResult, WPA_PSK) || isContainXXX(scanResult, WPA2_PSK)) {
            isLock = true;
        } else {
            isLock = false;
        }
        stateText = ESS_TEXT;
        if (isContainXXX(scanResult, WPA_PSK)) {
            stateText = WPA_TEXT;
        }
        if (isContainXXX(scanResult, WPA2_PSK)) {
            stateText = WPA2_TEXT;
        }
        if (isContainXXX(scanResult, WPA_PSK) && isContainXXX(scanResult, WPA2_PSK)) {
            stateText = WPA_WPA2_TEXT;
        }
        return stateText;
    }

    private static boolean isContainXXX(ScanResult scanResult, String xxx) {
        return scanResult.capabilities.contains(xxx);
    }

}
