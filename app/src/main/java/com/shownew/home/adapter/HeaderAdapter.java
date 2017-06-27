package com.shownew.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shownew.home.R;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/14 0014
 */

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderRecylerViewHolder> {
    private LayoutInflater inflater;
    private int[] imgId ;

    public HeaderAdapter(Context context, int[] imgId) {
        this.inflater = LayoutInflater.from(context);
        this.imgId=imgId;
    }

    @Override
    public HeaderRecylerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View converView = inflater.inflate(R.layout.layout_header_item, parent, false);
        return new HeaderRecylerViewHolder(converView);
    }

    @Override
    public void onBindViewHolder(HeaderRecylerViewHolder holder, final int position) {
        holder.circleIv.setImageResource(imgId[position]);
        holder.circleIv.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if(null!=mHeaderListener){
                    mHeaderListener.clickPosition(position);
                }
            }
        });
    }

    private HeaderListener mHeaderListener;

    public void setHeaderListener(HeaderListener headerListener) {
        mHeaderListener = headerListener;
    }

    public interface HeaderListener{
        void clickPosition(int postion);
    }

    @Override

    public int getItemCount() {
        return imgId.length;
    }

    class HeaderRecylerViewHolder extends RecyclerView.ViewHolder {
        ImageView circleIv;

        public HeaderRecylerViewHolder(View itemView) {
            super(itemView);
            circleIv = (ImageView) itemView.findViewById(R.id.header_icon);
        }
    }
}
