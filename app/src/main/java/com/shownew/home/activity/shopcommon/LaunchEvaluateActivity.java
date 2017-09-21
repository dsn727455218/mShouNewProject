package com.shownew.home.activity.shopcommon;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.entity.OderMenuEntity;
import com.shownew.home.utils.widget.EvaluesRatingBar;
import com.shownew.home.utils.widget.WordWrapLayoutiml;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.utils.compress.CompressConfig;
import com.wp.baselib.utils.compress.CompressImageUtil;
import com.wp.baselib.utils.imagepicker.ImagePicker;
import com.wp.baselib.utils.imagepicker.bean.ImageItem;
import com.wp.baselib.utils.imagepicker.ui.ImageGridActivity;
import com.wp.baselib.utils.imagepicker.ui.ImagePreviewActivity;
import com.wp.baselib.utils.imagepicker.view.CropImageView;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Response;


public class LaunchEvaluateActivity extends BaseActivity implements View.OnClickListener {

    private WordWrapLayoutiml wrapLayoutiml;
    private ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
    private OderMenuEntity menuEntity;
    private ShopAPI shopAPI;
    private EditText talkContent;
    private EvaluesRatingBar dLsgradeRatingBar;
    private EvaluesRatingBar dSvgradeRatingBar;
    private EvaluesRatingBar dPdgradeRatingBar;
    private View talkParent;
    private View againParent;
    private TextView historyContent;
    private TextView historyTalkTimeTv;
    private ImageView shopImg;
    private WordWrapLayoutiml currentWordWrap;

    private WordWrapLayoutiml show_img_iml;


