package com.shownew.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.AddressManagerActivity;

import java.util.ArrayList;

/**
 * Created by jianghejie on 15/11/26.
 */
public class AddressManagerAdapter extends RecyclerView.Adapter<AddressManagerAdapter.ViewHolder> {
    public ArrayList<String> datas = null;
    private Context context;

    public AddressManagerAdapter(Context context, ArrayList<String> datas) {
        this.datas = datas;
        this.context = context;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_address_manager_item, viewGroup, false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.add_address.setVisibility(View.GONE);
        if(getItemCount()-1==position){
            viewHolder.add_address.setVisibility(View.VISIBLE);
        }
        viewHolder.addressEditTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof AddressManagerActivity){
                    AddressManagerActivity managerActivity= (AddressManagerActivity) context;
                    managerActivity.enterAddressEdit();
                }
            }
        });
        viewHolder.addressDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof AddressManagerActivity){
                    AddressManagerActivity managerActivity= (AddressManagerActivity) context;
                    managerActivity.delectAddress();
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
    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView addressEditTv;
        private TextView addressDeleteTv;
        public Button add_address;

        public ViewHolder(View view) {
            super(view);
            addressEditTv = (TextView) view.findViewById(R.id.address_edit_tv);
            addressDeleteTv=(TextView)view.findViewById(R.id.address_delete_tv);
            add_address= (Button) view.findViewById(R.id.add_address);
        }
    }
}
