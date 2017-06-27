package com.shownew.home.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.shownew.home.activity.OrderMenuActivity;
import com.shownew.home.module.entity.OderMenuEntity;
import com.wp.baselib.utils.StringUtil;

import java.util.ArrayList;



/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/21 0021
 */

public class OderMenuAdapter extends RecyclerView.Adapter<OderMenuAdapter.OrderMenuViewHolder> {
    private ArrayList<OderMenuEntity> data;
    private OrderMenuActivity mMenuActivity;

    public OderMenuAdapter(OrderMenuActivity mMenuActivity, ArrayList<OderMenuEntity> data) {
        this.data = data;
        this.mMenuActivity = mMenuActivity;
    }

    @Override
    public OrderMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_oder_meny_item, null);
        return new OrderMenuViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final OrderMenuViewHolder holder, final int position) {
        final OderMenuEntity menuEntity = data.get(position);
        if (menuEntity == null)
            return;
        holder.order_delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuActivity.deleteOderMenu(menuEntity.getOId());
            }
        });

         String url = menuEntity.getOSimg();
        holder.my_car_header_iv.setTag(url);
        if (!TextUtils.isEmpty(url) && url.equals(holder.my_car_header_iv.getTag())) {
            Glide.with(mMenuActivity).load(url).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                    holder.my_car_header_iv.setImageBitmap(resource);
                }
            });
        }

        holder.ordermenu_name.setText(menuEntity.getOTitle());
        holder.order_time.setText(menuEntity.getODate());
        holder.my_car_count.setText(Html.fromHtml(String.format("<font color=#828894>数量：</font>  x%s", menuEntity.getONum())));
        int state = menuEntity.getOState();
        holder.order_delete_iv.setVisibility(View.INVISIBLE);
        if (state == 0) {
            holder.order_delete_iv.setVisibility(View.VISIBLE);
        }
        //（state：0=未支付    1=已支付    2=已发货）
        holder.my_car_state.setText(Html.fromHtml(String.format("¥%s  <font color=#52acff>(%s)</font>", StringUtil.formatMoney(menuEntity.getOTotalprice()), state == 0 ? "未支付" : state == 1 ? "已支付" : "已发货")));
        holder.oder_menu_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOderMenuItemClickLisener != null) {
                    mOderMenuItemClickLisener.clickItem(menuEntity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class OrderMenuViewHolder extends RecyclerView.ViewHolder {
        ImageView order_delete_iv;
        ImageView my_car_header_iv;
        TextView ordermenu_name;
        TextView my_car_count;
        TextView my_car_state;
        TextView order_time;
        View oder_menu_parent;

        public OrderMenuViewHolder(View itemView) {
            super(itemView);
            order_delete_iv = (ImageView) itemView.findViewById(R.id.order_delete_iv);
            my_car_header_iv = (ImageView) itemView.findViewById(R.id.my_car_header_iv);
            ordermenu_name = (TextView) itemView.findViewById(R.id.ordermenu_name);
            my_car_count = (TextView) itemView.findViewById(R.id.my_car_count);
            my_car_state = (TextView) itemView.findViewById(R.id.my_car_state);
            order_time = (TextView) itemView.findViewById(R.id.order_time);
            oder_menu_parent = itemView.findViewById(R.id.oder_menu_parent);
        }
    }

    private OderMenuItemClickLisener mOderMenuItemClickLisener;

    public void setOderMenuItemClickLisener(OderMenuItemClickLisener oderMenuItemClickLisener) {
        mOderMenuItemClickLisener = oderMenuItemClickLisener;
    }

    public interface OderMenuItemClickLisener {
        void clickItem(OderMenuEntity orderNo);
    }
}
