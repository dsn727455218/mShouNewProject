package com.shownew.home.activity.shop;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.shopcommon.AllEvalueteActivity;
import com.shownew.home.activity.shopcommon.ShoppingCartActivity;
import com.shownew.home.adapter.ShopHomeAdapter;
import com.shownew.home.db.DatabaseUtils;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.dao.ShopCarEntity;
import com.shownew.home.module.entity.SuperMarkeDetailEntity;
import com.shownew.home.module.entity.SuperMarketEntity;
import com.shownew.home.utils.GlideImageLoader;
import com.shownew.home.utils.PreviewImgUtils;
import com.shownew.home.utils.dialog.ShareDialog;
import com.shownew.home.utils.dialog.ShopPopwindow;
import com.shownew.home.utils.dialog.ShopSelectDialog;
import com.shownew.home.utils.widget.WordWrapLayoutiml;
import com.wp.baselib.AndroidActivity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.TimeUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.CustomShapeImageView;
import com.wp.baselib.widget.banner.Banner;
import com.wp.baselib.widget.banner.BannerConfig;
import com.wp.baselib.widget.banner.Transformer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

import static com.shownew.home.R.id.add_shop;


public class ShopDetailActivity extends AndroidActivity implements View.OnClickListener {
    private XRecyclerView mRecyclerView;
    private ShopHomeAdapter mDataAdapter;
    private View mShopDetailTitle;
    private ShouNewApplication mShouNewApplication;
    private ShopAPI mShopAPI;
    private String mShopId;
    private SuperMarkeDetailEntity mSuperMarkeDetailEntity;
    private TextView mShopIntroTv;
    private TextView mShopDetailPricesTv;
    private TextView mShopOldPrices;
    private TextView mKuaidiMoney;
    private TextView mShopDetailAddress;
    private LinearLayout mShopDetailImgParent;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isRefresh;
    private boolean isClickItem;
    private View mMoreMenu;
    private TextView mTags;
    /**
     * 是否  收藏
     */
    private TextView isFavor;

