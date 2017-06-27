package com.shownew.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.module.entity.KdEntity;
import com.wp.baselib.BaseObjectAdapter;

import java.util.ArrayList;

/**
 * 订单详情中的物流资料
 *
 * @author Jason
 * @version 1.0
 * @date 2017/5/2 0002
 */

public class LogisticsAdater extends BaseObjectAdapter {

    private ArrayList<KdEntity> datas;

    public LogisticsAdater(Context context, ArrayList<KdEntity> datas) {
        super(context, datas);
        this.datas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_logistice_items, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.setValues(datas.get(position), position);
        return convertView;
    }

    class ViewHolder extends BaseViewHolder<KdEntity> {
        ImageView logisitice_circle;
        TextView content_tv;
        TextView content_time;

        /**
         * 装载视图并初始化视图内组件
         *
         * @param convertView
         */
        public ViewHolder(View convertView) {
            super(convertView);
            logisitice_circle = (ImageView) convertView.findViewById(R.id.logisitice_circle);
            content_tv = (TextView) convertView.findViewById(R.id.content_tv);
            content_time = (TextView) convertView.findViewById(R.id.content_time);
        }

        @Override
        public void setValues(KdEntity object, int position) {
            if (object == null)
                return;
            if (position == 0) {
                logisitice_circle.setImageResource(R.drawable.shape_logistice_green);
            } else {
                logisitice_circle.setImageResource(R.drawable.shape_logistice_grey);
            }
            content_tv.setText(object.getAcceptStation());
            content_time.setText(object.getAcceptTime());
        }
    }
}
