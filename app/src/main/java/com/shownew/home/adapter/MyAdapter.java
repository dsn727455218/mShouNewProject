package com.shownew.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.module.entity.HomeAdverEntity;

import java.util.ArrayList;

/**
 * Created by jianghejie on 15/11/26.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    public ArrayList<HomeAdverEntity> datas = null;
    private Context context;

    public MyAdapter(Context context, ArrayList<HomeAdverEntity> datas) {
        this.datas = datas;
        this.context = context;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.service_recycler_item, viewGroup, false);
        return new ViewHolder(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

         String url = datas.get(position).getAImg();
        viewHolder.img.setTag(url);
        if (!TextUtils.isEmpty(url) && url.equals(viewHolder.img.getTag())) {
            Glide.with(context).load(url).asBitmap().placeholder(R.drawable.sevice_runseize).error(R.drawable.sevice_runseize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    viewHolder.img.setImageBitmap(resource);
                }
            });
        }

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mServiceClickLisener) {
                    mServiceClickLisener.clickService(datas.get(position));
                }
            }
        });
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }

    private ServiceClickLisener mServiceClickLisener;

    public void setServiceClickLisener(ServiceClickLisener serviceClickLisener) {
        mServiceClickLisener = serviceClickLisener;
    }

    public interface ServiceClickLisener {
        void clickService(HomeAdverEntity homeAdverEntity);
    }
}
