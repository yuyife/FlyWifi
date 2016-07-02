package com.yuyife.flywifi.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yuyife.flywifi.MainActivity;
import com.yuyife.flywifi.R;
import com.yuyife.flywifi.interf.OnConnectResult;
import com.yuyife.flywifi.interf.OnKeySubmitAction;
import com.yuyife.flywifi.util.NetWorkUtil;

/**
 * @author yuyife
 *         基类Fragment
 *         主要功能：
 *         ---在依附activity时转换成MainActivity实例
 *         ---对所有继承此基类的子类统一使用 initData,initWidget两个方法
 */
public abstract class BaseFragment extends Fragment implements OnConnectResult, OnKeySubmitAction {
    protected static final int REFRESH_VIEW_BACKGROUND = 2568;
    protected static final int CANCEL = 2365;
    protected static final int CONNECT_TEST = 21275;


    protected static final long SET_VIEW_BACKGROUND_AT_TIME = 250L;
    protected static final long RECEIVER_OPEN_AT_TIME = 4000L;

    private static final String TAG = "BaseFragment";
    protected MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        } else {
            new ClassCastException("类型转换出错，依附的context不是MainActivity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        testCount = 0;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initWidget();
    }

    abstract protected void initData();

    abstract protected void initWidget();

    protected void showToast(CharSequence txt) {
        Toast.makeText(activity, txt, Toast.LENGTH_SHORT).show();
    }

    protected void showSnackBar(View v, CharSequence txt) {
        Snackbar.make(v, txt, Snackbar.LENGTH_SHORT).show();
        //showToast(txt);
    }

    private int resId = 0;

    /**
     * 子类设置view的背景,不延迟
     * 默认资源id为 R.drawable.item_layout_shape
     */
    protected void setViewBackground(View view) {
        setViewBackground(view, -1);
    }

    /**
     * 子类设置view的透明背景,不延迟，可传多个view对象
     */
    protected void setViewBackgroundTransparent(View... v) {
        for (int i = 0; i < v.length; i++) {
            setViewBackground(v[i], 1);
        }
    }

    /**
     * 子类设置view背景,不延迟
     *
     * @param resId 资源id
     */
    protected void setViewBackground(View view, int resId) {
        this.resId = resId;
        Message message = Message.obtain();
        message.obj = view;
        message.what = REFRESH_VIEW_BACKGROUND;
        baseHandler.handleMessage(message);
    }

    /**
     * 子类设置view背景,延迟
     *
     * @param resId 资源id
     */
    protected void setViewBackground(View view, int resId, Long atTime) {
        this.resId = resId;
        Message message = Message.obtain();
        message.obj = view;
        message.what = REFRESH_VIEW_BACKGROUND;
        baseHandler.sendMessageDelayed(message, atTime);
    }

    /**
     * 延迟设置背景
     * 默认资源id为 R.drawable.frame_shape
     */
    protected void setViewBackground(View view, Long atTime) {
        this.resId = 0;
        Message message = Message.obtain();
        message.obj = view;
        message.what = REFRESH_VIEW_BACKGROUND;
        baseHandler.sendMessageDelayed(message, atTime);
    }

    /**
     * 基类处理公用的 消息
     * 1.设置view背景
     * 2.返回主页面
     * 3.连接wifi
     */
    protected Handler baseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case REFRESH_VIEW_BACKGROUND:
                    View view = (View) msg.obj;
                    if (view != null) {
                        if (resId == -1) {
                            view.setBackgroundResource(R.drawable.item_layout_shape);
                        } else if (resId == 0) {
                            view.setBackgroundResource(R.drawable.frame_shape);
                        } else if (resId == 1) {
                            view.setBackgroundResource(android.R.color.transparent);
                        } else {
                            view.setBackgroundResource(resId);
                        }

                    }
                    resId = 0;
                    break;
                case CONNECT_TEST:
                    connectTest();

                    break;
                case CANCEL:
                    //表示 用户手动点击取消 关闭当前fragment 返回到主页面
                    activity.setShowWifiHintFlag(false);
                    activity.showHomeTitle();
                    activity.setFragment(MainActivity.homeRecyclerFragment);
                    break;

            }
        }
    };


    /***
     * 以下代码是yuyife经过不断，反复调试，
     * 成功封装了向子类做出-->身份验证的返回（true,false）
     */

    //protected boolean isNetWorkConnect = false, isNetWorkConnectTemp = false;
    //protected boolean isNetWorkAvailable = false, isNetWorkAvailableTemp = false;
    protected int netWorkType = 100, netWorkTypeTemp = 100;

    protected void refreshNetWorkState() {
        //isNetWorkAvailable = NetWorkUtil.isNetWorkAvailable(activity);
        //isNetWorkConnect = NetWorkUtil.isNetWorkConnect(activity);
        netWorkType = NetWorkUtil.checkNetworkType(activity);
    }

    protected void refreshNetWorkStateTemp() {
        //isNetWorkConnectTemp = isNetWorkConnect;
        //isNetWorkAvailableTemp = isNetWorkAvailable;
        netWorkTypeTemp = netWorkType;
    }


    /***
     * 使用方法：
     * step 1 :refreshNetWorkState();
     * step 2 :refreshNetWorkStateTemp();
     * step 3 :connectTest()
     * <p/>
     * 封装之后 ： startConnectTest()
     */

    protected void startConnectTest() {
        testCount = 0;
        refreshNetWorkState();
        refreshNetWorkStateTemp();
        baseHandler.sendEmptyMessageDelayed(CONNECT_TEST, 1000);
    }

    protected int testCount = 0;

    //此方法的意义在于 没有收到 case ConnectivityManager.CONNECTIVITY_ACTION:
    //做出 判断身份验证不通过的判断
    private void connectTest() {
        //Log.e(TAG, "isNetWorkAvailable:" + isNetWorkAvailable);
        //Log.e(TAG, "isNetWorkConnect:" + isNetWorkConnect);
        Log.e(TAG, "netWorkType:" + netWorkType);

        Log.e(TAG, "testCount:" + testCount);
        testCount++;
        refreshNetWorkState();
        // 如果 连接之前是 没有网络
        // 如果 连接之前是 移动网络
        // 如果 连接之前是 wifi网络
        // 如果 连接之前是 其他网络

        if (netWorkTypeTemp == NetWorkUtil.TYPE_NET_WORK_DISABLED) {
            // 如果 连接之前是 没有网络
            if (netWorkType == NetWorkUtil.TYPE_WIFI) {
                //连接之后 是 wifi
                /** ------身份验证通过------ 的第一种情况*/
                onConnectResult(true);
                testCount = 0;
            } else {
                //连接之后 还不是 wifi

                if (testCount > 4) {
                    /* 验证3次之后 可以做出 身份验证不通过 的判断 */
                    onConnectResult(false);
                    testCount = 0;
                } else {
                    /* 3次以下 身份验证不通过 延迟一秒继续 connectTest()*/
                    baseHandler.sendEmptyMessageDelayed(CONNECT_TEST, 1000);
                }

            }
        } else if (netWorkTypeTemp == NetWorkUtil.TYPE_WIFI) {
            // 如果 连接之前是 wifi网络
            if (netWorkType == NetWorkUtil.TYPE_WIFI) {
                //连接之后 是 wifi
                if (testCount > 4) {
                    /** 连续3次之后 结果还是一样 可以做出 身份验证通过 的判断 */
                    /** ------身份验证通过------ 的第二种情况*/
                    onConnectResult(true);
                    testCount = 0;
                } else {
                    /* 3次以下 结果还是一样 延迟一秒继续 connectTest()*/
                    baseHandler.sendEmptyMessageDelayed(CONNECT_TEST, 1000);
                }
            } else {
                //连接之后 还不是 wifi
                if (testCount > 4) {
                    /* 连续3次之后 结果还是一样 可以做出 身份验证不通过 的判断 */
                    onConnectResult(false);
                    testCount = 0;
                } else {
                    /* 3次以下 结果还是一样 延迟一秒继续 connectTest()*/
                    baseHandler.sendEmptyMessageDelayed(CONNECT_TEST, 1000);
                }
            }
        } else {
            // 如果 连接之前是 移动网络 或者 是 其他网络
            if (netWorkType == NetWorkUtil.TYPE_WIFI) {
                //连接之后 是 wifi
                /** ------身份验证通过------ 的第三种情况*/
                onConnectResult(true);
                testCount = 0;
            } else {
                //连接之后 还不是 wifi
                if (testCount > 4) {
                    /* 连续3次之后 结果还是一样 可以做出 身份验证不通过 的判断 */
                    onConnectResult(false);
                    testCount = 0;
                } else {
                    /* 3次以下 结果还是一样 延迟一秒继续 connectTest()*/
                    baseHandler.sendEmptyMessageDelayed(CONNECT_TEST, 1000);
                }
            }
        }
    }
}
