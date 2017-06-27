package com.shownew.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.FunctionRenewActivity;
import com.shownew.home.module.entity.SelectEntity;
import com.wp.baselib.BaseObjectAdapter;

import java.util.ArrayList;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/2 0002
 */

public class FunctionRenewAdater extends BaseObjectAdapter {
    private ArrayList<SelectEntity> datas;

    public FunctionRenewAdater(Context context, ArrayList<SelectEntity> datas) {
        super(context, datas);
        this.datas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_text_function, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.setValues(datas.get(position), position);
        return convertView;
    }

    class ViewHolder extends BaseViewHolder<SelectEntity> {
        TextView renew_text;

        /**
         * 装载视图并初始化视图内组件
         *
         * @param convertView
         */
        public ViewHolder(View convertView) {
            super(convertView);
            renew_text = (TextView) convertView.findViewById(R.id.renew_text);
        }

        @Override
        public void setValues(final SelectEntity object, final int position) {
            if (object.isSelect()) {
                renew_text.setBackgroundResource(R.drawable.shape_function_selected);
            } else {
                renew_text.setBackgroundResource(R.drawable.select_function_renew_border);
            }
            renew_text.setText(object.getContent());
            renew_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setIsSelect(datas, position);
                    if (mContext instanceof FunctionRenewActivity) {
                        FunctionRenewActivity functionRenewActivity = (FunctionRenewActivity) mContext;
                        functionRenewActivity.setSelect(object);
                    }
                }
            });
        }

        private void setIsSelect(ArrayList<SelectEntity> datas, int position) {
            for (int i = 0; i < getCount(); i++) {
                if (i == position) {
                    datas.get(i).setSelect(true);
                } else {
                    datas.get(i).setSelect(false);
                }
            }
            notifyDataSetChanged();
        }
    }


}
