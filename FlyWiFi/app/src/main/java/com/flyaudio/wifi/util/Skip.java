package com.flyaudio.wifi.util;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.View;

import com.flyaudio.wifi.MainActivity;
import com.flyaudio.wifi.R;
import com.flyaudio.wifi.fragment.save.AlreadyConnectFragment;
import com.flyaudio.wifi.fragment.save.ConnectingFragment;
import com.flyaudio.wifi.fragment.will.OpenConnectFragment;
import com.flyaudio.wifi.fragment.will.WpaConnectFragment;

/**
 * @author YUYIFE
 */
public class Skip {

    public static final String TAG = "skip";

    public static final int AUTO = 0;
    public static final int CLICK = 1;

    @SuppressLint("NewApi")
    public static void skip(MainActivity activity, View v, int way) {
        WpaConnectFragment wpa = WpaConnectFragment.getInstance();
        OpenConnectFragment open = OpenConnectFragment.getInstance();
        ConnectingFragment connecting = ConnectingFragment.getInstance();
        AlreadyConnectFragment already = AlreadyConnectFragment.getInstance();

        ScanResult scanResult = (ScanResult) v.getTag(R.id.wifi_adapter_item_root_tag_scan_result);
        String levelString = (String) v.getTag(R.id.wifi_adapter_item_root_tag_signal_text);
        boolean isLock = (boolean) v.getTag(R.id.wifi_adapter_item_root_tag_is_lock);
        boolean isSuccess = (boolean) v.getTag(R.id.wifi_adapter_item_root_tag_is_connected_success);
        boolean isCurrent = (boolean) v.getTag(R.id.wifi_adapter_item_root_tag_is_current_connect);

        Log.e(TAG, "isCurrent:" + isCurrent);
        Log.e(TAG, "isSuccess:" + isSuccess);
        Log.e(TAG, "isLock:" + isLock);
        Log.e(TAG, "levelString:" + levelString);
        Log.e(TAG, "scanResultSSID:" + scanResult.SSID);
        if (isCurrent) {
            //当前连接
            if (isSuccess) {
                //记录过配置
                if (isLock) {
                    //有锁
                    connecting.setSuccess(true);
                    connecting.setCurrentConnect(true);
                    connecting.setLock(true);
                    connecting.setLevelString(levelString);
                    connecting.setCurrentScanResult(scanResult);
                    activity.isLock = true;
                    if (way == AUTO) {
                        activity.setFragment(connecting);
                    } else {
                        activity.setFragment(connecting);
                    }
                    //activity.setHomeTitle(3);
                } else {
                    connecting.setSuccess(false);
                    connecting.setCurrentConnect(true);
                    connecting.setLock(false);
                    connecting.setLevelString(levelString);
                    connecting.setCurrentScanResult(scanResult);
                    activity.isLock = false;
                    if (way == AUTO) {
                        activity.setFragment(connecting);

                    } else {
                        activity.setFragment(connecting);
                    }
                    //activity.setHomeTitle(3);
                }
            } else {
                if (isLock) {
                    wpa.setSuccess(false);
                    wpa.setCurrentConnect(true);
                    wpa.setLock(true);
                    wpa.setLevelString(levelString);
                    wpa.setCurrentScanResult(scanResult);
                    activity.isLock = true;
                    if (way == AUTO) {
                        activity.setFragment(wpa);
                    } else {
                        activity.setFragment(wpa);
                    }
                    //activity.setHomeTitle(1);
                } else {
                    open.setSuccess(false);
                    open.setLock(false);
                    open.setCurrentConnect(true);
                    open.setLevelString(levelString);
                    open.setCurrentScanResult(scanResult);
                    activity.isLock = false;
                    if (way == AUTO) {
                        activity.setFragment(open);
                    } else {
                        activity.setFragment(open);
                    }
                    //activity.setHomeTitle(2);
                }
            }
        } else {
            //不是当前
            if (isSuccess) {
                //有配置
                if (isLock) {
                    //有锁
                    already.setSuccess(true);
                    already.setCurrentConnect(false);
                    already.setLock(true);
                    already.setLevelString(levelString);
                    already.setCurrentScanResult(scanResult);
                    activity.isLock = true;
                    if (way == AUTO) {
                        activity.setFragment(already);
                    } else {
                        activity.setFragment(already);
                    }
                    //activity.setHomeTitle(4);
                } else {
                    already.setSuccess(false);
                    already.setCurrentConnect(false);
                    already.setLock(false);
                    already.setLevelString(levelString);
                    already.setCurrentScanResult(scanResult);
                    activity.isLock = false;
                    if (way == AUTO) {
                        activity.setFragment(already);
                    } else {
                        activity.setFragment(already);
                    }
                    //activity.setHomeTitle(4);
                }
            } else {
                if (isLock) {
                    wpa.setSuccess(false);
                    wpa.setCurrentConnect(false);
                    wpa.setLock(true);
                    wpa.setLevelString(levelString);
                    wpa.setCurrentScanResult(scanResult);
                    activity.isLock = true;
                    if (way == AUTO) {
                        activity.setFragment(wpa);
                    } else {
                        activity.setFragment(wpa);
                    }
                    //activity.setHomeTitle(1);
                } else {
                    open.setSuccess(false);
                    open.setLock(false);
                    open.setCurrentConnect(false);
                    open.setLevelString(levelString);
                    open.setCurrentScanResult(scanResult);
                    activity.isLock = false;
                    if (way == AUTO) {
                        activity.setFragment(open);
                    } else {
                        activity.setFragment(open);
                    }
                    //activity.setHomeTitle(2);
                }
            }
        }
    }
}
