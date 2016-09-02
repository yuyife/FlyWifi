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
import com.flyaudio.wifi.bean.WifiConnectBean;
import com.flyaudio.wifi.util.SPHelper;
import com.flyaudio.wifi.util.WifiSafetyString;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife
 *         成功连接过的wifi，提供免密码连接和取消保存
 */
public class AlreadyConnectFragment extends BaseWifiFragment {


    private static final String TAG = "AlreadyConnectFragment";
    public static AlreadyConnectFragment instance = null;
    @Bind(R.id.already_wifi_name)
    TextView alreadyWifiName;
    @Bind(R.id.already_wifi_signal)
    TextView alreadyWifiSignal;
    @Bind(R.id.already_wifi_safety)
    TextView alreadyWifiSafety;
    @Bind(R.id.already_cancel_save)
    public LinearLayout alreadyCancelSave;
    @Bind(R.id.already_cancel)
    public LinearLayout alreadyCancel;
    @Bind(R.id.already_connect)
    public LinearLayout alreadyConnect;


    @OnClick({R.id.already_cancel_save, R.id.already_cancel, R.id.already_connect})
    public void onAlreadyClick(View v) {
        switch (v.getId()) {
            case R.id.already_cancel_save:
                activity.setKeyIndex(2);
                viewTransparent();
                setViewBackground(alreadyCancelSave);
                cancelSave();
                break;
            case R.id.already_cancel:
                activity.setKeyIndex(3);
                viewTransparent();
                setViewBackground(alreadyCancel);
                cancel();
                break;

            case R.id.already_connect:
                activity.setKeyIndex(4);
                viewTransparent();
                setViewBackground(alreadyConnect);
                connect();
                break;
        }
    }


    public AlreadyConnectFragment() {
    }

    public static AlreadyConnectFragment getInstance() {
        if (instance == null) {
            return new AlreadyConnectFragment();
        }
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_already_connect_fragment, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.alreadyConnectFragment = this;
        MainActivity.keySubmitAction = this;
        activity.setKeyIndex(4);
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, alreadyCancelSave, alreadyCancel, alreadyConnect);
        setViewBackground(alreadyConnect);
        //初始化Main标题
        activity.wifiTitleImage.setVisibility(View.VISIBLE);
        activity.wifiTitleAppend.setText("保存页面");
    }

    @Override
    protected void initData() {
        alreadyWifiName.setText(currentScanResult.SSID);
        alreadyWifiSignal.setText(levelString);
        if (isLock()) {
            alreadyWifiSafety.setText(WifiSafetyString.makeSafetyString(currentScanResult));
        } else {
            alreadyWifiSafety.setText("无");
        }
    }

    @Override
    protected void initWidget() {

    }


    //透明view
    private void viewTransparent() {
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, alreadyCancelSave, alreadyCancel
                , alreadyConnect);
    }

    private void cancel() {
        baseCancel(this);
    }

    private void cancelSave() {
        boolean isRemove =
                SPHelper.saveMapStringForRemove(activity, currentScanResult.SSID);
        if (isRemove) {
            baseCancel(this);
            MainActivity.homeRecyclerFragment.refreshHomeAdapter(I_AM_ALREADY);
            Log.e(TAG, "isRemove:" + isRemove);
        } else {
            Log.e(TAG, "isRemove:" + isRemove);
        }
    }

    private void connect() {
        String pwd;
        if (isLock()) {
            pwd = SPHelper.getMapForJson(SPHelper.getMapString(activity)).get(currentScanResult.SSID);
            MainActivity.homeRecyclerFragment.setBeanAndConnect(new WifiConnectBean(currentScanResult, pwd, true));
        } else {
            pwd = "";
            MainActivity.homeRecyclerFragment.setBeanAndConnect(new WifiConnectBean(currentScanResult, pwd, false));
        }
        baseCancel(this);
    }

    @Override
    public void onKeySubmitAction(int index) {

        Log.e("mainKey", "执行确认键操作，我是Already:" + index);

        switch (index) {
            case 1:
                cancel();
                break;
            case 2:
                //取消保存
                cancelSave();
                break;
            case 3:
                //取消
                cancel();
                break;
            case 4:
                //连接
                connect();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
