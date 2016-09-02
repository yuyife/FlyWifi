package com.flyaudio.wifi.base;

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

import com.flyaudio.wifi.R;
import com.flyaudio.wifi.bean.WifiConnectBean;
import com.flyaudio.wifi.interf.OnConnectResult;
import com.flyaudio.wifi.interf.OnConnectivityAction;
import com.flyaudio.wifi.interf.OnWifiReceiveAction;
import com.flyaudio.wifi.util.NetWorkUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author yuyife
 *         Created by Administrator on 2016/7/5.
 */
public abstract class BaseConnectFragment extends BaseFragment implements OnWifiReceiveAction, OnConnectResult, OnConnectivityAction {

    private static final String TAG = "BaseConnectFragment";
    protected boolean isConnectClick = false;
    protected boolean isNeed = true;
    protected BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnectClick) {
                switch (intent.getAction()) {
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        //是不是正在获得IP地址
                        break;
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                        //当前是否有网络连上

                        // 会收到多次的情况
                        if (isNeed) {
                            Log.e(TAG, "已经接收到：CONNECTIVITY_ACTION");
                            onConnectivityAction();
                            baseConnectHandler.sendEmptyMessageDelayed(PING_AGAIN, 3000);

                            isNeed = false;
                            baseConnectHandler.sendEmptyMessage(CHANGE_IS_NEED);
                        }
                        break;

                }
            } else {
                Log.e(TAG, "onReceive---isConnectClick:" + isConnectClick);
            }

        }
    };

    protected void connectAction(boolean isListen) {
        if (isListen) {
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION); //信号强度变化
            mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); //网络状态变化
            mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); //wifi状态，是否连上，密码
            mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);  //是不是正在获得IP地址
            mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
            mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//连上与否

            activity.registerReceiver(connectReceiver, mFilter);
        } else {
            connectAction(true);
            activity.unregisterReceiver(connectReceiver);
        }
    }

    protected final static String PING_OK = "success";
    protected final static String PING_NO = "failed";
    protected final static String OTHER = "other";//手机网络不需要ping
    protected final static String DISABLED = "disabled";

    //连接后，3秒启动 ping
    protected class NetPing extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String result;
            refreshNetWorkState();
            Log.e(TAG, "netWorkType:" + netWorkType);
            if (netWorkType == NetWorkUtil.TYPE_NET_WORK_DISABLED) {
                //没有网络
                result = DISABLED;
            } else if (netWorkType == NetWorkUtil.TYPE_WIFI) {
                result = activity.getWifiUtil().ping(activity.getString(R.string.wifi_ping_address));
            } else {
                result = OTHER;
            }

            Log.e(TAG, "result:" + result);
            onNetPing(result);
            return result;
        }
    }


    protected static final int CONNECT_WIFI = 9954;
    private static final int CHANGE_IS_NEED = 9464;
    protected static final int CONNECT_TEST = 21275;
    protected static final int PING_AGAIN = 23275;

    protected boolean connectResult = false;
    private boolean isLock = false;
    protected Handler baseConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CONNECT_WIFI:
                    int id = activity.getWifiUtil().getNetworkId();
                    if (id != 0) {
                        activity.getWifiUtil().disconnectWifi(id);
                    }
                    Log.e(TAG, "收到子类的 wifi连接 请求");
                    Log.e(TAG, "isConnectClick:" + isConnectClick);
                    //处理子类公用的 wifi连接 请求
                    WifiConnectBean wifiConnectBean = (WifiConnectBean) msg.obj;
                    Log.e(TAG, "ssid:" + wifiConnectBean.getScanResult().SSID);
                    Log.e(TAG, "pwd:" + wifiConnectBean.getSsidPwd());
                    isLock = wifiConnectBean.isLock();
                    Log.e(TAG, "isLock:" + isLock);
                    if (isLock) {
                        connectResult = activity.getWifiUtil()
                                .addNetwork(activity.getWifiUtil()
                                        .createWifiInfo(
                                                wifiConnectBean.getScanResult().SSID,
                                                wifiConnectBean.getSsidPwd(),
                                                3));
                    } else {
                        connectResult = activity.getWifiUtil()
                                .addNetwork(activity.getWifiUtil()
                                        .createWifiInfo(
                                                wifiConnectBean.getScanResult().SSID,
                                                "",
                                                1));
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
                    }, 12000);
                    break;

                case CONNECT_TEST:
                    connectTest();
                    break;
                case PING_AGAIN:
                    refreshNetWorkState();
                    new NetPing().execute();
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeed = true;
        testCount = 0;
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    /***
     * 使用方法：
     * step 1 :refreshNetWorkState();
     * step 2 :refreshNetWorkStateTemp();
     * step 3 :connectTest()
     * <p>
     * 封装之后 ： startConnectTest()
     */

    protected void startConnectTest() {
        Log.e(TAG, "startConnectTest开始启动");
        testCount = 0;
        refreshNetWorkState();
        refreshNetWorkStateTemp();
        baseConnectHandler.sendEmptyMessageDelayed(CONNECT_TEST, 1000);
    }

    protected int testCount = 0;

    //此方法的意义在于 没有收到 case ConnectivityManager.CONNECTIVITY_ACTION:
    //做出 判断身份验证不通过的判断
    private void connectTest() {

        //Log.e(TAG, "isNetWorkAvailable:" + isNetWorkAvailable);
        //Log.e(TAG, "isNetWorkConnect:" + isNetWorkConnect);
        Log.e(TAG, "netWorkType:" + netWorkType);
        Log.e(TAG, "testCount:" + testCount);
        testCount++;
        refreshNetWorkState();
        // 如果 连接之前是 没有网络
        // 如果 连接之前是 移动网络
        // 如果 连接之前是 wifi网络
        // 如果 连接之前是 其他网络

        if (netWorkTypeTemp == NetWorkUtil.TYPE_NET_WORK_DISABLED) {
            // 如果 连接之前是 没有网络
            if (netWorkType == NetWorkUtil.TYPE_WIFI) {
                //连接之后 是 wifi
                /** ------身份验证通过------ 的第一种情况*/
                onConnectResult(true);
                testCount = 0;
            } else {
                //连接之后 还不是 wifi

                if (testCount > 4) {
                    /* 验证3次之后 可以做出 身份验证不通过 的判断 */
                    if (isLock) {
                        onConnectResult(false);
                        testCount = 0;
                    } else {
                        testCount = 0;
                        baseConnectHandler.sendEmptyMessageDelayed(CONNECT_TEST, 800);
                    }
                } else {
                    /* 3次以下 身份验证不通过 延迟一秒继续 connectTest()*/
                    baseConnectHandler.sendEmptyMessageDelayed(CONNECT_TEST, 800);
                }

            }
        } else if (netWorkTypeTemp == NetWorkUtil.TYPE_WIFI) {
            // 如果 连接之前是 wifi网络
            if (netWorkType == NetWorkUtil.TYPE_WIFI) {
                //连接之后 是 wifi
                if (testCount > 4) {
                    /** 连续3次之后 结果还是一样 可以做出 身份验证通过 的判断 */
                    /** ------身份验证通过------ 的第二种情况*/
                    onConnectResult(true);
                    testCount = 0;
                } else {
                    /* 3次以下 结果还是一样 延迟一秒继续 connectTest()*/
                    baseConnectHandler.sendEmptyMessageDelayed(CONNECT_TEST, 800);
                }
            } else {
                //连接之后 还不是 wifi
                if (testCount > 4) {
                    /* 连续3次之后 结果还是一样 可以做出 身份验证不通过 的判断 */
                    if (isLock) {
                        onConnectResult(false);
                        testCount = 0;
                    } else {
                        testCount = 0;
                        baseConnectHandler.sendEmptyMessageDelayed(CONNECT_TEST, 800);
                    }
                } else {
                    /* 3次以下 结果还是一样 延迟一秒继续 connectTest()*/
                    baseConnectHandler.sendEmptyMessageDelayed(CONNECT_TEST, 800);
                }
            }
        } else {
            // 如果 连接之前是 移动网络 或者 是 其他网络
            if (netWorkType == NetWorkUtil.TYPE_WIFI) {
                //连接之后 是 wifi
                /** ------身份验证通过------ 的第三种情况*/
                onConnectResult(true);
                testCount = 0;
            } else {
                //连接之后 还不是 wifi
                if (testCount > 4) {
                    /* 连续3次之后 结果还是一样 可以做出 身份验证不通过 的判断 */
                    if (isLock) {
                        onConnectResult(false);
                        testCount = 0;
                    } else {
                        testCount = 0;
                        baseConnectHandler.sendEmptyMessageDelayed(CONNECT_TEST, 800);
                    }
                } else {
                    /* 3次以下 结果还是一样 延迟一秒继续 connectTest()*/
                    baseConnectHandler.sendEmptyMessageDelayed(CONNECT_TEST, 800);
                }
            }
        }
    }


    /***
     * 以下代码是yuyife经过不断，反复调试，
     * 成功封装了向子类做出-->身份验证的返回（true,false）
     */

    //protected boolean isNetWorkConnect = false, isNetWorkConnectTemp = false;
    //protected boolean isNetWorkAvailable = false, isNetWorkAvailableTemp = false;
    protected int netWorkType = 100, netWorkTypeTemp = 100;

    protected void refreshNetWorkState() {
        //isNetWorkAvailable = NetWorkUtil.isNetWorkAvailable(activity);
        //isNetWorkConnect = NetWorkUtil.isNetWorkConnect(activity);
        netWorkType = NetWorkUtil.checkNetworkType(activity);
    }

    protected void refreshNetWorkStateTemp() {
        //isNetWorkConnectTemp = isNetWorkConnect;
        //isNetWorkAvailableTemp = isNetWorkAvailable;
        netWorkTypeTemp = netWorkType;
    }

    /**
     * ----------------------------------------↑↑↑↑为子类处理wifi连接，-------------------------------------------
     */



}
