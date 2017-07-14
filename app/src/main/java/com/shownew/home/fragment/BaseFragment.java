package com.shownew.home.fragment;


import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.PublicApi;
import com.shownew.home.receiver.MsgBroadCasetRecivier;
import com.shownew.home.utils.GlideImageLoader;
import com.wp.baselib.widget.banner.Banner;
import com.wp.baselib.widget.banner.BannerConfig;
import com.wp.baselib.widget.banner.Transformer;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements MsgBroadCasetRecivier.MsgLisener {

    protected Context context;
    protected ShouNewApplication mShouNewApplication;
    private MsgBroadCasetRecivier mBroadcastReceiver;
    protected PublicApi mPublicApi;

    public BaseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShouNewApplication = ShouNewApplication.getInstance();
        mBroadcastReceiver = new MsgBroadCasetRecivier();
        mPublicApi = new PublicApi(mShouNewApplication);
        mShouNewApplication.registerReceiver(mBroadcastReceiver, new IntentFilter(Config.BROADCASEREVEIVER_MGS_ACTION));
        mBroadcastReceiver.setMsgLisener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }


    @Override
    public void callback() {
        exsitUnReadMsg();
    }

    /**
     * 是否消息
     *
     * @param isRead
     */
    protected void isHaveMsg(int isRead) {

    }

    /**
     * 初始化banner
     *
     * @param mBanner
     */
    protected void initBanner(Banner mBanner) {
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader(R.drawable.home_bannerseize));
        //设置图片集合

        mBanner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(3000);

        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);

        //banner设置方法全部调用完毕时最后调用
        //        mBanner.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        exsitUnReadMsg();
    }

    private void exsitUnReadMsg() {
        mPublicApi.exsitUnReadMsg(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("unRead")) {
                                int unRead = jsonObject.getInt("unRead");
                                isHaveMsg(unRead);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    isHaveMsg(0);
                }
            }

            @Override
            protected void handleLogin() {
                isHaveMsg(0);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            mShouNewApplication.unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }
}
