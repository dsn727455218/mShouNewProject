package com.shownew.home.utils.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.WordWrapLayout;


/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/28 0028
 */

public class ShopSelectDialog extends BaseDialog implements View.OnClickListener {

    private final TextView mSelectContentTv;
    private final TextView mSelectCount;
    private final TextView mPricesTv;
    private String prices[];
    private double curprices;

    /**
     * //构造方法 来实现 最基本的对话框
     *
     * @param context
     */
    public ShopSelectDialog(Context context, String imgUrl, String prices, String color) {
        super(context, Gravity.BOTTOM);
        View view = mInflater.inflate(R.layout.layout_dialog_shop, null);
        Button select_sure = (Button) view.findViewById(R.id.select_sure);
        view.findViewById(R.id.reduce_number).setOnClickListener(this);
        view.findViewById(R.id.add_number).setOnClickListener(this);
        final ImageView shopImg = (ImageView) view.findViewById(R.id.shop_img);
        mPricesTv = (TextView) view.findViewById(R.id.prices_tv);
        if (prices.contains(",")) {
            this.prices = prices.split(",");
        } else {
            this.prices=new String[1];
            this.prices[0] = prices;
        }
        mSelectContentTv = (TextView) view.findViewById(R.id.select_content_tv);
        mSelectCount = (TextView) view.findViewById(R.id.select_count);
        WordWrapLayout typeParent = (WordWrapLayout) view.findViewById(R.id.type_parent);
        addViewColor(context, color, typeParent);
        colorLisener(typeParent);
        select_sure.setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        loadImg(context, imgUrl, shopImg);


        int width = display.getWidth();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        getDialog().setContentView(view, layoutParams);
    }

    private int count = 1;

    private void loadImg(Context context, String imgUrl, final ImageView shopImg) {
        Glide.with(context).

                load(imgUrl).

                asBitmap().

                placeholder(R.drawable.square_seize).

                error(R.drawable.square_seize).

                into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        shopImg.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        shopImg.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        shopImg.setImageDrawable(placeholder);
                    }
                });
    }

    private String mColor;

    private void colorLisener(final WordWrapLayout typeParent) {
        final int childCount = typeParent.getChildCount();
        for (int chlidPostion = 0; chlidPostion < childCount; chlidPostion++) {
            final int postion = chlidPostion;
            typeParent.getChildAt(postion).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int chlid = 0; chlid < childCount; chlid++) {
                        TextView textView = (TextView) typeParent.getChildAt(chlid);
                        if (postion == chlid) {
                            mColor = textView.getText().toString();
                            mSelectContentTv.setText(String.format("已选\"%s\"", mColor));
                            textView.setBackgroundResource(R.drawable.shape_shop_color);
                            textView.setTextColor(Color.WHITE);
                            count = Integer.parseInt(mSelectCount.getText().toString());
                            mSelectCount.setText(String.valueOf(count));
                            curprices = Double.valueOf(prices[postion]);
                            mPricesTv.setText(String.format("¥%s", StringUtil.formatMoney(Double.valueOf(prices[postion]) * count)));
                        } else {
                            textView.setBackgroundResource(0);
                            textView.setTextColor(Color.BLACK);
                        }
                    }
                }
            });
        }
        setDefaultValues(typeParent);
    }

    /**
     * 设置默认的值
     *
     * @param typeParent
     */
    private void setDefaultValues(WordWrapLayout typeParent) {
        TextView textView = (TextView) typeParent.getChildAt(0);
        mColor = textView.getText().toString();
        mSelectContentTv.setText(String.format("已选\"%s\"", mColor));
        textView.setBackgroundResource(R.drawable.shape_shop_color);
        textView.setTextColor(Color.WHITE);
        count = Integer.parseInt(mSelectCount.getText().toString());
        mSelectCount.setText(String.valueOf(count));
        curprices = Double.valueOf(prices[0]);
        mPricesTv.setText(String.format("¥%s", StringUtil.formatMoney(Double.valueOf(prices[0]) * count)));
    }

    private void addViewColor(Context context, String color, WordWrapLayout typeParent) {
        typeParent.removeAllViews();
        if (!TextUtils.isEmpty(color) && color.contains(",")) {
            String[] colors = color.split(",");
            for (String color1 : colors) {
                TextView textView = new TextView(context);
                textView.setPadding(15, 5, 15, 5);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setText(color1);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                typeParent.addView(textView);
            }
        } else if (!TextUtils.isEmpty(color)) {
            TextView textView = new TextView(context);
            textView.setPadding(15, 5, 15, 5);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setText(color);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams);
            typeParent.addView(textView);
        }
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.select_sure:
                if (null != mDialogLisener && !TextUtils.isEmpty(mColor)) {
                    count = Integer.parseInt(mSelectCount.getText().toString());
                    mDialogLisener.sure(mColor, count, curprices);
                    dismiss();
                } else {
                    ToastUtil.showToast("请选择分类");
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.add_number:
                count = Integer.parseInt(mSelectCount.getText().toString());
                if (count < 9999) {
                    count++;
                    mSelectCount.setText(String.valueOf(count));
                    mPricesTv.setText(String.format("¥%s", StringUtil.formatMoney(curprices * count)));
                }
                break;
            case R.id.reduce_number:
                count = Integer.parseInt(mSelectCount.getText().toString());
                if (count > 1) {
                    count--;
                    mSelectCount.setText(String.valueOf(count));
                    mPricesTv.setText(String.format("¥%s", StringUtil.formatMoney(curprices * count)));
                }
                break;
        }
    }

    private DialogLisener mDialogLisener;

    public ShopSelectDialog setDialogLisener(DialogLisener dialogLisener) {
        mDialogLisener = dialogLisener;
        return this;
    }

    public interface DialogLisener {
        void sure(String color, int number, double prices);
    }

}
