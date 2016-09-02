package com.flyaudio.wifi.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.flyaudio.wifi.R;
import com.flyaudio.wifi.util.WifiUtil;

/**
 * 基类Activity
 *
 * @author yuyife
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context context;

    protected WifiUtil wifiUtil;

    public WifiUtil getWifiUtil() {
        return wifiUtil;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        wifiUtil = WifiUtil.getInstance(context);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
        initWidget();
    }

    abstract protected void initData();

    abstract protected void initWidget();

    protected void showToast(CharSequence txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }

    protected void setViewBackgroundTransparent(View... v) {
        for (int i = 0; i < v.length; i++) {
            if (v[i] != null) {
                v[i].setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    protected void setViewSelectState(View v) {
        if (v != null) {
            v.setBackgroundResource(R.drawable.frame_shape);
        }
    }

}
