package com.shownew.home.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.UserAPI;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * 更换手机号码
 */
public class ModifyPhoneNumberActivity extends BaseActivity implements View.OnClickListener {

    private EditText mInputVertifyCode;
    private TextView mSendCode;
    private EditText mLoginPasswordEd;
    private EditText mNewsPhoneEd;

    private ShouNewApplication mShouNewApplication;
    private UserAPI mUserAPI;
    private TimeTask mTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone_number);
        mShouNewApplication = ShouNewApplication.getInstance();
        mUserAPI = new UserAPI(mShouNewApplication);
        mTimeTask = new TimeTask(60 * 1000, 1000);
        initViews();
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("更换电话号码");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);

        findViewById(R.id.commit_phone).setOnClickListener(this);
        mSendCode = (TextView) findViewById(R.id.send_code);
        mSendCode.setOnClickListener(this);
        mInputVertifyCode = (EditText) findViewById(R.id.input_vertify_code);
        mLoginPasswordEd = (EditText) findViewById(R.id.login_password);
        mNewsPhoneEd = (EditText) findViewById(R.id.news_phone_ed);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.commit_phone: //提交
                commitRegiser();
                break;
            case R.id.send_code:
                vertifyPhone();
                break;
        }
    }


    /**
     * 提交申请
     */
    private void commitRegiser() {
        final String phone = mNewsPhoneEd.getText().toString().trim();
        if (!checkPhone(phone)) {
            return;
        }
        final String code = mInputVertifyCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showToast("请输入验证码");
            return;
        }
        if (code.length() != 4) {
            ToastUtil.showToast("验证码为4位");
            return;
        }
        String password = mLoginPasswordEd.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast("请输入登录密码");
            return;
        }
        commitAccount(phone, password, code);
    }

    /**
     * 修改手机号
     *
     * @param phone
     * @param password
     * @param phoneCode
     */
    private void commitAccount(String phone, String password, String phoneCode) {
        mUserAPI.changePhone(phone, phoneCode, password, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String data) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                try {
                    closeLoadingDialog();
                    if (null == exception) {
                        if (200 == json.getInt(com.wp.baselib.Config.STATUS_CODE)) {
                            if (json.has("data")) {
                                JSONObject jsonObject = json.getJSONObject("data");
                                if (jsonObject.has("result")) {
                                    int code = jsonObject.getInt("result");
                                    //-2-该手机已被注册       -1-验证码错误      0-密码错误      1-更换成功
                                    String msg = "";
                                    switch (code) {
                                        case 0:
                                            msg = "密码错误";
                                            break;
                                        case 1:
                                            msg = "更换成功";
                                            break;
                                        case -1:
                                            msg = "验证码错误";
                                            break;
                                        case -2:
                                            msg = "该手机已被注册";
                                            break;
                                    }
                                    ToastUtil.showToast(msg);
                                    if (1 == code) {
                                        mainApplication.redirect(LoginActivity.class);
                                        finish();
                                    }
                                }
                            }
                        }
                    } else {
                        ToastUtil.showToast("修改失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onLoading() {
                createLoadingDialog();
            }
        });
    }

    /**
     * 检查手机号码
     *
     * @param phone
     * @return
     */
    private boolean checkPhone(String phone) {
        hideInputKeyword();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast("请输入电话号码");
            return false;
        }
        if (!StringUtil.isMobileNo(phone)) {
            ToastUtil.showToast("请输入正确的电话号码");
            return false;
        }
        return true;
    }

    private void vertifyPhone() {
        String phone = mNewsPhoneEd.getText().toString().trim();
        if (!checkPhone(phone)) {
            return;
        }
        sendMsgCode(phone);
    }


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
