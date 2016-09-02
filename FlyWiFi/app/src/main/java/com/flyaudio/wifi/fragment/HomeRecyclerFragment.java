package com.flyaudio.wifi.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyaudio.wifi.MainActivity;
import com.flyaudio.wifi.R;
import com.flyaudio.wifi.adapter.WifiAdapter;
import com.flyaudio.wifi.base.BaseConnectFragment;
import com.flyaudio.wifi.bean.WifiConnectBean;
import com.flyaudio.wifi.interf.OnItemSelectAction;
import com.flyaudio.wifi.util.SPHelper;
import com.flyaudio.wifi.util.SortWifiScanResult;
import com.flyaudio.wifi.util.WifiUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife
 *         显示 wifi列表
 *         监控 wifi连接 的过程
 */
public class HomeRecyclerFragment extends BaseConnectFragment implements OnItemSelectAction {

    private static final String TAG = "HomeRecyclerFragment";
    public static HomeRecyclerFragment instance = null;
    @Bind(R.id.wifi_open_checkbox)
    CheckBox wifiOpenCheckbox;
    @Bind(R.id.wifi_switch_layout)
    public LinearLayout wifiSwitchLayout;
    @Bind(R.id.wifi_refresh_text_hint)
    TextView wifiRefreshTextHint;
    @Bind(R.id.wifi_refresh)
    public RelativeLayout wifiRefresh;
    @Bind(R.id.wifi_content_text_hint)
    TextView wifiContentTextHint;
    @Bind(R.id.wifi_recyclerView)
    RecyclerView wifiRecyclerView;

    private boolean isContinue = false;

