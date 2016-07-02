package com.yuyife.flywifi.util;

import com.yuyife.flywifi.MainActivity;
import com.yuyife.flywifi.R;
import com.yuyife.flywifi.fragment.EssSaveFragment;
import com.yuyife.flywifi.fragment.InputPwdFragment;
import com.yuyife.flywifi.fragment.WpaSaveFragment;
import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

/**
 * YUYIFE
 */
public class Skip {

	public static final String TAG = "skip";

	@SuppressLint("NewApi")
	public static void skip(MainActivity activity, View v) {

		InputPwdFragment inputPwdInstance = InputPwdFragment.getInstance();
		WpaSaveFragment wpaSaveFragment = WpaSaveFragment.getInstance();
		EssSaveFragment essSaveFragment = EssSaveFragment.getInstance();
		// 是否有锁
		String tagIsLock = (String) v.getTag(R.id.wifi_adapter_item_root_tag_is_ess);
		// 是否就当前 连接项
		String tagCurrentConnect = (String) v.getTag(R.id.wifi_adapter_item_root_tag_scan_current_connect);
		// 是否被成功连接过
		String tagIsSuccess = (String) v.getTag(R.id.wifi_adapter_item_root_tag_is_success);

		if (tagIsLock.equals(activity.getString(R.string.wifi_adapter_connect_item_root_tag_is_wpa))) {
			Log.e(TAG, "这是一个加密网络:" + tagIsLock);
			// 这里点击的就是一个加密网络
			// 1.是当前连接的，2.不是当前连接
			if (tagCurrentConnect.equals(activity.getString(R.string.wifi_adapter_connect_item_root_tag_value_yes))) {
				Log.e(TAG, "这是一个加密网络——当前正在连接:" + tagCurrentConnect);
				// 跳转到 用户输入界面
				wpaSaveFragment.setCurrentConnectFlag(true);
				wpaSaveFragment.setLevelString((String) v.getTag(R.id.wifi_adapter_item_root_tag_signal));
				wpaSaveFragment.setScanResult((ScanResult) v.getTag(R.id.wifi_adapter_item_root_tag_scan_result));
				activity.setFragment(wpaSaveFragment);

			} else if (tagCurrentConnect
					.equals(activity.getString(R.string.wifi_adapter_connect_item_root_tag_value_no))) {
				// 当前条目是已经连接的wifi 跳转到 保存界面
				// Toast.makeText(context, "当前条目是已经连接的wifi 跳转到 保存界面",
				// Toast.LENGTH_SHORT).show();
				Log.e(TAG, "这是一个加密网络——当前没有连接:" + tagCurrentConnect);
				if (tagIsSuccess
						.equals(activity.getString(R.string.wifi_adapter_connect_item_root_tag_is_success_yes))) {
					Log.e(TAG, "这是一个加密网络——当前没有连接--但曾成功连接过:" + tagIsSuccess);
					activity.showWifiSaveTitle();
					wpaSaveFragment.setCurrentConnectFlag(false);
					wpaSaveFragment.setLevelString((String) v.getTag(R.id.wifi_adapter_item_root_tag_signal));
					wpaSaveFragment.setScanResult((ScanResult) v.getTag(R.id.wifi_adapter_item_root_tag_scan_result));
					activity.setFragment(wpaSaveFragment);
					// 解决 免密码登录 遗留下来 wifi主人换密码的问题
					// 入口
					/* 将跳转之前的数据，给main保存，方便home */

				} else {
					Log.e(TAG, "这是一个加密网络——当前没有连接--也没有曾成功连接过:" + tagIsSuccess);
					activity.showInputTitle();
					inputPwdInstance.setESSWifiFlag(false);
					inputPwdInstance.setLevelString((String) v.getTag(R.id.wifi_adapter_item_root_tag_signal));
					inputPwdInstance.setScanResult((ScanResult) v.getTag(R.id.wifi_adapter_item_root_tag_scan_result));
					activity.setFragment(inputPwdInstance);
				}

			} else {

			}
		} else if (tagIsLock.equals(activity.getString(R.string.wifi_adapter_connect_item_root_tag_is_ess))) {
			Log.e(TAG, "这是一个开放网络:" + tagIsLock);
			// 这里点击的就是一个开放网络
			// 1.是当前连接的，2.不是当前连接
			if (tagCurrentConnect.equals(activity.getString(R.string.wifi_adapter_connect_item_root_tag_value_yes))) {
				Log.e(TAG, "这是一个开放网络——并且当前正在连接:" + tagCurrentConnect);
				essSaveFragment.setCurrentConnectFlag(true);
				essSaveFragment.setLevelString((String) v.getTag(R.id.wifi_adapter_item_root_tag_signal));
				essSaveFragment.setScanResult((ScanResult) v.getTag(R.id.wifi_adapter_item_root_tag_scan_result));
				activity.setFragment(essSaveFragment);
			} else if (tagCurrentConnect
					.equals(activity.getString(R.string.wifi_adapter_connect_item_root_tag_value_no))) {
				// 正在连接
				Log.e(TAG, "这是一个开放网络——当前没有连接:" + tagCurrentConnect);
				if (tagIsSuccess
						.equals(activity.getString(R.string.wifi_adapter_connect_item_root_tag_is_success_yes))) {
					Log.e(TAG, "这是一个开放网络——当前没有连接--但曾成功连接过:" + tagIsSuccess);
					activity.showWifiSaveTitle();
					essSaveFragment.setCurrentConnectFlag(false);
					essSaveFragment.setLevelString((String) v.getTag(R.id.wifi_adapter_item_root_tag_signal));
					essSaveFragment.setScanResult((ScanResult) v.getTag(R.id.wifi_adapter_item_root_tag_scan_result));
					activity.setFragment(essSaveFragment);
				} else {
					Log.e(TAG, "这是一个开放网络——当前没有连接--也没有曾成功连接过:" + tagIsSuccess);
					activity.showHomeTitle();
					inputPwdInstance.setESSWifiFlag(true);
					inputPwdInstance.setLevelString((String) v.getTag(R.id.wifi_adapter_item_root_tag_signal));
					inputPwdInstance.setScanResult((ScanResult) v.getTag(R.id.wifi_adapter_item_root_tag_scan_result));
					activity.setFragment(inputPwdInstance);
				}

			}
		}
	}
}
