package com.wp.baselib;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.wp.baselib.utils.ExitUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.utils.UMengUtil;

/**
 * 所有Actvity的基类
 *
 * @author summer
 */
public class AndroidActivity extends FragmentActivity {

    protected ProgressDialog progressDialog;
    private static final String TAG = AndroidActivity.class.getSimpleName();
    protected MainApplication mainApplication;
    protected Bundle mBundle;
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        mainApplication = (MainApplication) getApplication();
        ExitUtil.getInstance().addInstance(this);
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            readSaveBundle(mBundle);
        } else if (savedInstanceState != null) {
            mBundle = savedInstanceState.getBundle(getClass().getSimpleName());
            readSaveBundle(mBundle);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mBundle != null)
            outState.putBundle(getClass().getSimpleName(), mBundle);
        super.onSaveInstanceState(outState);
    }

    /**
     * 不为空的数据
     */
    protected void readSaveBundle(Bundle bundle) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        UMengUtil.uMengOnPause(this);
        closeProgressDialog();
        //volley取消网络队列
        OkGo.getInstance().cancelTag(this);
        OkGo.getInstance().cancelAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengUtil.uMengOnResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.cancelToast();
        ExitUtil.getInstance().finishActivity(this);
    }


    /**
     * 显示进度条
     *
     * @param message
     */

    public void showProgressDialog(String message) {
        showProgressDialog(message, true);
    }


    /**
     * 创建progressDialog
     *
     * @param cancel 点击是否取消
     * @return
     */
    public void createLoadingDialog(boolean cancel) {
        if (!isFinishing()) {
            if (mLoadingDialog == null) {
                LayoutInflater inflater = LayoutInflater.from(this);
                View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
                RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dialog_view);// 加载布局
                // 创建自定义样式dialog
                mLoadingDialog = new Dialog(this, R.style.loading_dialog);
                mLoadingDialog.setCancelable(cancel);// false 不可以用“返回键”取消
                mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));// 设置布局
            }
            if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
                mLoadingDialog.show();
            }
        }
    }

    /**
     * 显示进度条
     */
    public void createLoadingDialog() {
        createLoadingDialog(false);
    }

    /**
     * 关闭自定义的Dialog
     */
    public void closeLoadingDialog() {
        if ((mLoadingDialog != null && (!isFinishing())) && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    /**
     * 显示进度条
     *
     * @param message
     */

    public void showProgressDialog(String message, boolean cancel) {
        if (!isFinishing()) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this.getParent() != null ? this.getParent() : this);
                progressDialog.setMessage(message);
                progressDialog.setCancelable(cancel);
                progressDialog.setCanceledOnTouchOutside(cancel);
            }
            progressDialog.show();
        }
    }

    /**
     * 关闭进度条
     */
    public void closeProgressDialog() {

        if ((progressDialog != null && (!isFinishing())) && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeProgressDialog();

    }


}
