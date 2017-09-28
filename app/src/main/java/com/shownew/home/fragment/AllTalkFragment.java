package com.shownew.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.adapter.AllEvelateAdapter;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.entity.AllEvelateEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

/**
 * 评价
 */
public class AllTalkFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "type";
    private static final String SHOP_ID = "products";
    private static final String SHOP_TYPE = "productsType";
    private String type;
    private String productId;
    private String productsType;
    private ShopAPI shopAPI;

    private int page;
    private ShouNewApplication shouNewApplication;
    private View view;
    private XRecyclerView mRecyclerView;
    private TextView mEmptyTips;
    private ProgressBar mEmptyProgressBar;
    private View mEmptyView;
    private AllEvelateAdapter allEvelateAdapter;
    private ArrayList<AllEvelateEntity> allEvelateEntities;

    public static AllTalkFragment newInstance(String type, String productsType, String products) {
        AllTalkFragment fragment = new AllTalkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, type);
        args.putString(SHOP_ID, products);
        args.putString(SHOP_TYPE, productsType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_PARAM1);
            productId = getArguments().getString(SHOP_ID);
            productsType = getArguments().getString(SHOP_TYPE);
            shouNewApplication = ShouNewApplication.getInstance();
            shopAPI = new ShopAPI(shouNewApplication);
        }
        initVariable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_blank, container, false);
            mEmptyView = view.findViewById(R.id.empty_view);
            mEmptyView.setBackgroundColor(getResources().getColor(R.color.white));
            mEmptyView.setOnClickListener(this);
            mEmptyTips = (TextView) view.findViewById(R.id.textView);
            mEmptyProgressBar = (ProgressBar) view.findViewById(R.id.head_progressBar);
            mRecyclerView = (XRecyclerView) view.findViewById(R.id.list);
            allEvelateEntities = new ArrayList<AllEvelateEntity>();
            allEvelateAdapter = new AllEvelateAdapter(this, shouNewApplication, allEvelateEntities);
            mRecyclerView.setAdapter(allEvelateAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
            mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
            mRecyclerView.setEmptyView(mEmptyView);
            mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
            mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }

                @Override
                public void onLoadMore() {
                    isRefresh = false;
                    getDiscussList();
                }
            });

//            refresh();
        }

        return view;
    }


    private boolean isFragmentVisible;
    private boolean isReuseView;
    private boolean isFirstVisible;
    private View rootView;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //setUserVisibleHint()有可能在fragment的生命周期外被调用
        if (rootView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstVisible = false;
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentVisibleChange(false);
        }
    }

    private void onFragmentVisibleChange(boolean isVisible) {

        if (isVisible) {
            //更新界面数据，如果数据还在下载中，就显示加载框
            refresh();
        } else {
            //关闭加载框
        }
    }

    private void onFragmentFirstVisible() {
        //去服务器下载数据

    }

    @Override
    public void onViewCreated(View view1, @Nullable Bundle savedInstanceState) {
        //如果setUserVisibleHint()在rootView创建前调用时，那么
        //就等到rootView创建完后才回调onFragmentVisibleChange(true)
        //保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        if (rootView == null) {
            rootView = view1;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    onFragmentFirstVisible();
                    isFirstVisible = false;
                }
                onFragmentVisibleChange(true);
                isFragmentVisible = true;
            }
        }
        super.onViewCreated(isReuseView ? rootView : view1, savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        initVariable();
    }

    private void initVariable() {
        isFirstVisible = true;
        isFragmentVisible = false;
        rootView = null;
        isReuseView = true;
    }

    /**
     * 设置是否使用 view 的复用，默认开启
     * view 的复用是指，ViewPager 在销毁和重建 Fragment 时会不断调用 onCreateView() -> onDestroyView()
     * 之间的生命函数，这样可能会出现重复创建 view 的情况，导致界面上显示多个相同的 Fragment
     * view 的复用其实就是指保存第一次创建的 view，后面再 onCreateView() 时直接返回第一次创建的 view
     *
     * @param isReuse
     */
    protected void reuseView(boolean isReuse) {
        isReuseView = isReuse;
    }

    private void refresh() {
        isRefresh = true;
        page = 1;
        getDiscussList();
    }

    private boolean isRefresh;

    private void getDiscussList() {
        if (TextUtils.isEmpty(productsType))
            return;
        shopAPI.getDiscussList(productsType, productId, type, page, shouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                int size = allEvelateEntities.size();
                if (isRefresh && size > 0) {
                    allEvelateEntities.clear();
                    mRecyclerView.refreshComplete();
                } else {
                    mRecyclerView.loadMoreComplete();
                }
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("discussList")) {
                                ArrayList<AllEvelateEntity> evelateEntities = JsonUtils.fromJson(jsonObject.getString("discussList"), new TypeToken<ArrayList<AllEvelateEntity>>() {
                                }.getType());
                                if (evelateEntities != null && evelateEntities.size() > 0) {
                                    allEvelateEntities.addAll(evelateEntities);
                                    page++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (size == 0) {
                        mEmptyView.setEnabled(true);
                        mEmptyTips.setText("没有数据\n点击我刷新");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.setNoMore(true);
                    }
                }
                allEvelateAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_view:
                mEmptyView.setEnabled(false);
                mEmptyTips.setText("数据加载中...");
                mEmptyProgressBar.setVisibility(View.VISIBLE);
                refresh();
                break;
        }
    }

    public void fabulous(AllEvelateEntity allEvelateEntity) {
        if (allEvelateEntity == null)
            return;
        long disnice = allEvelateEntity.getDIsnice();
        if (disnice == -1) {//未登录
            if (!Preference.getBoolean(getActivity(), Preference.IS_LOGIN, false)) {
                shouNewApplication.jumpLoginActivity(getActivity());
            }
        } else if (disnice == 0) {//点赞
            shopAPI.fabulous(allEvelateEntity.getDId(), shouNewApplication.new ShouNewHttpCallBackLisener() {
                @Override
                protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                    if (exception == null) {
                        refresh();
                    }
                }
            });
        }


    }
}
