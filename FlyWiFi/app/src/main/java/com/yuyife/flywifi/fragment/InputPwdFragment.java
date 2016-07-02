package com.yuyife.flywifi.fragment;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.yuyife.flywifi.MainActivity;
import com.yuyife.flywifi.R;
import com.yuyife.flywifi.anim.MyAnimation;
import com.yuyife.flywifi.base.BaseWifiConnectFragment;
import com.yuyife.flywifi.bean.WifiConnectBean;
import com.yuyife.flywifi.util.SPHelper;
import com.yuyife.flywifi.util.WifiSafetyString;
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife Wifi密码输入(包括加密网络和开放网络)
 */
public class InputPwdFragment extends BaseWifiConnectFragment {
	private static final String TAG = "InputPwdFragment";
	private static final int INPUT_CONNECT_TEXT_STEP_HINT = 11;
	private static final int INPUT_CONNECT_TEXT_STEP_PING = 12;
	private static final int INPUT_CONNECT_TEXT_STEP_Ok = 13;
	private static final int INPUT_CONNECT_TEXT_STEP_ERR = 404;
	private static final int INPUT_CONNECT_TEXT_STEP_NO = 14;
	// private static final int INPUT_CONNECT_TEST = 15;
	private static final int INPUT_CONNECT_OK = 16;

	public static InputPwdFragment instance = null;
	@Bind(R.id.input_signal_tv)
	TextView inputSignalTv;
	@Bind(R.id.input_safety_tv)
	TextView inputSafetyTv;
	@Bind(R.id.input_SSID_pwd_tv)
	TextView inputSSIDPwdTv;
	@Bind(R.id.input_SSID_tv)
	TextView inputSSIDTv;
	@Bind(R.id.input_pwd_ed)
	public EditText inputPwdEd; // 输入编辑框
	@Bind(R.id.input_show_checkbox)
	CheckBox inputShowCheckbox;
	@Bind(R.id.input_show_checkbox_layout)
	public AutoLinearLayout inputShowCheckboxLayout; // 是否显示密码 layout
	@Bind(R.id.input_cancel_tv)
	public TextView inputCancelTv; // 取消
	@Bind(R.id.input_connect_hint)
	TextView inputConnectHint;
	@Bind(R.id.input_connect)
	public AutoLinearLayout inputConnect;// 连接

	@OnClick({ R.id.input_cancel_tv, R.id.input_connect, R.id.input_pwd_ed, R.id.input_show_checkbox_layout })
	public void onInputClick(View v) {
		switch (v.getId()) {
		case R.id.input_pwd_ed:
			setViewBackgroundTransparent(activity.wifiTitleLayout, inputCancelTv, inputConnect, inputShowCheckboxLayout,
					inputPwdEd);
			setViewBackground(inputPwdEd);
			activity.setKeyIndex(2);
			break;
		case R.id.input_show_checkbox_layout:
			setViewBackgroundTransparent(activity.wifiTitleLayout, inputCancelTv, inputConnect, inputShowCheckboxLayout,
					inputPwdEd);
			setViewBackground(inputShowCheckboxLayout);
			check();
			break;
		case R.id.input_cancel_tv:
			// 取消
			cancel();
			break;
		case R.id.input_connect:
			connect();
			break;
		}
	}

	public InputPwdFragment() {
	}

	public static InputPwdFragment getInstance() {
		if (instance == null) {
			return new InputPwdFragment();
		}
		return instance;
	}

	private ScanResult scanResult;
	private String levelString = "";
	private boolean isESSWifi = false;

	public void setScanResult(ScanResult scanResult) {
		this.scanResult = scanResult;
	}

	public void setLevelString(String levelString) {
		this.levelString = levelString;
	}

	public void setESSWifiFlag(boolean isESSWifi) {
		this.isESSWifi = isESSWifi;
	}

	public boolean isESSWifi() {
		return this.isESSWifi;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.wifi_input_pwd_fragment, null);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MainActivity.inputFragment = this;

