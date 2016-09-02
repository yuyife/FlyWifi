package com.flyaudio.wifi.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.flyaudio.wifi.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Wifi工具
 *
 * @author yuyife
 */
public class WifiUtil {
    private static final String TAG = "WifiUtil";
    public static boolean ipIsTrue = false;
    private Context context;

    public static WifiUtil instance = null;

    private WifiUtil(Context context) {
        this.context = context;
        init();
    }

    public static WifiUtil getInstance(Context context) {
        if (instance == null) {
            return new WifiUtil(context);
        }
        return instance;
    }

    private WifiManager wifiManager;             //管理wifi
    private ConnectivityManager connectManager;  //管理网络连接
    private NetworkInfo netInfo;                 //网络连接
    private WifiInfo wifiInfo;                   //wifi
    private DhcpInfo dhcpInfo;                   //动态主机配置协议
    private ArrayList<ScanResult> wifiList;          //存放周围wifi热点对象的列表
    private List<WifiConfiguration> wifiConfigurationList;
    private WifiManager.WifiLock wifiLock;


    public DhcpInfo getDhcpInfo() {
        return dhcpInfo;
    }

    public NetworkInfo getNetInfo() {
        return netInfo;
    }

    public WifiInfo getWifiInfo() {
        return wifiInfo;
    }

    public void init() {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);        //获得系统wifi服务
        connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        netInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        dhcpInfo = wifiManager.getDhcpInfo();
        wifiInfo = wifiManager.getConnectionInfo();

