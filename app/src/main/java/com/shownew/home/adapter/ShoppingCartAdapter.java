package com.shownew.home.adapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.activity.shopcommon.ShoppingCartActivity;
import com.shownew.home.activity.shouniushop.ShopMallDetailActivity;
import com.shownew.home.module.dao.ShopCarEntity;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.widget.CustomShapeImageView;

import java.util.ArrayList;


/**
 * Created by WP on 2017/8/9.
 */

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<ShopCarEntity> shopCarEntities;
    private ShoppingCartActivity context;
    private ShouNewApplication shouNewApplication;
    private CarOnClickListener carOnClickListener;

    public ShoppingCartAdapter(ShoppingCartActivity context, ArrayList<ShopCarEntity> shopCarEntities) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.shopCarEntities = shopCarEntities;
        shouNewApplication = ShouNewApplication.getInstance();
    }

    @Override
    public ShoppingCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShoppingCartViewHolder(inflater.inflate(R.layout.layout_shopping_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(final ShoppingCartViewHolder holder, final int position) {
        final ShopCarEntity shopCarEntity = shopCarEntities.get(position);

        if (shopCarEntity == null)
            return;
        carOnClickListener = new CarOnClickListener(shopCarEntity, position, holder);
        shopCarEntity.setSinglePrice(shopCarEntity.getShPrice() / shopCarEntity.getShNum());
        holder.cart_title.setText(shopCarEntity.getShTitle());
        holder.cart_color.setText(String.format("%s  X%s", shopCarEntity.getShColor(), shopCarEntity.getShNum()));
        holder.cart_prices.setText(String.format("¥%s", StringUtil.formatMoney(shopCarEntity.getShPrice())));
        holder.select_item.setImageResource(shopCarEntity.isSelect() ? R.drawable.selected_cart : R.drawable.not_selected_cart);
        String url = shopCarEntity.getShSimg();
        holder.shoping_img.setTag(url);
        if (!TextUtils.isEmpty(url) && url.equals(holder.shoping_img.getTag())) {
            Glide.with(context).load(url).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.shoping_img.setImageBitmap(resource);
                }
            });
        }
        holder.select_item.setOnClickListener(carOnClickListener);

        holder.cart_edit.setOnClickListener(carOnClickListener);
        isEdit();
        holder.cart_edit_ll.setVisibility(View.GONE);
        holder.show_info_ll.setVisibility(View.VISIBLE);
        if (shopCarEntity.isEdit()) {
            holder.cart_edit_ll.setVisibility(View.VISIBLE);
            holder.show_info_ll.setVisibility(View.GONE);
            holder.cart_parent.setOnClickListener(null);
        } else {
            holder.cart_parent.setOnClickListener(carOnClickListener);
        }
        holder.cart_delete.setOnClickListener(carOnClickListener);
        holder.cart_complete.setOnClickListener(carOnClickListener);
        holder.show_shop_count.setText(String.valueOf(shopCarEntity.getShNum()));
        holder.add_shop.setOnClickListener(carOnClickListener);
        holder.redcuce_shop.setOnClickListener(carOnClickListener);
    }


    private class CarOnClickListener implements View.OnClickListener {
        private ShopCarEntity shopCarEntity;
        private int position;
        private ShoppingCartViewHolder holder;

        public CarOnClickListener(ShopCarEntity shopCarEntity, int position, ShoppingCartViewHolder holder) {
            this.shopCarEntity = shopCarEntity;
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            int num;
            switch (v.getId()) {
                case R.id.cart_edit:
                    shopCarEntity.setEdit(true);
                    notifyDataSetChanged();
                    break;
                case R.id.cart_parent:
                    Bundle bundle = new Bundle();
                    if (shopCarEntity.getShMpid() == 0) {
                        bundle.putString("shopId", String.valueOf(shopCarEntity.getShPid()));
                        shouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
                    } else {
                        bundle.putString("shopId", String.valueOf(shopCarEntity.getShMpid()));
                        shouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
                    }
                    break;
                case R.id.redcuce_shop:
                    num = shopCarEntity.getShNum();
                    num = Math.max(--num, 1);
                    shopCarEntity.setShNum(num);
                    shopCarEntity.setShPrice(num * shopCarEntity.getSinglePrice());
                    holder.show_shop_count.setText(String.valueOf(shopCarEntity.getShNum()));
                    holder.cart_color.setText(String.format("%s  X%s", shopCarEntity.getShColor(), shopCarEntity.getShNum()));
                    callBackData();
                    break;
                case R.id.add_shop:
                    num = shopCarEntity.getShNum();
                    num = Math.max(++num, 1);
                    shopCarEntity.setShNum(num);
                    shopCarEntity.setShPrice(num * shopCarEntity.getSinglePrice());
                    holder.show_shop_count.setText(String.valueOf(shopCarEntity.getShNum()));
                    holder.cart_color.setText(String.format("%s  X%s", shopCarEntity.getShColor(), shopCarEntity.getShNum()));
                    callBackData();
                    break;
                case R.id.cart_complete:
                    shopCarEntity.setEdit(false);
                    context.saveShopCar(shopCarEntity);
                    notifyDataSetChanged();
                    break;
                case R.id.cart_delete:
                    context.deleteShopCar(shopCarEntities, position);
                    callBackData();
                    break;
                case R.id.select_item:
                    shopCarEntity.setSelect(!shopCarEntity.isSelect());
                    isAllSelect();
                    callBackData();
                    notifyDataSetChanged();
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return shopCarEntities.size();
    }

    class ShoppingCartViewHolder extends RecyclerView.ViewHolder {

        CustomShapeImageView shoping_img;
        ImageView select_item;
        TextView cart_title;
        TextView cart_color;
        TextView cart_prices;
        TextView cart_edit;
        TextView show_shop_count;
        View show_info_ll;
        View cart_edit_ll;
        ImageView add_shop;
        ImageView redcuce_shop;
        TextView cart_delete;
        View cart_parent;
        TextView cart_complete;

        public ShoppingCartViewHolder(View itemView) {
            super(itemView);
            add_shop = (ImageView) itemView.findViewById(R.id.add_shop);
            redcuce_shop = (ImageView) itemView.findViewById(R.id.redcuce_shop);
            select_item = (ImageView) itemView.findViewById(R.id.select_item);
            shoping_img = (CustomShapeImageView) itemView.findViewById(R.id.shoping_img);
            cart_title = (TextView) itemView.findViewById(R.id.cart_title);
            cart_color = (TextView) itemView.findViewById(R.id.cart_color);
            cart_prices = (TextView) itemView.findViewById(R.id.cart_prices);
            cart_edit = (TextView) itemView.findViewById(R.id.cart_edit);
            show_shop_count = (TextView) itemView.findViewById(R.id.show_shop_count);
            cart_complete = (TextView) itemView.findViewById(R.id.cart_complete);
            cart_delete = (TextView) itemView.findViewById(R.id.cart_delete);
            cart_edit_ll = itemView.findViewById(R.id.cart_edit_ll);
            show_info_ll = itemView.findViewById(R.id.show_info_ll);
            cart_parent = itemView.findViewById(R.id.cart_parent);
        }

    }

    public void isEdit() {
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            if (shopCarEntities.get(i).isEdit()) {//取消全选
                context.isEdit(true);
                return;
            } else if (i == size - 1) {
                context.isEdit(false);
            }
        }
    }

    public void isAllSelect() {
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            if (!shopCarEntities.get(i).isSelect()) {//取消全选
                context.isSelect(false);
                return;
            }
            if (i == size - 1) {
                if (shopCarEntities.get(i).isSelect()) {//全选
                    context.isSelect(true);

                    return;
                }
            }
        }

    }

    private void callBackData() {
        if (shopCarEntities != null)

        {
            int shopCount = 0;
            double shopPrices = 0;
            for (ShopCarEntity shopCarEntityIml : shopCarEntities) {
                if (shopCarEntityIml.isSelect()) {
                    shopCount += shopCarEntityIml.getShNum();
//                    shopPrices += shopCarEntityIml.getShKdprice();
                    shopPrices += shopCarEntityIml.getShPrice();
                }
            }
            context.callBackData(shopCount, shopPrices);
        }


    }

}
