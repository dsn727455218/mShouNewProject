package com.shownew.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.adapter.MyCarAdapter;
import com.shownew.home.module.DeviceAPI;
import com.shownew.home.module.entity.CarEntity;
import com.shownew.home.utils.dialog.CommonDialog;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.utils.compress.CompressConfig;
import com.wp.baselib.utils.compress.CompressImageUtil;
import com.wp.baselib.utils.imagepicker.ImagePicker;
import com.wp.baselib.utils.imagepicker.bean.ImageItem;
import com.wp.baselib.utils.imagepicker.ui.ImageGridActivity;
import com.wp.baselib.utils.imagepicker.view.CropImageView;
import com.wp.baselib.widget.TitleBarView;
import com.wp.zxing.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Response;

public class MyCarActivity extends BaseActivity implements View.OnClickListener {

    private XRecyclerView mXRecyclerView;
    private MyCarAdapter mMyCarAdapter;
    private DeviceAPI mDeviceAPI;
    private ArrayList<CarEntity> mCarEntities;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        mDeviceAPI = new DeviceAPI(mShouNewApplication);
        mCarEntities = new ArrayList<CarEntity>();
        initViews();
    }

    private void initViews() {
        mXRecyclerView = (XRecyclerView) findViewById(R.id.my_car_recyclerView);
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        findViewById(R.id.empty_view).setVisibility(View.GONE);
        titleBarView.setTitle("我的车辆");
        titleBarView.setRightText("新增");
        titleBarView.setOnRightTvClick(this);

        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        mMyCarAdapter = new MyCarAdapter(this, mCarEntities);
        mXRecyclerView.setAdapter(mMyCarAdapter);
        mXRecyclerView.setLoadingMoreEnabled(false);
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(mShouNewApplication));
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getCarDataList();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        isRefresh = true;
        getCarDataList();
    }

    private boolean isRefresh;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.commitFeed:
                addCar();
                break;
        }
    }

    private void getCarDataList() {
        mDeviceAPI.getCarInfoList(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (isRefresh) {
                    mXRecyclerView.refreshComplete();
                    if (mCarEntities.size() > 0) {
                        mCarEntities.clear();
                    }
                }
                if (mCarEntities.size() > 0) {
                    mXRecyclerView.loadMoreComplete();
                }
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("carList")) {
                                String carList = jsonObject.getString("carList");
                                if (!TextUtils.isEmpty(carList)) {
                                    ArrayList<CarEntity> carEntities = JsonUtils.fromJson(carList, new TypeToken<ArrayList<CarEntity>>() {
                                    }.getType());
                                    mCarEntities.addAll(carEntities);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mCarEntities.size() > 0) {
                        mXRecyclerView.setNoMore(true);
                    }
                }
                mMyCarAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 删除车辆
     *
     * @param cId
     */
    public void delectCar(final String cId) {
        new CommonDialog(this, "是否删除这辆车").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (1 == flag) {
                    delectCarD(cId);
                }
            }
        }).setCancelable(true).show();
    }

    /**
     * 注销车辆
     *
     * @param cId
     */
    private void delectCarD(String cId) {

        mDeviceAPI.delectCar(cId, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (exception == null) {
                    ToastUtil.showToast("注销成功！");
                    refresh();
                } else {
                    ToastUtil.showToast("注销失败！");
                }
            }

            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }
        });

    }

    /**
     * 绑定车辆
     *
     * @param cId
     */
    public void bindCar(final String cId, String isBinb) {
        if ("1".equals(isBinb)) {
            ToastUtil.showToast("该车辆已被绑定");
            return;
        }
        new CommonDialog(this, "是否绑定这辆车").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (1 == flag) {
                    bindCarD(cId);
                }
            }
        }).setCancelable(true).show();
    }

    /**
     * 车辆绑定
     *
     * @param cId
     */
    private void bindCarD(String cId) {
        mDeviceAPI.bindCar(cId, mShouNewApplication.new ShouNewHttpCallBackLisener() {

            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (exception == null) {
                    ToastUtil.showToast("绑定成功！");
                    refresh();
                } else {
                    ToastUtil.showToast("绑定失败！");
                }
            }

            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }
        });
    }

    /**
     * 修改车辆
     *
     * @param carEntity
     */
    public void modifyCar(CarEntity carEntity) {
        if (null != carEntity) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("car", carEntity);
            mainApplication.redirectAndPrameter(NewCarRegisterActivity.class, bundle);
        }
    }

    //添加车辆
    public void addCar() {
        mainApplication.redirectAndPrameterResult(this, CaptureActivity.class, null, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            String qrCode = data.getStringExtra("QRCode");
            if (com.wp.zxing.config.Config.decode_time_out.equals(qrCode)) {
                ToastUtil.showToast("二维码扫描超时！请手动输入设备号！");
                mainApplication.redirect(NewCarRegisterActivity.class);
            } else if (com.wp.zxing.config.Config.jump_saoma.equals(qrCode)) {
                mainApplication.redirect(NewCarRegisterActivity.class);
            } else {
                if (qrCode.contains(Config.SN)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("QRCode", qrCode);
                    mainApplication.redirectAndPrameter(NewCarRegisterActivity.class, bundle);
                }
            }

        }
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

    private String cid;

    /**
     * 上传车辆图片
     *
     * @param cId
     */
    public void uploadCarImg(String cId) {
        this.cid = cId;
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new com.wp.baselib.utils.GlideImageLoader());
        imagePicker.setMultiMode(false);   //多选
        imagePicker.setShowCamera(true);  //显示拍照按钮
        //                imagePicker.setSelectLimit(1);    //最多选择9张
        imagePicker.setCrop(true);       //不进行裁剪
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    /**
     * 上传图片
     *
     * @param filePath
     */
    private void uploadFile(String filePath) {
        CompressConfig config = CompressConfig.ofDefaultConfig();
        new CompressImageUtil(this, config).compress(filePath, new CompressImageUtil.CompressListener() {
            @Override
            public void onCompressSuccess(final String imgPath) {
                if (TextUtils.isEmpty(imgPath) && TextUtils.isEmpty(cid))
                    return;
                mDeviceAPI.uploadCarImg(cid, new File(imgPath), mShouNewApplication.new ShouNewHttpCallBackLisener() {
                    @Override
                    protected void onLoading() {
                        super.onLoading();
                        createLoadingDialog();
                    }

                    @Override
                    protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                        closeLoadingDialog();
                        if (exception == null) {
                            ToastUtil.showToast("上传成功");
                            refresh();
                        } else {
                            ToastUtil.showToast("上传失败");
                        }
                    }
                });
            }

            @Override
            public void onCompressFailed(String imgPath, String msg) {
                ToastUtil.showToast("压缩失败");
            }
        });
    }
}
