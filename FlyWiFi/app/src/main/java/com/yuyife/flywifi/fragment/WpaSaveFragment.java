package com.yuyife.flywifi.fragment;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuyife.flywifi.MainActivity;
import com.yuyife.flywifi.R;
import com.yuyife.flywifi.base.BaseWifiConnectFragment;
import com.yuyife.flywifi.bean.WifiConnectBean;
import com.yuyife.flywifi.util.SPHelper;
import com.yuyife.flywifi.util.WifiSafetyString;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife 加密的保存页面 有一处不严谨的地方： 虽然上次使用密码成功连接了网络，但是经过时间的洗礼，wifi主人改变了密码
 *         此时再用以前的密码登录，就会发生验证错误 （先留下一个悬念，稍后再优化）
 */
public class WpaSaveFragment extends BaseWifiConnectFragment {

	private static final String TAG = "WpaSaveFragment";
	public static WpaSaveFragment instance = null;
	@Bind(R.id.wpa_save_signal_tv)
	TextView wpaSaveSignalTv;
	@Bind(R.id.wpa_save_safety_tv)
	TextView wpaSaveSafetyTv;
	@Bind(R.id.wpa_save_cancel_save)
	public AutoLinearLayout wpaSaveCancelSave;// 取消保存

	@Bind(R.id.wpa_save_cancel_save_text)
	TextView wpaSaveCancelSaveText;

	@Bind(R.id.wpa_save_cancel_save_hint)
	AutoLinearLayout wpaSaveCancelSaveHint;
	@Bind(R.id.wpa_save_cancel_save_hint_top)
	TextView wpaSaveCancelSaveHintTop;
	@Bind(R.id.wpa_save_cancel_save_hint_bottom)
	TextView wpaSaveCancelSaveHintBottom;

	@Bind(R.id.wpa_save_cancel)
	public TextView wpaSaveCancel; // 取消
	@Bind(R.id.wpa_save_connect)
	public AutoLinearLayout wpaSaveConnect; // 连接
	@Bind(R.id.wpa_save_connect_hint)
	TextView wpaSaveConnectHint;

	private int cancelSaveCount = 0;

	@OnClick({ R.id.wpa_save_cancel_save, R.id.wpa_save_cancel, R.id.wpa_save_connect })
	public void onWpaSaveClick(View v) {
		switch (v.getId()) {
		case R.id.wpa_save_cancel_save:
			// 取消保存
			if (isCurrentConnect) {
				// 当前连接 text = 断开连接
				disconnect();
			} else {
				// 非当前连接
				cancelSave();
			}

			break;
		case R.id.wpa_save_cancel:
			// 取消
			cancel();
			break;
		case R.id.wpa_save_connect:
			// 连接
			connect();
			break;
		}
	}

	public WpaSaveFragment() {
	}

	public static WpaSaveFragment getInstance() {
		if (instance == null) {
			return new WpaSaveFragment();
		}
		return instance;
	}

	private ScanResult scanResult;
	private String levelString = "";

	private boolean isCurrentConnect = false;

	public void setCurrentConnectFlag(boolean isCurrentConnect) {
		this.isCurrentConnect = isCurrentConnect;
	}

	public void setScanResult(ScanResult scanResult) {
		this.scanResult = scanResult;
	}

	public void setLevelString(String levelString) {
		this.levelString = levelString;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.wifi_wpa_save_fragment, null);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MainActivity.wpaFragment = this;
		receiverAction(false);
		cancelSaveCount = 0;
		wpaSaveSignalTv.setText(levelString);

