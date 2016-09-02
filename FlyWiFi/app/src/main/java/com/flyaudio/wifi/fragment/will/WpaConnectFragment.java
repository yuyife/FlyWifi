package com.flyaudio.wifi.fragment.will;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyaudio.wifi.MainActivity;
import com.flyaudio.wifi.R;
import com.flyaudio.wifi.base.BaseWifiFragment;
import com.flyaudio.wifi.bean.WifiConnectBean;
import com.flyaudio.wifi.util.WifiSafetyString;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yuyife
 *         有加密的wifi网络，输入密码的 连接页面
 */
public class WpaConnectFragment extends BaseWifiFragment {


    public static WpaConnectFragment instance = null;
    @Bind(R.id.input_wifi_name)
    TextView inputWifiName;
    @Bind(R.id.input_signal)
    TextView inputSignal;
    @Bind(R.id.input_safety)
    TextView inputSafety;

    @Bind(R.id.input_pwd_layout)
    public LinearLayout inputPwdLayout;
    // @Bind(R.id.input_pwd_tip)
    //TextView inputPwdTip;
    @Bind(R.id.input_pwd)
    public EditText inputPwd;

    @Bind(R.id.input_pwd_checkbox_txt)
    TextView inputPwdCheckboxTxt;
    @Bind(R.id.input_pwd_checkbox)
    CheckBox inputPwdCheckbox;
    @Bind(R.id.input_pwd_checkbox_layout)
    public LinearLayout inputPwdCheckboxLayout;
    @Bind(R.id.input_cancel)
    public LinearLayout inputCancel;
    @Bind(R.id.input_connect)
    public LinearLayout inputConnect;


    @OnClick({R.id.input_pwd, R.id.input_pwd_checkbox_layout, R.id.input_cancel, R.id.input_connect})
    public void onWpaClick(View v) {
        switch (v.getId()) {
            case R.id.input_pwd:
                activity.setKeyIndex(2);
                viewTransparent();
                setViewBackground(inputPwdLayout);
                //show();
                break;
            case R.id.input_pwd_checkbox_layout:
                activity.setKeyIndex(3);
                viewTransparent();
                setViewBackground(inputPwdCheckboxLayout);

                show();
                break;
            case R.id.input_cancel:
                activity.setKeyIndex(4);
                viewTransparent();
                setViewBackground(inputCancel);
                cancel();

                break;
            case R.id.input_connect:
                activity.setKeyIndex(5);
                viewTransparent();
                setViewBackground(inputConnect);
                connect();
                break;
        }
    }

    private void viewTransparent() {
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, inputPwdLayout, inputPwdCheckboxLayout
                , inputCancel, inputConnect);
    }

    public WpaConnectFragment() {
    }

    public static WpaConnectFragment getInstance() {
        if (instance == null) {
            return new WpaConnectFragment();
        }
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_wpa_connect_fragment, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.wpaConnectFragment = this;
        MainActivity.keySubmitAction = this;
        activity.setKeyIndex(2);
        input();
        setViewBackgroundTransparent(activity.wifiTitleImageLayout, inputPwdLayout,
                inputCancel, inputConnect, inputPwdCheckboxLayout);
        setViewBackground(inputPwdLayout);
        //初始化Main标题
        activity.wifiTitleImage.setVisibility(View.VISIBLE);
        activity.wifiTitleAppend.setText("密码输入");
    }

    @Override
    protected void initData() {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initWidget() {
        if (inputWifiName != null
                && inputSignal != null
                && inputSafety != null
                && inputPwdLayout != null
                && inputPwdCheckbox != null
                ) {
            inputWifiName.setText(currentScanResult.SSID);
            inputSignal.setText(levelString);
            inputSafety.setText(WifiSafetyString.makeSafetyString(currentScanResult));

            inputPwd.setHintTextColor(Color.WHITE);

            inputPwd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            inputPwdCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        inputPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        // 否则隐藏密码
                        inputPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            });

        }
    }


    private void cancel() {
        baseCancel(this);
    }

    private void connect() {
        String pwd = inputPwd.getText().toString();
        if (pwd.length() == 0) {
            inputPwdCheckboxTxt.setText("未输密码");
            inputPwd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (inputPwdCheckboxTxt != null) {
                        inputPwdCheckboxTxt.setText("显示密码");
                    }
                }
            }, 1000);
        } else if (pwd.length() < 8) {
            inputPwdCheckboxTxt.setText("密码小于8位");
            inputPwd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (inputPwdCheckboxTxt != null) {
                        inputPwdCheckboxTxt.setText("显示密码");
                    }
                }
            }, 1000);
        } else {
            //把wifi name,密码,isLock,返回到home
            baseCancel(this);
            MainActivity.homeRecyclerFragment.setBeanAndConnect(new WifiConnectBean(currentScanResult, pwd, true));

        }
    }

    private void input() {
        inputPwd.setFocusable(true);
        inputPwd.setFocusableInTouchMode(true);
        inputPwd.requestFocus();
        inputPwd.findFocus();
    }

    public void viewFocusable(View view) {

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.findFocus();
    }

    private void show() {
        boolean isChecked = inputPwdCheckbox.isChecked();
        if (isChecked) {
            inputPwdCheckbox.setChecked(false);
        } else {
            inputPwdCheckbox.setChecked(true);
        }
    }

    @Override
    public void onKeySubmitAction(int index) {
        Log.e("mainKey", "执行确认键操作，我是wpa:" + index);
        switch (index) {
            case 1:
                cancel();
                viewFocusable(activity.wifiTitle);
                break;
            case 2:
                //密码输入
                input();
                break;
            case 3:
                //显示密码
                viewFocusable(inputPwdCheckboxLayout);
                show();
                break;
            case 4:
                viewFocusable(inputCancel);
                cancel();
                break;
            case 5:
                viewFocusable(inputConnect);
                connect();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