    private WordWrapLayoutiml again_select_img;
    private EditText again_talk_content;
    /**
     * 评价id
     */
    private String did;
    private String id = "";
    private String shopType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_evaluate);
        shopAPI = new ShopAPI(mShouNewApplication);
        initView();
    }

    @Override
    protected void readSaveBundle(Bundle bundle) {
        super.readSaveBundle(bundle);
        if (bundle != null) {
            menuEntity = bundle.getParcelable("menuEntity");
        }
    }

    private void initView() {
        initTitle();
        findViewById(R.id.commit_btn).setOnClickListener(this);
        wrapLayoutiml = (WordWrapLayoutiml) findViewById(R.id.select_img);


        talkParent = findViewById(R.id.talk_parent);
        againParent = findViewById(R.id.again_talk_rl);
        talkContent = (EditText) findViewById(R.id.talk_content);
        dLsgradeRatingBar = (EvaluesRatingBar) findViewById(R.id.dLsgrade);
        dSvgradeRatingBar = (EvaluesRatingBar) findViewById(R.id.dSvgrade);
        dPdgradeRatingBar = (EvaluesRatingBar) findViewById(R.id.dPdgrade);


        //追平

        historyContent = (TextView) findViewById(R.id.history_talk_tv);
        historyTalkTimeTv = (TextView) findViewById(R.id.history_talk_time_tv);
        shopImg = (ImageView) findViewById(R.id.add_talk_img);
        show_img_iml = (WordWrapLayoutiml) findViewById(R.id.show_img);
        again_select_img = (WordWrapLayoutiml) findViewById(R.id.again_select_img);
        again_talk_content = (EditText) findViewById(R.id.again_talk_content);


    }

    /**
     * 添加   添加图片的那个图片
     */
    private void addDeafultImg() {
        ArrayList<ImageView> views = currentWordWrap.getImage(1);
        ImageView addpic = views.get(0);
        addpic.setImageResource(R.drawable.pic_evaluate);
        addpic.setScaleType(ImageView.ScaleType.CENTER);
        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_img();
            }
        });
    }


    private int selectMax = 6;

    /**
     * 选择图片
     */
    private void select_img() {
        int size = imageItems.size();
        if (size >= 6) {
            ToastUtil.showToast("以达到最大限制，请删除重新添加！");
            return;
        }
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new com.wp.baselib.utils.GlideImageLoader());
        imagePicker.setMultiMode(true);   //多选
        imagePicker.setShowCamera(true);  //显示拍照按钮

        imagePicker.setSelectLimit(selectMax - size);    //最多选择9张
        imagePicker.setStyle(CropImageView.Style.CIRCLE);
        imagePicker.setCrop(true);       //不进行裁剪
        Intent intent = new Intent(LaunchEvaluateActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageItem> imageItem = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageItems.addAll(imageItem);
                loadImg();
            }
        }
    }

    private void loadImg() {
        int imageSize = imageItems.size();
        if (imageSize > 0) {
            int fileSize = imageSize + 1;
            ArrayList<ImageView> views = currentWordWrap.getImage(fileSize);
            for (int i = 0; i < fileSize; i++) {
                ImageView imageView = views.get(i);
                if (i == fileSize - 1) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    Glide.with(LaunchEvaluateActivity.this).load(R.drawable.pic_evaluate).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            select_img();
                        }
                    });
                } else {
                    final int currentPositon = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LaunchEvaluateActivity.this, ImagePreviewActivity.class);
                            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, currentPositon);
                            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imageItems);
                            intent.putExtra(ImagePreviewActivity.ISORIGIN, false);
                            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
                        }
                    });
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(LaunchEvaluateActivity.this).load(imageItems.get(i).path).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                }
            }
        } else {
            addDeafultImg();
        }
    }

    private void initTitle() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("发起评价");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        setBarColor(R.color.orgin);
        titleBarView.setLeftIcon(R.drawable.back_arrow);
        titleBarView.getTitleBgTv().setBackgroundResource(R.color.orgin);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.backBtn:
                finish();
                break;
            case R.id.commit_btn:
                shopDiscuss();
                break;
        }
    }

    /**
     * 商品评价
     */
    private void shopDiscuss() {
        if (menuEntity == null) {
            return;
        }
       if (selectMax == 3) {
            String againTalkStr = again_talk_content.getText().toString().trim();
            if (TextUtils.isEmpty(againTalkStr)) {
                ToastUtil.showToast("请输入评价内容");
                return;
            }
        }

        final ArrayList<File> files = new ArrayList<File>();
        if (imageItems.size() > 0) {
            for (ImageItem imageItem : imageItems) {
                CompressConfig config = CompressConfig.ofDefaultConfig();
                new CompressImageUtil(this, config).compress(imageItem.path, new CompressImageUtil.CompressListener() {
                    @Override
                    public void onCompressSuccess(String imgPath) {
                        files.add(new File(imgPath));
                        if (files.size() == imageItems.size()) {
                            postFile(files);
                        }
                    }

                    @Override
                    public void onCompressFailed(String imgPath, String msg) {

                    }
                });

            }


        } else {
            createLoadingDialog();
            judgeType();
        }

    }

    /**
     * 追加评论
     */
    private void getZhuiAddEvaluete() {
        shopAPI.zuijiaEvaluete(menuEntity.getOId(), mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("discuss")) {
                                JSONObject discuss = jsonData.getJSONObject("discuss");
                                did = discuss.getString("dId");
                                id = discuss.getString("dPid");
                                id = "0".equals(id) ? discuss.getString("dMpid") : id;  //商品id
                                shopType = "0".equals(id) ? "productId" : "mallproductId";//判断是什么商品

                                historyContent.setText(discuss.getString("dText"));
                                historyTalkTimeTv.setText(discuss.getString("dDate"));
                                if (menuEntity != null) {
                                    mShouNewApplication.loadImg(menuEntity.getOSimg(), shopImg);
                                }
                                String dimg = discuss.getString("dImg");
                                if (!TextUtils.isEmpty(dimg)) {
                                    ArrayList<ImageView> imageViews;
                                    if (dimg.contains(",")) {
                                        String dims[] = dimg.split(",");
                                        show_img_iml.setShow(true);
                                        show_img_iml.setWidth((int) (mShouNewApplication.terminalWidth * 0.7));
                                        int length = dims.length;
                                        imageViews = show_img_iml.getImage(dims.length);
                                        for (int i = 0; i < length; i++) {
                                            imageViews.get(i).setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            mShouNewApplication.loadImg(dims[i], imageViews.get(i));
                                        }
                                    } else {
                                        imageViews = show_img_iml.getImage(1);
                                        ImageView imageView = imageViews.get(0);
                                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        mShouNewApplication.loadImg(dimg, imageView);
                                    }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (menuEntity == null) {
            finish();
            return;
        }

        if (menuEntity.getoIsdiscuss() == 1) {
            selectMax = 3;
            currentWordWrap = again_select_img;
            talkParent.setVisibility(View.GONE);
            againParent.setVisibility(View.VISIBLE);
            getZhuiAddEvaluete();
        } else if (menuEntity.getoIsdiscuss() == 0) {
            currentWordWrap = wrapLayoutiml;
            selectMax = 6;
            talkParent.setVisibility(View.VISIBLE);
            againParent.setVisibility(View.GONE);
        }
        currentWordWrap.setWidth((int) (mShouNewApplication.terminalWidth * 0.8));
        currentWordWrap.setLineShowCount(3);
        loadImg();
        currentWordWrap.setDelectImgLisener(new WordWrapLayoutiml.DelectImgLisener() {
            @Override
            public void delete(int position) {
                if (imageItems != null && imageItems.size() > position) {
                    imageItems.remove(position);//删除图片
                    loadImg();
                }
            }
        });
    }


    private void postFile(ArrayList<File> files) {
        shopAPI.upTalkImg(files, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("dImg")) {
                                if (selectMax == 6) {
                                    evaluteData(jsonObject.getString("dImg"));
                                } else if (selectMax == 3) {
                                    againEvaluetes(jsonObject.getString("dImg"));
                                }
                            } else {
                                judgeType();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            judgeType();
                        }
                    }
                } else {
                    judgeType();
                }
            }
        });
    }


    private void judgeType() {
        if (selectMax == 6) {
            evaluteData("");
        } else if (selectMax == 3) {
            againEvaluetes("");
        }

    }

    private void evaluteData(String imgUrl) {
        if (menuEntity == null) {
            closeLoadingDialog();
            return;
        }
        final String dicussContent = talkContent.getText().toString().trim();
        final int dPdgrade = Math.round(dPdgradeRatingBar.getRating());
        final int dSvgrade = Math.round(dSvgradeRatingBar.getRating());
        final int dLsgrade = Math.round(dLsgradeRatingBar.getRating());
        shopAPI.discuss(menuEntity.getOId(), dicussContent, dPdgrade, dSvgrade, dLsgrade, imgUrl, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                createLoadingDialog();
                if (exception == null) {
                    ToastUtil.showToast("评价成功");
                    id = String.valueOf(menuEntity.getOPid());
                    id = "0".equals(id) ? menuEntity.getoMpid() : id;  //商品id
                    shopType = "0".equals(id) ? "productId" : "mallproductId";//判断是什么商品
                    if(TextUtils.isEmpty(id)&&TextUtils.isEmpty(shopType)){
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("shopType", shopType);
                    mShouNewApplication.redirectAndPrameter(AllEvalueteActivity.class, bundle);
                    finish();
                } else {
                    ToastUtil.showToast("评价失败");
                }

            }
        });
    }

    /**
     * 再次评价
     */
    private void againEvaluetes(String dImg) {
        String againTalkStr = again_talk_content.getText().toString().trim();
        if (TextUtils.isEmpty(againTalkStr)) {
            ToastUtil.showToast("请输入评价内容");
            closeLoadingDialog();
            return;
        }
        shopAPI.againEvaluetes(did, againTalkStr, dImg, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                createLoadingDialog();
                if (exception == null) {
                    ToastUtil.showToast("评价成功");
                    if(TextUtils.isEmpty(id)&&TextUtils.isEmpty(shopType)){
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("shopType", shopType);
                    mShouNewApplication.redirectAndPrameter(AllEvalueteActivity.class, bundle);
                    finish();
                } else {
                    ToastUtil.showToast("评价失败");
                }
            }
        });
    }


}