		wpaSaveSafetyTv.setText(WifiSafetyString.makeSafetyString(scanResult));
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initWidget() {

		if (isCurrentConnect) {
			wpaSaveConnectHint.setText(activity.getString(R.string.current_connect_text_hint));
			wpaSaveCancelSaveText.setText(activity.getString(R.string.wifi_cancel_save_is_connect_text));

		} else {
			wpaSaveConnectHint.setText(activity.getString(R.string.current_connect_no_pwd_text_hint));
			wpaSaveCancelSaveText.setText(activity.getString(R.string.wifi_cancel_save_is_not_connect_text));
		}
		wpaSaveCancelSaveHint.setVisibility(View.GONE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		receiverAction(false);
		ButterKnife.unbind(this);
	}

	@Override
	public void onConnectivityAction() {
		Log.e(TAG, "connectivityActionFlag");

	}

	@Override
	public void onNetPing(String pingResult) {
		Log.e(TAG, "onNetPing:" + pingResult);
		if (pingResult.equals(OK)) {
			// 说明 wifi连接成功,收到当前网络链接的广播，并且ping通baidu
		} else if (pingResult.equals(NO)) {
			// 说明 wifi发出连接收到当前网络链接的广播，但是没有ping通baidu
		} else if (pingResult.equals(OTHER)) {
		} else {
			// 说明 wifi发出连接收到当前网络链接的广播，但是ping的时候报错
		}

	}

	private int resultCount = 0;

	@Override
	public void onConnectResult(boolean connectResult) {
		if (connectResult) {
			Log.e(TAG, "scanResult.SSID:" + scanResult.SSID);
			SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, scanResult.SSID);
			Log.e(TAG, "SPHelper.SSID:" + SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY));
			// MainActivity.homeRecyclerFragment.setIpFlag(2);
			connectOkTip(wpaSaveConnectHint);
			baseHandler.sendEmptyMessageDelayed(CANCEL, SET_VIEW_BACKGROUND_AT_TIME);
		} else {
			if (resultCount == 0) {
				startConnectTest();
				resultCount++;
			} else {
				connectErrTip(wpaSaveConnectHint);
			}
		}
	}

	@Override
	public void onKeySubmitAction(int index) {
		switch (index) {
		case 1:
			cancel();
			break;
		case 2:
			if (isCurrentConnect) {
				// showToast("断开连接");
				disconnect();
			} else {
				// showToast("取消保存");
				cancelSave();
			}
			break;
		case 3:
			// showToast("取消");
			cancel();
			break;
		case 4:
			// showToast("连接");
			connect();
			break;
		}
	}

	private void disconnect() {
		activity.setKeyIndex(2);
		setViewBackgroundTransparent(activity.wifiTitleLayout, wpaSaveCancel, wpaSaveConnect);
		setViewBackground(wpaSaveCancelSave);
		int netWorkId = activity.getWifiUtil().getNetworkId();
		int ip = activity.getWifiUtil().getIPAddress();
		Log.e(TAG, "netWorkId:" + netWorkId);
		Log.e(TAG, "IP:" + ip);

		activity.getWifiUtil().disconnectWifi(netWorkId);
		if (wpaSaveCancelSaveHint.getVisibility() == View.GONE) {
			wpaSaveCancelSaveHint.setVisibility(View.VISIBLE);
			wpaSaveConnectHint.setText("");

			wpaSaveCancelSaveHintTop.setText("");
			wpaSaveCancelSaveHintBottom.setText("已断开");

		}
		// MainActivity.homeRecyclerFragment.setIpFlag(3);
		baseHandler.sendEmptyMessageDelayed(CANCEL, SET_VIEW_BACKGROUND_AT_TIME);
	}

	private void cancelSave() {
		activity.setKeyIndex(2);
		if (cancelSaveCount == 0) {
			if (wpaSaveCancelSaveHint.getVisibility() == View.GONE) {
				wpaSaveCancelSaveHint.setVisibility(View.VISIBLE);

				connectCancelSaveTip(wpaSaveCancelSaveHintTop);
				connectCancelSaveSubtmitTip(wpaSaveCancelSaveHintBottom);

			}
			setViewBackgroundTransparent(activity.wifiTitleLayout, wpaSaveCancel, wpaSaveConnect);
			setViewBackground(wpaSaveCancelSave);
			setViewBackground(wpaSaveCancelSave, R.drawable.frame_shape_red, SET_VIEW_BACKGROUND_AT_TIME);
			cancelSaveCount = 1;
		} else if (cancelSaveCount == 1) {
			// 处理删除网络配置的操作
			// 1.先变化提示，提示用户删除成功
			// 2.把连接的提示清除
			// 3.隔1秒后自动关闭
			cancelSaveCount = 0;
			wpaSaveConnectHint.setText("");

			wpaSaveCancelSaveHintTop.setText("");
			wpaSaveCancelSaveHintBottom.setText("已取消");
			// ---以上为ui的操作，
			boolean isRemove = SPHelper.saveMapStringForRemove(activity, scanResult.SSID);
			if (isRemove) {
				baseHandler.sendEmptyMessageDelayed(CANCEL, SET_VIEW_BACKGROUND_AT_TIME);
			} else {
				wpaSaveCancelSaveHintBottom.setText("Remove Failed:" + isRemove);
			}
		}
	}

	private void cancel() {
		activity.setKeyIndex(3);
		setViewBackgroundTransparent(activity.wifiTitleLayout, wpaSaveCancelSave, wpaSaveConnect);
		setViewBackground(wpaSaveCancel);
		baseHandler.sendEmptyMessage(CANCEL);
	}

	private void connect() {
		activity.setKeyIndex(4);
		setViewBackgroundTransparent(activity.wifiTitleLayout, wpaSaveCancelSave, wpaSaveCancel);
		setViewBackground(wpaSaveConnect);
		if (isCurrentConnect) {
			// Snackbar.make(wpaSaveConnect,
			// activity.getString(R.string.current_connect_text_hint),
			// Snackbar.LENGTH_LONG).show();
			showSnackBar(wpaSaveConnect, activity.getString(R.string.current_connect_text_hint));

		} else {
			receiverAction(true);
			isConnectClick = true;
			// 进行连接的操作
			Map<String, String> map = SPHelper.getMapForJson(SPHelper.getMapString(activity));

			String pwd = map.get(scanResult.SSID);

			Message message = Message.obtain();
			message.what = CONNECT_WIFI;
			message.obj = new WifiConnectBean(scanResult, scanResult.SSID, pwd, false);
			baseConnectHandler.handleMessage(message);
			wpaSaveConnectHint.setText(activity.getString(R.string.input_connect_text_ess_hint));
			startConnectTest();// 启动测试
		}
	}
}
