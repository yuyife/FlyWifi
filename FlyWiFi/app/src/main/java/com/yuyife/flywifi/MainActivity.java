package com.yuyife.flywifi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuyife.flywifi.base.BaseActivity;
import com.yuyife.flywifi.bean.FlagChange;
import com.yuyife.flywifi.fragment.EssSaveFragment;
import com.yuyife.flywifi.fragment.HomeRecyclerFragment;
import com.yuyife.flywifi.fragment.InputPwdFragment;
import com.yuyife.flywifi.fragment.WpaSaveFragment;
import com.yuyife.flywifi.interf.OnItemSelectAction;
import com.yuyife.flywifi.interf.OnKeySubmitAction;
import com.yuyife.flywifi.util.SPHelper;
import com.yuyife.flywifi.util.WifiUtil;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife
 *         <p/>
 *         flyWifi 目前设计为 1个Activity:MainActivity ---管理4个Fragment
 *         --------HomeRecyclerFragment --------InputPwdFragment
 *         --------WpaSaveFragment --------EssSaveFragment
 */
public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	@Bind(R.id.wifi_title_layout)
	public AutoLinearLayout wifiTitleLayout;
	@Bind(R.id.wifi_title)
	TextView wifiTitle;
	@Bind(R.id.wifi_title_image)
	ImageView wifiTitleImage;
	@Bind(R.id.wifi_title_append)
	TextView wifiTitleAppend;

	@OnClick({ R.id.wifi_title_layout })
	public void onMainClick(View v) {
		switch (v.getId()) {
		case R.id.wifi_title_layout:
			switch (titleFlag) {
			case 0:
				break;
			case 1:
				setShowWifiHintFlag(false);
				showHomeTitle();
				setFragment(homeRecyclerFragment);
				break;
			case 2:
				setShowWifiHintFlag(false);
				showHomeTitle();
				setFragment(homeRecyclerFragment);
				break;
			}
			break;
		}
	}

	private int titleFlag = 0;// 0-home,1-input,2-wifiSave
	private int fragmentFlag = 0;// 0-home,1-input,2-wpaSave,3-essSave
	public static HomeRecyclerFragment homeRecyclerFragment = null;
	public static InputPwdFragment inputFragment = null;
	public static WpaSaveFragment wpaFragment = null;
	public static EssSaveFragment essFragment = null;

	private OnKeySubmitAction keySubmitAction;
	private OnItemSelectAction itemSelectAction;

	// HomeRecyclerFragment 获得wifiUtil对象后，调用set方法赋值
	private WifiUtil wifiUtil;

	public void setWifiUtil(WifiUtil wifiUtil) {
		this.wifiUtil = wifiUtil;
	}

	public WifiUtil getWifiUtil() {
		return wifiUtil;
	}

	private boolean isHasMapString = false;

	private boolean showWifiHintFlag = false;

	public boolean isShowWifiHintFlag() {
		return showWifiHintFlag;
	}

	public void setShowWifiHintFlag(boolean flag) {
		this.showWifiHintFlag = flag;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		showWifiHintFlag = true;

		homeRecyclerFragment = HomeRecyclerFragment.getInstance();
		inputFragment = InputPwdFragment.getInstance();
		wpaFragment = WpaSaveFragment.getInstance();
		essFragment = EssSaveFragment.getInstance();
		SPHelper.setString(this, SPHelper.CURRENT_SSID_KEY, getString(R.string.home_connect_wifi_init_value));

		isHasMapString = SPHelper.isHasMapString(context);
		if (isHasMapString) {
			Map<String, String> map = SPHelper.getMapForJson(SPHelper.getMapString(context));

			Log.e(TAG, "MAP:" + map.toString());
		} else {
			// 说明还没有成功连接过任何wifi网络
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void setFragment(Fragment fragment) {
		keySubmitAction = (OnKeySubmitAction) fragment;
		keyIndex = 1;
		if (fragment instanceof HomeRecyclerFragment) {
			fragmentFlag = 0;
			itemSelectAction = (OnItemSelectAction) fragment;
		} else if (fragment instanceof InputPwdFragment) {
			fragmentFlag = 1;
		} else if (fragment instanceof WpaSaveFragment) {
			fragmentFlag = 2;
		} else if (fragment instanceof EssSaveFragment) {
			fragmentFlag = 3;
		}

		// 曾发生过以下异常
		// java.lang.IllegalStateException: Can not perform this action after
		// onSaveInstanceState
		try {
			getSupportFragmentManager().beginTransaction().replace(R.id.wifi_fragment, fragment).commit();
		} catch (Exception e) {
			Toast.makeText(this, "setFragment Exception:" + e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			getSupportFragmentManager().beginTransaction().replace(R.id.wifi_fragment, fragment).commit();
		}
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initWidget() {
		showHomeTitle();
		setFragment(homeRecyclerFragment);
		fragmentFlag = 0;
	}

	public void showHomeTitle() {
		titleFlag = 0;
		wifiTitleImage.setVisibility(View.GONE);
		wifiTitleAppend.setVisibility(View.GONE);
	}

	public void showInputTitle() {
		titleFlag = 1;
		wifiTitleImage.setVisibility(View.VISIBLE);
		wifiTitleAppend.setText("密码输入");
		wifiTitleAppend.setVisibility(View.VISIBLE);
	}

	public void showWifiSaveTitle() {
		titleFlag = 2;
		wifiTitleImage.setVisibility(View.VISIBLE);
		wifiTitleAppend.setText("WIFI保存设置");
		wifiTitleAppend.setVisibility(View.VISIBLE);
	}

	private int keyIndex = 1;

	public void setKeyIndex(int keyIndex) {
		this.keyIndex = keyIndex;
	}

	private boolean isNext = false;
	private int itemPosition = 0;
	private boolean isNeedChangeKeyIndex = true;

	public void setIsNeedChangeKeyIn(boolean isNeedChangeKeyIndex) {
		this.isNeedChangeKeyIndex = isNeedChangeKeyIndex;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isEss = false;
		if (inputFragment == null) {
			isEss = false;
		} else {
			isEss = inputFragment.isESSWifi();
		}
		// 获取 当前选择的条目
		switch (keyCode) {
		// 音量减小 用做 ++++++++++++++ next
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			// case KeyEvent.KEYCODE_DPAD_DOWN:
			if (isNeedChangeKeyIndex) {
				keyIndex = FlagChange.nextIndex(isEss, fragmentFlag, keyIndex);
				isNext = true;
				if (keyIndex == 4) {
					itemPosition = 0;
				} else {
					if (isNext) {

						itemPosition = itemPosition + 1;

					}
				}
				mainKey(keyIndex);
			} else {

			}
			return true;
		// 音量增大 用做 ————-- pre
		case KeyEvent.KEYCODE_VOLUME_UP:
			// case KeyEvent.KEYCODE_DPAD_UP:
			keyIndex = FlagChange.preIndex(isEss, fragmentFlag, keyIndex);
			isNext = false;
			isNeedChangeKeyIndex = true;
			if (keyIndex == 4) {
				itemPosition = 0;
			} else {
				if (keyIndex == 3) {
					homeRecyclerFragment.cleanSelect();
				}
				if (!isNext) {
					itemPosition = itemPosition - 1;
				}
			}
			mainKey(keyIndex);
			return true;

		case KeyEvent.KEYCODE_DPAD_LEFT:
			// pre
			keyIndex = FlagChange.preIndex(isEss, fragmentFlag, keyIndex);
			isNext = false;
			isNeedChangeKeyIndex = true;
			if (keyIndex == 4) {
				itemPosition = 0;
			} else {
				if (keyIndex == 3) {
					homeRecyclerFragment.cleanSelect();
				}
				if (!isNext) {
					itemPosition = itemPosition - 1;
				}
			}
			mainKey(keyIndex);
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			// next
			if (isNeedChangeKeyIndex) {

				keyIndex = FlagChange.nextIndex(isEss, fragmentFlag, keyIndex);
				isNext = true;

				if (keyIndex == 4) {
					itemPosition = 0;
				} else {
					if (isNext) {

						itemPosition = itemPosition + 1;

					}
				}
				mainKey(keyIndex);
			} else {

			}
			return true;
		// 返回按钮，用做 ok
		case KeyEvent.KEYCODE_BACK:
			// case KeyEvent.KEYCODE_DPAD_CENTER:
			keySubmitAction.onKeySubmitAction(keyIndex);

			// mainBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void mainKey(int keyIndex) {
		Log.e("mainkey", "keyIndex:" + keyIndex);
		if (fragmentFlag == 0) {
			// home
			setViewBackground(wifiTitleLayout, homeRecyclerFragment.wifiOpenLayout, homeRecyclerFragment.wifiRefresh);

			if (keyIndex < 4) {
				switch (keyIndex) {
				// case 0:
				// //去除焦点
				// break;
				case 1:
					// 1以上显示焦点
					wifiTitleLayout.setBackgroundResource(R.drawable.frame_shape);
					break;
				case 2:
					homeRecyclerFragment.wifiOpenLayout.setBackgroundResource(R.drawable.frame_shape);
					break;
				case 3:
					homeRecyclerFragment.wifiRefresh.setBackgroundResource(R.drawable.frame_shape);
					break;
				}
			} else {
				itemSelectAction.onItemSelectAction(isNext, itemPosition);
			}

		} else if (fragmentFlag == 1) {
			// input
			setViewBackground(wifiTitleLayout, inputFragment.inputPwdEd, inputFragment.inputShowCheckboxLayout,
					inputFragment.inputCancelTv, inputFragment.inputConnect);
			switch (keyIndex) {
			// case 0:
			// //去除焦点
			// break;
			case 1:
				// 1以上显示焦点
				wifiTitleLayout.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 2:
				inputFragment.inputPwdEd.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 3:
				inputFragment.inputShowCheckboxLayout.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 4:
				inputFragment.inputCancelTv.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 5:
				inputFragment.inputConnect.setBackgroundResource(R.drawable.frame_shape);
				break;

			}

		} else if (fragmentFlag == 2) {
			// wpaFragment
			setViewBackground(wifiTitleLayout, wpaFragment.wpaSaveCancelSave, wpaFragment.wpaSaveCancel,
					wpaFragment.wpaSaveConnect);
			switch (keyIndex) {
			// case 0:
			// //去除焦点
			// break;
			case 1:
				// 1以上显示焦点
				wifiTitleLayout.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 2:
				wpaFragment.wpaSaveCancelSave.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 3:
				wpaFragment.wpaSaveCancel.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 4:
				wpaFragment.wpaSaveConnect.setBackgroundResource(R.drawable.frame_shape);
				break;

			}

		} else if (fragmentFlag == 3) {
			// essFragment
			setViewBackground(wifiTitleLayout, essFragment.essSaveCancelSave, essFragment.essSaveCancel,
					essFragment.essSaveConnect);
			switch (keyIndex) {
			// case 0:
			// //去除焦点
			// break;
			case 1:
				// 1以上显示焦点
				wifiTitleLayout.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 2:
				essFragment.essSaveCancelSave.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 3:
				essFragment.essSaveCancel.setBackgroundResource(R.drawable.frame_shape);
				break;
			case 4:
				essFragment.essSaveConnect.setBackgroundResource(R.drawable.frame_shape);
				break;

			}
		}

	}

	private void setViewBackground(View... v) {
		for (int i = 0; i < v.length; i++) {
			if (v[i] != null) {
				v[i].setBackgroundResource(android.R.color.transparent);
			}
		}
	}

	private void mainBack() {
		if (fragmentFlag == 0) {

		} else {
			setShowWifiHintFlag(false);
			showHomeTitle();
			setFragment(homeRecyclerFragment);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		showWifiHintFlag = false;
	}
}
