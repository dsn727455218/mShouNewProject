package com.shownew.home.adapter;

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
import com.shownew.home.activity.shopcommon.ShopCollectActivity;
import com.shownew.home.module.entity.CollectShopEntity;
import com.wp.baselib.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by WP on 2017/7/28.
 */

public class CollectShopAdapter extends RecyclerView.Adapter<CollectShopAdapter.CollectShopHolder> {
    private ArrayList<CollectShopEntity> collectShopEntities;
    private ShopCollectActivity shopCollectActivity;

    public CollectShopAdapter(ArrayList<CollectShopEntity> collectShopEntities, ShopCollectActivity shopCollectActivity) {
        this.collectShopEntities = collectShopEntities;
        this.shopCollectActivity = shopCollectActivity;
    }

    @Override
    public CollectShopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CollectShopHolder(LayoutInflater.from(shopCollectActivity).inflate(R.layout.layout_list_search, parent, false));
    }

    @Override
    public void onBindViewHolder(final CollectShopHolder holder, int position) {
        final CollectShopEntity collectShopEntity = collectShopEntities.get(position);
        if (collectShopEntity == null)
            return;
        holder.search_detail.setText(collectShopEntity.getCoTitle());
        holder.price.setText(String.format("¥%s", StringUtil.formatMoney(collectShopEntity.getCoPrice())));
        holder.old_prices.setVisibility(View.GONE);

        String url = collectShopEntity.getCoSimg();
        holder.search_img.setTag(url);
        if (!TextUtils.isEmpty(url) && url.equals(holder.search_img.getTag())) {
            Glide.with(shopCollectActivity).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.search_img.setImageBitmap(resource);
                }
            });
        }
        holder.shape_search_list_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopCollectActivity.enterShopDetail(collectShopEntity);
            }
        });
        holder.shape_search_list_rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.collect_parentll.setVisibility(View.VISIBLE);
                return true;
            }
        });
        holder.delete_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shopCollectActivity.collection(collectShopEntity, holder.collect_parentll);
            }
        });
        holder.cancel_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.collect_parentll.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectShopEntities.size();
    }

    class CollectShopHolder extends RecyclerView.ViewHolder {
        ImageView search_img;
        TextView search_detail;
        TextView price;
        TextView old_prices;
        View collect_parentll;

        TextView delete_collect;
        TextView cancel_collect;
        View shape_search_list_rl;

        public CollectShopHolder(View itemView) {
            super(itemView);
            search_detail = (TextView) itemView.findViewById(R.id.search_detail);
            search_img = (ImageView) itemView.findViewById(R.id.search_img);
            price = (TextView) itemView.findViewById(R.id.price);
            old_prices = (TextView) itemView.findViewById(R.id.old_prices);
            delete_collect = (TextView) itemView.findViewById(R.id.delete_collect);
            cancel_collect = (TextView) itemView.findViewById(R.id.cancel_collect);
            collect_parentll = itemView.findViewById(R.id.collect_parentll);
            shape_search_list_rl = itemView.findViewById(R.id.shape_search_list_rl);
        }
    }
}
