package com.shownew.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.shownew.home.module.entity.ShopMaillTypeEntity;

import java.util.ArrayList;


/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/14 0014
 */

public class ShopHeaderAdapter extends RecyclerView.Adapter<ShopHeaderAdapter.HeaderRecylerViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<ShopMaillTypeEntity> mMaillTypeEntities;
    private Context mContext;

    public ShopHeaderAdapter(Context context, ArrayList<ShopMaillTypeEntity> mMaillTypeEntities) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mMaillTypeEntities = mMaillTypeEntities;
    }

    @Override
    public HeaderRecylerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View converView = inflater.inflate(R.layout.layout_shop_header_item, parent, false);
        return new HeaderRecylerViewHolder(converView);
    }

    @Override
    public void onBindViewHolder(final HeaderRecylerViewHolder holder, final int position) {
        if (mMaillTypeEntities != null && mMaillTypeEntities.size() > 0) {
            final ShopMaillTypeEntity shopMaillTypeEntity = mMaillTypeEntities.get(position);
            final String url = String.format("%s1", shopMaillTypeEntity.getMtImg());
            holder.header_img.setTag(url);
            if (!TextUtils.isEmpty(url) && url.equals(holder.header_img.getTag()))
                Glide.with(mContext).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.header_img.setImageBitmap(resource);
                        holder.circleIv.setText(shopMaillTypeEntity.getMtName());
                        ((View) holder.circleIv.getParent()).setOnClickListener(new View.OnClickListener() {


                            @Override
                            public void onClick(View v) {
                                if (null != mShopHeaderClickLisener) {
                                    mShopHeaderClickLisener.click(shopMaillTypeEntity);
                                }
                            }
                        });
                    }
                });
        }

    }

    @Override

    public int getItemCount() {
        return mMaillTypeEntities.size();
    }

    class HeaderRecylerViewHolder extends RecyclerView.ViewHolder {
        TextView circleIv;
        ImageView header_img;

        public HeaderRecylerViewHolder(View itemView) {
            super(itemView);
            circleIv = (TextView) itemView.findViewById(R.id.header_icon);
            header_img = (ImageView) itemView.findViewById(R.id.header_img);
        }
    }

    private ShopHeaderClickLisener mShopHeaderClickLisener;

    public void setShopHeaderClickLisener(ShopHeaderClickLisener shopHeaderClickLisener) {
        mShopHeaderClickLisener = shopHeaderClickLisener;
    }

    public interface ShopHeaderClickLisener {
        void click(ShopMaillTypeEntity shopMaillTypeEntity);
    }
}
