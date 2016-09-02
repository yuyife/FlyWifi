package com.flyaudio.wifi;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyaudio.wifi.base.BaseActivity;
import com.flyaudio.wifi.base.BaseFragment;
import com.flyaudio.wifi.bean.FlagChange;
import com.flyaudio.wifi.fragment.HomeRecyclerFragment;
import com.flyaudio.wifi.fragment.save.AlreadyConnectFragment;
import com.flyaudio.wifi.fragment.save.ConnectingFragment;
import com.flyaudio.wifi.fragment.will.OpenConnectFragment;
import com.flyaudio.wifi.fragment.will.WpaConnectFragment;
import com.flyaudio.wifi.interf.OnItemSelectAction;
import com.flyaudio.wifi.interf.OnKeySubmitAction;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private static final String TAG = "WIFI_ACTIVITY";
    @Bind(R.id.wifi_title_layout)
    public LinearLayout wifiTitleLayout;
    @Bind(R.id.wifi_title_image_Layout)
    public LinearLayout wifiTitleImageLayout;
    @Bind(R.id.wifi_title_image)
    public ImageView wifiTitleImage;
    @Bind(R.id.wifi_title_append)
    public TextView wifiTitleAppend;
    @Bind(R.id.wifi_title)
    public TextView wifiTitle;

    @OnClick({R.id.wifi_title_layout})
    public void onMainClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_title_layout:
                switch (fragmentFlag) {
                    case 0:
                        //home
                        if (wifiTitleImage.getVisibility() == View.VISIBLE) {
                            exitApp();
                        }
                        if (keyIndex == 1) {
                            exitApp();
                        }
                        break;
                    case 1:
                        //wpa connect
                        backHomeFragment(wpaConnectFragment);
                        break;
                    case 2:
                        //open connect
                        backHomeFragment(openConnectFragment);
                        break;
                    case 3:
                        //connecting
                        backHomeFragment(connectingFragment);
                        break;
                    case 4:
                        //already connect
                        backHomeFragment(alreadyConnectFragment);
                        break;
                }
                break;
        }
    }

    /**
     * ----------------------------------------↑↑↑↑V注解和点击-------------------------------------------
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    public static HomeRecyclerFragment homeRecyclerFragment;

    public static WpaConnectFragment wpaConnectFragment;
    public static OpenConnectFragment openConnectFragment;
    public static ConnectingFragment connectingFragment;
    public static AlreadyConnectFragment alreadyConnectFragment;


    public static OnKeySubmitAction keySubmitAction;
    public static OnItemSelectAction itemSelectAction;

    @Override
    protected void initData() {
//        wpaConnectFragment = WpaConnectFragment.getInstance();
//        openConnectFragment = OpenConnectFragment.getInstance();
//        connectingFragment = ConnectingFragment.getInstance();
//        alreadyConnectFragment = AlreadyConnectFragment.getInstance();

        setFragment(homeRecyclerFragment);
    }

    @Override
    protected void initWidget() {

    }

    /**
     * ----------------------------------------↑↑↑↑初始化-------------------------------------------
     */


    private int keyIndex = 1;

    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public int getKeyIndex() {
        return this.keyIndex;
    }

    private int fragmentFlag = 0;// 0-home,1-wpa,2-open,3-connect,4-already

    /**
     * @param //需要被当前Activity显示的 BaseFragment fragment
     * @功能描述： 1.记录当前Fragment 是谁。（当前MainActivity正要显示的是谁）
     * 2.初始化其 按键响应的焦点
     * 3.显示
     */
    public void setFragment(BaseFragment fragment) {

        if (fragment instanceof HomeRecyclerFragment) {
            //titleFlag = 0;
            fragmentFlag = 0;

            keyIndex = 3;//home，默认焦点 刷新
            Log.e("mainkey", "HomeRecyclerFragment:");
            setViewBackgroundTransparent(wifiTitleImageLayout, homeRecyclerFragment.wifiSwitchLayout, homeRecyclerFragment.wifiRefresh);
            setViewSelectState(homeRecyclerFragment.wifiRefresh);
            homeRecyclerFragment.autoSkipDataMonitoring(0, 0);
            homeRecyclerFragment.selectDataMonitoring(0, 0);
            homeRecyclerFragment.refreshAdapter();

        } else if (fragment instanceof WpaConnectFragment) {
            //titleFlag = 1;
            fragmentFlag = 1;
            keyIndex = 2;//加密网络，默认焦点 输入密码
            Log.e("mainkey", "加密网络，默认焦点 输入密码:");
        } else if (fragment instanceof OpenConnectFragment) {
            //titleFlag = 2;
            fragmentFlag = 2;
            keyIndex = 3;//开放网络，默认焦点 连接
            Log.e("mainkey", "开放网络，默认焦点 连接:");
        } else if (fragment instanceof ConnectingFragment) {
            //titleFlag = 3;
            fragmentFlag = 3;
            keyIndex = 2;//正在连接，默认焦点 取消
            Log.e("mainkey", "正在连接，默认焦点 取消:");
        } else if (fragment instanceof AlreadyConnectFragment) {
            // titleFlag = 4;
            fragmentFlag = 4;
            keyIndex = 2;//保存页面，默认焦点 连接
            Log.e("mainkey", "保存页面，默认焦点 连接:");
        }
        if (fragmentFlag == 0) {
            if (homeRecyclerFragment == null) {
                homeRecyclerFragment = HomeRecyclerFragment.getInstance();
                //titleFlag = 0;
                fragmentFlag = 0;
                keyIndex = 3;//home，默认焦点 刷新
                getSupportFragmentManager().beginTransaction().add(R.id.wifi_fragment, homeRecyclerFragment)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction().show(homeRecyclerFragment)
                        .commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().hide(homeRecyclerFragment).commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.wifi_fragment, fragment)
                    .commit();
        }

        Log.e(TAG, "fragmentFlag:" + fragmentFlag);
        //setHomeTitle(fragmentFlag);
    }

    /**
     * @param //需要被当前Activity Remove 的 BaseFragment fragment
     *                        功能描述：就是返回，显示home
     *                        1.从哪里（from）来到哪里去(to)
     *                        2.remove(from) BaseFragment fragment
     *                        3.显示(to) home
     */
    public void backHomeFragment(BaseFragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        setFragment(homeRecyclerFragment);
    }

    public boolean isLock = false;//用来判断 title是否加框


    /**
     * ----------------------------------------↑↑↑↑管理Fragment,并显示相应的title-------------------------------------------
     */
    private boolean isNext = false;
    private int itemPosition = 0;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int size = homeRecyclerFragment.getWifiDataSize();
        // 获取 当前选择的条目
        switch (keyCode) {
            // 音量减小 用做 ++++++++++++++ next
            //case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Log.e("mainKey", "size:" + homeRecyclerFragment.getWifiDataSize());
                if (keyIndex < (size + 3)) {
                    keyIndex = FlagChange.nextIndex(fragmentFlag, keyIndex);
                    isNext = true;
                    if (keyIndex == 4) {
                        itemPosition = 0;
                    } else {
                        if (isNext) {
                            itemPosition = itemPosition + 1;
                        }
                    }
                    mainKeyboard(keyIndex);
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // next
                if (keyIndex < (size + 3)) {
                    keyIndex = FlagChange.nextIndex(fragmentFlag, keyIndex);
                    isNext = true;

                    if (keyIndex == 4) {
                        itemPosition = 0;
                    } else {
                        if (isNext) {

                            itemPosition = itemPosition + 1;

                        }
                    }
                    mainKeyboard(keyIndex);
                }

                return true;
            /**
             * ----------------------------------------↑↑↑↑下一个，-------------------------------------------
             */
            case KeyEvent.KEYCODE_DPAD_LEFT:
                // pre
                keyIndex = FlagChange.preIndex(fragmentFlag, keyIndex);
                isNext = false;
                if (keyIndex == 4) {
                    itemPosition = 0;
                } else {
                    if (keyIndex == 3) {
                        homeRecyclerFragment.selectDataMonitoring(0, 0);
                        homeRecyclerFragment.autoSkipDataMonitoring(0, 0);
                        homeRecyclerFragment.refreshAdapter();
                        //再刷新
                    }
                    if (!isNext) {
                        itemPosition = itemPosition - 1;
                    }
                }
                mainKeyboard(keyIndex);
                return true;
            // 音量增大 用做 ————-- pre
            //case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_DPAD_UP:
                keyIndex = FlagChange.preIndex(fragmentFlag, keyIndex);
                isNext = false;
                if (keyIndex == 4) {
                    itemPosition = 0;
                } else {
                    if (keyIndex == 3) {
                        homeRecyclerFragment.selectDataMonitoring(0, 0);
                        homeRecyclerFragment.autoSkipDataMonitoring(0, 0);
                        homeRecyclerFragment.refreshAdapter();
                    }
                    if (!isNext) {
                        itemPosition = itemPosition - 1;
                    }
                }
                mainKeyboard(keyIndex);
                return true;
            /**
             * ----------------------------------------↑↑↑↑上一个，-------------------------------------------
             */
            //确认按钮
            case KeyEvent.KEYCODE_ENTER:
                if (fragmentFlag == 0) {
                    if (keyIndex == 1) {
                        exitApp();
                    } else {
                        keySubmitAction.onKeySubmitAction(keyIndex);
                    }
                } else {
                    keySubmitAction.onKeySubmitAction(keyIndex);
                }
                return true;
            // 返回按钮
            case KeyEvent.KEYCODE_BACK:
                mainBack();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void mainKeyboard(int keyIndex) {
        Log.e("mainkey", "fragmentFlag:" + fragmentFlag);
        Log.e("mainkey", "keyIndex:" + keyIndex);
        Log.e("mainkey", "----------------------------");
        if (fragmentFlag == 0) {
            // home
            wifiTitleImage.setVisibility(View.GONE);
            setViewBackgroundTransparent(wifiTitleLayout);
            setViewBackgroundTransparent(wifiTitleImageLayout, homeRecyclerFragment.wifiSwitchLayout, homeRecyclerFragment.wifiRefresh);

            if (keyIndex < 4) {
                switch (keyIndex) {
                    // case 0:
                    // //去除焦点
                    // break;
                    case 1:
                        // 1以上显示焦点
                        wifiTitleImage.setVisibility(View.GONE);
                        //wifiTitleImageLayout.setBackgroundResource(R.drawable.frame_shape);
                        setViewSelectState(wifiTitleLayout);
                        break;
                    case 2:
                        homeRecyclerFragment.wifiSwitchLayout.setBackgroundResource(R.drawable.frame_shape);
                        break;
                    case 3:
                        homeRecyclerFragment.wifiRefresh.setBackgroundResource(R.drawable.frame_shape);
                        break;
                }
            } else {

                itemSelectAction.onItemSelectAction(isNext, itemPosition);
            }

        } else if (fragmentFlag == 1) {
            // wpa
            setViewBackgroundTransparent(wifiTitleImageLayout, wpaConnectFragment.inputPwdLayout, wpaConnectFragment.inputPwdCheckboxLayout,
                    wpaConnectFragment.inputCancel, wpaConnectFragment.inputConnect);
            switch (keyIndex) {
                // case 0:
                // //去除焦点
                // break;
                case 1:
                    // 1以上显示焦点
                    wifiTitleImageLayout.setBackgroundResource(R.drawable.frame_shape);
                    wpaConnectFragment.viewFocusable(wifiTitle);
                    break;
                case 2:
                    wpaConnectFragment.inputPwdLayout.setBackgroundResource(R.drawable.frame_shape);
                    wpaConnectFragment.viewFocusable(wpaConnectFragment.inputPwd);
                    break;
                case 3:
                    wpaConnectFragment.inputPwdCheckboxLayout.setBackgroundResource(R.drawable.frame_shape);
                    wpaConnectFragment.viewFocusable(wpaConnectFragment.inputPwdCheckboxLayout);
                    break;
                case 4:
                    wpaConnectFragment.inputCancel.setBackgroundResource(R.drawable.frame_shape);
                    wpaConnectFragment.viewFocusable(wpaConnectFragment.inputCancel);
                    break;
                case 5:
                    wpaConnectFragment.inputConnect.setBackgroundResource(R.drawable.frame_shape);
                    wpaConnectFragment.viewFocusable(wpaConnectFragment.inputConnect);
                    break;

            }

        } else if (fragmentFlag == 2) {
            // open
            setViewBackgroundTransparent(wifiTitleImageLayout, openConnectFragment.openCancel, openConnectFragment.openConnect);
            switch (keyIndex) {
                // case 0:
                // //去除焦点
                // break;
                case 1:
                    // 1以上显示焦点
                    wifiTitleImageLayout.setBackgroundResource(R.drawable.frame_shape);
                    break;
                case 2:
                    openConnectFragment.openCancel.setBackgroundResource(R.drawable.frame_shape);
                    break;
                case 3:
                    openConnectFragment.openConnect.setBackgroundResource(R.drawable.frame_shape);
                    break;

            }

        } else if (fragmentFlag == 3) {
            // connect
            setViewBackgroundTransparent(wifiTitleImageLayout, connectingFragment.connectingCancel, connectingFragment.connectingCancelSave);
            switch (keyIndex) {
                // case 0:
                // //去除焦点
                // break;
                case 1:
                    // 1以上显示焦点
                    wifiTitleImageLayout.setBackgroundResource(R.drawable.frame_shape);
                    break;
                case 2:
                    connectingFragment.connectingCancel.setBackgroundResource(R.drawable.frame_shape);
                    break;
                case 3:
                    connectingFragment.connectingCancelSave.setBackgroundResource(R.drawable.frame_shape);
                    break;


            }
        } else if (fragmentFlag == 4) {
            // already
            setViewBackgroundTransparent(wifiTitleImageLayout, alreadyConnectFragment.alreadyCancelSave, alreadyConnectFragment.alreadyCancel
                    , alreadyConnectFragment.alreadyConnect);
            switch (keyIndex) {
                // case 0:
                // //去除焦点
                // break;
                case 1:
                    // 1以上显示焦点
                    wifiTitleImageLayout.setBackgroundResource(R.drawable.frame_shape);
                    break;
                case 2:
                    alreadyConnectFragment.alreadyCancelSave.setBackgroundResource(R.drawable.frame_shape);
                    break;
                case 3:
                    alreadyConnectFragment.alreadyCancel.setBackgroundResource(R.drawable.frame_shape);
                    break;
                case 4:
                    alreadyConnectFragment.alreadyConnect.setBackgroundResource(R.drawable.frame_shape);
                    break;


            }
        }

    }

    /**
     * ----------------------------------------↑↑↑↑按键响应-------------------------------------------
     */
    private void mainBack() {

        switch (fragmentFlag) {
            case 0:
                exitApp();
                break;
            case 1:
                backHomeFragment(wpaConnectFragment);
                break;
            case 2:
                backHomeFragment(openConnectFragment);
                break;
            case 3:
                backHomeFragment(connectingFragment);
                break;
            case 4:
                backHomeFragment(alreadyConnectFragment);
                break;
        }
    }

    public void exitApp() {
        finish();
        System.exit(0);
    }
    /**
     * ----------------------------------------↑↑↑↑返回操作-------------------------------------------
     */
}