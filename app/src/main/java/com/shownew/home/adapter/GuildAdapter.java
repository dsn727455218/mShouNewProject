package com.shownew.home.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 欢迎页的适配器
 * @author
 * @version 1.0
 * @date 2017/3/31 0031
 */

public class GuildAdapter extends PagerAdapter {
    private ArrayList<View> views;
    public GuildAdapter(ArrayList<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = views.get(position);
        container.removeView(view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }
}
