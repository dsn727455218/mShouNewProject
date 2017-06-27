package com.wp.baselib.common;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.wp.baselib.AndroidActivity;
import com.wp.baselib.R;


/**
 * 通过FragmentTabHost实现
 * Tab布局基类包括（底部菜单和顶部菜单的管理）
 *
 * @author summer
 */
public class TabFragmentActivity extends AndroidActivity {

    private FragmentTabHost mTabHost;
    private TextView textview_unread;
    private View lastView; //记录上次显示视图
    private static String mLastTag;//记录上次位置
    private TabBarChanageListener tabBarChanageListener;

    public FragmentTabHost getmTabHost() {
        return mTabHost;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.tabhost_fragment);
        this.mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);


        this.mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                if (tabBarChanageListener != null) {
                    tabBarChanageListener.getTabButton(tabId, mLastTag);
                }
                mLastTag = tabId;
                if (lastView != null && lastView.getVisibility() != View.GONE) {
                    lastView.setVisibility(View.GONE);
                }
                FrameLayout frameLayout = mTabHost.getTabContentView();
                frameLayout.setVisibility(View.VISIBLE);
                lastView = frameLayout;
            }
        });
    }

    /**
     * 填充底部菜单 注意所有参数一一对应
     *
     * @param menu_id        预先设置菜单ID
     * @param top_icon_resid 菜单图标资源文件数组
     * @param str_resid      菜单文字资源文件数组
     * @param fragments      片段类数组
     */
    public void setBottomMenuAndIntent(String[] menu_id, int[] top_icon_resid, int[] str_resid, Class[] fragments) {

        setBottomMenuAndIntent(menu_id, top_icon_resid, str_resid, fragments, 0, 0);
    }

    /**
     * 填充底部菜单 注意所有参数一一对应
     *
     * @param menu_id                预先设置菜单ID
     * @param top_icon_resid         菜单图标资源文件数组
     * @param str_resid              菜单文字资源文件数组
     * @param fragments              片段类数组
     * @param titleColor             菜单文字颜色
     * @param tabBottomBackgroundRes 菜单背景颜色
     */
    public void setBottomMenuAndIntent(String[] menu_id, int[] top_icon_resid, int[] str_resid, Class[] fragments, int titleColor, int tabBottomBackgroundRes) {
        if (null != mTabHost) {
            for (int j = 0; j < menu_id.length; j++) {
                View view1 = View.inflate(TabFragmentActivity.this, R.layout.tab_item, null);// 菜单按钮布局文件

                TextView tab_title = ((TextView) view1.findViewById(R.id.tab_textview_title));
                tab_title.setCompoundDrawablesWithIntrinsicBounds(0, top_icon_resid[j], 0, 0);
                tab_title.setText(str_resid[j]);
                if (tabBottomBackgroundRes != 0) {
                    view1.setBackgroundResource(tabBottomBackgroundRes);
                }
                if (titleColor != 0) {
                    ColorStateList colorStateList = getResources().getColorStateList(titleColor);
                    tab_title.setTextColor(colorStateList);
                }
                /* 初始化意图 */
                mTabHost.addTab(buildTabSpec(menu_id[j] + "", view1), fragments[j], null);
            }
        }
    }

    /**
     * 是否隐藏底部
     *
     * @param isvisibility
     */
    public void hideBottomMenu(boolean isvisibility) {
        if (isvisibility) {
            this.mTabHost.getTabWidget().setVisibility(View.GONE);
        } else {
            this.mTabHost.getTabWidget().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置底部背景图片
     */
    public void setTabBackground(int bg_resid) {
        this.mTabHost.getTabWidget().setBackgroundResource(bg_resid);
    }

    /**
     * 初始化tab
     *
     * @param tag  唯一标示
     * @param view 每个选项的视图
     * @return
     */
    private TabHost.TabSpec buildTabSpec(String tag, View view) {

        return mTabHost.newTabSpec(tag).setIndicator(view);
    }

    /**
     * 启动指定的页面
     *
     * @param menu_id
     */
    public void startIabIntent(String menu_id) {
        this.mTabHost.setCurrentTabByTag(menu_id);
    }

    /**
     * 必须先构造菜单再调用该方法，不然不显示
     * 菜单选项中消息显示
     *
     * @param postion  索引从0开始,选择显示菜单位置 如果大于菜单项默认为0
     * @param bgimgres 提示信息的背景图标 可选 0
     * @param num      显示数量
     */
    public void setTipStr(int postion, int bgimgres, int num) {
        TabWidget tab = getmTabHost().getTabWidget();
        int count = tab.getChildCount();
        if (count > 0) {
            postion = postion > count ? 0 : postion;
            View menuview = tab.getChildAt(postion);
            textview_unread = (TextView) menuview.findViewById(R.id.tab_textview_unread);
            textview_unread.setVisibility(View.VISIBLE);
            if (bgimgres != 0) {
                if (getResources().getDrawable(bgimgres) != null) {
                    textview_unread.setBackgroundResource(bgimgres);
                }
            }
            textview_unread.setText(num + "");
        }
    }

    /**
     * 隐藏菜单消息
     *
     * @param postion 索引从0开始，如果大于菜单项默认为0
     */
    public void hideTipStr(int postion) {
        TabWidget tab = getmTabHost().getTabWidget();
        int count = tab.getChildCount();
        if (count > 0) {
            postion = postion > count ? 0 : postion;
            View menuview = tab.getChildAt(postion);
            textview_unread = (TextView) menuview.findViewById(R.id.tab_textview_unread);
            textview_unread.setVisibility(View.GONE);
        }
    }

    public void setTabBarChanageListener(TabBarChanageListener tabBarChanageListener) {
        this.tabBarChanageListener = tabBarChanageListener;
    }

    public interface TabBarChanageListener {

        /**
         * 获取当前和最后点击的按钮索引别名
         *
         * @param currentTag 当前索引别名
         * @param lastTag    最后索引别名
         */
        void getTabButton(String currentTag, String lastTag);
    }

}
