package com.yuyife.flywifi.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.yuyife.flywifi.MainActivity;
import com.yuyife.flywifi.R;
import com.yuyife.flywifi.adapter.WifiRecyclerAdapter;
import com.yuyife.flywifi.adapter.WifiRecyclerAdapter.ViewHolder;
import com.yuyife.flywifi.base.BaseFragment;
import com.yuyife.flywifi.interf.OnItemSelectAction;
import com.yuyife.flywifi.util.SPHelper;
import com.yuyife.flywifi.util.Skip;
import com.yuyife.flywifi.util.WifiUtil;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * -----------------------------AS版本使用的RecyclerView-----------------------------
 * --
 *
 * @author yuyife 显示给用户最直接的界面 主要包括 勾选wifi连接 点击刷新wifi列表 显示wifi列表 对wifi列表项目进行操作，
 *         ----连接wifi（加密，开放）, ----对曾经成功连接过的wifi进行管理，
 *         ---------如，取消保存（相当于删除网络），免密码连接，等
 */
public class HomeRecyclerFragment extends BaseFragment implements OnItemSelectAction {
	private static final String TAG = "HomeRecyclerFragment";
	private static final int CLOSE_WIFI = 456;
	private static final int SET_CONTEXT_TEXT_HINT = 457;
	private static final int RECEIVER_OPEN = 458;
	private static final int SET_ADAPTER = 459;
	private static final int REFRESH_TEXT_HINT_ONE = 460;
	private static final int REFRESH_TEXT_HINT_TWO = 461;
	private static final int REFRESH_TEXT_HINT_FINISH = 462;

	private static final int REFRESH_WIFI_DATA = 464;

	@Bind(R.id.wifi_open_checkbox)
	CheckBox wifiOpenCheckbox;
	@Bind(R.id.wifi_refresh_text_hint)
	TextView wifiRefreshTextHint;
	@Bind(R.id.wifi_refresh)
	public AutoLinearLayout wifiRefresh;
	@Bind(R.id.wifi_open_layout)
	public AutoLinearLayout wifiOpenLayout;
	@Bind(R.id.wifi_content_text_hint)
	TextView wifiContentTextHint;
	@Bind(R.id.wifi_recyclerView)
	RecyclerView wifiRecyclerView;
	@Bind(R.id.wifi_content_layout)
	AutoFrameLayout wifiContentLayout;

	@OnClick({ R.id.wifi_open_layout, R.id.wifi_refresh })
	public void onWifiViewClick(View view) {
		switch (view.getId()) {
		case R.id.wifi_open_layout:
			wifiOpenLayoutClick();
			break;
		case R.id.wifi_refresh:
			refresh();
			break;
		}
	}

	public static HomeRecyclerFragment instance = null;

	public HomeRecyclerFragment() {
	}

	public static HomeRecyclerFragment getInstance() {
		if (instance == null) {
			return new HomeRecyclerFragment();
		}
		return instance;
	}

	private void refreshWifiList() {
		refreshWifiData();
		uiHandler.sendEmptyMessageDelayed(REFRESH_TEXT_HINT_TWO, 200);
		uiHandler.sendEmptyMessageDelayed(REFRESH_TEXT_HINT_ONE, 400);
		uiHandler.sendEmptyMessageDelayed(REFRESH_TEXT_HINT_FINISH, 600);
	}

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_WIFI_DATA:
				refreshWifiData();
				break;
			case SET_ADAPTER:
				if (wifiContentTextHint != null) {
					wifiContentTextHint.setVisibility(View.GONE);
					wifiRecyclerView.setVisibility(View.VISIBLE);
					getCurrentSSIDName();
					Log.e(TAG, "setAdapter之前打印currentSSID：" + currentSSID);
					// Log.e(TAG, "setAdapter之前打印ipFlag：" + ipFlag);
					Log.e(TAG, "setAdapter之前打印本地保存的数据：" + SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY));
					for (int i = 0; i < wifiData.size(); i++) {
						if (wifiData.get(i).SSID.equals(currentSSID)) {
							// 把当前连接的ssid置顶
							ScanResult temp = wifiData.get(i);
							wifiData.set(i, wifiData.get(0));
							wifiData.set(0, temp);
						}
					}
					for (int i = 0; i < wifiData.size(); i++) {
						selectList.add(i, 0);
						keySubmitFlagList.add(i, 0);
					}

