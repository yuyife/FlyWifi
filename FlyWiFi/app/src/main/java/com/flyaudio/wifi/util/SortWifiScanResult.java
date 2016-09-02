package com.flyaudio.wifi.util;

import android.net.wifi.ScanResult;

import java.util.Comparator;

/**
 * @author yuyife
 *         Created by Administrator on 2016/7/5.
 */
public class SortWifiScanResult implements Comparator {
    @Override
    public int compare(Object o, Object t1) {
        ScanResult p1 = (ScanResult) o;
        ScanResult p2 = (ScanResult) t1;

        if (p1.level > p2.level)
            return -1;
        else if (p1.level == p2.level)
            return 0;
        else if (p1.level < p2.level)
            return 1;
        return 0;
    }
}
