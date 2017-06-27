package com.wp.baselib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * @Description 所有集合适配器抽象基类
 * @Author summer
 * @Date 2014年4月2日 上午10:57:30
 */
public abstract class BaseObjectAdapter extends BaseAdapter {

    protected final String TAG = this.getClass().getSimpleName();

    protected final Context mContext;
    protected final LayoutInflater mInflater;
    protected final ArrayList<?> mDatas;

    protected final MainApplication mApplication;

    /**
     * 带有全局应用的构造方法
     *
     * @param application
     * @param context     上下文
     * @param datas       数据集
     */
    public BaseObjectAdapter(MainApplication application, Context context, ArrayList<?> datas) {
        mApplication = application;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    /**
     * 不带有全局应用的构造方法
     *
     * @param context 上下文
     * @param datas   数据集
     */
    public BaseObjectAdapter(Context context, ArrayList<?> datas) {
        this((MainApplication) context.getApplicationContext(), context, datas);
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 抽象ViewHolder 用于Adapter里的视图填充
     *
     * @param <T>
     * @author summer
     * @description
     * @date 2014年4月4日 下午1:40:04
     */
    public abstract class BaseViewHolder<T> {

        /**
         * 装载视图并初始化视图内组件
         *
         * @param convertView
         */
        public BaseViewHolder(View convertView) {
        }

        /**
         * 为视图绑定数据
         *
         * @param object   数据对象
         * @param position 每个列表的位置
         */
        public abstract void setValues(T object, int position);

    }
}
