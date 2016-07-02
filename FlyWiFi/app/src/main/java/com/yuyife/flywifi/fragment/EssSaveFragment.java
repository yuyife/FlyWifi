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
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife 不加密的保存页面
 */
public class EssSaveFragment extends BaseWifiConnectFragment {

	private static final String TAG = "EssSaveFragment";
	public static EssSaveFragment instance = null;
	@Bind(R.id.ess_save_cancel)
	public TextView essSaveCancel; // 取消

	@Bind(R.id.ess_save_cancel_save)
	public AutoLinearLayout essSaveCancelSave;// 取消保存

	@Bind(R.id.ess_save_cancel_save_text)
	TextView essSaveCancelSaveText;

	@Bind(R.id.ess_save_cancel_save_hint)
	AutoLinearLayout essSaveCancelSaveHint;
	@Bind(R.id.ess_save_cancel_save_hint_bottom)
	TextView essSaveCancelSaveHintBottom;
	@Bind(R.id.ess_save_cancel_save_hint_top)
	TextView essSaveCancelSaveHintTop;

	@Bind(R.id.ess_save_connect)
	public AutoLinearLayout essSaveConnect;// 连接
	@Bind(R.id.ess_save_connect_hint)
	TextView essSaveConnectHint;

	private int cancelSaveCount = 0;

	@OnClick({ R.id.ess_save_cancel, R.id.ess_save_cancel_save, R.id.ess_save_connect })
	public void onEssSaveClick(View v) {
		switch (v.getId()) {
		case R.id.ess_save_cancel:

			cancel();
			break;
		case R.id.ess_save_cancel_save:

			// 取消保存
			if (isCurrentConnect) {
				// 当前连接 text = 断开链接，
				disconnect();
			} else {
				// 非当前连接
				cancelSave();
			}

			break;
		case R.id.ess_save_connect:
			connect();
			break;
		}
	}

	public EssSaveFragment() {
	}

