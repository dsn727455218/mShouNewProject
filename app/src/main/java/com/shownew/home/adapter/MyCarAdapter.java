package com.shownew.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.activity.MyCarActivity;
import com.shownew.home.module.entity.CarEntity;

import java.util.ArrayList;


/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/17 0017
 */

public class MyCarAdapter extends RecyclerView.Adapter<MyCarAdapter.MyCarViewHolder> {
    private ArrayList<CarEntity> data;
    private Context mContext;

    public MyCarAdapter(Context mContext, ArrayList<CarEntity> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyCarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View converView = layoutInflater.inflate(R.layout.layout_my_car_item, parent, false);
        return new MyCarViewHolder(converView);
    }

    @Override
    public void onBindViewHolder(MyCarViewHolder holder, final int position) {
        final CarEntity carEntity = data.get(position);
        holder.new_car.setVisibility(View.GONE);
        holder.new_car_ll.setVisibility(View.VISIBLE);
        if (carEntity == null){
            holder.new_car_ll.setVisibility(View.GONE);
            holder.new_car.setVisibility(View.VISIBLE);
            holder.new_car.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext != null && mContext instanceof MyCarActivity) {
                        MyCarActivity carActivity = (MyCarActivity) mContext;
                        carActivity.addCar();
                    }
                }
            });
            return;
        }

        //绑定车辆
        holder.new_car_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mContext != null && mContext instanceof MyCarActivity) {
                    MyCarActivity carActivity = (MyCarActivity) mContext;
                    carActivity.bindCar(carEntity.getCId(), carEntity.getcDefault());
                }
                return false;
            }
        });
        //删除车辆
        holder.my_car_delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null && mContext instanceof MyCarActivity) {
                    MyCarActivity carActivity = (MyCarActivity) mContext;
                    carActivity.delectCar(carEntity.getCId());
                }
            }
        });
        //修改车辆
        holder.modify_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null && mContext instanceof MyCarActivity) {
                    MyCarActivity carActivity = (MyCarActivity) mContext;
                    carActivity.modifyCar(carEntity);
                }
            }
        });
        setData(holder, carEntity);
    }

    /**
     * 为组件绑定数据
     *
     * @param holder
     * @param carEntity
     */

    private void setData(final MyCarViewHolder holder, final CarEntity carEntity) {
        if (carEntity != null) {

            holder.my_car_name.setText(Html.fromHtml(String.format("%s_<small>%s</small>", carEntity.getCName(), carEntity.getCBattery() + "v")));
            holder.my_car_cur_name.setText(!"1".equals(carEntity.getcDefault()) ? carEntity.getCMark() : Html.fromHtml(String.format("%s<font color=#0078D7>(%s)</font>", carEntity.getCMark(), "正在使用")));
            holder.mCarUserName.setText(String.format("用户姓名:%s", carEntity.getCUname()));
            holder.mCar_paihao.setText(String.format("车牌号码:%s", carEntity.getCLicencenum()));
            holder.mCar_vertify_number.setText(String.format("身份证号:%s", carEntity.getCUidcard()));
            holder.mCar_type_number.setText(String.format("车辆型号:%s", carEntity.getCModelnum()));
            holder.mCar_jia_number.setText(String.format("车 架 号:%s", carEntity.getCFramenum()));


            String url = carEntity.getCIcon();
            holder.mMy_car_header_iv.setTag(url);
            if (!TextUtils.isEmpty(url) && url.equals(holder.mMy_car_header_iv.getTag())) {
                Glide.with(mContext).load(url).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.mMy_car_header_iv.setImageBitmap(resource);
                    }
                });
            }

            holder.mMy_car_header_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext != null && mContext instanceof MyCarActivity) {
                        MyCarActivity carActivity = (MyCarActivity) mContext;
                        carActivity.uploadCarImg(carEntity.getCId());
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyCarViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout new_car_ll;
        private TextView my_car_delete_iv;
        private TextView modify_car;
        private TextView my_car_name;
        private TextView my_car_cur_name;
        private final TextView mCarUserName;
        private final TextView mCar_paihao;
        private final TextView mCar_vertify_number;
        private final TextView mCar_type_number;
        private final TextView mCar_jia_number;
        private final ImageView mMy_car_header_iv;
        private ImageView new_car;


        public MyCarViewHolder(View itemView) {
            super(itemView);
            new_car_ll = (LinearLayout) itemView.findViewById(R.id.new_car_ll);
            my_car_delete_iv = (TextView) itemView.findViewById(R.id.my_car_delete_iv);
            modify_car = (TextView) itemView.findViewById(R.id.modify_car);

            my_car_name = (TextView) itemView.findViewById(R.id.my_car_name);
            my_car_cur_name = (TextView) itemView.findViewById(R.id.my_car_cur_name);
            mCarUserName = (TextView) itemView.findViewById(R.id.car_userName);
            mCar_paihao = (TextView) itemView.findViewById(R.id.car_paihao);
            mCar_vertify_number = (TextView) itemView.findViewById(R.id.car_vertify_number);
            mCar_type_number = (TextView) itemView.findViewById(R.id.car_type_number);
            mCar_jia_number = (TextView) itemView.findViewById(R.id.car_jia_number);
            mMy_car_header_iv = (ImageView) itemView.findViewById(R.id.my_car_header_iv);
            new_car = (ImageView) itemView.findViewById(R.id.new_car);
        }
    }
}