		if (inputConnectHint != null) {
			inputConnectHint.setText("");
		}
		if (scanResult != null) {
			if (inputSSIDTv != null) {
				if (TextUtils.isEmpty(scanResult.SSID)) {
					// 此处就没有名字的wifi网络
					inputSSIDTv.setText(activity.getString(R.string.wifi_ssid_is_none) + "\t\t");
				} else {
					inputSSIDTv.setText(scanResult.SSID);
					inputSafetyTv.setText(WifiSafetyString.makeSafetyString(scanResult));
					inputSignalTv.setText(levelString);
				}
			}

			if (isESSWifi) {
				// 这是开放网络
				inputPwdEd.setVisibility(View.GONE);
				inputShowCheckboxLayout.setVisibility(View.GONE);
				inputSSIDPwdTv.setText(activity.getString(R.string.wifi_is_open));

				setViewBackground(inputConnect);
			} else {

			}
		}
		receiverAction(true);
		refreshNetWorkState();
		Log.e(TAG, "netWorkType:" + netWorkType);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initWidget() {
		inputShowCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				showPwd(isChecked);
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
		receiverAction(false);
	}

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case INPUT_CONNECT_TEXT_STEP_HINT:
				if (isESSWifi) {
					connectingTip(inputConnectHint);

				} else {
					connectCheckingTip(inputConnectHint);
				}
				break;
			case INPUT_CONNECT_TEXT_STEP_PING:
				connectPingTip(inputConnectHint);
				break;
			case INPUT_CONNECT_TEXT_STEP_Ok:
				if (inputConnectHint != null) {
					connectOkTip(inputConnectHint);
				}
				uiHandler.sendEmptyMessageDelayed(INPUT_CONNECT_OK, 1000);

				break;
			case INPUT_CONNECT_TEXT_STEP_NO:
				connectPingNoTip(inputConnectHint);
				break;
			case INPUT_CONNECT_TEXT_STEP_ERR:
				if (inputConnectHint != null) {

					connectErrTip(inputConnectHint);
					MyAnimation.shakeAnim(inputConnectHint, 300);
				}
				break;

			case INPUT_CONNECT_OK:
				// 表示 网络连接成功 关闭
				// 此时返回 wifi列表，应该告知用户，已经成功连接到刚才输入密码的wifi名称
				activity.showHomeTitle();
				activity.setFragment(MainActivity.homeRecyclerFragment);
				// 此时可以来一个关闭的动画

