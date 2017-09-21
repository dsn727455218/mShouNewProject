package com.shownew.home.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.activity.shopcommon.SureOderMenuShopCarActivity;
import com.shownew.home.module.dao.ShopCarEntityIml;
import com.wp.baselib.BaseObjectAdapter;
import com.wp.baselib.utils.StringUtil;

import java.util.ArrayList;


/**
 * Created by WP on 2017/8/14.
 */

public class OrderMenuListAdapter extends BaseObjectAdapter {
    ArrayList<ShopCarEntityIml> carEntityImls;
    private SureOderMenuShopCarActivity sureOderMenuShopCarActivity;

    public OrderMenuListAdapter(SureOderMenuShopCarActivity context, ArrayList<ShopCarEntityIml> datas) {
        super(context, datas);
        this.sureOderMenuShopCarActivity = context;
        this.carEntityImls = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_common_odermenu, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.setValues(carEntityImls.get(position), position);
        return convertView;
    }


    class ViewHolder extends BaseViewHolder<ShopCarEntityIml> {
        ImageView mShop_img;
        TextView mShop_title;
        TextView mShop_color;
        TextView mShop_prices;
        TextView mShop_number;
        TextView mKuaidi_fee;
        TextView mCount_number;
        TextView mTotal_prices;
        TextView mTotal_tv;
        EditText mKuaidi_fee_ed;
        View reduceNumber;
        View addNumber;

        /**
         * 装载视图并初始化视图内组件
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mTotal_tv = (TextView) view.findViewById(R.id.total_tv);
            mShop_img = (ImageView) view.findViewById(R.id.shop_img);
            mShop_title = (TextView) view.findViewById(R.id.shop_title);
            mKuaidi_fee_ed = (EditText) view.findViewById(R.id.kuaidi_fee_ed);
            mShop_color = (TextView) view.findViewById(R.id.shop_color);
            mShop_prices = (TextView) view.findViewById(R.id.shop_prices);
            mShop_number = (TextView) view.findViewById(R.id.shop_number);
            mKuaidi_fee = (TextView) view.findViewById(R.id.kuaidi_fee);
            mCount_number = (TextView) view.findViewById(R.id.count_number);
            mTotal_prices = (TextView) view.findViewById(R.id.total_prices);
            reduceNumber = view.findViewById(R.id.reduce_number);
            addNumber = view.findViewById(R.id.add_number);
        }

        @Override
        public void setValues(final ShopCarEntityIml shopCarEntityIml, int position) {
            if (shopCarEntityIml != null) {
                reduceNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int num = shopCarEntityIml.getShNum();
                        num = Math.max(--num, 1);
                        shopCarEntityIml.setShNum(num);
                        shopCarEntityIml.setShPrice(num * shopCarEntityIml.getSinglePrice());
                        mShop_number.setText(String.valueOf(shopCarEntityIml.getShNum()));
                        callBackData();
                    }
                });
                addNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int num = shopCarEntityIml.getShNum();
                        num = Math.max(++num, 1);
                        shopCarEntityIml.setShNum(num);
                        shopCarEntityIml.setShPrice(num * shopCarEntityIml.getSinglePrice());
                        mShop_number.setText(String.valueOf(shopCarEntityIml.getShNum()));
                        callBackData();
                    }
                });
                mKuaidi_fee.setText(String.format("快递：%s元", StringUtil.formatMoney(shopCarEntityIml.getShKdprice())));
                mShop_prices.setText(String.format("¥%s", StringUtil.formatMoney(shopCarEntityIml.getSinglePrice())));
                mShop_title.setText(shopCarEntityIml.getShTitle());
                mShop_number.setText(String.valueOf(shopCarEntityIml.getShNum()));
                String url = shopCarEntityIml.getShSimg();
                mShop_img.setTag(url);
                if (!TextUtils.isEmpty(url) && url.equals(mShop_img.getTag())) {
                    Glide.with(mContext).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            mShop_img.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            mShop_img.setImageDrawable(errorDrawable);
                        }
                    });
                }
                mKuaidi_fee_ed.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s)) {
                            shopCarEntityIml.setoNote(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
    }


    private void callBackData() {
        if (carEntityImls != null) {
            sureOderMenuShopCarActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int shopCount = 0;
                    double shopPrices = 0;
                    for (ShopCarEntityIml shopCarEntityIml : carEntityImls) {
                        shopCount += shopCarEntityIml.getShNum();
                        shopPrices += shopCarEntityIml.getShKdprice();
                        shopPrices += shopCarEntityIml.getShPrice();
                    }
                    sureOderMenuShopCarActivity.callBackData(shopCount, shopPrices);
                }
            });
        }
    }
}
