package com.wp.baselib.common;

import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wp.baselib.AndroidActivity;
import com.wp.baselib.MainApplication;
import com.wp.baselib.R;

import java.util.ArrayList;


/**
 * TopTab分栏基类封装
 * 
 * @author summer
 * 一般调用设置如下两个方法即可：
 * 1、initTabTitle 设置actionBar title
 * 2、initBodyView 设置每个选项页面
 */
public class ActionBarActivity extends AndroidActivity {

	private ViewPager mPager;// 页卡内容
	private ArrayList<View> listViews; // Tab页面列表
	private ImageView cursor;// 下标图片
	private int offset = 0;// 下标图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int tabTitleCount;// tab 的总数量
	private int oldTabTitleColor;// 旧的字体颜色
	private Drawable oldTabBackRes;// 旧的字体背景图片
	private TabChangeListener tabchangeListener;//tab切换时的回调
	private MainApplication mainApplication;
	/**
	 * topTab分栏
	 */
	private LinearLayout tabTitleLineLayout;
	/**
	 * 当前选择tabTitle的颜色
	 */
	private int currIndexColor = 0;

	/**
	 * 当前选择tabtitle背景图片
	 */
	public Drawable currIndexBackgroundRes;
	
	/**
	 * 主体布局框架
	 */
	public LinearLayout mainView;
	
	/**
	 * tabTitle和下标行 可以选项的背景等
	 */
	public FrameLayout tabtile_and_cursorline;
	
	/**
	 * 继承该类调用如下代码初始化即可  目前最多只支持3个选项卡
	 * 初始化调用核心两个方法initTabTitle()和initBodyView();
	 * 其他根据属性设置相应样式
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainApplication = (MainApplication)getApplication();
		setContentView(R.layout.layout_actionbar_main);
		mainView = (LinearLayout) findViewById(R.id.actionbar_mainview);
		tabtile_and_cursorline = (FrameLayout) findViewById(R.id.tab_line);
		tabTitleLineLayout = (LinearLayout) findViewById(R.id.tab_action_title);
	}

	/**
	 * 初始化tabtitle视图
	 * 
	 * @param CursorImageDrawable
	 *            下标图片
	 * @param textViews
	 *            tabtitle视图
	 */
	public void initTabTitle(int CursorImageDrawable, TextView... textViews) {
		tabTitleCount = textViews.length;
		for (int i = 0; i < tabTitleCount; i++) {
			TextView tv = textViews[i];
			tv.setOnClickListener(new MyOnClickListener(i));
			tv.setWidth(mainApplication.terminalWidth/tabTitleCount);
			tabTitleLineLayout.addView(tv);
			ColorStateList csl = tv.getTextColors();
			oldTabTitleColor = csl.getDefaultColor();// tab字体颜色
			oldTabBackRes = tv.getBackground();// tab背景图片
		}
		InitImageView(tabTitleCount, CursorImageDrawable);
		InitViewPager();// 页面视图
	}

	/**
	 * 初始化下标图标
	 */
	private void InitImageView(int viewsize, int CursorImageDrawable) {
		cursor = (ImageView) findViewById(R.id.cursor);
		if(CursorImageDrawable!=0)
			cursor.setBackgroundResource(CursorImageDrawable);
		LayoutParams lp=cursor.getLayoutParams();
		lp.width=mainApplication.terminalWidth/tabTitleCount; //设置下标宽度
		cursor.setLayoutParams(lp);
		offset = (mainApplication.terminalWidth/viewsize) / 2;// 计算偏移量 居中
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 设置所有tabtitle的字体颜色
	 * 
	 * @param color
	 *            设置默认颜色 不填为0即可
	 * @param currIndexColor
	 *            设置被选中后的 不填为0 即可
	 */
	public void setTabTitleColor(int color, int currIndexColor) {
		oldTabTitleColor = color;
		this.currIndexColor = currIndexColor;
		if (color == 0)
			return;
		for (int i = 0; i < tabTitleCount; i++) {
			TextView tv = (TextView) tabTitleLineLayout.getChildAt(i);
			if (currIndexColor != 0 && i == 0) {
				tv.setTextColor(currIndexColor);
			} else {
				tv.setTextColor(color);
			}
		}

	}

	/**
	 * 设置所有tabtitle的字体背景颜色
	 * 
	 * @param color_res
	 *            默认背景图片 不设置为null
	 * @param currIndexBackgroundRes
	 *            选中的背景图片 不设置为null
	 */
	public void setTabTitleBgColor(Drawable color_res,
			Drawable currIndexBackgroundRes) {

		oldTabBackRes = color_res;
		this.currIndexBackgroundRes = currIndexBackgroundRes;
		if (color_res == null)
			return;
		for (int i = 0; i < tabTitleCount; i++) {
			TextView tv = (TextView) tabTitleLineLayout.getChildAt(i);
			if (currIndexBackgroundRes != null && i == 0) {
				tv.setBackgroundDrawable(currIndexBackgroundRes);
			} else {
				tv.setBackgroundDrawable(color_res);
			}
		}


}
	/**
	 * 设置所有tabtitle的padding
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setTabTitlePadding(int left, int top, int right, int bottom) {

		for (int i = 0; i < tabTitleCount; i++) {
			TextView tv = (TextView) tabTitleLineLayout.getChildAt(i);
			tv.setPadding(left, top, right, bottom);
		}
	}

	/**
	 * 设置TabChangeListener监听器
	 */
	public void setTabChangeListener(TabChangeListener tabchangeListener){
		this.tabchangeListener=tabchangeListener;
	}
	
	
	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	}

	/**
	 * 注意每个视图不能用同一个实例化添加
	 * 初始化主视图body 注意一定要和头部TabTitle数量统一
	 */
	public void initBodyView(View... views) {

	
		if (tabTitleCount == views.length) {
			for (View view : views) {
				if (listViews == null) {
					throw new RuntimeException(
							"initTabTitle() 必须放在 initBodyView()之前");
				}
				listViews.add(view);
			}
			mPager.setAdapter(new MyPagerAdapter(listViews));
			mPager.setCurrentItem(0);
			mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		} else {
			throw new RuntimeException("tabtitle 和tabbody 数量不统一哈！");
		}
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public ArrayList<View> mListViews;

		public MyPagerAdapter(ArrayList<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(offset*(1<<currIndex), offset*2*arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
			currIndexParam(currIndex);
			if(tabchangeListener!=null){
				tabchangeListener.onTabChange(currIndex);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * 设置当前被选择的属性
	 * 
	 * @param index
	 */
	private void currIndexParam(int index) {

		if (tabTitleCount > 0) {
			for (int i = 0; i < tabTitleCount; i++) { // 清除其他已设置的样式
				TextView textview = (TextView) tabTitleLineLayout.getChildAt(i);
				textview.setTextColor(oldTabTitleColor);
				textview.setBackgroundDrawable(oldTabBackRes);
			}
			TextView textview = (TextView) tabTitleLineLayout.getChildAt(index);// 被选择
			if (currIndexColor != 0)
				textview.setTextColor(currIndexColor);

			if (currIndexBackgroundRes != null)
				textview.setBackgroundDrawable(currIndexBackgroundRes);
		}
	}
	/**
	 * 视图发生变化的时候调用
	 * @author summer
	 *
	 */
	public interface TabChangeListener{
		
		/**
		 * 页面发生变化的时候调用
		 * @param currIndex
		 */
		void onTabChange(int currIndex);
	}
}