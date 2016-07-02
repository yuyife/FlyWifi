package com.yuyife.flywifi.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyife.flywifi.MainActivity;
import com.yuyife.flywifi.R;
import com.yuyife.flywifi.util.Skip;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * -----------------------------AS版本使用的RecyclerView-----------------------------
 * --
 *
 * @author yuyife Created by Administrator on 2016/6/29.
 */
@TargetApi(Build.VERSION_CODES.DONUT)
public class WifiRecyclerAdapter extends RecyclerView.Adapter<WifiRecyclerAdapter.ViewHolder> {
    private static final String TAG = "WifiRecyclerAdapter";
    private String currentConnectSSID;
    private Context context;
    // private MainActivity activity;
    private ArrayList<ScanResult> wifiData;
    private List<String> ssidList;
    private List<Integer> selectList;
    private List<Integer> keySubmitFlagList;
    private LayoutInflater inflater;
    private boolean isLock = true;
    private String levelString = "";

    public WifiRecyclerAdapter(Context context, ArrayList<ScanResult> wifiData, String currentConnectSSID,
                               List<String> ssidList, List<Integer> selectList, List<Integer> keySubmitFlagList) {
        this.context = context;
        // this.activity = (MainActivity) this.context;
        this.inflater = LayoutInflater.from(this.context);
        this.wifiData = wifiData;
        this.currentConnectSSID = currentConnectSSID;
        this.ssidList = ssidList;
        this.selectList = selectList;
        this.keySubmitFlagList = keySubmitFlagList;
        Log.e(TAG, "currentConnectSSID:" + currentConnectSSID);

    }

    private String stateText = "";

    private void judge(int position) {
        if (isContainXXX(wifiData.get(position), WPA_PSK) || isContainXXX(wifiData.get(position), WPA2_PSK)) {
            isLock = true;
        } else {
            isLock = false;
        }
        stateText = ESS_TEXT;
        if (isContainXXX(wifiData.get(position), WPA_PSK)) {
            stateText = WPA_TEXT;
        }
        if (isContainXXX(wifiData.get(position), WPA2_PSK)) {
            stateText = WPA2_TEXT;
        }
        if (isContainXXX(wifiData.get(position), WPA_PSK) && isContainXXX(wifiData.get(position), WPA2_PSK)) {
            stateText = WPA_WPA2_TEXT;
        }
    }

    private final static String GOOD = "强";
    private final static String GOOD_1 = "较强";
    private final static String GOOD_2 = "一般";
    private final static String GOOD_3 = "差";