        //refreshWifiList();
    }

    private void printInfo(ArrayList<ScanResult> list) {
        for (int i = 0; i < list.size(); i++) {
            Log.e(TAG, "名称SSID:" + list.get(i).SSID);
            Log.e(TAG, "BSSID:" + list.get(i).BSSID);
            Log.e(TAG, "信号强度level:" + list.get(i).level);
            Log.e(TAG, "功能capabilities:" + list.get(i).capabilities);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Log.e(TAG, "时间戳:" + list.get(i).timestamp);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e(TAG, "发生地venueName:" + list.get(i).venueName);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e(TAG, "运营商operatorFriendlyName:" + list.get(i).operatorFriendlyName);
            }
            Log.e(TAG, "----\n \n \n");
        }
    }

    public ArrayList<ScanResult> refreshWifiList() {

        boolean scan = wifiManager.startScan();
        Log.e(TAG, "startScan result:" + scan);
        wifiList = (ArrayList<ScanResult>) wifiManager.getScanResults();
        wifiConfigurationList = wifiManager.getConfiguredNetworks();

        if (wifiList != null) {
            Log.e(TAG, "startScan result:" + wifiList.size());
            for (int i = 0; i < wifiList.size(); i++) {
                ScanResult result = wifiList.get(i);
                Log.i(TAG, "startScan result[" + i + "]" + result.SSID + "," + result.BSSID);
            }
            Log.e(TAG, "startScan result end.");
        } else {
            Log.e(TAG, "startScan result is null.");
        }

        return wifiList;
    }

    public ArrayList<ScanResult> getWifiList() {
        return refreshWifiList();
    }

    public String getCurrentWifiInfo() {
        String wifiProperty = "当前连接Wifi信息如下：" + wifiInfo.getSSID() + '\n' +
                "ip:" + formatString(dhcpInfo.ipAddress) + '\n' +
                "mask:" + formatString(dhcpInfo.netmask) + '\n' +
                "netgate:" + formatString(dhcpInfo.gateway) + '\n' +
                "dns:" + formatString(dhcpInfo.dns1);

        Log.e(TAG, wifiProperty);
        if (formatString(dhcpInfo.ipAddress).equals("0.0.0.0")) {
            ipIsTrue = false;
        } else {
            ipIsTrue = true;
        }
        Log.e("HomeRecyclerFragment", "ip:" + formatString(dhcpInfo.ipAddress));
        return wifiInfo.getSSID();
    }

    //将搜索到的wifi,排除空名称
    public ArrayList<ScanResult> removeNoneSSID(ArrayList<ScanResult> list) {
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.isEmpty(list.get(i).SSID)) {
                list.remove(i);
            }
        }
        return list;
    }

    //将搜索到的wifi根据信号强度从强到弱进行排序
    private void sortByLevel(ArrayList<ScanResult> list) {
        for (int i = 0; i < list.size(); i++)
            for (int j = 1; j < list.size(); j++) {
                if (list.get(i).level < list.get(j).level)    //level属性即为强度
                {
                    ScanResult temp = null;
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
    }

    public String formatString(int value) {
        String strValue = "";
        byte[] ary = intToByteArray(value);
        for (int i = ary.length - 1; i >= 0; i--) {
            strValue += (ary[i] & 0xFF);
            if (i > 0) {
                strValue += ".";
            }
        }
        return strValue;
    }

    public byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public int checkState() {
        return wifiManager.getWifiState();
    }

    public boolean isOpenWifi() {
        if (wifiManager != null) {
            if (checkState() == wifiManager.WIFI_STATE_ENABLED || checkState() == wifiManager.WIFI_STATE_ENABLING) {
                return true;
            } else {
                return false;
            }
        } else {
            Log.e("MainActivity", "wifiManager==null");
        }
        return false;
    }

    public void setWifiState(boolean state) {
        int wifiState;
        if (wifiManager != null) {

            wifiState = wifiManager.getWifiState();
            if (wifiState == wifiManager.WIFI_STATE_ENABLED || wifiState == wifiManager.WIFI_STATE_ENABLING) {
                if (!state) {
                    wifiManager.setWifiEnabled(false);
                }
            } else if (wifiState == wifiManager.WIFI_STATE_DISABLED || wifiState == wifiManager.WIFI_STATE_DISABLING) {

                if (state) {
                    wifiManager.setWifiEnabled(true);
                }
            } else {

            }

        } else {
            Log.e("MainActivity", "wifiManager==null");
        }
    }


    public void acquireWifiLock() {//锁定wifiLock
        wifiLock.acquire();
    }

    public void releaseWifiLock() {//解锁wifiLock
        if (wifiLock.isHeld()) {
            wifiLock.acquire();
        }
    }

    public void creatWifiLock(String wifiLockName) {
        wifiLock = wifiManager.createWifiLock(wifiLockName);
    }


    public List<WifiConfiguration> getConfiguration() {
        return wifiConfigurationList;
    }

    public void connectConfiguration(int index) {//指定配置好的网络进行连接
        if (index > wifiConfigurationList.size()) {
            return;
        }
        wifiManager.enableNetwork(wifiConfigurationList.get(index).networkId, true);
    }


    public String getMacAddress() {
        return (wifiInfo == null) ? "NULL" : wifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (wifiInfo == null) ? "NULL" : wifiInfo.getBSSID();
    }

    //得到当前连接的的ip
    public int getIPAddress() {
        return (wifiInfo == null) ? 0 : wifiInfo.getIpAddress();
    }

    private String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }

    public String getIp() {
        // 获取wifi服务
        // 判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
//          wifiManager.setWifiEnabled(true);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        }
        return "127.0.0.1";
    }

    //得到当前连接的速度
    public int getLinkSpeed() {
        return (wifiInfo == null) ? 0 : wifiInfo.getLinkSpeed();
    }

    //得到连接的id
    public int getNetworkId() {
        return (wifiInfo == null) ? 0 : wifiInfo.getNetworkId();
    }

    // 添加一个网络配置并连接
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = wifiManager.addNetwork(wcg);
        boolean b = wifiManager.enableNetwork(wcgID, true);
        Log.e(TAG, "addNetwork--" + wcgID);
        Log.e(TAG, "enableNetwork--" + b);
        // int status = wifiManager.getConfiguredNetworks().get(wcgID).status;
        //Log.e(TAG, "status--" + status);
        return b;
    }

    //断开指定id的网络
    public void disconnectWifi(int netId) {
        wifiManager.disableNetwork(netId);
        wifiManager.disconnect();
    }

    /*创建WIFI配置*/
    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        Log.e(TAG, "SSID:" + SSID + ",password:" + Password);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);

        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        } else {
            Log.i(TAG, "IsExsits is null.");
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            Log.e(TAG, "Type =1.");
            //config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            Log.e(TAG, "Type =2.");
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {

            Log.e(TAG, "Type =3.");
            config.preSharedKey = "\"" + Password + "\"";

            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        // 查看以前是否已经配置过该SSID
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }


    public String ping(String str) {
        String result;
        Process p;
        try {
            //ping -c 3 -w 100  中  ，
            // -c 是指ping的次数 3是指ping 3次 ，
            // -w 100  以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + str);
            int status = p.waitFor();
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            Log.e(TAG, "Return ============" + buffer.toString());
            if (status == 0) {
                result = "success";
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }


    /**
     * 保存wifi休眠状态也不断开连接
     */
    public void WifiNeverDormancy(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();

        int value = Settings.System.getInt(resolver, Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(mContext.getString(R.string.wifi_sleep_policy_default), value);

        editor.commit();
        if (Settings.System.WIFI_SLEEP_POLICY_NEVER != value) {
            Settings.System.putInt(resolver, Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);

        }
        Log.e(TAG, "wifi value:" + value);
    }


}

