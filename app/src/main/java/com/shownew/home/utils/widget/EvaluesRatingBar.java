package com.shownew.home.utils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shownew.home.R;



/**商品评价
 * Created by WP on 2017/7/25.
 */

public class EvaluesRatingBar extends LinearLayout {
    private Context context;
    private int max;
    private float rating;
    private String leftStr;
    private String[] right_text;
    private RatingBar ratingBar;

    public EvaluesRatingBar(Context context) {
        this(context, null);
    }

    public EvaluesRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EvaluesRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EvaluesRatingBar);
        max = a.getInt(R.styleable.EvaluesRatingBar_max, 5);
        rating = a.getFloat(R.styleable.EvaluesRatingBar_rating, 0);
        leftStr = a.getString(R.styleable.EvaluesRatingBar_left_text);
        a.recycle();
        right_text = context.getResources().getStringArray(R.array.right_text);
        initViews();
    }

    private void initViews() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        TextView leftTv = new TextView(context);
        leftTv.setTextColor(getResources().getColor(R.color.color_evalue_state));
        leftTv.setPadding(0, 0, 20, 0);
        leftTv.setText(leftStr);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        leftTv.setLayoutParams(layoutParams);
        ratingBar = (RatingBar) LayoutInflater.from(context).inflate(R.layout.item_ratingbar, null);
        ratingBar.setMax(max);
        ratingBar.setRating(rating);
        ratingBar.setLayoutParams(layoutParams);
        final TextView rightTv = new TextView(context);
        rightTv.setLayoutParams(layoutParams);
        rightTv.setTextColor(getResources().getColor(R.color.color_evalue));
        rightTv.setPadding(20, 0, 0, 0);
        addView(leftTv);
        addView(ratingBar);
        addView(rightTv);
        rightTv.setText(right_text[Math.round(rating)]);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rightTv.setText(right_text[Math.round(v)-1]);
            }
        });

    }

    public float getRating() {
        return ratingBar.getRating();
    }
}
