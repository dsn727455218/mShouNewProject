package com.shownew.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.shownew.home.fragment.AllTalkFragment;

import java.util.ArrayList;

/**评价的Fragment
 * Created by WP on 2017/7/27.
 */

public class AllEvalueteFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> arrayList;
    private String products;
    private String productType;

    public AllEvalueteFragmentPagerAdapter(FragmentManager fm, ArrayList<String> arrayList, String productType, String products) {
        super(fm);
        this.arrayList = arrayList;
        this.products = products;
        this.productType=productType;
    }

    @Override
    public String getPageTitle(int position) {
        return arrayList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return AllTalkFragment.newInstance(String.valueOf(position),productType, products);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
