package com.shownew.home.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.UserEntity;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * 交易密码设置
 */
public class TransactionActivity extends BaseActivity implements View.OnClickListener {
    private EditText loginPwdEd;
    private EditText mPhoneCode;
    private EditText mTransactionPwd;
    private EditText mTransactionPwdAgain;
    private UserAPI mUserAPI;
    private TimeTask mTimeTask;
    private TextView mSendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        mUserAPI = new UserAPI(mShouNewApplication);
        mTimeTask = new TimeTask(60 * 1000, 1000);
        initViews();
    }

    private void initViews() {

        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("交易密码设置");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        loginPwdEd = (EditText) findViewById(R.id.login_password);
        mPhoneCode = (EditText) findViewById(R.id.transaction_phone_code);
        mSendCode = (TextView) findViewById(R.id.get_vertify_code);
        mSendCode.setOnClickListener(this);
        mTransactionPwd = (EditText) findViewById(R.id.transaction_password);
        mTransactionPwdAgain = (EditText) findViewById(R.id.transaction_password_again);
        findViewById(R.id.transation_commit).setOnClickListener(this);
        mTransactionPwdAgain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.transation_commit:
                commit();
                break;
            case R.id.get_vertify_code:
                vertifyPhone();
                break;
        }
    }

    /**
     * 验证手机号
     */
    private void vertifyPhone() {
        UserEntity userEntity = mUserAPI.getUserInfo();
        if (null != userEntity) {
            UserEntity.UserBean userBean = userEntity.getUser();
            if (null != userBean) {
                sendMsgCode(userBean.getUPhone());
            }
        }
    }

    /**
     * 发送验证码
     *
     * @param phone
     */
    private void sendMsgCode(String phone) {
        if (System.currentTimeMillis() - Preference.getLong(mShouNewApplication, "day_time", 0) > 24 * 60 * 60 * 1000) {
            Preference.putInt(mShouNewApplication, "recoder_count", 0);
        }
        if (5 < Preference.getInt(mShouNewApplication, "recoder_count", 0)) {
            ToastUtil.showToast("已经超过验证码发送次数");
            return;
        }
        Preference.putLong(mShouNewApplication, "day_time", System.currentTimeMillis());
        mUserAPI.sendMsgCode(phone, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected JSONObject parseData(String data) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (exception == null) {
                    if (null != mTimeTask) {
                        mTimeTask.start();
                        mSendCode.setTextColor(mShouNewApplication.getResources().getColor(R.color.color_press));
                        //记录发送的次数
                        Preference.putInt(mShouNewApplication, "recoder_count", Preference.getInt(mShouNewApplication, "recoder_count", 0) + 1);
                        ToastUtil.showToast("验证码已发送到你的手机");
                        mSendCode.setEnabled(false);
                        return;
                    }

                }
                mSendCode.setEnabled(true);
                mSendCode.setText("重新发送");
            }

            @Override
            protected void onLoading() {
                createLoadingDialog();
                mSendCode.setEnabled(false);
                mSendCode.setText("正在发送");
            }
        });

    }

    /**
     * 提交申请
     */
    private void commit() {
        UserEntity userEntity = mUserAPI.getUserInfo();
        if (null != userEntity) {
            UserEntity.UserBean userBean = userEntity.getUser();
            if (null != userBean) {
                final String code = mPhoneCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showToast("请输入验证码");
                    return;
                }
                if (code.length() != 4) {
                    ToastUtil.showToast("验证码为4位");
                    return;
                }
                final String firstPassword = mTransactionPwd.getText().toString().trim();
                String againPassword = mTransactionPwdAgain.getText().toString().trim();
                if (TextUtils.isEmpty(firstPassword) || TextUtils.isEmpty(againPassword)) {
                    ToastUtil.showToast("请输入密码");
                    return;
                }
                if (!firstPassword.equals(againPassword)) {
                    ToastUtil.showToast("两次密码不相等，请重新输入");
                    return;
                }
                if (firstPassword.length() != 6 || againPassword.length() != 6) {
                    ToastUtil.showToast("交易密码必须为6位");
                    return;
                }
                String loginPwd = loginPwdEd.getText().toString().trim();
                if (TextUtils.isEmpty(loginPwd)) {
                    ToastUtil.showToast("请输入登录密码");
                    return;
                }
                setTransactionPwd(userBean.getUPhone(), loginPwd, firstPassword, code);
            }
        }
    }

    /**
     * 修改用户交易密码
     *
     * @param phone
     * @param firstPassword
     * @param code
     */
    private void setTransactionPwd(String phone, String loginPwd, String firstPassword, String code) {
        mUserAPI.changeTradePass(phone, loginPwd, firstPassword, code, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (exception == null) {

                    if (json.has("data")) {
                        try {
                            //                            result：-1=验证码错误    0=密码错误    1=修改成功
                            JSONObject dataJson = json.getJSONObject("data");

                            if (dataJson.has("result")) {
                                String msg = "";
                                int result = dataJson.getInt("result");
                                if (-1 == result) {
                                    msg = "验证码错误";
                                } else if (0 == result) {
                                    msg = "登录密码错误";
                                } else if (1 == result) {
                                    msg = "修改成功";
                                    ToastUtil.showToast(msg);
                                    finish();
                                    return;
                                }
                                ToastUtil.showToast(msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                } else {
                    ToastUtil.showToast("设置失败");
                }
            }

            @Override
            protected void onLoading() {
                createLoadingDialog();
            }
        });
    }

    private class TimeTask extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mSendCode.setText(String.format("%3d秒", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            mSendCode.setEnabled(true);
            mSendCode.setText("重新发送");
            mSendCode.setTextColor(mShouNewApplication.getResources().getColor(R.color.select_color));
        }
    }
}
