package com.shownew.home.fragment;


import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.FunctionRenewActivity;
import com.shownew.home.activity.MainActivity;
import com.shownew.home.activity.MicroCustomerActivity;
import com.shownew.home.activity.SetActivity;
import com.shownew.home.activity.ShouWalletActivity;
import com.shownew.home.activity.UserInfoActivity;
import com.shownew.home.activity.msg.AllMsgActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.SourcesEntity;
import com.shownew.home.module.entity.UserEntity;
import com.shownew.home.utils.AppUpdateUtil;
import com.shownew.home.utils.dialog.CommonDialog;
import com.shownew.home.utils.dialog.ShareDialog;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {
    private View mConverView;
    private ImageView mHeaderIcon;
    private TextView mNichengTv;
    private UserAPI mUserAPI;
    private TitleBarView mTitleBarView;
    private TextView mSys_tips;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mConverView) {
            mConverView = inflater.inflate(R.layout.fragment_my, container, false);
            mUserAPI = new UserAPI(mShouNewApplication);
            initViews();
        }
        return mConverView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfoData();
        exsitUnReadMsg();
        new AppUpdateUtil(context, mShouNewApplication).UpdateExecute(true,mSys_tips);
    }

    private void initViews() {
        mTitleBarView = (TitleBarView) mConverView.findViewById(R.id.headbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTitleBarView.setPadding(0, ShouNewApplication.getStatusBarHeight(getActivity()), 0, 0);
        }
        mTitleBarView.setTitle("我的");
        mTitleBarView.setTitleSize(20);
        mTitleBarView.setTitleTextColor(R.color.color_title);
        mTitleBarView.setOnMoreClickListener(this);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setMoreIcon(R.drawable.share);
        mTitleBarView.setLeftIcon(R.drawable.news);

        mSys_tips = (TextView) mConverView.findViewById(R.id.sys_tips);

        mHeaderIcon = (ImageView) mConverView.findViewById(R.id.my_header_icon);
        mNichengTv = (TextView) mConverView.findViewById(R.id.my_nichegn_tv);
        TextView mUserInfoTv = (TextView) mConverView.findViewById(R.id.my_user_info_tv);
        mHeaderIcon.setOnClickListener(this);
        mNichengTv.setOnClickListener(this);
        mUserInfoTv.setOnClickListener(this);
        mConverView.findViewById(R.id.sys_setting_tv).setOnClickListener(this);
        mConverView.findViewById(R.id.function_renew_tv).setOnClickListener(this);
        mConverView.findViewById(R.id.my_hou_wallet_tv).setOnClickListener(this);
        mConverView.findViewById(R.id.micro_custormer).setOnClickListener(this);
        mConverView.findViewById(R.id.user_help).setOnClickListener(this);
        mConverView.findViewById(R.id.logout).setOnClickListener(this);
    }

    /**
     * 退出登陆
     */
    private void logout() {
        mUserAPI.logout(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    ToastUtil.showToast("注销成功");
//                    mShouNewApplication.redirect(MainActivity.class);
                    Preference.putBoolean(mShouNewApplication, Preference.IS_LOGIN, false);
                }

            }

            @Override
            protected void onLoading() {
                ToastUtil.showToast("注销中");
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.logout:
                new CommonDialog(context, "是否注销账号").setCommonListener(new CommonDialog.CommonListener() {
                    @Override
                    public void sure(int flag) {
                        if (1 == flag) {
                            logout();
                        }
                    }
                }).setCancelable(true).show();
                break;
            case R.id.my_header_icon:
            case R.id.my_nichegn_tv:
            case R.id.my_user_info_tv:
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(context);
                    return;
                }
                mShouNewApplication.redirect(UserInfoActivity.class);
                break;
            case R.id.sys_setting_tv:
                mShouNewApplication.redirect(SetActivity.class);
                break;
            case R.id.function_renew_tv:
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(context);
                    return;
                }
                mShouNewApplication.redirect(FunctionRenewActivity.class);
                break;
            case R.id.my_hou_wallet_tv:
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(context);
                    return;
                }
                mShouNewApplication.redirect(ShouWalletActivity.class);
                break;
            case R.id.micro_custormer:
                mShouNewApplication.redirect(MicroCustomerActivity.class);
                break;
            case R.id.user_help:
                ArrayList<SourcesEntity> sourcesEntities = mUserAPI.getSourcesData();
                if (sourcesEntities != null && sourcesEntities.size() >= 4) {
                    mShouNewApplication.redirectWeb("使用手册", sourcesEntities.get(2).getSImg());
                }
                break;
            case R.id.backBtn://消息
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(context);
                    return;
                }
                mShouNewApplication.redirect(AllMsgActivity.class);
                if (context != null && context instanceof MainActivity) {
                    MainActivity activity = (MainActivity) context;
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                }
                return;
            case R.id.title_bar_more:
                new ShareDialog(context, mShouNewApplication).setCancelable(true).show();
                break;

        }
    }

    private void getUserInfoData() {
        mUserAPI.getUserData(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject result = json.getJSONObject("data");

                            String user = result.getString("user");
                            if (!TextUtils.isEmpty(user)) {
                                UserEntity.UserBean userBean = JsonUtils.fromJson(user, UserEntity.UserBean.class);
                                if (null != userBean) {
                                    mNichengTv.setText(TextUtils.isEmpty(userBean.getUNickname()) ? "修改昵称" : userBean.getUNickname());
                                    Glide.with(context).load(userBean.getUIcon()).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            mHeaderIcon.setImageBitmap(resource);
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        });
    }

    private void exsitUnReadMsg() {
        mUserAPI.exsitUnReadMsg(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("unRead")) {
                                int unRead = jsonObject.getInt("unRead");
                                if (0 == unRead) {
                                    mTitleBarView.getMsgCircle().setVisibility(View.GONE);
                                } else if (1 == unRead) {
                                    mTitleBarView.getMsgCircle().setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    mTitleBarView.getMsgCircle().setVisibility(View.GONE);
                }
            }
        });

    }
}