    @OnClick({R.id.wifi_switch_layout, R.id.wifi_refresh})
    public void onHomeClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_switch_layout:
                if (isContinue) {
                    activity.setKeyIndex(2);
                    viewTransparent();
                    setViewBackground(wifiSwitchLayout);
                    switchWifi();
                    isContinue = false;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isContinue = true;
                        }
                    }, 1000);
                }
                break;
            case R.id.wifi_refresh:
                if (wifiStateFlag) {
                    //wifi状态为开启，点击有有效
                    activity.setKeyIndex(3);
                    viewTransparent();
                    setViewBackground(wifiRefresh);

                    refresh();
                } else {

                }
                break;
        }
    }

    private void showRefreshTextHint(final String txt, int time) {
        if (wifiRefreshTextHint != null) {
            wifiRefreshTextHint.postDelayed(new Runnable() {
                @Override
                public void run() {
                    wifiRefreshTextHint.setText(txt);
                }
            }, time);
        }
    }

    private void viewTransparent() {
        if (activity.wifiTitleImageLayout != null) {
            if (wifiSwitchLayout != null) {
                if (wifiRefresh != null) {
                    setViewBackgroundTransparent(activity.wifiTitleImageLayout, wifiSwitchLayout, wifiRefresh);
                }
            }
        }
    }

    /**
     * ---------------------------------------- ↑↑↑↑ View注解，点击-------------------------------------------
     */
    public HomeRecyclerFragment() {
    }

    public static HomeRecyclerFragment getInstance() {
        if (instance == null) {
            return new HomeRecyclerFragment();
        }
        return instance;
    }

    /**
     * ----------------------------------------↑↑↑↑构造-------------------------------------------
     */


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_home_recycler_fragment, null);
        ButterKnife.bind(this, view);
        isContinue = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setKeyIndex(3);
        MainActivity.homeRecyclerFragment = this;
        MainActivity.itemSelectAction = this;
        MainActivity.keySubmitAction = this;
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, wifiSwitchLayout, wifiRefresh);
        setViewBackground(wifiRefresh);
        Log.e("mainKey", "---------keyIndex--------" + activity.getKeyIndex());

        uiHandler.sendEmptyMessageDelayed(AUTO_REFRESH_ADAPTER, 10000);

        //初始化Main标题
        activity.wifiTitleImage.setVisibility(View.GONE);
        activity.wifiTitleAppend.setText("");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "hidden:" + hidden);
        if (hidden) {
            //隐藏，
            uiHandler.removeMessages(AUTO_REFRESH_ADAPTER);
        } else {
            //显示
            MainActivity.homeRecyclerFragment = this;
            MainActivity.itemSelectAction = this;
            MainActivity.keySubmitAction = this;


            uiHandler.sendEmptyMessageDelayed(AUTO_REFRESH_ADAPTER, 10000);

            activity.wifiTitleImage.setVisibility(View.GONE);
            activity.wifiTitleAppend.setText("");

            isContinue = true;
        }
    }

    private int wifiDataSize = 0;

    //返回wifi列表长度
    public int getWifiDataSize() {
        return wifiDataSize;
    }

    private void setWifiDataSize(int wifiDataSize) {
        this.wifiDataSize = wifiDataSize;
    }

    private List<ScanResult> wifiData;
    private List<Integer> stateFlagData;
    private List<Integer> selectData;
    private List<Integer> autoSkipData;

    //protected boolean wifiStateFlag = false;
    private LinearLayoutManager linearLayoutManager;
    private WifiAdapter adapter;

    private boolean isHasMap = false;//第一次进入时，判断是否有本地数据
    private WifiUtil wifiUtil;

    @Override
    protected void initData() {
        wifiUtil = activity.getWifiUtil();
        wifiStateFlag = wifiUtil.isOpenWifi();

        linearLayoutManager = new LinearLayoutManager(activity);
        wifiRecyclerView.setLayoutManager(linearLayoutManager);
        //拿到本地保存的wifi配置列表
        isHasMap = SPHelper.isHasMapString(activity);
        if (isHasMap) {
            Map<String, String> map = SPHelper.getMapForJson(SPHelper.getMapString(activity));
            Log.e(TAG, "map:" + map.toString());
            Log.e(TAG, "SSID_LIST:" + SPHelper.ssidList.toString());
        } else {
            SPHelper.ssidList.clear();
        }
        wifiReceiver = new NetworkConnectChangedReceiver();
        switchAction(true);


    }

    @Override
    protected void initWidget() {

        wifiOpenCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAction(false);
                wifiUtil.setWifiState(b);
                wifiStateFlag = b;//更新wifi开启关闭的状态
                open2closeWifi(b);

                compoundButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchAction(true);
                    }
                }, 4000);

            }
        });
        //如果第一次是关闭，setChecked(wifiStateFlag)，并不会触发 OnCheckedChangeListener
        wifiOpenCheckbox.setChecked(wifiStateFlag);
        if (wifiStateFlag) {
        } else {
            //提示勾选网络
            openWifiTip();
        }

    }
    /**
     * ----------------------------------------↑↑↑↑初始化-------------------------------------------
     */

    /**
     * 打开和关闭wifi
     */
    private void open2closeWifi(boolean isChecked) {
        if (isChecked) {
            //打开
            searchWifiTip();
            uiHandler.sendEmptyMessage(FIRST_DATA);
        } else {
            //关闭
            closeWifiTip();
            uiHandler.sendEmptyMessageDelayed(OPEN_TIP, 1000);
        }
    }


    //如果连接搜索10秒都没有wifi，就判断搜索遇到问题
    private int refreshDataCount = 0;

    /**
     * 刷新wifi列表数据
     */
    private void getFirstData() {
        Log.e(TAG, "refreshData");
        wifiData = wifiUtil.refreshWifiList();
        if (wifiData!=null&&wifiData.size() > 0) {
            refreshDataCount = 0;
            //得到最新的 wifi热点数据
            for (int i = 0; i < wifiData.size(); i++) {
                if (TextUtils.isEmpty(wifiData.get(i).SSID)) {
                    //排除名称为空的wifi热点
                    wifiData.remove(i);
                }
            }
            //排序
            Collections.sort(wifiData, new SortWifiScanResult());
            //初始化控制 连接wifi 过程显示的 标记
            stateFlagData = new ArrayList<>();
            //初始化 选中 list
            selectData = new ArrayList<>();
            //初始化 自动跳转 list
            autoSkipData = new ArrayList<>();
            for (int i = 0; i < wifiData.size(); i++) {
                //默认flag == 0,表示当前没有连接，1正在连接，2，获取ip地址，3已连接，4，身份验证出现问题
                stateFlagData.add(i, 0);
                selectData.add(i, 0);
                autoSkipData.add(i, 0);
            }
            setWifiDataSize(wifiData.size());
            //设置适配器
            setHomeAdapter();
        } else {
            if (refreshDataCount >= 10) {
                //判断搜索遇到问题
                errorWifiTip();
                getFirstData();//再来一次
            } else {
                uiHandler.sendEmptyMessageDelayed(FIRST_DATA, 1000);
            }
            refreshDataCount += 1;
        }
    }

    /**
     * 10秒自动刷新
     */
    private void autoRefreshData(boolean isAuto) {

        activity.setKeyIndex(3);
        setViewBackgroundTransparent(activity.wifiTitleLayout, activity.wifiTitleImageLayout, wifiSwitchLayout, wifiRefresh);
        setViewBackground(wifiRefresh);

        Log.e(TAG, "autoRefreshData");
        wifiData = wifiUtil.refreshWifiList();
        if (wifiData.size() > 0) {
            refreshDataCount = 0;
            //得到最新的 wifi热点数据
            for (int i = 0; i < wifiData.size(); i++) {
                if (TextUtils.isEmpty(wifiData.get(i).SSID)) {
                    //排除名称为空的wifi热点
                    wifiData.remove(i);
                }
            }
            //排序
            Collections.sort(wifiData, new SortWifiScanResult());
            //初始化控制 连接wifi 过程显示的 标记
            stateFlagData = new ArrayList<>();
            //初始化 选中 list
            selectData = new ArrayList<>();
            //初始化 自动跳转 list
            autoSkipData = new ArrayList<>();
            for (int i = 0; i < wifiData.size(); i++) {
                if (SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY).equals(wifiData.get(i).SSID)) {
                    ScanResult si = wifiData.get(i);
                    wifiData.remove(i);
                    wifiData.add(0, si);
                }
            }
            for (int i = 0; i < wifiData.size(); i++) {
                //默认flag == 0,表示当前没有连接，1正在连接，2，获取ip地址，3已连接，4，身份验证出现问题
                stateFlagData.add(i, 0);
                selectData.add(i, 0);
                autoSkipData.add(i, 0);
            }
            setWifiDataSize(wifiData.size());
            //设置适配器
            Log.e("列表第一个是谁：", "本地保存：" + SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY));
            Log.e("列表第一个是谁：", wifiData.get(0).SSID);
            if (wifiRecyclerView != null) {
                adapter = new WifiAdapter(activity, wifiData, stateFlagData, SPHelper.ssidList, selectData, autoSkipData);
                wifiRecyclerView.setAdapter(adapter);
            }
        } else {
            if (refreshDataCount >= 10) {
                //判断搜索遇到问题
                errorWifiTip();
            } else {
                uiHandler.sendEmptyMessageDelayed(FIRST_DATA, 1000);
            }
            refreshDataCount += 1;
        }
        if (isAuto) {
            uiHandler.sendEmptyMessageDelayed(AUTO_REFRESH_ADAPTER, 10000);
        } else {

        }
    }

    /**
     * 此方法 只处理 第一次打开应用时，显示wifi列表，
     * 或手动打开关闭时 显示wifi列表调用
     */
    private void setHomeAdapter() {
        uiHandler.sendEmptyMessage(SET_ADAPTER);
    }

    /**
     * 此方法
     * 处理，
     * connecting点击取消保存后的刷新
     * 和 already 点击取消保存后的刷新
     */
    public void refreshHomeAdapter(int who) {
        SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, currentSSIDtxt);
        String saveSSID = SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY);
        Log.e(TAG, "未做判断currentSSID:" + saveSSID);
        if (who == I_AM_CONNECTING) {
            stateFlagDataMonitoring(0);//清除标记状态
        } else {

        }
        uiHandler.sendEmptyMessage(REFRESH_ADAPTER);
    }

    private final static int SET_ADAPTER = 100;
    private final static int REFRESH_ADAPTER = 101;
    private final static int AUTO_REFRESH_ADAPTER = 1011;

    private final static int OPEN_TIP = 102;

    private final static int FIRST_DATA = 1012;

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_ADAPTER:
                    noneTip();
                    saveCurrentSSID();

                    adapter = new WifiAdapter(activity, wifiData, stateFlagData, SPHelper.ssidList, selectData, autoSkipData);
                    wifiRecyclerView.setAdapter(adapter);
                    break;
                case REFRESH_ADAPTER:
                    //noneTip();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case AUTO_REFRESH_ADAPTER:
                    if (wifiStateFlag) {
                        autoRefreshData(true);
                    }
                    break;
                case OPEN_TIP:
                    openWifiTip();
                    break;
                case FIRST_DATA:
                    //在打开网络时调用，和未获取到wifi数据时，隔一秒重新获取
                    getFirstData();
                    break;
            }
        }
    };

    /**
     * 在设置adapter时，调用,显示当前 已连接
     */
    private final String currentSSIDtxt = "wifi_ssid_null";

    /**
     * 为 已连接 当前正连接着的wifi名称赋值
     */
    private void saveCurrentSSID() {
        String currentSSID = wifiUtil.getCurrentWifiInfo();
        if (WifiUtil.ipIsTrue) {
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
        } else {
            currentSSID = currentSSIDtxt;
        }
        Log.e(TAG, "ipIsTrue:" + WifiUtil.ipIsTrue);
        SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, currentSSID);

        String saveSSID = SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY);
        Log.e(TAG, "未做判断currentSSID:" + saveSSID);

        if (isHasMap) {
            //有保存列表
            for (int i = 0; i < SPHelper.ssidList.size(); i++) {
                if (saveSSID.equals(SPHelper.ssidList.get(i))) {
                    //如果 当前 wifi ssid 是已经保存配置里的其中一个
                    for (int j = 0; j < wifiData.size(); j++) {
                        if (saveSSID.equals(wifiData.get(j).SSID)) {
                            ScanResult sj = wifiData.get(j);
                            wifiData.remove(j);
                            wifiData.add(0, sj);
                            Log.e(TAG, "已做判断currentSSID:" + saveSSID);
                        } else {

                        }
                    }
                } else {
                    //如果 当前 wifi ssid 不是已经保存配置里的其中一个，
                    // 这种情况说明 存在两个wifi管理器   也置顶 处理

                }
            }

        } else {
            //无保存列表
            //如果 当前 wifi ssid 是已经保存配置里的其中一个
            for (int j = 0; j < wifiData.size(); j++) {
                if (saveSSID.equals(wifiData.get(j).SSID)) {
                    ScanResult sj = wifiData.get(j);
                    wifiData.remove(j);
                    wifiData.add(0, sj);
                    Log.e(TAG, "无保存列表，已做判断currentSSID:");
                } else {

                }
            }

        }
    }


    /**
     * 为wpa,open,already，点击连接，提供连接wifi入口
     */
    private WifiConnectBean bean;

    public void setBeanAndConnect(WifiConnectBean bean) {
        this.bean = bean;
        Log.e(TAG, "ssid:" + bean.getScanResult().SSID);
        Log.e(TAG, "isLock:" + bean.isLock());
        Log.e(TAG, "pwd:" + bean.getSsidPwd());

        startConnect();
    }

    private void startConnect() {
        //连接的过程在 recyclerView的Item里进行监控
        for (int i = 0; i < wifiData.size(); i++) {
            if (wifiData.get(i).SSID.equals(bean.getScanResult().SSID)) {
                if (i == 0) {
                } else {
                    ScanResult si = wifiData.get(i);
                    Log.e(TAG, "正在把连接的条目置顶");
                    wifiData.remove(i);
                    wifiData.add(0, si);
                }

            }
        }

        stateFlagDataMonitoring(1);

        isConnectClick = true;
        connectAction(true);//注册连接 广播
        Message msg = Message.obtain();
        msg.what = CONNECT_WIFI;
        msg.obj = bean;
        baseConnectHandler.handleMessage(msg);

        scrollItemToPosition(0);
    }

    private void scrollItemToPosition(int pos) {
        linearLayoutManager.scrollToPositionWithOffset(pos, 0);
    }


    /**
     * 监控跟进 选中标记
     */
    public void selectDataMonitoring(int pos, int flag) {
        for (int i = 0; i < wifiData.size(); i++) {
            if (i == pos) {
                selectData.set(i, flag);
            } else {
                selectData.set(i, 0);
            }
        }
    }

    /**
     * 监控跟进 自动跳转标记
     */
    public void autoSkipDataMonitoring(int pos, int flag) {
        for (int i = 0; i < wifiData.size(); i++) {
            if (i == pos) {
                autoSkipData.set(i, flag);
            } else {
                autoSkipData.set(i, 0);
            }
            Log.e("mainKey", ":i=:" + autoSkipData.get(i));
        }
    }

    /**
     * 监控跟进 用户连接的wifi列表项目 标记
     * 持续更新 text  0-正常，1-正在连接，2-空，3-成功，4-失败
     */
    private void stateFlagDataMonitoring(int flag) {
        for (int i = 0; i < wifiData.size(); i++) {
            if (i == 0) {
                stateFlagData.set(i, flag);
            } else {
                stateFlagData.set(i, 0);
            }

        }
        if (flag == 3) {
            //连接成功  取消监听
            connectAction(true);// 取消注册连接 广播
            connectSuccess();
            SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, bean.getScanResult().SSID);
        } else {
            SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, currentSSIDtxt);
        }
        uiHandler.sendEmptyMessage(REFRESH_ADAPTER);
    }


    private void openWifiTip() {
        wifiContentTextHint.setText(activity.getString(R.string.wifi_text_open_hint));
    }

    private void searchWifiTip() {
        wifiContentTextHint.setText(activity.getString(R.string.wifi_text_search_hint));
    }

    private void errorWifiTip() {
        wifiContentTextHint.setText(activity.getString(R.string.wifi_text_error_hint));
    }

    private void closeWifiTip() {
        wifiData.clear();
        uiHandler.sendEmptyMessage(REFRESH_ADAPTER);
        wifiContentTextHint.setText(activity.getString(R.string.wifi_text_close_hint));
    }

    private void noneTip() {
        if (wifiContentTextHint != null) {
            wifiContentTextHint.setText("");
        }
    }

    @Override
    public void onConnectivityAction() {
        Log.e(TAG, "onConnectivityAction");
        if (activity.isLock) {
        } else {
            startConnectTest();
        }
    }

    @Override
    public void onNetPing(String pingResult) {

        Log.e(TAG, "onNetPing---pingResult:---" + pingResult);

        if (activity.isLock) {
            //wpa不执行
            if (pingResult.equals(PING_OK)) {
                stateFlagDataMonitoring(3);
                connectSuccess();

            } else if (pingResult.equals(PING_NO)) {
                stateFlagDataMonitoring(3);
                connectSuccess();
            } else if (pingResult.equals(DISABLED)) {
                //返回没有网络
                //stateFlagDataMonitoring(4);
                stateFlagDataMonitoring(2);
                //再次启动判断
                startConnectTest();
            }
        } else {

        }


    }

    //
    private void connectSuccess() {
        //
        boolean isSaveOK = SPHelper.saveMapStringForAdd(activity, bean.getScanResult().SSID, bean.getSsidPwd());
        if (isSaveOK) {
            // 保存成功后 刷新ssid列表
            Log.e(TAG, "已把此次网络配置加入本地保存");
        } else {
            Log.e(TAG, "此次网络配置加入本地保存--发生错误");
        }
    }

    @Override
    public void onConnectResult(boolean connectResult) {
        Log.e(TAG, "connectResult:" + connectResult);
        //终极判断
        if (connectResult) {
            stateFlagDataMonitoring(3);

        } else {
            stateFlagDataMonitoring(4);
        }

    }

    /**
     * ----------------------------------------↑↑↑↑WIFI连接功能相关，-------------------------------------------
     */
    @Override
    public void onWifiReceiveAction(boolean wifiState) {
        Log.e(TAG, "onWifiReceiveAction:" + wifiState);
        if (wifiOpenCheckbox != null) {
            wifiOpenCheckbox.setChecked(wifiState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        switchAction(false);
    }


    private NetworkConnectChangedReceiver wifiReceiver;
    private boolean wifiStateFlag = false;

    private class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                // 监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e(TAG, "wifiState:" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLING:
                    case WifiManager.WIFI_STATE_ENABLED:
                        wifiStateFlag = true;
                        onWifiReceiveAction(wifiStateFlag);
                        break;

                    case WifiManager.WIFI_STATE_DISABLING:
                    case WifiManager.WIFI_STATE_DISABLED:
                        wifiStateFlag = false;
                        onWifiReceiveAction(wifiStateFlag);
                        break;

                }


            }

        }
    }

    private void switchAction(boolean isListen) {
        switchAction(activity, isListen);
    }

    private void switchAction(MainActivity activity, boolean isListen) {
        if (isListen) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            activity.registerReceiver(wifiReceiver, filter);
            Log.e(TAG, "connectAction:--注册 registerReceiver");
        } else {
            switchAction(activity, true);
            activity.unregisterReceiver(wifiReceiver);
            Log.e(TAG, "connectAction:--注销 unregisterReceiver");
        }
    }

    /**
     * ----------------------------------------↑↑↑↑监听通知栏的开关，-------------------------------------------
     */

    public void refreshAdapter() {
        uiHandler.sendEmptyMessage(REFRESH_ADAPTER);
    }


    @Override
    public void onItemSelectAction(boolean isNext, int itemPosition) {
        //Log.e("mainKey", "isNext:" + isNext);
        //Log.e("mainKey", "itemPosition:" + itemPosition);
        selectDataMonitoring(itemPosition, 1);
        if (isNext) {
            if (itemPosition % 4 == 0) {
                // Log.e("mainKey", "itemPosition % 4 == 0__itemPosition:" + itemPosition);
                scrollItemToPosition(itemPosition);
            }

        } else {
            if (itemPosition % 3 == 0) {
                //Log.e("mainKey", "itemPosition % 3 == 0__itemPosition:" + itemPosition);
                if (itemPosition - 3 >= 0)
                    scrollItemToPosition(itemPosition - 3);
            }
        }


        refreshAdapter();

    }

    @Override
    public void onKeySubmitAction(int index) {
        Log.e("mainKey", "执行确认键操作，我是home:" + index);
        switch (index) {
            case 1:
                //MainActivity处理了 退出程序的操作
                break;
            case 2:
                switchWifi();
                break;
            case 3:
                if (wifiStateFlag) {
                    refresh();
                }
                break;
            default:
                int pos = index - 4;
                autoSkipDataMonitoring(pos, 1);
                uiHandler.sendEmptyMessage(REFRESH_ADAPTER);
                break;
        }

    }

    private void refresh() {
        wifiRefreshTextHint.setText(".");
        showRefreshTextHint("..", 500);
        showRefreshTextHint("...", 1000);
        showRefreshTextHint("..", 1500);
        showRefreshTextHint(".", 2000);
        showRefreshTextHint("", 2500);

        autoRefreshData(false);
    }

    private void switchWifi() {
        boolean isChecked = wifiOpenCheckbox.isChecked();
        if (isChecked) {
            wifiOpenCheckbox.setChecked(false);
        } else {
            wifiOpenCheckbox.setChecked(true);
        }
    }
    /**
     * ----------------------------------------↑↑↑↑响应按键，-------------------------------------------
     */

}
