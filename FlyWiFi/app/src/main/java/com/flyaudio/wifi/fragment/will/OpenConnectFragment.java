package com.flyaudio.wifi.fragment.will;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife
 *         开放网络 连接页面
 */
public class OpenConnectFragment extends BaseWifiFragment {

    public static OpenConnectFragment instance = null;
    @Bind(R.id.open_wifi_name)
    TextView openWifiName;
    @Bind(R.id.open_wifi_signal)
    TextView openWifiSignal;
    @Bind(R.id.open_cancel)
    public LinearLayout openCancel;
    @Bind(R.id.open_connect)
    public LinearLayout openConnect;

    @OnClick({R.id.open_cancel, R.id.open_connect})
    public void onOpenClick(View v) {
        switch (v.getId()) {
            case R.id.open_cancel:
                activity.setKeyIndex(2);
                viewTransparent();
                setViewBackground(openCancel);
                cancel();

                break;

            case R.id.open_connect:
                activity.setKeyIndex(3);
                viewTransparent();
                setViewBackground(openConnect);
                connect();

                break;
        }
    }


    private void viewTransparent() {
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, openCancel, openConnect);
    }

    public OpenConnectFragment() {
    }

    public static OpenConnectFragment getInstance() {
        if (instance == null) {
            return new OpenConnectFragment();
        }
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_open_connect_fragment, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.openConnectFragment = this;
        MainActivity.keySubmitAction = this;
        activity.setKeyIndex(3);
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, openCancel, openConnect);
        setViewBackground(openConnect);
        //初始化Main标题
        activity. wifiTitleImage.setVisibility(View.VISIBLE);
        activity. wifiTitleAppend.setText("开放网络");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidget() {
        openWifiName.setText(currentScanResult.SSID);
        openWifiSignal.setText(levelString);
    }


    @Override
    public void onKeySubmitAction(int index) {
        Log.e("mainKey", "执行确认键操作，我是open:" + index);
        switch (index) {
            case 1:
                cancel();
                break;
            case 2:
                //取消
                cancel();
                break;
            case 3:
                //连接
                connect();
                break;
        }
    }

    private void cancel() {
        baseCancel(this);
    }

    private void connect() {
        baseCancel(this);
        MainActivity.homeRecyclerFragment.setBeanAndConnect(new WifiConnectBean(currentScanResult, "", false));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
