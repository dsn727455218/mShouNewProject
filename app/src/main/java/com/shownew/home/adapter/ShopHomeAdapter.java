package com.shownew.home.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.entity.ShopMallListEntity;
import com.shownew.home.module.entity.SuperMarketEntity;
import com.wp.baselib.utils.StringUtil;

import java.util.ArrayList;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/28 0028
 */

public class ShopHomeAdapter extends RecyclerView.Adapter<ShopHomeAdapter.ShouHomeViewHolder> {
    private ArrayList<Object> objects;
    private final static String HTML = "<small>¥</small>%s";
    private ShouNewApplication mShouNewApplication;

    public ShopHomeAdapter(ShouNewApplication mShouNewApplication, ArrayList<Object> mSuperMarketEntityArrayList) {
        this.objects = mSuperMarketEntityArrayList;
        this.mShouNewApplication = mShouNewApplication;
    }

    @Override
    public ShouHomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_shop_home_item, parent, false);
        return new ShouHomeViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final ShouHomeViewHolder holder, int position) {
        Object object = objects.get(position);
        if (object instanceof SuperMarketEntity) {
            SuperMarketEntity superMarketEntity = (SuperMarketEntity) object;
            setSuperMarketData(holder, superMarketEntity);
        } else if (object instanceof ShopMallListEntity) {
            ShopMallListEntity superMarketEntity = (ShopMallListEntity) object;
            setShopMallData(holder, superMarketEntity);
        }

    }

    private void setShopMallData(final ShouHomeViewHolder holder, final ShopMallListEntity superMarketEntity) {
        if (superMarketEntity == null)
            return;
        holder.shopPrices.setText(Html.fromHtml(String.format(HTML, StringUtil.formatMoney(superMarketEntity.getMpPrice()))));
        holder.shop_title.setText(superMarketEntity.getMpTitle());
        Spannable spanStrikethrough = new SpannableString(String.format("原价%s", StringUtil.formatMoney(superMarketEntity.getMpOldprice())));
        StrikethroughSpan stSpan = new StrikethroughSpan();  //设置删除线样式
        spanStrikethrough.setSpan(stSpan, 0, spanStrikethrough.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.shop_olde_prices.setText(spanStrikethrough);
        holder.home_item_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShopHomeLisener != null) {
                    mShopHomeLisener.clickShopItem(superMarketEntity.getMpId());
                }
            }
        });


        String url = superMarketEntity.getMpSimg();
        holder.shop_img.setTag(url);
        if (!TextUtils.isEmpty(url) && url.equals(holder.shop_img.getTag())) {
            Glide.with(mShouNewApplication).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.shop_img.setImageBitmap(resource);
                }
            });
        }

    }

    private void setSuperMarketData(final ShouHomeViewHolder holder, final SuperMarketEntity superMarketEntity) {
        if (superMarketEntity == null)
            return;
        holder.shopPrices.setText(Html.fromHtml(String.format(HTML, StringUtil.formatMoney(superMarketEntity.getpPrice()))));
        holder.shop_title.setText(superMarketEntity.getpTitle());
        Spannable spanStrikethrough = new SpannableString(String.format("原价%s", StringUtil.formatMoney(superMarketEntity.getpOldprice())));
        StrikethroughSpan stSpan = new StrikethroughSpan();  //设置删除线样式
        spanStrikethrough.setSpan(stSpan, 0, spanStrikethrough.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.shop_olde_prices.setText(spanStrikethrough);
        holder.home_item_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShopHomeLisener != null) {
                    mShopHomeLisener.clickShopItem(superMarketEntity.getpId());
                }
            }
        });
        holder.tags.setVisibility(View.GONE);
        if ("首牛".equals(superMarketEntity.getpOwn())) {
            holder.tags.setVisibility(View.VISIBLE);
            holder.tags.setText(superMarketEntity.getpOwn());
        }
         String url = superMarketEntity.getpSimg();
        holder.shop_img.setTag(url);
        if (!TextUtils.isEmpty(url) && url.equals(holder.shop_img.getTag())) {
            Glide.with(mShouNewApplication).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.shop_img.setImageBitmap(resource);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    class ShouHomeViewHolder extends RecyclerView.ViewHolder {
        TextView shopPrices;
        TextView shop_olde_prices;
        View home_item_parent;
        ImageView shop_img;
        TextView shop_title;
        TextView tags;

        public ShouHomeViewHolder(View itemView) {
            super(itemView);
            shopPrices = (TextView) itemView.findViewById(R.id.shop_prices);
            shop_olde_prices = (TextView) itemView.findViewById(R.id.shop_olde_prices);
            home_item_parent = itemView.findViewById(R.id.home_item_parent);
            shop_img = (ImageView) itemView.findViewById(R.id.shop_img);
            shop_title = (TextView) itemView.findViewById(R.id.shop_title);
            tags = (TextView) itemView.findViewById(R.id.tags);
        }
    }

    private ShopHomeLisener mShopHomeLisener;

    public void setShopHomeLisener(ShopHomeLisener shopHomeLisener) {
        mShopHomeLisener = shopHomeLisener;
    }

    public interface ShopHomeLisener {
        void clickShopItem(String shopId);
    }
}
