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
        View convertView = LayoutInflater.from(mMenuActivity).inflate(R.layout.layout_my_order_menu, parent, false);
        return new OrderMenuViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final OrderMenuViewHolder holder, final int position) {
        final OderMenuEntity menuEntity = data.get(position);
        if (menuEntity == null)
            return;
//        holder.order_delete_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMenuActivity.deleteOderMenu(menuEntity.getOId());
//            }
//        });

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
//        holder.order_time.setText((0 == menuEntity.getOPid() ? "首牛商城" : "车配超市"));
        holder.order_time.setText(menuEntity.getODate());
        holder.my_car_count.setText(String.format("数量：X%s", menuEntity.getONum()));
        final int state = menuEntity.getOState();
        holder.agin_pay.setVisibility(View.GONE);
        holder.handle_event.setVisibility(View.VISIBLE);
//oState：0=未支付 1=待发货 2=已发货  3-已签收  4-已完成  5-已关闭
        switch (state) {
            case 0:
                holder.handle_event.setText("取消订单");
                holder.order_delete_iv.setText("未支付");
                holder.agin_pay.setVisibility(View.VISIBLE);
                holder.agin_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMenuActivity.againPayOderMenu(menuEntity);
                    }
                });
                break;
            case 1:
                if(menuEntity.getoIsbatch()==1){
                    holder.handle_event.setVisibility(View.GONE);
                }
                holder.handle_event.setText("取消订单");
                holder.order_delete_iv.setText("待发货");
                break;
            case 2:
                holder.handle_event.setText("确认收货");
                holder.order_delete_iv.setText("已发货");
                break;
            case 3:
                holder.handle_event.setText("确认收货");
                holder.order_delete_iv.setText("已签收");
                break;
            case 4:
                holder.order_delete_iv.setText("已完成");
                if (menuEntity.getoIsdiscuss() == 0) {
                    holder.handle_event.setText("商品评论");
                } else if (menuEntity.getoIsdiscuss() == 1) {
                    holder.handle_event.setText("追加评论");
                } else if (menuEntity.getoIsdiscuss() == 2) {
                    holder.handle_event.setVisibility(View.GONE);
                }
                break;
            case 5:
                holder.order_delete_iv.setText("已关闭");
                holder.handle_event.setText("删除订单");
                break;
        }



        holder.handle_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state) {
                    case 0:
                    case 1:
                        //取消订单
                        mMenuActivity.cancelOderMenu(menuEntity.getOId());
                        break;
                    case 5:
                        //删除订单
                        mMenuActivity.deleteOderMenu(menuEntity.getOId());
                        break;
                    case 2:
                    case 3:
                        mMenuActivity.confirmReceived(menuEntity.getOId());
                        //确认收货
                        break;
                    case 4:
                        //评论
                        mMenuActivity.shopTalk(menuEntity);
//                        if (menuEntity.getoIsdiscuss() == 0) {
//                            mMenuActivity.shopTalk(menuEntity);
//                        } else {
//
//                        }
                        break;
                }
            }
        });

        holder.my_car_state.setText(String.format("支付：¥%s", StringUtil.formatMoney(menuEntity.getOTotalprice())));
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
        TextView order_delete_iv;
        ImageView my_car_header_iv;
        TextView ordermenu_name;
        TextView my_car_count;
        TextView my_car_state;
        TextView order_time;
        View oder_menu_parent;
        TextView handle_event;
        View agin_pay;

        public OrderMenuViewHolder(View itemView) {
            super(itemView);
            order_delete_iv = (TextView) itemView.findViewById(R.id.order_delete_iv);
            my_car_header_iv = (ImageView) itemView.findViewById(R.id.my_car_header_iv);
            ordermenu_name = (TextView) itemView.findViewById(R.id.ordermenu_name);
            my_car_count = (TextView) itemView.findViewById(R.id.my_car_count);
            my_car_state = (TextView) itemView.findViewById(R.id.my_car_state);
            order_time = (TextView) itemView.findViewById(R.id.order_time);
            oder_menu_parent = itemView.findViewById(R.id.oder_menu_parent);
            handle_event = (TextView) itemView.findViewById(R.id.handle_event);
            agin_pay = itemView.findViewById(R.id.agin_pay);
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
