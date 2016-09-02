package com.flyaudio.wifi.interf;

/**
 * @author yuyife
 * 接收 ConnectvityAction 动作
 * 接收 NetPing 动作
 */
public interface OnConnectivityAction {
    void onConnectivityAction();
    void onNetPing(String pingResult);
}