    private WordWrapLayoutiml wrapLayoutiml;
    private View visiable_evelautes;
    private TextView evelute_count;
    private CustomShapeImageView my_info_header_scv;
    private TextView nicheng_tv;
    private TextView evelute_content;
    private TextView evelute_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        mShouNewApplication = ShouNewApplication.getInstance();
        mShopAPI = new ShopAPI(mShouNewApplication);
        if (mBundle != null) {
            mShopId = mBundle.getString("shopId");
            int typeId = mBundle.getInt("typeId");
        }
        initViews();
        refresh();
    }

    private void initViews() {
        mShopDetailTitle = findViewById(R.id.shop_detail_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            findViewById(R.id.shop_detail_title_parent).setPadding(0, ShouNewApplication.getStatusBarHeight(this), 0, 0);
            mShopDetailTitle.findViewById(R.id.shop_title).setPadding(0, ShouNewApplication.getStatusBarHeight(this), 0, 0);
            mShopDetailTitle.setPadding(0, ShouNewApplication.getStatusBarHeight(this), 0, 0);
        }
        isFavor = (TextView) findViewById(R.id.is_favor);
        isFavor.setOnClickListener(this);
        findViewById(add_shop).setOnClickListener(this);
        mMoreMenu = findViewById(R.id.more_menu);
        mMoreMenu.setOnClickListener(this);
        findViewById(R.id.back_shop_detail).setOnClickListener(this);
        findViewById(R.id.buy_shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Preference.getBoolean(ShopDetailActivity.this, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(ShopDetailActivity.this);
                    return;
                }
                if (mSuperMarkeDetailEntity != null) {
                    new ShopSelectDialog(ShopDetailActivity.this, mSuperMarkeDetailEntity.getPSimg(), mSuperMarkeDetailEntity.getpAllprice(), mSuperMarkeDetailEntity.getPColor()).setDialogLisener(new ShopSelectDialog.DialogLisener() {
                        @Override
                        public void sure(String color, int number, double prices) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("mSuperMarkeDetailEntity", mSuperMarkeDetailEntity);
                            bundle.putString("color", color);
                            bundle.putInt("number", number);
                            bundle.putDouble("prices", prices);
                            mShouNewApplication.redirectAndPrameter(SureOderMenuActivity.class, bundle);
                        }
                    }).setCancelable(true).show();
                }
            }
        });
        mRecyclerView = (XRecyclerView) findViewById(R.id.shop_detail_xrecyclerview);


        mDataAdapter = new ShopHomeAdapter(mShouNewApplication, mListData);
        mRecyclerView.setAdapter(mDataAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.addOnScrollListener(new RecyclerViewListener());
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mDataAdapter.setShopHomeLisener(new ShopHomeAdapter.ShopHomeLisener() {
            @Override
            public void clickShopItem(String shopId) {
                //                mShopId = shopId;
                //                isClickItem = true;
                //                getProductInfo();
                Bundle bundle = new Bundle();
                bundle.putString("shopId", shopId);
                mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
            }
        });

        mRecyclerView.addHeaderView(getHeaderView());
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getProductList();
            }
        });
    }



    private void refresh() {
        isRefresh = true;
        page = 1;
        getProductInfo();
        getProductList();
    }

    private ArrayList<Object> mListData = new ArrayList<Object>();
    private int page = 1;

    private void getProductList() {
        mShopAPI.getProductList(0, page, mShopId, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (isRefresh) {
                    mRecyclerView.refreshComplete();
                } else {
                    mRecyclerView.loadMoreComplete();
                }
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            if (isRefresh && mListData.size() > 0) {
                                mListData.clear();
                            }
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("productList")) {
                                String productList = jsonData.getString("productList");
                                ArrayList<SuperMarketEntity> superMarketEntities = JsonUtils.fromJson(productList, new TypeToken<ArrayList<SuperMarketEntity>>() {
                                }.getType());
                                if (superMarketEntities != null && superMarketEntities.size() > 0) {
                                    mListData.addAll(superMarketEntities);
                                    page++;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mListData.size() != 0) {
                        mRecyclerView.setNoMore(true);
                    }
                }
                mDataAdapter.notifyDataSetChanged();
            }
        });
    }


    // 记录首次按下位置
    private float mFirstPosition = 0;
    // 是否正在放大
    private Boolean mScaling = false;
    private Banner mBanner;
    /**
     * 存储RecyclerView滑动的距离
     */
    private int scroolY;

    private View getHeaderView() {
        View hearderView = getLayoutInflater().inflate(R.layout.layout_shop_detail_header, null);

        initHeaderViews(hearderView);
        LinearLayout evaluateLl = (LinearLayout) hearderView.findViewById(R.id.shop_evaluate_tuwen_img);
        getEvaluateView(evaluateLl);
        mBanner = (Banner) hearderView.findViewById(R.id.shop_detail_banner);
        hearderView.findViewById(R.id.shop_share).setOnClickListener(this);
        mTags = (TextView) hearderView.findViewById(R.id.tags);
        initBanner();
        // 设置图片初始大小 这里我设为满屏的16:9,根据自己需要调整
        final ViewGroup.LayoutParams lp = mBanner.getLayoutParams();
        lp.width = mShouNewApplication.terminalWidth;
        //        lp.height = mShouNewApplication.terminalWidth * 9 / 16;
        lp.height = mShouNewApplication.terminalWidth;
        mBanner.setLayoutParams(lp);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scroolY -= dy;
                if (Math.abs(scroolY) > mBanner.getHeight()) {
                    mShopDetailTitle.setAlpha(1);
                    mShopDetailTitle.setVisibility(View.VISIBLE);
                } else if (Math.abs(scroolY) > 1) {
                    mShopDetailTitle.setVisibility(View.VISIBLE);
                    float alph = Math.abs(scroolY) * 1.0f / mBanner.getHeight();
                    mShopDetailTitle.setAlpha(alph);
                } else {
                    mShopDetailTitle.setVisibility(View.INVISIBLE);
                }
            }
        });
        // 设置触摸的监听事件
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.LayoutParams lp = mBanner.getLayoutParams();
                switch (event.getAction()) {
                    //手指抬起时触发
                    case MotionEvent.ACTION_UP:
                        // 手指离开后恢复图片
                        mScaling = false;
                        replyImage();
                        break;
                    //手指移动时触发
                    case MotionEvent.ACTION_MOVE:
                        if (!mScaling) {
                            if (scroolY == 0) {
                                mFirstPosition = event.getY();// 滚动到顶部时记录位置，否则正常返回
                            } else {
                                break;
                            }
                        }
                        int distance = (int) ((event.getY() - mFirstPosition) * 0.6); // 滚动距离乘以一个系数
                        if (distance < 0) { // 如果当前位置比记录位置要小，正常返回
                            break;
                        }

                        // 处理放大的关键代码
                        mScaling = true;
                        lp.width = mShouNewApplication.terminalWidth + distance;
                        //                        lp.height = (mShouNewApplication.terminalWidth + distance) * 9 / 16;

                        lp.height = (mShouNewApplication.terminalWidth + distance);
                        mBanner.setLayoutParams(lp);
                        return true; // 返回true表示已经消费该事件
                }
                return false;
            }
        });

        return hearderView;
    }

    private void initHeaderViews(View hearderView) {
        mShopIntroTv = (TextView) hearderView.findViewById(R.id.shop_intro_tv);
        mShopDetailPricesTv = (TextView) hearderView.findViewById(R.id.shop_detail_prices_tv);
        mShopOldPrices = (TextView) hearderView.findViewById(R.id.shop_old_prices);
        mKuaidiMoney = (TextView) hearderView.findViewById(R.id.kuaidi_money);
        mShopDetailAddress = (TextView) hearderView.findViewById(R.id.shop_detail_address);
        mShopDetailImgParent = (LinearLayout) hearderView.findViewById(R.id.shop_detail_tuwen_img);
    }


    // 手指抬起图片回弹动画 (使用了属性动画)
    private void replyImage() {
        final ViewGroup.LayoutParams lp = mBanner.getLayoutParams();
        final float w = mBanner.getLayoutParams().width;// 图片当前宽度
        final float h = mBanner.getLayoutParams().height;// 图片当前高度
        final float newW = mShouNewApplication.terminalWidth;// 图片原宽度
        //        final float newH = mShouNewApplication.terminalWidth * 9 / 16;// 图片原高度
        final float newH = mShouNewApplication.terminalWidth;// 图片原高度
        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                lp.width = (int) (w - (w - newW) * cVal);
                lp.height = (int) (h - (h - newH) * cVal);
                mBanner.setLayoutParams(lp);
            }
        });
        //开启动画
        anim.start();

    }

    private void initBanner() {


        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //        mBanner.  setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader(R.drawable.square_seize));
        //设置图片集合
        //        mBanner.setImages(images);
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
        //        mBanner.setBannerTitles(data);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(false);
        //设置轮播时间
        mBanner.setDelayTime(3000);
        mBanner.setLimitPager();

        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        //        mBanner.start();
    }

    private void getEvaluateView(LinearLayout evaluateLl) {
        View layout_evalute = getLayoutInflater().inflate(R.layout.layout_evalute, null);

        visiable_evelautes = layout_evalute.findViewById(R.id.visiable_evelautes);
        evelute_count = (TextView) layout_evalute.findViewById(R.id.evelute_count);
        my_info_header_scv = (CustomShapeImageView) layout_evalute.findViewById(R.id.my_info_header_scv);
        nicheng_tv = (TextView) layout_evalute.findViewById(R.id.nicheng_tv);
        evelute_content = (TextView) layout_evalute.findViewById(R.id.evelute_content);
        wrapLayoutiml = (WordWrapLayoutiml) layout_evalute.findViewById(R.id.evelute_img);
        wrapLayoutiml.setShow(true);
        wrapLayoutiml.setWidth((int) (mShouNewApplication.terminalWidth *0.7));
        evelute_time = (TextView) layout_evalute.findViewById(R.id.evelute_time);
        findViewById(R.id.cart_menu).setOnClickListener(this);
        layout_evalute.findViewById(R.id.look_all).setOnClickListener(this);
        evaluateLl.addView(layout_evalute);
    }

    private void getProductInfo() {
        mShopAPI.getProductInfo(mShopId, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("product")) {
                                String product = jsonData.getString("product");
                                mSuperMarkeDetailEntity = JsonUtils.fromJson(product, SuperMarkeDetailEntity.class);
                                if (mSuperMarkeDetailEntity != null) {
                                    mBanner.setImages(mSuperMarkeDetailEntity.getPImg());
                                    if (mSuperMarkeDetailEntity.getPImg() != null) {
                                        int length = mSuperMarkeDetailEntity.getPImg().size();
                                        ArrayList<String> datatitle = new ArrayList<String>();
                                        for (int i = 0; i < length; i++) {
                                            datatitle.add("");
                                        }
                                        mBanner.setBannerTitles(datatitle);
                                    }
                                    mBanner.start();
                                    mShopIntroTv.setText(mSuperMarkeDetailEntity.getPTitle());
                                    mShopDetailPricesTv.setText(String.format("¥%s", StringUtil.formatMoney(mSuperMarkeDetailEntity.getPPrice())));


                                    mTags.setVisibility(View.GONE);
                                    if ("首牛".equals(mSuperMarkeDetailEntity.getpOwn())) {
                                        mTags.setVisibility(View.VISIBLE);
                                        mTags.setText(mSuperMarkeDetailEntity.getpOwn());
                                    }
                                    Spannable spanStrikethrough = new SpannableString(String.format("原价%s", StringUtil.formatMoney(mSuperMarkeDetailEntity.getPOldprice())));
                                    StrikethroughSpan stSpan = new StrikethroughSpan();  //设置删除线样式
                                    spanStrikethrough.setSpan(stSpan, 0, spanStrikethrough.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                                    mShopOldPrices.setText(spanStrikethrough);
                                    int mpColect = mSuperMarkeDetailEntity.getpCollect();
                                    isFavor.setText(mpColect == 0 ? "未收藏" : "已收藏");
                                    isFavor.setCompoundDrawablesWithIntrinsicBounds(0, mpColect == 0 ? R.drawable.collection_gary : R.drawable.collection_orange, 0, 0);
                                    mKuaidiMoney.setText(String.format("快递:%s元", StringUtil.formatMoney(mSuperMarkeDetailEntity.getPKdprice())));
                                    mShopDetailAddress.setText(mSuperMarkeDetailEntity.getPAddress());
                                    ArrayList<String> imgs = mSuperMarkeDetailEntity.getPImgs();

                                    if (imgs != null && imgs.size() > 0) {
                                        mShopDetailImgParent.removeAllViews();
                                        for (String img : imgs) {
                                            final ImageView imageView = new ImageView(ShopDetailActivity.this);
                                            ViewGroup.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mShouNewApplication.terminalWidth);
                                            imageView.setImageResource(R.drawable.square_seize);
                                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                            imageView.setLayoutParams(layoutParams);
                                            Glide.with(ShopDetailActivity.this).load(img).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    imageView.setImageBitmap(bitmap);
                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                    super.onLoadFailed(e, errorDrawable);
                                                    imageView.setImageDrawable(errorDrawable);
                                                }
                                            });
                                            mShopDetailImgParent.addView(imageView);
                                        }
                                    }
                                }
                            }
                            if (jsonData.has("total")) {
                                long total = jsonData.getLong("total");
                                evelute_count.setText(Html.fromHtml(String.format("<font color=#ff8a29>用户评价</font>(%s)", total > 999 ? "999+" : total)));
                                if (total <= 0) {
                                    visiable_evelautes.setVisibility(View.GONE);
                                } else {
                                    visiable_evelautes.setVisibility(View.VISIBLE);
                                }
                            }
                            if (jsonData.has("discuss")) {//评价
                                JSONObject discuss = jsonData.getJSONObject("discuss");
                                mShouNewApplication.loadImg(discuss.getString("dUicon"), my_info_header_scv);
                                nicheng_tv.setText(discuss.getString("dUname"));
                                String dimg = discuss.getString("dImg");
                                evelute_time.setText(discuss.getString("dDate"));
                                evelute_content.setText(discuss.getString("dText"));
                                if (!TextUtils.isEmpty(dimg)) {
                                    ArrayList<ImageView> imageViews;
                                    if (dimg.contains(",")) {
                                        String dims[] = dimg.split(",");
                                        int length = dims.length;
                                        imageViews = wrapLayoutiml.getImage(length);
                                        for (int i = 0; i < length; i++) {
                                            ImageView imageView = imageViews.get(i);
                                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            String imgUrl = dims[i];
                                            imageView.setTag(imgUrl);
                                            if (!TextUtils.isEmpty(imgUrl) && imgUrl.equals(imageView.getTag())) {
                                                mShouNewApplication.loadImg(imgUrl, imageView);
                                            }
                                            PreviewImgUtils.previewImg(imageView,dims,i,mShouNewApplication);
                                        }
                                    } else {
                                        imageViews = wrapLayoutiml.getImage(1);
                                        ImageView imageView = imageViews.get(0);
                                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        imageView.setTag(dimg);
                                        if (!TextUtils.isEmpty(dimg) && dimg.equals(imageView.getTag())) {
                                            mShouNewApplication.loadImg(dimg, imageView);
                                        }
                                        PreviewImgUtils.previewImg(imageView,new String[]{dimg},0,mShouNewApplication);
                                    }

                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //                    if (isClickItem) {
                    //                        isClickItem = false;
                    //                        moveToPosition(0);
                    //                    }
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cart_menu:
                mShouNewApplication.redirect(ShoppingCartActivity.class);
                break;
            case R.id.look_all:
                Bundle bundle = new Bundle();
                bundle.putParcelable("shop", mSuperMarkeDetailEntity);
                mShouNewApplication.redirectAndPrameter(AllEvalueteActivity.class, bundle);
                break;
            case R.id.add_shop://添加购物车
                if (mSuperMarkeDetailEntity != null) {
                    new ShopSelectDialog(this, mSuperMarkeDetailEntity.getPSimg(), mSuperMarkeDetailEntity.getpAllprice(), mSuperMarkeDetailEntity.getPColor()).setDialogLisener(new ShopSelectDialog.DialogLisener() {
                        @Override
                        public void sure(String color, int number, double prices) {
                            ShopCarEntity shopCarEntity = new ShopCarEntity();
                            shopCarEntity.setShColor(color);
                            shopCarEntity.setShMpid(0);
                            shopCarEntity.setShNum(number);
                            shopCarEntity.setShPid(Integer.parseInt(mSuperMarkeDetailEntity.getPId()));
                            shopCarEntity.setShPrice(prices * number);
                            shopCarEntity.setSinglePrice(prices);
                            shopCarEntity.setShDate(TimeUtil.getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
                            shopCarEntity.setShSimg(mSuperMarkeDetailEntity.getPSimg());
                            shopCarEntity.setShTitle(mSuperMarkeDetailEntity.getPTitle());
                            shopCarEntity.setShKdprice(mSuperMarkeDetailEntity.getPKdprice());

                            if (!Preference.getBoolean(mShouNewApplication, Preference.IS_LOGIN, false)) {
                                DatabaseUtils.insert(ShopDetailActivity.this, shopCarEntity);
                                ToastUtil.showToast("添加成功");
                            } else {
                                updateShopCar(shopCarEntity);
                            }
                        }
                    }).setCancelable(true).show();
                }
                break;
            case R.id.is_favor://收藏
                if (mSuperMarkeDetailEntity != null) {
                    collection();
                }
                break;
            case R.id.shop_share:
                new ShareDialog(this, mShouNewApplication).setCancelable(true).show();
                break;
            case R.id.back_shop_detail:
                finish();
                break;
            case R.id.more_menu:
                new ShopPopwindow(this,mShouNewApplication).showPopupWindow(mMoreMenu, (int) (mMoreMenu.getWidth() / 2 * 0.1));
                break;
        }
    }


    private boolean move;

    private class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的100米！）
            if (move) {
                move = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                int n = -mLinearLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < mRecyclerView.getChildCount()) {
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int top = mRecyclerView.getChildAt(n).getTop();
                    //最后的移动
                    mRecyclerView.scrollBy(0, top);
                }
            }
        }
    }

    /**
     * 移动到指定位置
     *
     * @param n
     */
    private void moveToPosition(int n) {
        scroolY = 0;
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            mRecyclerView.scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }

    }


    private void collection() {

        if (!Preference.getBoolean(ShopDetailActivity.this, Preference.IS_LOGIN, false)) {
            mShouNewApplication.jumpLoginActivity(ShopDetailActivity.this);
            return;
        }
        //  cannelCollection  取消
        //collect  收藏
        String collect = mSuperMarkeDetailEntity.getpCollect() == 0 ? "collect" : "cannelCollection";
        String shopId = mSuperMarkeDetailEntity.getPId();
        mShopAPI.collection(1, shopId, collect, mShouNewApplication.new ShouNewHttpCallBackLisener() {

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    int mpColect = mSuperMarkeDetailEntity.getpCollect();
                    if (mpColect == 0) {
                        ToastUtil.showToast("已收藏");
                    } else {
                        ToastUtil.showToast("已取消");
                    }
                    mSuperMarkeDetailEntity.setpCollect(mpColect != 0 ? 0 : 1);
                    isFavor.setText(mpColect != 0 ? "未收藏" : "已收藏");
                    isFavor.setCompoundDrawablesWithIntrinsicBounds(0, mpColect != 0 ? R.drawable.collection_gary : R.drawable.collection_orange, 0, 0);
                } else {
                    ToastUtil.showToast("操作失败，请重试");
                }
            }
        });
    }

    /**
     * 加入购物车
     *
     * @param shopCarEntities
     */
    private void updateShopCar(ShopCarEntity shopCarEntities) {
        if (shopCarEntities != null) {
            try {
                ArrayList<ShopCarEntity> carEntities = new ArrayList<ShopCarEntity>();
                carEntities.add(shopCarEntities);
                mShopAPI.updateShopCar(JsonUtils.toJson(carEntities), mShouNewApplication.new ShouNewHttpCallBackLisener() {
                    @Override
                    protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
//                        mShouNewApplication.redirect(ShoppingCartActivity.class);
                        if(exception==null){
                            ToastUtil.showToast("添加成功");
                        }else {
                            ToastUtil.showToast("添加失败");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
