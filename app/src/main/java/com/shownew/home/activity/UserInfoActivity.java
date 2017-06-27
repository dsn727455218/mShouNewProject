package com.shownew.home.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.UserEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.utils.compress.CompressConfig;
import com.wp.baselib.utils.compress.CompressImageUtil;
import com.wp.baselib.utils.imagepicker.ImagePicker;
import com.wp.baselib.utils.imagepicker.bean.ImageItem;
import com.wp.baselib.utils.imagepicker.ui.ImageGridActivity;
import com.wp.baselib.utils.imagepicker.view.CropImageView;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Response;


public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private EditText mNichengEd;
    private ImageView mMyInfoHeaderIv;
    private UserAPI mUserAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        mMyInfoHeaderIv = (ImageView) findViewById(R.id.my_info_header_iv);
        mNichengEd = (EditText) findViewById(R.id.nicheng_ed);
        findViewById(R.id.user_info_ll).setOnClickListener(this);

        titleBarView.setTitle("个人资料");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        mMyInfoHeaderIv.setOnClickListener(this);
        findViewById(R.id.change_phone_tv).setOnClickListener(this);
        findViewById(R.id.modify_pwd_tv).setOnClickListener(this);
        findViewById(R.id.address_manage_tv).setOnClickListener(this);
        mNichengEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    modifyNicheng(mNichengEd.getText().toString());
                }
            }
        });
        mNichengEd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nicheng_ed:
                mNichengEd.setFocusable(true);
                mNichengEd.setFocusableInTouchMode(true);
                mNichengEd.requestFocus();
                break;
            case R.id.user_info_ll:
                hideInputKeyword();
                mNichengEd.setFocusable(false);
                break;
            case R.id.my_info_header_iv:
                hideInputKeyword();
                mNichengEd.setFocusable(false);
                ImagePicker imagePicker = ImagePicker.getInstance();
                imagePicker.setImageLoader(new com.wp.baselib.utils.GlideImageLoader());
                imagePicker.setMultiMode(false);   //多选
                imagePicker.setShowCamera(true);  //显示拍照按钮
                //                imagePicker.setSelectLimit(1);    //最多选择9张
                imagePicker.setStyle(CropImageView.Style.CIRCLE);
                imagePicker.setCrop(true);       //不进行裁剪
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.change_phone_tv:
                hideInputKeyword();
                mNichengEd.setFocusable(false);
                mainApplication.redirect(ModifyPhoneNumberActivity.class);
                break;
            case R.id.modify_pwd_tv:
                hideInputKeyword();
                mNichengEd.setFocusable(false);
                Bundle bundle = new Bundle();
                bundle.putInt("loginOrforgetpwd_flag", 3);//忘记密码
                mainApplication.redirectAndPrameter(RegisterActivity.class, bundle);
                break;
            case R.id.address_manage_tv:
                hideInputKeyword();
                mNichengEd.setFocusable(false);
                mainApplication.redirect(AddressEditActivity.class);
                break;
            case R.id.backBtn:
                mNichengEd.setFocusable(false);
                finish();
                break;
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageItem> imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imageItems != null && imageItems.size() > 0) {
                    uploadFile(imageItems.get(0).path);
                }
            } else {
                Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 文件上传
     * @param pathImg
     */
    private void uploadFile(final String pathImg) {
        CompressConfig config = CompressConfig.ofDefaultConfig();
        new CompressImageUtil(this, config).compress(pathImg, new CompressImageUtil.CompressListener() {
            @Override
            public void onCompressSuccess(final String path) {
                if (TextUtils.isEmpty(path))
                    return;
                mUserAPI.uploadUserHeadIcon(new File(path), mShouNewApplication.new ShouNewHttpCallBackLisener() {
                    @Override
                    protected Object parseData(String result) {
                        return null;
                    }

                    @Override
                    protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                        closeLoadingDialog();
                        if (null == exception) {
                            ToastUtil.showToast("上传成功");
                            mMyInfoHeaderIv.setImageURI(Uri.parse(path));
                        } else {
                            ToastUtil.showToast("上传失败");
                        }
                    }

                    @Override
                    protected void onLoading() {
                        createLoadingDialog();
                    }
                });
            }

            @Override
            public void onCompressFailed(String imgPath, String msg) {
                        ToastUtil.showToast("图片压缩失败");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfoData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeLoadingDialog();
    }

    /**
     * 修改用户昵称
     *
     * @param name
     */
    private void modifyNicheng(String name) {
        mUserAPI.modifyNicheng(name, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if(exception==null){
                    ToastUtil.showToast("修改昵称成功");
                }else {
                    ToastUtil.showToast("修改昵称失败");
                }
            }

            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }
        });
    }

    private void getUserInfoData() {
        mUserAPI.getUserData(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject result = json.getJSONObject("data");

                            String user = result.getString("user");
                            if (!TextUtils.isEmpty(user)) {
                                UserEntity.UserBean userBean = JsonUtils.fromJson(user, UserEntity.UserBean.class);
                                if(null!=userBean){
                                    mNichengEd.setText(TextUtils.isEmpty(userBean.getUNickname())?"修改昵称":userBean.getUNickname());
                                    Glide.with(UserInfoActivity.this).load(userBean.getUIcon()).asBitmap().placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            mMyInfoHeaderIv.setImageBitmap(resource);
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        });
    }

}
