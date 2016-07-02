package com.yuyife.flywifi.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuyife.flywifi.R;
import com.yuyife.flywifi.bean.WifiConnectBean;
import com.yuyife.flywifi.interf.OnConnectivityAction;
import com.yuyife.flywifi.util.NetWorkUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 本类已实现连接wifi的功能
 *
 * @author yuyife
 */
public abstract class BaseWifiConnectFragment extends BaseFragment implements OnConnectivityAction {


    private static final String TAG = "BaseWifiConnectFragment";


    protected boolean isConnectClick = false;
    protected boolean isNeed = true;
    protected BroadcastReceiver inputReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnectClick) {
                switch (intent.getAction()) {
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        //是不是正在获得IP地址
                        break;
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                        //当前是否有网络连上
                        Log.e(TAG, "已经接收到：CONNECTIVITY_ACTION");
                        // 会收到多次的情况
                        if (isNeed) {
                            onConnectivityAction();
                            new NetPing().execute();
                            isNeed = false;
                            baseConnectHandler.sendEmptyMessage(CHANGE_IS_NEED);
                        }
                        break;

                }
            } else {
            }
            Log.e(TAG, "onReceive---isConnectClick:" + isConnectClick);
        }
    };

    protected void receiverAction(boolean isListen) {
        if (isListen) {
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION); //信号强度变化
            mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); //网络状态变化
            mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); //wifi状态，是否连上，密码
            mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);  //是不是正在获得IP地址
            mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
            mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//连上与否

            activity.registerReceiver(inputReceiver, mFilter);
        } else {
            receiverAction(true);
            activity.unregisterReceiver(inputReceiver);
        }
    }

    protected final static String OK = "success";
    protected final static String NO = "failed";
    protected final static String OTHER = "other";

    //private int pintCount  = 0;
    protected class NetPing extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String result;
            refreshNetWorkState();
            if (netWorkType != NetWorkUtil.TYPE_WIFI) {
                //uiHandler.sendEmptyMessage(INPUT_CONNECT_TEXT_STEP_ERR);
                result = OTHER;
            } else {
                result = activity.getWifiUtil().ping(activity.getString(R.string.wifi_ping_address));
            }

            Log.e(TAG, result);
            onNetPing(result);
            return result;
        }
    }


    protected String currentWifiPwd = "";
    protected String currentWifiSsid = "";


    protected static final int CONNECT_WIFI = 9954;
    private static final int CHANGE_IS_NEED = 9464;


    protected boolean connectResult = false;
    protected Handler baseConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CONNECT_WIFI:
                    Log.e(TAG, "收到子类的 wifi连接 请求");
                    Log.e(TAG, "isConnectClick:" + isConnectClick);
                    //处理子类公用的 wifi连接 请求
                    WifiConnectBean wifiConnectBean = (WifiConnectBean) msg.obj;
                    Log.e(TAG, "ssid:" + wifiConnectBean.getScanResult().SSID);
                    Log.e(TAG, "pwd:" + wifiConnectBean.getSsidPwd());
                    Log.e(TAG, "isESSWifi:" + wifiConnectBean.isESSWifi());

                    if (wifiConnectBean.isESSWifi()) {
                        connectResult = activity.getWifiUtil()
                                .addNetwork(activity.getWifiUtil()
                                        .createWifiInfo(
                                                wifiConnectBean.getScanResult().SSID,
                                                "",
                                                1));
                    } else {
                        connectResult = activity.getWifiUtil()
                                .addNetwork(activity.getWifiUtil()
                                        .createWifiInfo(
                                                wifiConnectBean.getScanResult().SSID,
                                                wifiConnectBean.getSsidPwd(),
                                                3));
                        //加入网络之后，会收到 获取ip的广播，成功后会收到
                        //当前是否有网络连上 （针对于netWorkType == 0）
                    }
                    //showToast("connectRusult:" + connectResult);
                    break;
                case CHANGE_IS_NEED:
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isNeed = true;
                        }
                    }, 5000);
                    break;
            }
        }
    };

    protected void connectCancelSaveSubtmitTip(TextView view) {
        setViewTips(view, activity.getString(R.string.wifi_cancel_save_tips_again));
    }

    protected void connectCancelSaveTip(TextView view) {
        setViewTips(view, activity.getString(R.string.wifi_cancel_save_tips));
    }

    protected void connectingTip(TextView view) {
        setViewTips(view, activity.getString(R.string.input_connect_text_ess_hint));
    }

    protected void connectCheckingTip(TextView view) {
        setViewTips(view, activity.getString(R.string.input_connect_text_hint_pwd));
    }

    protected void connectErrTip(TextView view) {
        setViewTips(view, activity.getString(R.string.input_connect_text_hint_err));
    }

    protected void connectOkTip(TextView view) {
        setViewTips(view, activity.getString(R.string.input_connect_text_hint_ok));
    }

    protected void connectPingTip(TextView view) {
        setViewTips(view, activity.getString(R.string.input_connect_text_hint_ping));
    }

    protected void connectPingNoTip(TextView view) {
        setViewTips(view, activity.getString(R.string.input_connect_text_hint_no));
    }

    protected void connectPingOkTip(TextView view) {
        setViewTips(view, activity.getString(R.string.input_connect_ping));
    }

    private void setViewTips(TextView view, String tips) {
        if (view != null) {
            view.setText(tips);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeed = true;
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