    private Drawable getLevelDrawable(boolean isLock, int level) {
        if (isLock) {
            // 有锁
            if (level <= 0 && level >= -10) {
                // 0 - -10
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4);
            } else if (level < -10 && level >= -20) {
                // -11 - -20
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4);
            } else if (level < -20 && level >= -30) {
                // -21 - -30
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4);
            } else if (level < -30 && level >= -40) {
                // -11 - -20
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4);
            } else if (level < -40 && level >= -50) {
                // -11 - -20
                levelString = GOOD_1;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_3);
            } else if (level < -50 && level >= -60) {
                // -11 - -20
                levelString = GOOD_1;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_3);
            } else if (level < -60 && level >= -70) {
                // -11 - -20
                levelString = GOOD_2;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_2);
            } else if (level < -70 && level >= -80) {
                // -11 - -20
                levelString = GOOD_2;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_1);
            } else if (level < -80 && level >= -90) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -90 && level >= -100) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -100 && level >= -110) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -110 && level >= -120) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -120 && level >= -130) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -130 && level >= -140) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            } else if (level < -140 && level >= -150) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0);
            }
        } else {
            // 无锁
            if (level <= 0 && level >= -10) {
                // 0 - -10
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_4);
            } else if (level < -10 && level >= -20) {
                // -11 - -20
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_4);
            } else if (level < -20 && level >= -30) {
                // -21 - -30
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_4);
            } else if (level < -30 && level >= -40) {
                // -11 - -20
                levelString = GOOD;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_4);
            } else if (level < -40 && level >= -50) {
                // -11 - -20
                levelString = GOOD_1;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_3);
            } else if (level < -50 && level >= -60) {
                // -11 - -20
                levelString = GOOD_1;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_3);
            } else if (level < -60 && level >= -70) {
                // -11 - -20
                levelString = GOOD_2;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_2);
            } else if (level < -70 && level >= -80) {
                // -11 - -20
                levelString = GOOD_2;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_1);
            } else if (level < -80 && level >= -90) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -90 && level >= -100) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -100 && level >= -110) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -110 && level >= -120) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -120 && level >= -130) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -130 && level >= -140) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            } else if (level < -140 && level >= -150) {
                levelString = GOOD_3;
                return context.getResources().getDrawable(R.drawable.ic_wifi_signal_0);
            }
        }
        return null;
    }

    private static final String WPA_TEXT = "通过WPA进行保护";
    private static final String WPA2_TEXT = "通过WPA2进行保护";
    private static final String WPA_WPA2_TEXT = "通过WPA/WPA2进行保护";
    private static final String ESS_TEXT = "开放网络";

    private static final String WPA_PSK = "WPA";
    private static final String WPA2_PSK = "WPA2";

    private static final String ESS = "ESS";
    private static final String IBSS = "IBSS";

    private boolean isContainXXX(ScanResult scanResult, String xxx) {
        return scanResult.capabilities.contains(xxx);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.wifi_list_item_layout, null);
        return new ViewHolder(convertView);
    }

    private void selectAndAutoSkip(ViewHolder holder, int position) {
        if (selectList.get(position) == 1) {
            // 1是选中，
            holder.itemRoot.setBackgroundResource(R.drawable.frame_shape);
        } else {
            // 0是不选中
            holder.itemRoot.setBackgroundResource(R.drawable.item_selector);
        }

        if (keySubmitFlagList.get(position) == 1) {
            // 自动跳转
            Skip.skip((MainActivity) context, holder.itemRoot);
        } else {
            // 不自动跳转
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 先默认把 是否是加密网络 设置为加密网络
        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_ess,
                context.getString(R.string.wifi_adapter_connect_item_root_tag_is_wpa));
        // 先默认把是否成功连接的tag 设置为 未成功连接
        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_success,
                context.getString(R.string.wifi_adapter_connect_item_root_tag_is_success_no));
        // 先默认把是否是当前正在连接 设置为 不是当前连接
        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_scan_current_connect,
                context.getString(R.string.wifi_adapter_connect_item_root_tag_value_no));

        holder.name.setText(wifiData.get(position).SSID);

        judge(position);

        if (isLock) {
            // 这是加密网络
            holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_ess,
                    context.getString(R.string.wifi_adapter_connect_item_root_tag_is_wpa));
        } else {
            // 这是开放网络
            holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_ess,
                    context.getString(R.string.wifi_adapter_connect_item_root_tag_is_ess));

        }


        if (wifiData.get(position).SSID.equals(currentConnectSSID)) {
            Log.e(TAG, "当前连接的 ssid,匹配成功，pos：" + position);
            // 只是匹配 当前连接的 ssid
            holder.name.setTextColor(Color.WHITE);
            holder.state.setText(context.getString(R.string.wifi_adapter_connect_text));
            holder.state.setTextColor(Color.WHITE);
            holder.arrow.setVisibility(View.VISIBLE);

            holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_scan_current_connect,
                    context.getString(R.string.wifi_adapter_connect_item_root_tag_value_yes));
        } else {
            Log.e(TAG, "当前连接的 ssid,匹配---不---成功，pos：" + position);
            holder.state.setText(stateText);
            holder.name.setTextColor(context.getResources().getColor(R.color.color_text));
            holder.state.setTextColor(context.getResources().getColor(R.color.color_text));
            holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_scan_current_connect,
                    context.getString(R.string.wifi_adapter_connect_item_root_tag_value_no));
            // 不是当前连接的 ssid
            if (ssidList == null) {
                Log.e(TAG, "ssidList:== null");
                // 没有保存的列表
                holder.arrow.setVisibility(View.INVISIBLE);
                holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_success,
                        context.getString(R.string.wifi_adapter_connect_item_root_tag_is_success_no));
            } else {
                Log.e(TAG, "ssidList:" + ssidList.toString());
                holder.arrow.setVisibility(View.INVISIBLE);
                // 先恢复，后更改
                boolean success = false;
                for (int i = 0; i < ssidList.size(); i++) {
                    if (wifiData.get(position).SSID.equals(ssidList.get(i))) {
                        // 如果 当前的wifiData.get(position) 是 ssidList中的某一个
                        Log.e(TAG, "成功匹配--当前ssid:" + wifiData.get(position).SSID + "--正在比较:" + ssidList.get(i));
                        // 1.标记右箭头
                        holder.arrow.setVisibility(View.VISIBLE);
                        // 2.告诉 itemRoot 这个被成功连接过
                        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_success,
                                context.getString(R.string.wifi_adapter_connect_item_root_tag_is_success_yes));
                        // 3.点击跳到 相应的保存页面
                        success = true;
                    } else {
                        // 1.不要 标记右箭头
                        if (success) {
                            Log.e(TAG, "已执行success:true");
                        } else {
                            Log.e(TAG, "当前ssid:" + wifiData.get(position).SSID + "--正在比较:" + ssidList.get(i));
                            // 2.告诉 itemRoot 这个没有被成功连接过
                            holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_success,
                                    context.getString(R.string.wifi_adapter_connect_item_root_tag_is_success_no));
                            // 3.点击跳到 相应的输入密码 页面
                        }
                    }
                }
            }

        }

        stateText = "";
        holder.signal.setImageDrawable(getLevelDrawable(isLock, wifiData.get(position).level));

        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_signal, levelString);
        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_scan_result, wifiData.get(position));
        holder.itemRoot.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.DONUT)
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                /**
                 * holder.itemRoot 设置的tag有 是否有锁 是否就当前 连接项 是否被成功连接过 当前信号强度
                 * 当前wifi扫描的结果对象
                 */
                Skip.skip((MainActivity) context, v);
            }
        });

        selectAndAutoSkip(holder, position);
    }

    @Override
    public int getItemCount() {
        return wifiData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView state;
        public ImageView signal;
        public ImageView arrow;
        public AutoLinearLayout itemRoot;

        public ViewHolder(View itemView) {
            super(itemView);
            itemRoot = (AutoLinearLayout) itemView.findViewById(R.id.wifi_item_root_layout);
            name = (TextView) itemView.findViewById(R.id.wifi_item_name);
            state = (TextView) itemView.findViewById(R.id.wifi_item_state);
            signal = (ImageView) itemView.findViewById(R.id.wifi_item_signal);
            arrow = (ImageView) itemView.findViewById(R.id.wifi_item_arrow);
        }
    }
}
