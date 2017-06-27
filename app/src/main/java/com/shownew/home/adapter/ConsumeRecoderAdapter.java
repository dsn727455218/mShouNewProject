package com.shownew.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.module.entity.RecoderEntity;
import com.wp.baselib.utils.StringUtil;

import java.util.ArrayList;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/21 0021
 */

public class ConsumeRecoderAdapter extends RecyclerView.Adapter<ConsumeRecoderAdapter.ConsumeRecoderViewHolder> {
    private ArrayList<RecoderEntity> data;

    public ConsumeRecoderAdapter(ArrayList<RecoderEntity> data) {
        this.data = data;
    }

    @Override
    public ConsumeRecoderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_consume_recoder_item, null);
        return new ConsumeRecoderViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ConsumeRecoderViewHolder holder, int position) {
        holder.custom_count_tv.setText(String.format("X%s", data.get(position).getRNum()));
        holder.custom_time.setText(data.get(position).getRDate());
        holder.custom_detail_tv.setText(data.get(position).getRTitle());
        holder.custom_prices.setText(String.format("¥%s", StringUtil.formatMoney(data.get(position).getRPrice())));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ConsumeRecoderViewHolder extends RecyclerView.ViewHolder {
        TextView custom_detail_tv;
        TextView custom_count_tv;
        TextView custom_time;
        TextView custom_prices;

        public ConsumeRecoderViewHolder(View itemView) {
            super(itemView);
            custom_count_tv = (TextView) itemView.findViewById(R.id.custom_count_tv);
            custom_detail_tv = (TextView) itemView.findViewById(R.id.custom_detail_tv);
            custom_time = (TextView) itemView.findViewById(R.id.custom_time);
            custom_prices = (TextView) itemView.findViewById(R.id.custom_prices);

        }
    }

}
