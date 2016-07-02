package com.yuyife.flywifi.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * 基类Activity
 *
 * @author yuyife
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
        initWidget();
    }

    abstract protected void initData();

    abstract protected void initWidget();

    protected void showToast(CharSequence txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }

    protected void showSnackBar(View v,CharSequence txt) {
        Snackbar.make(v, txt, Snackbar.LENGTH_SHORT).show();
    }
}
