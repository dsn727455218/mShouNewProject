package com.shownew.home.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.db.DatabaseUtils;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.dao.ShopCarEntity;
import com.shownew.home.module.entity.UserEntity;
import com.wp.baselib.Config;
import com.wp.baselib.utils.DesUtil;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.LogUtil;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Response;


/**
 * Created by home on 2017/3/31 0031.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ShouNewApplication mShouNewApplication;
    private UserAPI mUserAPI;
    private ShopAPI shopAPI;
    private EditText mAccountEd;
    private EditText mPasswordEd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setBarColor(R.color.transparent);
        mShouNewApplication = ShouNewApplication.getInstance();
        mUserAPI = new UserAPI(mShouNewApplication);
        shopAPI = new ShopAPI(mShouNewApplication);
        initView();
    }

    private void initView() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("登录");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIcon(R.drawable.back_arrow);
        titleBarView.setTitleSize(20);

        mAccountEd = (EditText) findViewById(R.id.account_ed);
        mPasswordEd = (EditText) findViewById(R.id.password_ed);
        findViewById(R.id.parent_ll).setOnClickListener(this);
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.register_btn).setOnClickListener(this);
        findViewById(R.id.forget_password_btn).setOnClickListener(this);
        UserEntity userEntity = mUserAPI.getUserInfo();
        if (null != userEntity) {
            UserEntity.UserBean userBean = userEntity.getUser();
            if (userBean != null)
                mAccountEd.setText(userBean.getUPhone());
        }
    }


    /**
     * 登陆
     *
     * @param name
     * @param password
     */
    private void loginUser(String name, final String password) {

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            ToastUtil.showToast("请输入账号或密码");
            return;
        }
        if (!StringUtil.isMobileNo(name)) {
            ToastUtil.showToast("请输入正确的电话号码");
            return;
        }
        if (password.length() < 6) {
            ToastUtil.showToast("密码最少6位");
            return;
        }
        mUserAPI.Login(name, password, mShouNewApplication.new ShouNewHttpCallBackLisener<UserEntity>() {
            @Override
            protected UserEntity parseData(String data) {
                return JsonUtils.fromJson(data, UserEntity.class);
            }

            @Override
            protected void resultData(UserEntity userEntity, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (exception == null) {
                    if (null != userEntity) {
                        try {
                            //-2 该账户已被禁用      -1 无该账户      0 密码错误      1 登录成功
                            String msg = "";
                            int accountState = userEntity.getResult();
                            switch (accountState) {
                                case -2:
                                    msg = "该账户已被禁用";
                                    break;
                                case -1:
                                    msg = "无该账户";
                                    break;
                                case 0:
                                    msg = "密码错误";
                                    break;
                                case 1:
                                    msg = "登录成功";
                                    break;
                                case -3:
                                    msg="车辆正在被使用，稍后登录";
                                    break;
                            }
                            ToastUtil.showToast(msg);
                            if (1 == accountState) {
                                String pd = DesUtil.encryptDES(password, Config.DATA_DESKEY);
                                userEntity.getUser().setPassword(pd);
                                saveData(userEntity);
                                initPush(userEntity.getUser().getUPhone(), userEntity.getJSessionId());
                                Preference.putBoolean(mShouNewApplication, Preference.IS_LOGIN, true);
                                updateShopCar();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (exception instanceof ConnectException ||
                            exception instanceof SocketTimeoutException ||
                            exception instanceof UnknownHostException ||
                            exception instanceof SocketException) {
                        ToastUtil.showToast("请检查网络");
                    } else
                        ToastUtil.showToast("登录失败");
                }

            }

            @Override
            protected void onLoading() {
                createLoadingDialog();
            }


        });
    }

    @Override
    public void onClick(View v) {
        hideInputKeyword();
        Bundle bundle;
        switch (v.getId()) {
            case R.id.login_btn:
                String mUserName = mAccountEd.getText().toString().trim();
                String mPassword = mPasswordEd.getText().toString().trim();
                loginUser(mUserName, mPassword);
                break;
            case R.id.forget_password_btn:
                bundle = new Bundle();
                bundle.putInt("loginOrforgetpwd_flag", 2);//忘记密码
                mainApplication.redirectAndPrameter(RegisterActivity.class, bundle);
                break;
            case R.id.register_btn:
                bundle = new Bundle();
                bundle.putInt("loginOrforgetpwd_flag", 1);//注册
                mainApplication.redirectAndPrameter(RegisterActivity.class, bundle);
                break;
            case R.id.parent_ll:
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }

    private void saveData(UserEntity userEntity) {
        mUserAPI.saveUserInfo(userEntity);
    }

    /**
     * 初始化Jpush
     */
    private void initPush(String alias, String cHardcode) {
        LogUtil.d("====", "initPush");
        final Set<String> tags = new HashSet<>();
        tags.add(alias);
        JPushInterface.setAliasAndTags(LoginActivity.this, cHardcode, tags, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                LogUtil.d("====", i + "s:" + s + "set:" + set + "terminalIMEI========" + "" + mShouNewApplication.terminalIMEI);
            }
        });
    }


    private void updateShopCar() {
        List<ShopCarEntity> shopCarEntities = DatabaseUtils.getInstances().queryAllData();
        if (shopCarEntities != null && shopCarEntities.size() > 0) {
            shopAPI.updateShopCar(JsonUtils.toJson(shopCarEntities), mShouNewApplication.new ShouNewHttpCallBackLisener() {
                @Override
                protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                    DatabaseUtils.getInstances().deleteAll();
                    finish();
                }
            });

        } else {
            finish();
        }
    }
}