				// 保存此次 连接的 配置
				if (isESSWifi) {
					currentWifiPwd = activity.getString(R.string.wifi_is_open_pwd);
				} else {

				}
				boolean isSaveOK = SPHelper.saveMapStringForAdd(activity, currentWifiSsid, currentWifiPwd);
				// Toast.makeText(activity, "isSaveOK:" + isSaveOK,
				// Toast.LENGTH_SHORT).show();
				if (isSaveOK) {
					// 保存成功后 刷新ssid列表
					Log.e(TAG, "已把此次网络配置加入本地保存");
				} else {
					Log.e(TAG, "此次网络配置加入本地保存--发生错误");
				}
				break;

			}
		}
	};

	@Override
	public void onConnectivityAction() {
		// 收到 网络连接的广播 --》 onNetPing
		uiHandler.sendEmptyMessage(INPUT_CONNECT_TEXT_STEP_PING);

	}

	@Override
	public void onNetPing(String pingResult) {
		// 返回ping网络的结果
		if (pingResult.equals(OK)) {
			// 说明 wifi连接成功,收到当前网络链接的广播，并且ping通baidu
			uiHandler.sendEmptyMessage(INPUT_CONNECT_TEXT_STEP_Ok);
			SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, scanResult.SSID);
			// MainActivity.homeRecyclerFragment.setIpFlag(1);
		} else if (pingResult.equals(NO)) {
			// 说明 wifi连接成功,收到当前网络链接的广播，但是没有ping通baidu
			uiHandler.sendEmptyMessage(INPUT_CONNECT_TEXT_STEP_Ok);
			SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, scanResult.SSID);
			// MainActivity.homeRecyclerFragment.setIpFlag(1);
		} else {
			// 说明 wifi连接成功,收到当前网络链接的广播，但是没有ping的时候报错
			uiHandler.sendEmptyMessage(INPUT_CONNECT_TEXT_STEP_Ok);
			SPHelper.setString(activity, SPHelper.CURRENT_SSID_KEY, scanResult.SSID);
			// MainActivity.homeRecyclerFragment.setIpFlag(1);
		}

	}

	@Override
	public void onConnectResult(boolean connectResult) {
		// 调用开始测试，测试3次之后，返回结果
		if (connectResult) {

		} else {
			uiHandler.sendEmptyMessage(INPUT_CONNECT_TEXT_STEP_ERR);
		}
	}

	@Override
	public void onKeySubmitAction(int index) {
		switch (index) {
		case 1:
			// 返回home
			cancel();
			break;
		case 2:
			if (isESSWifi) {
				showToast("开放网络 的密码输入");
			} else {
				// showToast("加密网络 的密码输入");
				inputPwd();
			}
			break;
		case 3:
			if (isESSWifi) {
				showToast("开放网络 的显示密码");
			} else {
				// showToast("加密网络 的显示密码");
				check();
			}
			break;
		case 4:
			// showToast("取消");
			cancel();
			break;
		case 5:
			// showToast("连接");
			connect();
			break;
		}
	}

	private void check() {
		activity.setKeyIndex(3);
		boolean isChecked = inputShowCheckbox.isChecked();
		if (isChecked) {
			inputShowCheckbox.setChecked(false);
		} else {
			inputShowCheckbox.setChecked(true);
		}
	}

	private void inputPwd() {
		inputPwdEd.setFocusable(true);

		inputPwdEd.setFocusableInTouchMode(true);
		inputPwdEd.requestFocus();
		inputPwdEd.findFocus();
	}

	private void showPwd(boolean isChecked) {
		if (isChecked) {
			inputPwdEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		} else {
			// 否则隐藏密码
			inputPwdEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		}
	}

	private void cancel() {

		setViewBackgroundTransparent(activity.wifiTitleLayout, inputCancelTv, inputConnect, inputShowCheckboxLayout,
				inputPwdEd);
		setViewBackground(inputCancelTv);
		baseHandler.sendEmptyMessage(CANCEL);
	}

	private void connect() {
		activity.setKeyIndex(5);
		setViewBackgroundTransparent(activity.wifiTitleLayout, inputCancelTv, inputConnect, inputShowCheckboxLayout,
				inputPwdEd);
		setViewBackground(inputConnect);
		/*
		 * 1.没有密码:WIFICIPHER_NOPASS 2.用wep加密:WIFICIPHER_WEP
		 * 3.用wpa加密:WIFICIPHER_WPA
		 */
		/*
		 * 连接之前 先判断当前网络是否可用，是否是WIFI,是否移动数据
		 */
		// 连接 之前保存一份temp,用于验证正确wifi链接之后的判断

		isConnectClick = true;

		currentWifiSsid = scanResult.SSID;
		if (isESSWifi) {
			uiHandler.sendEmptyMessage(INPUT_CONNECT_TEXT_STEP_HINT);

		} else {
			if (TextUtils.isEmpty(inputPwdEd.getText().toString())) {
				// Snackbar.make(inputPwdEd, getString(
				// R.string.input_connect_text_hint_pwd_is_none),
				// Snackbar.LENGTH_LONG).show();

				showSnackBar(inputPwdEd, getString(R.string.input_connect_text_hint_pwd_is_none));
				return;

			} else if (inputPwdEd.getText().toString().length() < 8) {
				// Snackbar.make(inputPwdEd, getString(
				// R.string.input_connect_text_hint_min_8),
				// Snackbar.LENGTH_LONG).show();
				showSnackBar(inputPwdEd, getString(R.string.input_connect_text_hint_min_8));
				return;
			}

			currentWifiPwd = inputPwdEd.getText().toString();

			uiHandler.sendEmptyMessage(INPUT_CONNECT_TEXT_STEP_HINT);

			// 加入网络之后，会收到 获取ip的广播，成功后会收到
			// 当前是否有网络连上 （针对于netWorkType == 0）
			startConnectTest();// 启动测试
		}

		Message message = Message.obtain();
		message.what = CONNECT_WIFI;
		message.obj = new WifiConnectBean(scanResult, currentWifiSsid, currentWifiPwd, isESSWifi);
		baseConnectHandler.handleMessage(message);
	}
}
