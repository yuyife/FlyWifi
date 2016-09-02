package com.flyaudio.wifi.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flyaudio.wifi.MainActivity;
import com.flyaudio.wifi.R;
import com.flyaudio.wifi.interf.OnKeySubmitAction;


/**
 * @author yuyife
 *         基类Fragment
 *         主要功能：
 *         ---在依附activity时转换成MainActivity实例
 *         ---对所有继承此基类的子类统一使用 initData,initWidget两个方法
 */
public abstract class BaseFragment extends Fragment implements OnKeySubmitAction {
    protected static final int REFRESH_VIEW_BACKGROUND = 2568;

    protected static final int I_AM_CONNECTING = 879;
    protected static final int I_AM_ALREADY = 779;

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


    protected void baseCancel(BaseFragment fragment) {
        //activity.setNeedShowSearch(false);
        MainActivity.homeRecyclerFragment.autoSkipDataMonitoring(0,0);
        activity.backHomeFragment(fragment);
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


            }
        }
    };

}
