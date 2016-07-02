package com.yuyife.flywifi.util;

import android.net.wifi.ScanResult;

/**
 * @author yuyife
 *         Created by Administrator on 2016/6/30.
 */
public class WifiSafetyString {
    private static final String WPA_PSK = "WPA";
    private static final String WPA2_PSK = "WPA2";
    private static final String WPA_PSK_RESULT = "WPA PSK";
    private static final String WPA2_PSK_RESULT = "WPA2 PSK";
    private static final String WPA_WPA2_PSK_RESULT = "WPS/WPA2 PSK";

    public static String makeSafetyString(ScanResult scanResult) {
        String str = "无";
        if (scanResult.capabilities.contains(WPA_PSK)) {
            str = WPA_PSK_RESULT;
        }
        if (scanResult.capabilities.contains(WPA2_PSK)) {
            str = WPA2_PSK_RESULT;
        }
        if (scanResult.capabilities.contains(WPA2_PSK) && scanResult.capabilities.contains(WPA_PSK)) {
            str = WPA_WPA2_PSK_RESULT;
        }
        return str;
    }
}
