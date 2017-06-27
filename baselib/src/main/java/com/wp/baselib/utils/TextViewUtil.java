package com.wp.baselib.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.DrawableMarginSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * textView个性化设置工具
 * @description
 * @author summer
 * @date 2014年5月28日 下午5:38:39
 *
 */
public class TextViewUtil {
	
	private static final String TAG = TextViewUtil.class.getSimpleName();
	public static Map<String,Integer> faceNameToDrawableId = new HashMap<String, Integer>();
	public static ArrayList<Map<String,Object>> expressionList = null;
	
	/*static{
		faceNameToDrawableId.put("爱心", R.drawable.expression_ax);
		faceNameToDrawableId.put("委屈", R.drawable.expression_wq);
		faceNameToDrawableId.put("难过", R.drawable.expression_ng);
		faceNameToDrawableId.put("大哭", R.drawable.expression_dk);
		faceNameToDrawableId.put("大笑", R.drawable.expression_dx);
		faceNameToDrawableId.put("瘪嘴", R.drawable.expression_bz);
		faceNameToDrawableId.put("调皮", R.drawable.expression_tp);
		faceNameToDrawableId.put("色", R.drawable.expression_se);
		faceNameToDrawableId.put("愤怒", R.drawable.expression_fn);
		faceNameToDrawableId.put("微笑", R.drawable.expression_wx);
		faceNameToDrawableId.put("吃惊", R.drawable.expression_cj);
		faceNameToDrawableId.put("发呆", R.drawable.expression_fd);
		faceNameToDrawableId.put("外星人", R.drawable.expression_wxr);
		faceNameToDrawableId.put("五角星", R.drawable.expression_wjx);
	}*/
	
	
	/**
	 * 正则表情图片
	 * @param sourceStr
	 * @return
	 */
	private static ArrayList<Map<String,Object>> getFaceStartAndEndIndex(String sourceStr){
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String regex="\\[[\u4e00-\u9fa5]+\\]g*";
	    Pattern pattern= Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sourceStr);
		boolean isFind = matcher.find();
		while (isFind) {
			Map<String,Object> map = new HashMap<String, Object>();
			String faceName = matcher.group().substring(1,matcher.group().length()-1);
			map.put("startIndex",matcher.start());
			map.put("endIndex",matcher.end());
			map.put("faceName",faceName);
			list.add(map);
			isFind = matcher.find((Integer)map.get("endIndex")-1);
		}
		return list;
	}
	
	
	/**
	 * TextView Html
	 * @param context
	 * @param textView
	 * @param source
	 */

	public static void appendImageToTextView(final Context context, TextView textView, String source){
		   ImageGetter imageGetter = new ImageGetter() {
				
				@Override
				public Drawable getDrawable(String source) {
					int id = Integer.parseInt(source);
					Drawable drawable = context.getResources().getDrawable(id);
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
					return drawable;
				}
			};
			Spanned spanned = Html.fromHtml(source,imageGetter, null);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setText(spanned);
	}
	
	/**
	 * 添加图片到TextView 末尾
	 * @param context
	 * @param textView
	 * @param source
	 */

	public static void appendImageToTextView(Context context, TextView textView, String source, int drawbleId){
		appendImageToTextView(context, textView, source, drawbleId, false);
	}

	/**
	 * 添加图片到TextView  
	 * @param context
	 * @param textView
	 * @param source 字符串内容
	 * @param isFirst 是否添加到第一个，true 表示添加到最开始位置，false 表示最末尾
	 */
	
	public static void appendImageToTextView(Context context, TextView textView, String source, int drawbleId, boolean isFirst){
		if (isFirst) {
			source = "x"+source;
		} else {
			source = source + " x";
		}
		SpannableString spannable = new SpannableString(source);
		Drawable drawable = context.getResources().getDrawable(drawbleId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
		ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		if (isFirst) {
			spannable.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			int length = source.length();
			spannable.setSpan(span, length-1, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		textView.setText(spannable);
	}

	/**
	 * 解析表情图片
	 * @param sourceStr 需要解析的字符串
	 * @param context
	 * @return
	 */
	public static SpannableString decodeFaceFormStr(String sourceStr, Context context){
		
		ArrayList<Map<String,Object>> list = getFaceStartAndEndIndex(sourceStr);
			SpannableString spannable = new SpannableString(sourceStr);
			
			int size = list.size();
			Drawable drawable = null;
			
			for (int i=0; i<size; i++ ) {
					Map<String,Object> map = list.get(i);
					Integer faceId = faceNameToDrawableId.get(map.get("faceName"));
					if (faceId!=null) {
						drawable = context.getResources().getDrawable(faceId);
						drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
						ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
						spannable.setSpan(span, (Integer)map.get("startIndex"), (Integer)map.get("endIndex"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			return spannable;
	}
	
	/**
	 * 设置TextView前景颜色并且可以连续设置样式
	 * @param str 传入的字符串
	 * @param startindex 开始位置
	 * @param endpos 结束位置
	 * @param color 设置的颜色 0xFFFF5A00
	 * @return
	 */
	public static SpannableStringBuilder setTextViewColor(String str, int color, int startindex, int endpos){
		SpannableStringBuilder style=new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(color),startindex,endpos, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
	}
	
	/**
	 * 设置字体颜色
	 * @param str 传入的字符串
	 * @param color 设置的颜色值
	 * @return
	 */
	public static SpannableStringBuilder setTextColor(String str, int color) {
		int startindex = str.indexOf("\n");
		int endpos = str.length();
		SpannableStringBuilder style=new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(color),startindex,endpos, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
	}

	/**
	 * 设置个人主页菜单项
	 * @param str 传入的字符串
	 * @param color 设置的颜色值
	 * @return
	 */
	public static SpannableStringBuilder setUserInfoTabStyle(String str, int color, int size) {
		SpannableStringBuilder ssb = setTextColor(str, color);
		int startindex = str.indexOf("\n");
		ssb.setSpan(new AbsoluteSizeSpan(size,true), 0, startindex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD) , 0, startindex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ssb;
	}
	
	/**
	 * 设置textview的背景颜色
	 * @param str
	 * @param color
	 * @param startindex
	 * @param endpos
	 * @return
	 */
	public static SpannableStringBuilder setTextViewBgColor(String str, int color, int startindex, int endpos){
		SpannableStringBuilder style=new SpannableStringBuilder(str);
        style.setSpan(new BackgroundColorSpan(color),startindex,endpos, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
	}
	
	/**
	 * 设置textview的背景颜色
	 * @param str
	 * @param color
	 * @param startindex
	 * @param endpos
	 * @return
	 */
	public static SpannableStringBuilder setTextViewDrawable(String str, Drawable drwable, int startindex, int endpos){
		SpannableStringBuilder style=new SpannableStringBuilder(str);
        style.setSpan(new DrawableMarginSpan(drwable),startindex,endpos, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
	}
	
	/**
	 * 显示表情视图
	 * @return
	 */
	/*public static View showExpression(Context context){

		View view = LayoutInflater.from(context).inflate(
				R.layout.expressiondialog, null);
		GridView expressionGrid = (GridView) view.findViewById(R.id.expression_gridview);	
		expressionList = buildExpressionsList(context);
		ExpressionAdapter expressionAdapter = new ExpressionAdapter(context, expressionList);
		expressionGrid.setAdapter(expressionAdapter);
		return view;
	}
	*/
	


	/**
	 * 表情列表
	 * @param context
	 * @return
	 */
	private static ArrayList<Map<String,Object>> buildExpressionsList(Context context){
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
		Set<String> setKeys =  faceNameToDrawableId.keySet();
		for (String string : setKeys) {
			Map<String,Object> map = new HashMap<String, Object>();
			Drawable drawable =context.getResources().getDrawable(faceNameToDrawableId.get(string));
			map.put("drawabletitile",string);
			map.put("drawableface",drawable);
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 
	 * @param context
	 * @param edit
	 * @param faceTitle 表情字符
	 * @param faceImg 表情图片
	 */
	public static  void setFace(Context context, EditText edit, String faceTitle, Drawable faceImg) {

		int start = edit.getSelectionStart();
		Spannable ss = edit.getText().insert(start, "[" + faceTitle + "]");
		
		faceImg.setBounds(0, 0, faceImg.getIntrinsicWidth(), faceImg.getIntrinsicHeight());
		ImageSpan span = new ImageSpan(faceImg, faceTitle,
				ImageSpan.ALIGN_BOTTOM);
		ss.setSpan(span, start, start + ("[" + faceTitle + "]").length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit.setText(ss);
		edit.setSelection(start + ("[" + faceTitle + "]").length());
	}
	
	
}

/**
 * 表情适配器
 * @author xcb296
 *
 */
class ExpressionAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Map<String,Object>> list;
	
	public ExpressionAdapter(Context context, ArrayList<Map<String,Object>> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		Map<String,Object> map = list.get(position);
		ImageView image = new ImageView(context);
		image.setImageDrawable((Drawable)map.get("drawableface"));
		return image;
	}
}
