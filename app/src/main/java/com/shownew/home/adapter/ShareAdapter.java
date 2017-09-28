package com.shownew.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shownew.home.R;
import com.umeng.socialize.common.ResContainer;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.wp.baselib.BaseObjectAdapter;

import java.util.ArrayList;

/**分享适配
 * @author Jason
 * @version 1.0
 * @date 2017/5/3 0003
 */

public class ShareAdapter extends BaseObjectAdapter {
    private ArrayList<SnsPlatform> list;
    public ShareAdapter(Context context, ArrayList<SnsPlatform> datas) {
        super(context, datas);
        this.list = datas;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShareViewHolder shareViewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_share_dialog_item, parent, false);
            shareViewHolder = new ShareViewHolder(convertView);
            convertView.setTag(shareViewHolder);
        } else {
            shareViewHolder = (ShareViewHolder) convertView.getTag();
        }
        shareViewHolder.setValues(list.get(position), position);
        return convertView;
    }

    class ShareViewHolder extends BaseViewHolder {
        TextView share_icon;

        public ShareViewHolder(View itemView) {
            super(itemView);
            share_icon = (TextView) itemView.findViewById(R.id.share_icon);
        }

        @Override
        public void setValues(Object object, int position) {
            share_icon.setText(ResContainer.getResourceId(mContext, "string", list.get(position).mShowWord));
            share_icon.setCompoundDrawablesWithIntrinsicBounds(0, ResContainer.getResourceId(mContext, "drawable", list.get(position).mIcon), 0, 0);
        }
    }
}
