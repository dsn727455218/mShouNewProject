package com.shownew.home.adapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.map.DotMapActivity;
import com.shownew.home.module.entity.AfterMarkerEntity;
import com.shownew.home.module.entity.InsuranceEntity;
import com.shownew.home.utils.LocalUtils;

import java.util.ArrayList;

/**
 * 售后网点的适配器
 *
 * @author Jason
 * @version 1.0
 * @date 2017/4/21 0021
 */

public class AfterMarketOutletsAdapter extends RecyclerView.Adapter<AfterMarketOutletsAdapter.AfterMarketOutletsViewHolder> {

    private ArrayList<Object> data;
    private static final String HTML = "%s<br/><font color=0x595e66><small>地址：%s</small></font>";
    private ShouNewApplication mContext;
    private LatLng latLng1;

    public AfterMarketOutletsAdapter(ShouNewApplication mContext, ArrayList<Object> data, LatLng latLng1) {
        this.data = data;
        this.mContext = mContext;
        this.latLng1 = latLng1;
    }

    @Override
    public AfterMarketOutletsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View converView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_after_market_outlets_items, parent, false);
        return new AfterMarketOutletsViewHolder(converView);
    }

    @Override
    public void onBindViewHolder(final AfterMarketOutletsViewHolder holder, int position) {
        Object o = data.get(position);
        if (o instanceof AfterMarkerEntity) {//售后网点
            AfterMarkerEntity markerEntity = (AfterMarkerEntity) o;
            final String url = markerEntity.getBImg();
            setImg(holder, url);
            holder.mContact_phone.setText(String.format("联系电话:%s", markerEntity.getBPhone()));
            String gps = markerEntity.getBGps();
            jisuanDistances(holder, gps, 0);
            holder.mMy_car_name.setText(Html.fromHtml(String.format(HTML, markerEntity.getBName(), markerEntity.getBAddress())));
        } else if (o instanceof InsuranceEntity) {//低价保险
            InsuranceEntity insuranceEntity = (InsuranceEntity) o;
            final String url = insuranceEntity.getIImg();
            setImg(holder, url);
            holder.mContact_phone.setText(String.format("联系电话:%s", insuranceEntity.getIPhone()));
            String gps = insuranceEntity.getIGps();
            jisuanDistances(holder, gps, 1);
            holder.mMy_car_name.setText(Html.fromHtml(String.format(HTML, insuranceEntity.getIName(), insuranceEntity.getIAddress())));
        }
    }

    private void jisuanDistances(AfterMarketOutletsViewHolder holder, String gps, final int type) {
        if (!TextUtils.isEmpty(gps)) {
            final String[] lan = gps.split(",");
            holder.mDistance_tv.setText(String.format("%s", LocalUtils.distanceFromart(AMapUtils.calculateLineDistance(latLng1, new LatLng(Double.valueOf(lan[0]), Double.valueOf(lan[1]))))));
            holder.mDistance_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (type == 0) {
                        bundle.putInt("MapIcon", type);
                    } else if (1 == type) {
                        bundle.putInt("MapIcon", type);
                    }
                    bundle.putParcelable("location", new LatLng(Double.valueOf(lan[0]), Double.valueOf(lan[1])));
                    mContext.redirectAndPrameter(DotMapActivity.class, bundle);
                }
            });
        }
    }

    private void setImg(final AfterMarketOutletsViewHolder holder, final String url) {
        holder.mMy_car_header_iv.setTag(url);
        if (!TextUtils.isEmpty(url) && url.equals(holder.mMy_car_header_iv.getTag())) {
            Glide.with(mContext).load(url).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.mMy_car_header_iv.setImageBitmap(resource);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class AfterMarketOutletsViewHolder extends RecyclerView.ViewHolder {

        ImageView mMy_car_header_iv;
        TextView mMy_car_name;
        TextView mContact_phone;
        TextView mDistance_tv;

        public AfterMarketOutletsViewHolder(View itemView) {
            super(itemView);
            mMy_car_header_iv = (ImageView) itemView.findViewById(R.id.my_car_header_iv);
            mMy_car_name = (TextView) itemView.findViewById(R.id.my_car_name);
            mContact_phone = (TextView) itemView.findViewById(R.id.contact_phone);
            mDistance_tv = (TextView) itemView.findViewById(R.id.distance_tv);

        }
    }
}
