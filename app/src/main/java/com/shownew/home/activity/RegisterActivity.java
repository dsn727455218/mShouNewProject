package com.shownew.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.SourcesEntity;
import com.shownew.home.module.entity.UserEntity;
import com.shownew.home.utils.dialog.CommonDialog;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;
import com.wp.zxing.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;


/**
 * 注册   或  密码重置
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private ShouNewApplication mShouNewApplication;
    private UserAPI mUserAPI;
    private EditText mRegisterEd;
    private TextView mSendMsgCode;
    private EditText mRegisterPassword;
    private EditText mRegisterAgainPassword;
    private EditText mRegisterVertifyCode;
    private TimeTask mTimeTask;
    private int mFlag;
    private String tag;
    private CheckBox mAgreementCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mShouNewApplication = ShouNewApplication.getInstance();
        mUserAPI = new UserAPI(mShouNewApplication);
        mTimeTask = new TimeTask(60 * 1000, 1000);
        initViews();
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");

        titleBarView.setOnLeftOnClickListener(this);
        mSendMsgCode = (TextView) findViewById(R.id.get_vertify_code);
        mSendMsgCode.setOnClickListener(this);
        mRegisterEd = (EditText) findViewById(R.id.register_ed);
        mRegisterAgainPassword = (EditText) findViewById(R.id.register_again_password);
        mRegisterPassword = (EditText) findViewById(R.id.register_password);
        Button commitRegister = (Button) findViewById(R.id.commit_register);
        commitRegister.setOnClickListener(this);
        View parent = findViewById(R.id.register_parent_rl);
        parent.setOnClickListener(this);
        mRegisterVertifyCode = (EditText) findViewById(R.id.register_vertify_code);
        mAgreementCheckBox = (CheckBox) findViewById(R.id.agreement);
        View agreement_parent = findViewById(R.id.agreement_parent);
        agreement_parent.setVisibility(View.GONE);
        TextView agreement_tv = (TextView) findViewById(R.id.agreement_tv);
        if (null != mBundle) {
            mFlag = mBundle.getInt("loginOrforgetpwd_flag", 0);//判断是否是注册  重置密码  修改密码
            if (1 == mFlag) {
                titleBarView.setTitle("注册");
                mRegisterPassword.setHint("请输入登录密码");
                mRegisterAgainPassword.setHint("重复密码");
                tag = "register";
                agreement_parent.setVisibility(View.VISIBLE);
                agreement_tv.setText(Html.fromHtml(String.format("%s<font color=#3681f1>%s</font>", "我已阅读并同意", "《用户注册/使用协议》")));
                agreement_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<SourcesEntity> sourcesEntities = mUserAPI.getSourcesData();
                        if (sourcesEntities != null && sourcesEntities.size() >= 6) {
                            String url = sourcesEntities.get(5).getSImg();
                            mShouNewApplication.redirectWeb("", url);
                        }
                    }
                });
            } else if (2 == mFlag) {
                titleBarView.setTitle("密码重置");
                mRegisterPassword.setHint("输入新的登录密码");
                mRegisterAgainPassword.setHint("重复密码");
                tag = "upPass";
            } else if (3 == mFlag) {
                findViewById(R.id.line1).setBackgroundResource(R.drawable.dividingline_bai);
                findViewById(R.id.line2).setBackgroundResource(R.drawable.dividingline_bai);
                findViewById(R.id.line3).setBackgroundResource(R.drawable.dividingline_bai);
                findViewById(R.id.line4).setBackgroundResource(R.drawable.dividingline_bai);
                titleBarView.setTitle("修改密码");
                mRegisterPassword.setHint("输入新的登录密码");
                mRegisterPassword.setTextColor(getResources().getColor(R.color.color_text_my));
                mRegisterAgainPassword.setTextColor(getResources().getColor(R.color.color_text_my));

                mRegisterEd.setTextColor(getResources().getColor(R.color.color_text_my));
                mRegisterVertifyCode.setTextColor(getResources().getColor(R.color.color_text_my));
                mRegisterAgainPassword.setHint("重复密码");
                tag = "upPass";
                parent.setBackgroundResource(R.color.color_bg_grey);
            }
        }
        mRegisterAgainPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideInputKeyword();
                    commitRegiser();
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
            case R.id.get_vertify_code:
                vertifyPhone();
                break;
            case R.id.commit_register:
                commitRegiser();
                break;
            case R.id.register_parent_rl:
                hideInputKeyword();
                break;
        }
    }

    /**
     * 提交申请
     */
    private void commitRegiser() {
        final String phone = mRegisterEd.getText().toString().trim();
        if (!checkPhone(phone)) {
            return;
        }
        final String code = mRegisterVertifyCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showToast("请输入验证码");
            return;
        }
        if (code.length() != 4) {
            ToastUtil.showToast("验证码为4位");
            return;
        }
        final String firstPassword = mRegisterPassword.getText().toString().trim();
        String againPassword = mRegisterAgainPassword.getText().toString().trim();
        if (TextUtils.isEmpty(firstPassword) || TextUtils.isEmpty(againPassword)) {
            ToastUtil.showToast("请输入密码");
            return;
        }
        if (firstPassword.length() < 6 || againPassword.length() < 6) {
            ToastUtil.showToast("密码最低6位");
            return;
        }
        if (!firstPassword.equals(againPassword)) {
            ToastUtil.showToast("两次密码不相等，请重新输入");
            return;
        }
        if (1 == mFlag && !mAgreementCheckBox.isChecked()) {
            ToastUtil.showToast("请同意用户注册使用协议");
            return;
        }
        registerAccount(phone, firstPassword, code);
    }

    /**
     * 注册账号
     *
     * @param phone
     * @param password
     * @param phoneCode
     */
    private void registerAccount(final String phone, String password, String phoneCode) {
        mUserAPI.registerOrModifyPwdAccount(phone, password, phoneCode, tag, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String data) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                try {
                    closeLoadingDialog();
                    if (null == exception) {
                        String msg = "";
                        if (json.has("data")) {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("result")) {
                                int state = jsonObject.getInt("result");
                                //-1=该手机已被注册    0=验证码错误    1=注册成功
                                switch (state) {
                                    case 0:
                                        msg = "验证码错误";
                                        break;
                                    case 1:
                                        if (1 == mFlag) {
                                            msg = "注册成功";
                                        } else {
                                            msg = "重置密码成功,请重新登录！";
                                            mShouNewApplication.redirect(LoginActivity.class);
                                            finish();
                                        }
                                        break;
                                    case -1:
                                        if (1 == mFlag) {
                                            msg = "该手机已被注册";
                                        } else {
                                            msg = "该手机号未注册";
                                        }
                                        break;
                                }
                                ToastUtil.showToast(msg);
                                if (1 == mFlag && 1 == state) {
                                    new CommonDialog(RegisterActivity.this, "", "需要车辆注册吗?", "是", "否").setCommonListener(new CommonDialog.CommonListener() {
                                        @Override
                                        public void sure(int flag) {
                                            if (0 == flag) {
                                                ToastUtil.showToast("请重新登录");
                                                mShouNewApplication.redirect(LoginActivity.class);
                                                finish();
                                            } else if (1 == flag) {
                                                UserEntity userEntity = new UserEntity();
                                                UserEntity.UserBean userBean = userEntity.new UserBean();
                                                userBean.setUPhone(phone);
                                                userEntity.setUser(userBean);
                                                mUserAPI.saveUserInfo(userEntity);
                                                mainApplication.redirectAndPrameterResult(RegisterActivity.this, CaptureActivity.class, null, 1);
                                            }
                                        }
                                    }).setCancelable(true).show();
                                } else if (2 == mFlag && 1 == state) {
                                    mainApplication.redirect(LoginActivity.class);
                                    finish();
                                }
                            }
                        }


                    } else {
                        ToastUtil.showToast(1 == mFlag ? "注册失败!" : "重置密码失败!,请再试一次!");
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
        String phone = mRegisterEd.getText().toString().trim();
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
            protected JSONObject parseData(String data) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (exception == null) {
                    if (null != mTimeTask) {
                        mTimeTask.start();
                        mSendMsgCode.setTextColor(mShouNewApplication.getResources().getColor(R.color.color_press));
                        //记录发送的次数
                        Preference.putInt(mShouNewApplication, "recoder_count", Preference.getInt(mShouNewApplication, "recoder_count", 0) + 1);
                        ToastUtil.showToast("验证码已发送到你的手机");
                        mSendMsgCode.setEnabled(false);
                        return;
                    }

                }
                mSendMsgCode.setEnabled(true);
                mSendMsgCode.setText("重新发送");
            }

            @Override
            protected void onLoading() {
                createLoadingDialog();
                mSendMsgCode.setEnabled(false);
                mSendMsgCode.setText("正在发送");
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
            mSendMsgCode.setText(String.format("%3d秒", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            mSendMsgCode.setEnabled(true);
            mSendMsgCode.setText("重新发送");
            mSendMsgCode.setTextColor(mShouNewApplication.getResources().getColor(R.color.select_color));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            String qrCode = data.getStringExtra("QRCode");
            if (com.wp.zxing.config.Config.decode_time_out.equals(qrCode)) {
                ToastUtil.showToast("二维码扫描超时！请手动输入设备号！");
                mainApplication.redirect(NewCarRegisterActivity.class);
            } else if (com.wp.zxing.config.Config.jump_saoma.equals(qrCode)) {
                mainApplication.redirect(NewCarRegisterActivity.class);
            } else {
                if (qrCode.contains(Config.SN)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("QRCode", qrCode);
                    mainApplication.redirectAndPrameter(NewCarRegisterActivity.class, bundle);
                }
            }
             finish();
        }
    }
}
