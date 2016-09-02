package com.flyaudio.wifi.fragment.save;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyaudio.wifi.MainActivity;
import com.flyaudio.wifi.R;
import com.flyaudio.wifi.base.BaseWifiFragment;
import com.flyaudio.wifi.util.SPHelper;
import com.flyaudio.wifi.util.WifiSafetyString;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife
 *         正在连接着的wifi，显示wifi的基本信息，和，取消保存
 */
public class ConnectingFragment extends BaseWifiFragment {


    private static final String TAG = "ConnectingFragment";
    public static ConnectingFragment instance = null;
    @Bind(R.id.connecting_signal)
    TextView connectingSignal;
    @Bind(R.id.connecting_speed)
    TextView connectingSpeed;
    @Bind(R.id.connecting_safety)
    TextView connectingSafety;
    @Bind(R.id.connecting_ip)
    TextView connectingIp;
    @Bind(R.id.connecting_cancel)
    public LinearLayout connectingCancel;
    @Bind(R.id.connecting_cancel_save)
    public LinearLayout connectingCancelSave;

    @OnClick({R.id.connecting_cancel, R.id.connecting_cancel_save})
    public void onOpenClick(View v) {
        switch (v.getId()) {
            case R.id.connecting_cancel:
                activity.setKeyIndex(2);
                viewTransparent();
                setViewBackground(connectingCancel);
                cancel();
                break;

            case R.id.connecting_cancel_save:
                activity.setKeyIndex(3);
                viewTransparent();
                setViewBackground(connectingCancelSave);
                cancelSave();
                break;
        }
    }

    private void viewTransparent() {
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, connectingCancel, connectingCancelSave);
    }

    public ConnectingFragment() {
    }

    public static ConnectingFragment getInstance() {
        if (instance == null) {
            return new ConnectingFragment();
        }
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_connecting_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.connectingFragment = this;
        MainActivity.keySubmitAction = this;
        activity.setKeyIndex(2);
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, connectingCancelSave, connectingCancel);
        setViewBackground(connectingCancel);
        //初始化Main标题
        activity. wifiTitleImage.setVisibility(View.VISIBLE);
        activity. wifiTitleAppend.setText("已连接");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidget() {
        connectingSignal.setText(levelString);
        if (isLock()) {
            connectingSafety.setText(WifiSafetyString.makeSafetyString(currentScanResult));
        } else {
            connectingSafety.setText("无");
        }
        connectingIp.setText(activity.getWifiUtil().getIp());
        connectingSpeed.setText(activity.getWifiUtil().getLinkSpeed() + "Mbps");
    }

    private void cancel() {
        baseCancel(this);
    }

    private void cancelSave() {
        //断开
        int id = activity.getWifiUtil().getNetworkId();
        if (id != 0) {
            activity.getWifiUtil().disconnectWifi(id);
        }
        boolean isRemove = SPHelper.saveMapStringForRemove(activity, currentScanResult.SSID);
        if (isRemove) {
            baseCancel(this);
            MainActivity.homeRecyclerFragment.refreshHomeAdapter(I_AM_CONNECTING);
            Log.e(TAG, "isRemove:" + isRemove);
        } else {
            Log.e(TAG, "isRemove:" + isRemove);
        }
    }

    @Override
    public void onKeySubmitAction(int index) {
        Log.e("mainKey", "执行确认键操作，我是connecting:" + index);

        switch (index) {
            case 1:
                cancel();
                break;
            case 2:
                //取消
                cancel();
                break;
            case 3:
                //取消保存
                cancelSave();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
