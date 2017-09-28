package com.shownew.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.msg.MessageCenterActivity;
import com.shownew.home.module.entity.MsgListEntity;

import java.util.ArrayList;

/**消息中心
 * @author Jason
 * @version 1.0
 * @date 2017/4/17 0017
 */

public class MessageCenterAdapter extends RecyclerView.Adapter<MessageCenterAdapter.MessageCenterViewHolder> {
    private ArrayList<MsgListEntity> data;
    private Context mContext;

    public MessageCenterAdapter(Context mContext, ArrayList<MsgListEntity> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MessageCenterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_center_item, parent, false);
        return new MessageCenterViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(MessageCenterViewHolder holder, int position) {
        if (mContext != null && mContext instanceof MessageCenterActivity) {
            MessageCenterActivity centerActivity = (MessageCenterActivity) mContext;
            if (0 == centerActivity.getMsgType()) {
                holder.header_msg.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.vehiclenews, 0, 0);
            } else if (1 == centerActivity.getMsgType()) {
                holder.header_msg.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.logo, 0, 0);
            }
            holder.msgContent.setText(data.get(position).getNText());
            holder.msgTime.setText(data.get(position).getNDate());
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MessageCenterViewHolder extends RecyclerView.ViewHolder {
        TextView msgTime;
        TextView msgContent;
        TextView header_msg;

        public MessageCenterViewHolder(View itemView) {
            super(itemView);
            msgTime = (TextView) itemView.findViewById(R.id.msg_time);
            msgContent = (TextView) itemView.findViewById(R.id.msg_content);
            header_msg = (TextView) itemView.findViewById(R.id.header_msg);
        }
    }
}
