package com.shownew.home.utils.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.share.ShareUtils;
import com.shownew.home.adapter.ShareAdapter;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/3 0003
 */

public class ShareDialog extends BaseDialog {
    private ShareAdapter mShareAdapter;
    private final ShareUtils mShareUtils;

    /**
     * //构造方法 来实现 最基本的对话框
     * @param context
     */
    public ShareDialog(final Context context, ShouNewApplication shouNewApplication) {
        super(context, Gravity.BOTTOM);
        View view = mInflater.inflate(R.layout.layout_share_dialog, null);
        GridView gridView = (GridView) view.findViewById(R.id.share_gridview);
        mShareUtils = new ShareUtils(context,shouNewApplication);
        mShareAdapter = new ShareAdapter(context, mShareUtils.getPlatforms());
        gridView.setAdapter(mShareAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                mShareUtils.setSharePlatform(position, 0, (Activity) context);
            }
        });
        view.findViewById(R.id.share_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        int width = display.getWidth();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().setContentView(view, layoutParams);
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }
}
