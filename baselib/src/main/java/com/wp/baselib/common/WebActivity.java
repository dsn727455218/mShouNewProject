package com.wp.baselib.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.wp.baselib.R;
import com.wp.baselib.utils.LogUtil;
import com.wp.baselib.utils.UiUtil;
import com.wp.baselib.widget.TitleBarView;


/**
 * 关于我们/帮助中心 等web页面展示 默认不显示放大控件、不跳出内页
 *
 * @auther:summer 时间： 2013-1-22 下午2:28:32
 */
public class WebActivity extends ImmersiveActivity implements View.OnClickListener, DownloadListener {

    protected WebView mWebView; // webView
    private ImageView page_back_iv, page_forward_iv, page_refresh_iv; // 向后、向前、刷新
    private boolean isShowBottomControls; // 是否显示底部控件
    private boolean mIsLoad;// 是否在加载
    private String mTitle;// 固定标题
    private boolean isShowZoomControls;// 是否显示放大控件
    private boolean isGoOut;// 是否跳出浏览器
    private String mUrl;// 请求的网址
    private ProgressBar pb;
    private int mErrorCode;
    //设置全屏
    protected static final LayoutParams COVER_SCREEN_PARAMS = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    //头部视图
    private View mTopView, mErrorView;

    private ValueCallback<Uri> mUploadMsg;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private TitleBarView mTitleBarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_web);
        initView();
    }

    @Override
    public void readSaveBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        mUrl = bundle.getString("url");
        mTitle = bundle.getString("title");
        isShowZoomControls = bundle.getBoolean("isShowZoomControls");
        isGoOut = bundle.getBoolean("isGoOut");
        isShowBottomControls = bundle.getBoolean("isShowBottomControls");
        LogUtil.i("WebActivity", mUrl);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mWebView != null) {
                mWebView.onPause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        UiUtil.startWebIntent(WebActivity.this, url);
    }

    class LocalWebChromeClient extends WebChromeClient {

        private View myView = null;
        private CustomViewCallback myCallback = null;

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (TextUtils.isEmpty(mTitle)) {
                mTitleBarView.setTitle(title);
            }
        }

        //通过这个方法来控制进度条的进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);
            if (newProgress == 100) {
                pb.setVisibility(View.GONE);
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            updateControlBar(false);
            FrameLayout decor = (FrameLayout) getWindow().getDecorView();
            decor.addView(view, COVER_SCREEN_PARAMS);
            myView = view;
            myCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            if (myView != null) {
                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                }
                updateControlBar(true);
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.removeView(myView);
                myView = null;
            }
        }

        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "", "");
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, "");
        }

        // For Android 4.1
        @TargetApi(20)
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMsg = uploadMsg;
            //            PictureActivityUtil.doPickPhotoAction(WebActivity.this, uploadMsg, null);//打开拍照或相册的入口
        }

        // For Android 5.0+
        @TargetApi(23)
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            mUploadCallbackAboveL = filePathCallback;
            //            PictureActivityUtil.doPickPhotoAction(WebActivity.this, null, mUploadCallbackAboveL);//打开拍照或相册的入口
            return true;
        }
    }

    /**
     * 初始化视图
     */
    @SuppressLint("JavascriptInterface")
    private void initView() {
        mErrorView = findViewById(R.id.empty_view);
        pb = (ProgressBar) findViewById(R.id.pb);

        //init toolbar
        mTitleBarView = (TitleBarView) findViewById(R.id.headbar);

        mTitleBarView.setTitleTextColor(R.color.white);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        mTitleBarView.setTitleSize(20);
        // 如果没有传递title过来就取html页面的title
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleBarView.setTitle(mTitle);
        }

        View webViewBottom = findViewById(R.id.webviewBottom);
        if (isShowBottomControls) {
            webViewBottom.setVisibility(View.VISIBLE);
            page_back_iv = (ImageView) findViewById(R.id.page_back);
            page_forward_iv = (ImageView) findViewById(R.id.page_forward);
            page_refresh_iv = (ImageView) findViewById(R.id.page_refresh);
        } else {
            webViewBottom.setVisibility(View.GONE);
        }

        mWebView = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        LocalWebChromeClient localWebChromeClient = new LocalWebChromeClient();
        mWebView.setWebChromeClient(localWebChromeClient);

        // 浏览器设置
        WebSettings webSettings = mWebView.getSettings();
        if (isShowZoomControls) { // 支持缩放和显示缩放控件
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        // 阻塞图片
        webSettings.setBlockNetworkImage(true);
        mWebView.setEnabled(true);
        // 设置浏览器回调
        LocalWebClient webClient = new LocalWebClient();
        mWebView.setWebViewClient(webClient);

        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        // 加载URL
        mWebView.loadUrl(mUrl);
        // 文件下载
        mWebView.setDownloadListener(this);
        mWebView.addJavascriptInterface(this, "android");
    }

    /**
     * 浏览器回调接口
     */

    class LocalWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mErrorCode = 0;
            mIsLoad = true;
            if (!isFinishing()) {
//                showProgressDialog("加载中...");
                if (isShowBottomControls) {
                    webViewBottomControl(view);
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            closeProgressDialog();
            if (mErrorCode != 0) {
                view.setVisibility(View.INVISIBLE);
                mErrorView.setVisibility(View.VISIBLE);
                return;
            }
            mErrorView.setVisibility(View.GONE);

            if (TextUtils.isEmpty(mTitle)) {
                mTitleBarView.setTitle(mWebView.getTitle());
            }

            // 显示图片
            view.getSettings().setBlockNetworkImage(false);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.requestFocus();
            mIsLoad = false;

            if (!isFinishing()) {
                if (isShowBottomControls) {
                    webViewBottomControl(view);
                }
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isGoOut) { // 跳出内页面
                UiUtil.startWebIntent(WebActivity.this, url);
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mErrorCode = errorCode;
            view.setVisibility(View.INVISIBLE);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    /**
     * 向后
     *
     * @param v
     */
    public void goBackClick(View v) {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    /**
     * 向前
     *
     * @param v
     */
    public void goForwardClick(View v) {
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }

    /**
     * 刷新
     *
     * @param v
     */
    public void goRefreshClick(View v) {
        if (mIsLoad) {
            mWebView.stopLoading();
        } else {
            mWebView.reload();
        }
    }

    /**
     * webView底部样式
     *
     * @param
     * @param view
     */
    private void webViewBottomControl(WebView view) {
        if (mIsLoad) {
            page_refresh_iv.setImageResource(R.drawable.icon_web_stop);
        } else {
            page_refresh_iv.setImageResource(R.drawable.icon_web_refresh);
        }

        if (view.canGoBack()) {
            page_back_iv.setEnabled(true);
            page_back_iv.setImageResource(R.drawable.icon_web_back);
        } else {
            page_back_iv.setEnabled(false);
            page_back_iv.setImageResource(R.drawable.icon_web_back_normal);
        }
        if (view.canGoForward()) {
            page_forward_iv.setEnabled(true);
            page_forward_iv.setImageResource(R.drawable.icon_web_forward);
        } else {
            page_forward_iv.setEnabled(false);
            page_forward_iv.setImageResource(R.drawable.icon_web_forward_normal);
        }
    }

    /**
     * 是否显示头部视图
     *
     * @param show
     */
    private void updateControlBar(boolean show) {
        mTopView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mWebView != null) {
                mWebView.destroy();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) { // 点击返回健返回上一个页面
            if (mWebView != null) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /**
     * 判断是否是横屏显示 window.android.isOrientation(true)
     *
     * @param isLandscape true代表横屏
     */
    @JavascriptInterface
    public void isOrientation(boolean isLandscape) {
        int orientation = getRequestedOrientation();
        if (isLandscape) {
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            return;
        }
        if (orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backBtn && null != mWebView) {
            try {
                mWebView.destroy();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}