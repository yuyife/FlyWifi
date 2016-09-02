package com.flyaudio.wifi.base;

import android.net.wifi.ScanResult;

import com.flyaudio.wifi.interf.OnWifiReceiveAction;

/**
 * @author yuyife
 *         Created by Administrator on 2016/7/5.
 */
public abstract class BaseWifiFragment extends BaseFragment {
    private static final String TAG = "BaseWifiFragment";

    public String getLevelString() {
        return levelString;
    }

    public void setLevelString(String levelString) {
        this.levelString = levelString;
    }

    protected String levelString;

    public ScanResult getCurrentScanResult() {
        return currentScanResult;
    }

    public void setCurrentScanResult(ScanResult currentScanResult) {
        this.currentScanResult = currentScanResult;
    }

    protected ScanResult currentScanResult;


    public boolean isCurrentConnect() {
        return isCurrentConnect;
    }

    public void setCurrentConnect(boolean currentConnect) {
        isCurrentConnect = currentConnect;
    }

    protected boolean isCurrentConnect;


    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    protected boolean isLock;


    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    protected boolean isSuccess;


}
