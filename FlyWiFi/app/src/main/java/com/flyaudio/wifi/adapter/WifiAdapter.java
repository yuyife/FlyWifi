package com.flyaudio.wifi.adapter;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyaudio.wifi.MainActivity;
import com.flyaudio.wifi.R;
import com.flyaudio.wifi.util.SPHelper;
import com.flyaudio.wifi.util.Skip;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * @author yuyife
 *         Created by Administrator on 2016/7/4.
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    private static final String TAG = "WifiAdapter";

    private MainActivity activity;
    private List<ScanResult> wifiData;
    private List<Integer> stateFlagData;
    private List<String> ssidData;//已保存的wifi名称列表

    private List<Integer> selectData;//标记按键选中
    private List<Integer> autoSkipData;//标记跳转

    public WifiAdapter(MainActivity activity,
                       List<ScanResult> wifiData,
                       List<Integer> stateFlagData,
                       List<String> ssidData,
                       List<Integer> selectData,
                       List<Integer> autoSkipData) {
        this.activity = activity;
        this.wifiData = wifiData;
        this.stateFlagData = stateFlagData;
        this.ssidData = ssidData;
        this.selectData = selectData;
        this.autoSkipData = autoSkipData;
    }

    private void selectAndAutoSkip(ViewHolder holder, int position) {
        if (selectData.get(position) == 1) {
            // 1是选中，
            holder.itemRoot.setBackgroundResource(R.drawable.frame_shape);
        } else {
            // 0是不选中
            holder.itemRoot.setBackgroundResource(R.drawable.item_selector);
        }

        if (autoSkipData.get(position) == 1) {
            // 自动跳转
            Skip.skip(activity, holder.itemRoot,Skip.AUTO);
        } else {
            // 不自动跳转
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.wifi_home_recycler_item_layout, parent,false);
        AutoUtils.auto(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanResult scanResult = wifiData.get(position);
        /**初始化view*/
        holder.name.setTextColor(activity.getResources().getColor(R.color.color_text));
        holder.state.setTextColor(activity.getResources().getColor(R.color.color_text));
        holder.name.setText(scanResult.SSID);
        holder.state.setText(AdapterHelper.getWifiTypeText(scanResult));

        holder.signal.setImageDrawable(
                AdapterHelper
                        .getSignalDrawable(activity,
                                AdapterHelper.isLock,
                                scanResult.level));

        /**默认Tag*/
        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_scan_result, scanResult);
        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_signal_text, AdapterHelper.levelString);
        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_lock, AdapterHelper.isLock);//这两项已在 取wifi信号图标时就做了判断

        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_current_connect, false);
        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_connected_success, false);//这两项，需要外界条件重新赋值

        if (position == 0) {
            Log.e(TAG, "CURRENT_SSID_KEY:" + SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY));
        }
        //设置adapter时得到的当前wifi ssid   和每一项进行比较
        if (SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY)
                .equals(scanResult.SSID)) {
            //表示当前正连接
            holder.state.setText("已连接");

            holder.name.setTextColor(Color.WHITE);
            holder.state.setTextColor(Color.WHITE);

            holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_current_connect, true);//需要外界条件重新赋值

            if (ssidData != null) {
                if (ssidData.size() == 0) {
                    holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_connected_success, false);//需要外界条件重新赋值
                } else {
                    for (int i = 0; i < ssidData.size(); i++) {
                        if (SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY).equals(scanResult.SSID)) {
                            holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_connected_success, true);//需要外界条件重新赋值
                        } else {

                        }
                    }
                }
            } else {

            }
        } else {
            holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_current_connect, false);//需要外界条件重新赋值
            //表示不是当前正连接
            if (ssidData != null) {
                for (int i = 0; i < ssidData.size(); i++) {
                    if (ssidData.get(i).equals(scanResult.SSID)) {
                        holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_connected_success, true);//需要外界条件重新赋值
                        String txt = holder.state.getText().toString();
                        holder.state.setText("已保存 " + txt);
                    }
                }
            } else {

            }

            if (stateFlagData.get(position) == 1) {
                holder.state.setText("正在连接..");
                //Log.e(TAG, "isStartConnecting:" + stateFlagData.get(position));
            } else if (stateFlagData.get(position) == 2) {
                //获取ip地址
                //设置为空
                holder.state.setText("正在进行身份验证..");
                //holder.state.setText("正在获取ip地址");
            } else if (stateFlagData.get(position) == 3) {
                //已连接
                holder.state.setText("已连接");

                holder.name.setTextColor(Color.WHITE);
                holder.state.setTextColor(Color.WHITE);

                holder.itemRoot.setTag(R.id.wifi_adapter_item_root_tag_is_current_connect, true);//需要外界条件重新赋值

            } else if (stateFlagData.get(position) == 4) {
                //身份验证出现错误
                holder.state.setText("身份验证出现错误");
            } else {
               // Log.e(TAG, "isStartConnecting:" + stateFlagData.get(position));

                holder.name.setTextColor(activity.getResources().getColor(R.color.color_text));
                holder.state.setTextColor(activity.getResources().getColor(R.color.color_text));
            }

        }

        selectAndAutoSkip(holder, position);

        holder.itemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Skip.skip(activity, view,Skip.CLICK);
            }
        });


    }

    @Override
    public int getItemCount() {
        return wifiData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView state;
        public ImageView signal;
        public RelativeLayout itemRoot;

        public ViewHolder(View itemView) {
            super(itemView);
            itemRoot = (RelativeLayout) itemView.findViewById(R.id.wifi_item_root_layout);
            name = (TextView) itemView.findViewById(R.id.wifi_item_name);
            state = (TextView) itemView.findViewById(R.id.wifi_item_state);
            signal = (ImageView) itemView.findViewById(R.id.wifi_item_signal);
        }
    }
}
