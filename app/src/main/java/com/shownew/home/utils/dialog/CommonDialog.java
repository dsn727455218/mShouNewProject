package com.shownew.home.utils.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shownew.home.R;

/**公共的dialog
 * @author Jason
 * @version 1.0
 * @date 2017/4/18 0018
 */

public class CommonDialog extends BaseDialog implements View.OnClickListener {
    /**
     * //构造方法 来实现 最基本的对话框
     *
     * @param context
     */
    public CommonDialog(Context context, String title, String msg) {
        super(context, Gravity.CENTER);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_common_dialog, null);
        TextView commonTitle = (TextView) view.findViewById(R.id.common_title);
        TextView commonMsg = (TextView) view.findViewById(R.id.common_msg);
        view.findViewById(R.id.common_cancel).setOnClickListener(this);
        view.findViewById(R.id.common_sure).setOnClickListener(this);
        commonTitle.setText(TextUtils.isEmpty(title) ? "温馨提示" : title);
        commonMsg.setText(msg);
        setView(view);
    }

    public CommonDialog(Context context, String title, String msg, String sure, String cancel) {
        super(context, Gravity.CENTER);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_common_dialog, null);
        TextView commonTitle = (TextView) view.findViewById(R.id.common_title);
        TextView commonMsg = (TextView) view.findViewById(R.id.common_msg);
        TextView cancelTv = (TextView) view.findViewById(R.id.common_cancel);
        cancelTv.setText(cancel);
        cancelTv.setOnClickListener(this);
        TextView sureTv = (TextView) view.findViewById(R.id.common_sure);
        sureTv.setText(sure);
        sureTv.setOnClickListener(this);
        commonTitle.setText(TextUtils.isEmpty(title) ? "温馨提示" : title);
        commonMsg.setText(msg);
        setView(view);
    }

    public CommonDialog(Context context, String msg) {
        this(context, "", msg);
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }

    private int flag = 0;

    @Override
    public void onClick(View v) {

        dismiss();
        switch (v.getId()) {
            case R.id.common_cancel:
                flag = 0;
                break;
            case R.id.common_sure:
                flag = 1;
                break;
        }
        if (null != mCommonListener) {
            mCommonListener.sure(flag);
        }
    }

    private CommonListener mCommonListener;

    public CommonDialog setCommonListener(CommonListener commonListener) {
        mCommonListener = commonListener;
        return this;
    }

    public interface CommonListener {
        /**
         *   0  取消  1  确定
         * @param flag
         */
        void sure(int flag);
    }
}