	public static EssSaveFragment getInstance() {
		if (instance == null) {
			return new EssSaveFragment();
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
		View view = inflater.inflate(R.layout.wifi_ess_save_fragment, null);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MainActivity.essFragment = this;
		cancelSaveCount = 0;
		essSaveCancelSaveHint.setVisibility(View.GONE);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initWidget() {

		if (isCurrentConnect) {
			essSaveConnectHint.setText(activity.getString(R.string.current_connect_text_hint));
			essSaveCancelSaveText.setText(activity.getString(R.string.wifi_cancel_save_is_connect_text));
		} else {
			essSaveConnectHint.setText(activity.getString(R.string.current_connect_ess_text_hint));
			essSaveCancelSaveText.setText(activity.getString(R.string.wifi_cancel_save_is_not_connect_text));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		receiverAction(false);
		ButterKnife.unbind(this);
	}

	@Override
	public void onConnectivityAction() {
		Log.e(TAG, "onConnectivityAction");
	}

	@Override
	public void onNetPing(String pingResult) {

		if (pingResult.equals(OK)) {
			// 说明 wifi连接成功,收到当前网络链接的广播，并且ping通baidu
		} else if (pingResult.equals(NO)) {
			// 说明 wifi发出连接收到当前网络链接的广播，但是没有ping通baidu
		} else if (pingResult.equals(OTHER)) {
		} else {
			// 说明 wifi发出连接收到当前网络链接的广播，但是ping的时候报错
		}

	}

	@Override
	public void onConnectResult(boolean connectResult) {

		if (connectResult) {
			Log.e(TAG, "scanResult.SSID:" + scanResult.SSID);
			SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, scanResult.SSID);
			Log.e(TAG, "SPHelper.SSID:" + SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY));
			// MainActivity.homeRecyclerFragment.setIpFlag(2);

			connectOkTip(essSaveConnectHint);
			baseHandler.sendEmptyMessageDelayed(CANCEL, SET_VIEW_BACKGROUND_AT_TIME);
		} else {
			// connectErrTip(essSaveConnectHint);
			startConnectTest();
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
		setViewBackgroundTransparent(essSaveCancel, essSaveConnect);
		setViewBackground(essSaveCancelSave);

		int netWorkId = activity.getWifiUtil().getNetworkId();
		int ip = activity.getWifiUtil().getIPAddress();
		Log.e(TAG, "netWorkId:" + netWorkId);
		Log.e(TAG, "IP:" + ip);

		activity.getWifiUtil().disconnectWifi(netWorkId);
		if (essSaveCancelSaveHint.getVisibility() == View.GONE) {
			essSaveCancelSaveHint.setVisibility(View.VISIBLE);
			essSaveConnectHint.setText("");

			essSaveCancelSaveHintTop.setText("");
			essSaveCancelSaveHintBottom.setText("已断开");

		}
		// MainActivity.homeRecyclerFragment.setIpFlag(3);
		baseHandler.sendEmptyMessageDelayed(CANCEL, SET_VIEW_BACKGROUND_AT_TIME);
	}

	private void cancelSave() {
		if (cancelSaveCount == 0) {
			if (essSaveCancelSaveHint.getVisibility() == View.GONE) {
				essSaveCancelSaveHint.setVisibility(View.VISIBLE);
				essSaveCancelSaveHintTop.setText(activity.getString(R.string.wifi_cancel_save_tips_ess));
				essSaveCancelSaveHintBottom.setText(activity.getString(R.string.wifi_cancel_save_tips_again));
			}

			setViewBackgroundTransparent(essSaveCancel, essSaveConnect);
			setViewBackground(essSaveCancelSave);
			setViewBackground(essSaveCancelSave, R.drawable.frame_shape_red, SET_VIEW_BACKGROUND_AT_TIME);
			cancelSaveCount = 1;
		} else if (cancelSaveCount == 1) {
			// 处理删除网络配置的操作
			// 1.先变化提示，提示用户删除成功
			// 2.把连接的提示清除
			// 3.隔1秒后自动关闭
			cancelSaveCount = 0;
			essSaveConnectHint.setText("");

			essSaveCancelSaveHintTop.setText("");
			essSaveCancelSaveHintBottom.setText("已取消");
			// ---以上为ui的操作，
			boolean isRemove = SPHelper.saveMapStringForRemove(activity, scanResult.SSID);
			if (isRemove) {
				baseHandler.sendEmptyMessageDelayed(CANCEL, SET_VIEW_BACKGROUND_AT_TIME);
			} else {
				essSaveCancelSaveHintBottom.setText("Remove Failed:" + isRemove);
			}
		}
	}

	private void cancel() {
		activity.setKeyIndex(4);
		setViewBackgroundTransparent(essSaveCancelSave);
		setViewBackground(essSaveCancel);
		baseHandler.sendEmptyMessage(CANCEL);
	}

	private void connect() {
		activity.setKeyIndex(5);
		setViewBackgroundTransparent(essSaveCancel, essSaveCancelSave);
		setViewBackground(essSaveConnect);
		setViewBackground(essSaveConnect, SET_VIEW_BACKGROUND_AT_TIME);

		if (isCurrentConnect) {
			// Snackbar.make(essSaveConnect,
			// activity.getString(R.string.current_connect_text_hint),
			// Snackbar.LENGTH_LONG).show();

			showSnackBar(essSaveConnect, activity.getString(R.string.current_connect_text_hint));
		} else {
			receiverAction(true);
			isConnectClick = true;
			// 进行连接的操作

			Message message = Message.obtain();
			message.what = CONNECT_WIFI;
			message.obj = new WifiConnectBean(scanResult, scanResult.SSID, "", true);
			baseConnectHandler.handleMessage(message);
			connectingTip(essSaveConnectHint);
			startConnectTest();
		}
	}
}
