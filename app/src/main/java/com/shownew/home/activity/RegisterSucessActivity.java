package com.shownew.home.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;

public class RegisterSucessActivity extends BaseActivity {

    private TextView mRegisterTipsTv;
    private static final String HTML = "<big>%s</big><br/><big>%s</big><br/><small>%s秒后自动跳到登陆界面<small>";
    private AuthCountDownTimer mAuthCountDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sucess);
        setBarColor(R.color.transparent);
        mRegisterTipsTv = (TextView) findViewById(R.id.reigster_tips);
        mAuthCountDownTimer = new AuthCountDownTimer(5000, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mAuthCountDownTimer)
            mAuthCountDownTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mAuthCountDownTimer)
            mAuthCountDownTimer.cancel();
    }

    private class AuthCountDownTimer extends CountDownTimer {

        public AuthCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mRegisterTipsTv.setText(Html.fromHtml(String.format(HTML, "恭喜", "注册成功!", millisUntilFinished / 1000)));
        }

        @Override
        public void onFinish() {
            mainApplication.redirect(LoginActivity.class);
            finish();
        }
    }

}