					boolean isHas = SPHelper.isHasMapString(activity);
					if (isHas) {
						SPHelper.getMapForJson(SPHelper.getMapString(activity));
						adapter = new WifiRecyclerAdapter(activity, wifiUtil.removeNoneSSID(wifiData), currentSSID,
								SPHelper.ssidList, selectList, keySubmitFlagList);
					} else {
						adapter = new WifiRecyclerAdapter(activity, wifiUtil.removeNoneSSID(wifiData), currentSSID,
								null, selectList, keySubmitFlagList);
					}
					wifiRecyclerView.setAdapter(adapter);

				}
				break;
			case CLOSE_WIFI:
				if (wifiRefreshTextHint != null) {
					wifiContentTextHint.setText(getString(R.string.wifi_text_close_hint));
					uiHandler.sendEmptyMessageDelayed(SET_CONTEXT_TEXT_HINT, 2000);
				}
				break;
			case SET_CONTEXT_TEXT_HINT:
				if (wifiRefreshTextHint != null) {
					wifiContentTextHint.setText(getString(R.string.wifi_text_open_hint));
				}
				break;
			case RECEIVER_OPEN:
				receiverAction(true);
				break;
			case REFRESH_TEXT_HINT_ONE:
				if (wifiRefreshTextHint != null) {
					wifiRefreshTextHint.setText(".");
				}
				break;
			case REFRESH_TEXT_HINT_TWO:
				if (wifiRefreshTextHint != null) {
					wifiRefreshTextHint.setText("..");
				}
				break;
			case REFRESH_TEXT_HINT_FINISH:
				if (wifiRefreshTextHint != null) {
					wifiRefreshTextHint.setText("");
				}
				break;

			}
		}
	};

	private boolean isOpenWifi = false;
	private LinearLayoutManager layoutManager;
	private WifiRecyclerAdapter adapter;
	private WifiUtil wifiUtil;
	private ArrayList<ScanResult> wifiData;
	private List<Integer> selectList;
	private List<Integer> keySubmitFlagList;
	private NetworkConnectChangedReceiver wifiReceiver;

	public void setSelectList(List<Integer> selectList) {
		this.selectList = selectList;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.wifi_home_recycler_fragment, null);
		ButterKnife.bind(this, view);

		return view;
	}

	@Override
	protected void initData() {
		// ipFlag = 0;//是否要根据 ip地址 来显示当前已连接
		layoutManager = new LinearLayoutManager(activity);
		wifiUtil = WifiUtil.getInstance(activity);
		activity.setWifiUtil(wifiUtil);

		selectList = new ArrayList<>();
		keySubmitFlagList = new ArrayList<>();

		isOpenWifi = wifiUtil.isOpenWifi();
		// wifiData = wifiUtil.getWifiList();

		// 如果wifi关闭，wifiData长度为0；
		// Log.e(TAG, "initData----wifiData长度:" + wifiData.size());

		wifiReceiver = new NetworkConnectChangedReceiver();

		receiverAction(true);
		// 还是要手动注册，万一，打开应用，isOpenWifi==false,则不会触发注册
	}

	private void receiverAction(boolean isListen) {
		receiverAction(activity, isListen);
	}

	public void receiverAction(MainActivity activity, boolean isListen) {
		if (isListen) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			activity.registerReceiver(wifiReceiver, filter);
			Log.e(TAG, "receiverAction:--注册 registerReceiver");
		} else {
			receiverAction(activity, true);
			activity.unregisterReceiver(wifiReceiver);
			Log.e(TAG, "receiverAction:--注销 unregisterReceiver");
		}
	}

	// private LinearLayoutManager linearLayoutManager;

	@Override
	protected void initWidget() {
		// linearLayoutManager = new LinearLayoutManager(activity);
		wifiRecyclerView.setLayoutManager(layoutManager);
		wifiRecyclerView.setItemAnimator(new DefaultItemAnimator());

		wifiOpenCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				receiverAction(false);
				// 设置的时候先关闭 监听wifi状态的广播
				setCheckedChange(isChecked);

				// 设置完闭 延迟4秒打开监听wifi状态的广播
				uiHandler.sendEmptyMessageDelayed(RECEIVER_OPEN, RECEIVER_OPEN_AT_TIME);
			}
		});
		// 如果第一次打开应用，wifi是关闭的 isOpenWifi = false
		// wifiOpenCheckbox.setChecked(isOpenWifi);不会执行change
		wifiOpenCheckbox.setChecked(isOpenWifi);
	}

	private void setCheckedChange(boolean isChecked) {
		Log.e(TAG, "isChecked:" + isChecked);
		wifiUtil.setWifiState(isChecked);
		if (isChecked) {
			openWifi();
		} else {
			closeWifi();
		}
	}

	private void openWifi() {
		// 走生命周期方法，如果系统wifi状态是打开，就必然走这里
		isOpenWifi = true;
		// 在从其他 fragment返回 home时不需要 设置hint
		if (activity.isShowWifiHintFlag()) {
			wifiContentTextHint.setText(getString(R.string.wifi_text_search_hint));
		} else {
			wifiContentTextHint.setText("");
		}

		refreshWifiData();
	}

	private void refreshWifiData() {
		wifiData = wifiUtil.refreshWifiList();
		if (wifiData.size() == 0) {
			uiHandler.sendEmptyMessageDelayed(REFRESH_WIFI_DATA, 1000);
		} else {

			uiHandler.sendEmptyMessage(SET_ADAPTER);
		}
	}

	private void closeWifi() {
		isOpenWifi = false;
		wifiRecyclerView.setVisibility(View.GONE);
		wifiContentTextHint.setVisibility(View.VISIBLE);
		uiHandler.sendEmptyMessage(CLOSE_WIFI);
	}

	private String currentSSID = "";

	@Override
	public void onResume() {
		super.onResume();
		activity.setShowWifiHintFlag(true);
		MainActivity.homeRecyclerFragment = this;
		getCurrentSSIDName();

	}

	// private int ipFlag = 0;//0-默认值，
	// 1-从input返回是设置，
	// 2-从wpa,ess保存页面点击取消保存设置，3-从wpa,ess保存页面断开连接保存设置

	// public void setIpFlag(int ipFlag) {
	// this.ipFlag = ipFlag;
	// }

	// public int getIpFlag() {
	// return ipFlag;
	// }

	private void getCurrentSSIDName() {
		if (TextUtils.isEmpty(wifiUtil.getCurrentWifiInfo())) {
			currentSSID = "I_am_yuyife";
		} else {
			if (WifiUtil.ipIsTrue) {
				currentSSID = wifiUtil.getCurrentWifiInfo().substring(1, wifiUtil.getCurrentWifiInfo().length() - 1);
			} else {
				// if (getIpFlag() == 0) {
				//
				// currentSSID =
				// getString(R.string.home_connect_wifi_init_value);
				// } else if (getIpFlag() == 1) {
				// currentSSID =
				// getString(R.string.home_connect_wifi_init_value);
				//
				// } else if (getIpFlag() == 2) {
				// if (SPHelper.getString(activity, SPHelper.CURRENT_SSID_KEY)
				// == null
				// || SPHelper.getString(activity,
				// SPHelper.CURRENT_SSID_KEY).equals(getString(R.string.home_connect_wifi_init_value)))
				// {
				//
				// } else {
				// currentSSID = SPHelper.getString(activity,
				// SPHelper.CURRENT_SSID_KEY);
				// }
				// } else if (getIpFlag() == 3) {
				// currentSSID =
				// getString(R.string.home_connect_wifi_init_value);
				// }
				currentSSID = getString(R.string.home_connect_wifi_init_value);
				// ip获取不及时
				// 当从保存页面，点击免密码连接时，不需要判断ip
			}

		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.unbind(this);

	}

	private class NetworkConnectChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				// 监听wifi的打开与关闭，与wifi的连接无关
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				Log.e(TAG, "wifiState" + wifiState);
				switch (wifiState) {
				case WifiManager.WIFI_STATE_ENABLING:
				case WifiManager.WIFI_STATE_ENABLED:
					isOpenWifi = true;
					break;

				case WifiManager.WIFI_STATE_DISABLING:
				case WifiManager.WIFI_STATE_DISABLED:
					isOpenWifi = false;
					break;

				}
				if (wifiOpenCheckbox != null) {
					wifiOpenCheckbox.setChecked(isOpenWifi);
				}

			}

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		receiverAction(false);
	}

	@Override
	public void onConnectResult(boolean connectResult) {

	}

	@Override
	public void onKeySubmitAction(int index) {

		if (index < 4) {
			switch (index) {
			case 2:
				// showToast("wifi连接");
				open2close();
				break;
			case 3:
				// showToast("刷新");
				refresh();
				break;
			}
		} else {
			// 执行 硬件ok的操作
			// if ((index - 4) > -1) {
			// View view = layoutManager.getChildAt(index - 4);
			// if (view != null) {
			// ViewHolder viewHolder = (ViewHolder)
			// wifiRecyclerView.getChildViewHolder(view);
			//
			// Skip.skip(activity, viewHolder.itemRoot);
			// }
			// }
			int commitPos = index - 4;

			for (int i = 0; i < wifiData.size(); i++) {
				if (i == commitPos) {
					keySubmitFlagList.set(i, 1);
				} else {
					keySubmitFlagList.set(i, 0);
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	private void open2close() {
		boolean openFlag = wifiOpenCheckbox.isChecked();
		if (openFlag) {
			wifiOpenCheckbox.setChecked(false);
		} else {
			wifiOpenCheckbox.setChecked(true);
		}
	}

	private void wifiOpenLayoutClick() {
		// 0.选择选择焦点 1.改变keyIndex 2.执行打开或关闭
		setViewBackgroundTransparent(activity.wifiTitleLayout, wifiOpenLayout, wifiRefresh);
		setViewBackground(wifiOpenLayout);
		activity.setKeyIndex(2);
		boolean isChecked = wifiOpenCheckbox.isChecked();
		if (isChecked) {
			wifiOpenCheckbox.setChecked(false);
		} else {
			wifiOpenCheckbox.setChecked(true);
		}
	}

	private void refresh() {
		if (isOpenWifi) {
			activity.setKeyIndex(3);
			wifiRefreshTextHint.setText("...");
			refreshWifiList();
			setViewBackgroundTransparent(activity.wifiTitleLayout, wifiOpenLayout, wifiRefresh);
			setViewBackground(wifiRefresh, R.drawable.item_layout_shape);
		} else {
			// Snackbar.make(wifiRefresh,
			// getString(R.string.wifi_isClose_state_toast),
			// Snackbar.LENGTH_LONG).show();
			showSnackBar(wifiRefresh, getString(R.string.wifi_isClose_state_toast));
		}
	}

	// private List<ViewHolder> viewHolderList;

	public void cleanSelect() {
		for (int i = 0; i < wifiData.size(); i++) {
			selectList.set(i, 0);
		}
		adapter.notifyDataSetChanged();
	}

	public void cleanKeySubmit() {
		for (int i = 0; i < wifiData.size(); i++) {
			keySubmitFlagList.set(i, 0);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemSelectAction(boolean isNext, int itemPosition) {

		int count = wifiData.size();
		/*
		 * if (itemPosition == 0) { viewHolderList = new ArrayList<>(); }
		 * 
		 * if (itemPosition < count) {
		 * 
		 * View view = layoutManager.getChildAt(itemPosition); if (view != null)
		 * { ViewHolder viewHolder = (ViewHolder)
		 * wifiRecyclerView.getChildViewHolder(view);
		 * 
		 * viewHolderList.add(viewHolder); for (int i = 0; i <
		 * viewHolderList.size(); i++) {
		 * setViewBackgroundTransparent(viewHolderList.get(i).itemRoot); }
		 * setViewBackground(viewHolder.itemRoot); }
		 * 
		 * layoutManager.scrollToPositionWithOffset(itemPosition, 0); } else {
		 * 
		 * }
		 * 
		 */

		if (itemPosition >= wifiData.size()) {
			activity.setIsNeedChangeKeyIn(false);
		} else {

			for (int i = 0; i < wifiData.size(); i++) {
				if (i == itemPosition) {
					selectList.set(i, 1);
				} else {
					selectList.set(i, 0);
				}
			}
			layoutManager.scrollToPositionWithOffset(itemPosition, 0);

			adapter.notifyDataSetChanged();
		}
	}

}
