package com.shownew.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.activity.msg.MessageActionActivity;
import com.shownew.home.module.entity.MessageActionEntity;

import java.util.ArrayList;


/**活动消息
 * @author Jason
 * @version 1.0
 * @date 2017/5/24 0024
 */

public class MessageActionAdapter extends RecyclerView.Adapter<MessageActionAdapter.MessageActionViewHolder> {
    private ArrayList<MessageActionEntity> mMessageActionEntities;
    private Context context;

    public MessageActionAdapter(Context context, ArrayList<MessageActionEntity> messageActionEntities) {
        this.mMessageActionEntities = messageActionEntities;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @Override
    public MessageActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View msg;
        MessageActionViewHolder messageActionViewHolder = null;
        switch (viewType) {
            case 0:
                msg = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_action, parent, false);
                messageActionViewHolder = new MessageActionViewHolder(msg);
                break;
            case 1:
                msg = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_action2, parent, false);
                messageActionViewHolder = new MessageActionViewHolder(msg);
                break;
            case 2:
                msg = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_action3, parent, false);
                messageActionViewHolder = new MessageActionViewHolder(msg);
                break;
        }
        return messageActionViewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageActionViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        final MessageActionEntity messageActionEntity = mMessageActionEntities.get(position);
        if (null == messageActionEntity)
            return;
        switch (itemType) {
            case 0:
                holder.msg_title.setText(messageActionEntity.getNText());
                holder.msg_time.setText(messageActionEntity.getNDate());
                break;
            case 1:
                holder.msg_title.setText(messageActionEntity.getNText());
                break;
            case 2:
                holder.msg_title.setText(messageActionEntity.getNText());
                break;
        }


         String url = messageActionEntity.getNImg();
        holder.msg_img.setTag(url);
        if(!TextUtils.isEmpty(url)&&url.equals(holder.msg_img.getTag())){
            Glide.with(context).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.msg_img.setImageBitmap(resource);
                }
            });
        }


        ((View) holder.msg_img.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof MessageActionActivity){
                     MessageActionActivity actionActivity= (MessageActionActivity) context;
                     actionActivity.clickAction(messageActionEntity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageActionEntities.size();
    }

    class MessageActionViewHolder extends RecyclerView.ViewHolder {
        private ImageView msg_img;
        private TextView msg_title;
        private TextView msg_time;

        public MessageActionViewHolder(View itemView) {
            super(itemView);
            msg_img = (ImageView) itemView.findViewById(R.id.msg_img);
            msg_time = (TextView) itemView.findViewById(R.id.msg_time);
            msg_title = (TextView) itemView.findViewById(R.id.msg_tittle);
        }
    }
}
