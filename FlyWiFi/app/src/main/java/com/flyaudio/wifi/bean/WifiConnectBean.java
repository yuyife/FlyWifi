package com.flyaudio.wifi.bean;

import android.net.wifi.ScanResult;

/**
 * 用于 wifi连接 当参数传递给 基类
 * @author yuyife
 */

/**
 * 用于 wifi连接 当参数传递给 基类
 *
 * @author yuyife
 */
public class WifiConnectBean {
    private ScanResult scanResult;

    public String getSsidPwd() {
        return ssidPwd;
    }

    public void setSsidPwd(String ssidPwd) {
        this.ssidPwd = ssidPwd;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    private String ssidPwd;
    private boolean isLock;


    public WifiConnectBean(ScanResult scanResult,
                           String ssidPwd,
                           boolean isLock) {
        this.scanResult = scanResult;
        this.ssidPwd = ssidPwd;
        this.isLock = isLock;
    }
}
