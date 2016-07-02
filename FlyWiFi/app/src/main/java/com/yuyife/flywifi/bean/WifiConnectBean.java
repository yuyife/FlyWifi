package com.yuyife.flywifi.bean;

import android.net.wifi.ScanResult;

/**
 * 用于 wifi连接 当参数传递给 基类
 * @author yuyife
 */
public class WifiConnectBean {
    private ScanResult scanResult;
    private String ssidName;
    private String ssidPwd;
    private boolean isESSWifi;

    public boolean isESSWifi() {
        return isESSWifi;
    }

    public String getSsidPwd() {
        return ssidPwd;
    }

    public String getSsidName() {
        return ssidName;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }


    public WifiConnectBean(ScanResult scanResult,
                           String ssidName,
                           String ssidPwd,
                           boolean isESSWifi) {
        this.scanResult = scanResult;
        this.ssidName = ssidName;
        this.ssidPwd = ssidPwd;
        this.isESSWifi = isESSWifi;
    }
}
