package com.yuyife.flywifi.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.yuyife.flywifi.R;

/**
 * @author yuyife
 * */
public class MyAnimation {
    private static final String TAG = "MyAnimation";

    public static void startItemAnim(View view, int durationMillis) {

        Animation animIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.item_bottom_in);

        animIn.setFillAfter(true);
        animIn.setDuration(durationMillis);
        view.startAnimation(animIn);

    }

    public static void showViewAnim(View view, float fromX, float toX, float fromY, float toY) {
        AnimatorSet a = new AnimatorSet();
        a.playTogether(ObjectAnimator.ofFloat(view, "X", fromX, toX)
                , ObjectAnimator.ofFloat(view, "Y", fromY, toY));
        a.setDuration(300);
        a.start();
    }

    public static void fallAnim(View view, int mDuration) {
        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 2, 1.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "scaleY", 2, 1.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration * 3 / 2)

        );
       // mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }
    public static void shakeAnim(View view, int mDuration) {
        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0, .10f, -25, .26f, 25,.42f, -25, .58f, 25,.74f,-25,.90f,1,0).setDuration(mDuration)

        );
        //mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }
}







