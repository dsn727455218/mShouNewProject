package com.shownew.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.DeviceAPI;
import com.shownew.home.module.entity.CarEntity;
import com.shownew.home.module.entity.UserEntity;
import com.shownew.home.utils.dialog.SelectBatteryDialog;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.RegexUtils;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;
import com.wp.zxing.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Response;

public class NewCarRegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText mDeviceType_ed;
    /**
     * 设备号
     */
    private EditText mDeviceTypeNumberEt;
    /**
     * 车牌号
     */
    private EditText mNumberPlateDt;
    /**
     * 车架号
     */
    private EditText mCarFrameNumer;
    /**
     * 身份证号
     */
    private EditText mVertifyNumber;
    /**
     * 用户名
     */
    private EditText mUserName;
    /**
     * 车辆的名字
     */
    private EditText mMyCarNameEt;
    /**
     * 电池的类型
     */
    private TextView mBatteryType;
    /**
     * 车品牌的名字
     */
    private TextView mCarNameEt;
    /**
     * 车型号
     */
    private EditText mCarModeNumber;
    private DeviceAPI mDeviceAPI;
    private Button mCommit;
    private CarEntity mCarEntity;
    private ArrayList<String> mCarTypeListData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car_register);
        mDeviceAPI = new DeviceAPI(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("新车注册");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setTitleSize(20);
        mDeviceType_ed = (EditText) findViewById(R.id.device_type_tv);
        findViewById(R.id.select_battery_ll).setOnClickListener(this);
        View saoma = findViewById(R.id.saoma);
        saoma.setOnClickListener(this);

        mDeviceTypeNumberEt = (EditText) findViewById(R.id.device_type_tv);
        mCarNameEt = (TextView) findViewById(R.id.car_name_tv);
        mBatteryType = (TextView) findViewById(R.id.batter_type_tv);
        mMyCarNameEt = (EditText) findViewById(R.id.my_car_name_tv);
        mUserName = (EditText) findViewById(R.id.userName_et);
        mVertifyNumber = (EditText) findViewById(R.id.vertify_number);
        mCarFrameNumer = (EditText) findViewById(R.id.car_frame_number);
        mNumberPlateDt = (EditText) findViewById(R.id.number_palte_et);
        mCarModeNumber = (EditText) findViewById(R.id.car_model_number);
        mCommit = (Button) findViewById(R.id.commit_new_car);
        mCarNameEt.setOnClickListener(this);
        mCommit.setOnClickListener(this);
        if (mBundle != null) {
            String qrCode = mBundle.getString("QRCode");
            if (!TextUtils.isEmpty(qrCode)) {
                int length = 13 + Config.SN.length();
                mDeviceType_ed.setText(qrCode.substring(Config.SN.length(), length));
                mCarNameEt.setText(qrCode.substring(length, qrCode.length()));
            }
            mCarEntity = mBundle.getParcelable("car");
            mCommit.setText("提  交");
            if (null != mCarEntity) {
                titleBarView.setTitle("车辆信息");
                mDeviceTypeNumberEt.setText(mCarEntity.getCHardcode());
                mDeviceTypeNumberEt.setEnabled(false);
                saoma.setEnabled(false);
                mCarNameEt.setText(mCarEntity.getCMark());
                mCarNameEt.setEnabled(false);
                mVertifyNumber.setText(mCarEntity.getCUidcard());
                mCarModeNumber.setText(mCarEntity.getCModelnum());
                mCarFrameNumer.setText(mCarEntity.getCFramenum());
                mNumberPlateDt.setText(mCarEntity.getCLicencenum());
                mUserName.setText(mCarEntity.getCUname());
                mBatteryType.setText(String.valueOf(mCarEntity.getCBattery()));
                mMyCarNameEt.setText(mCarEntity.getCName());
                mCommit.setText("修  改");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_battery_ll:
                if (null != mCarEntity) {
                    if ("电动车".equals(mCarEntity.getCType())) {
                        ActionSheetDialog(null);
                        return;
                    } else if ("汽车".equals(mCarEntity.getCType())) {
                        return;
                    }
                }
                ArrayList<String> data = new ArrayList<>();
                data.add("汽车免选");
                ActionSheetDialog(data);
                break;
            case R.id.saoma:
                mainApplication.redirectAndPrameterResult(this, CaptureActivity.class, null, 1);
                break;
            case R.id.commit_new_car:
                newCarRegister();
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.car_name_tv:
                if (null != mCarTypeListData && mCarTypeListData.size() > 0) {
                    new SelectBatteryDialog(this, "选择车辆品牌", mCarTypeListData).setSelectBatteryLisener(new SelectBatteryDialog.SelectBatteryLisener() {
                        @Override
                        public void sure(String values) {
                            mCarNameEt.setText(values);
                        }
                    }).setCancelable(true).show();
                } else {
                    getCarType();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCarType();
    }

    private void newCarRegister() {
        String batteryStr = mBatteryType.getText().toString();
        String carNameStr = mCarNameEt.getText().toString();
        String myCarNameStr = mMyCarNameEt.getText().toString();
        String deviceTypeNumber = mDeviceTypeNumberEt.getText().toString();

        HashMap<String, String> hashMap = new HashMap<String, String>();


        if (TextUtils.isEmpty(batteryStr)) {
            ToastUtil.showToast("请选择电池类型");
            return;
        }
        if (TextUtils.isEmpty(carNameStr)) {
            ToastUtil.showToast("请输入车辆的品牌");
            return;
        }
        if (TextUtils.isEmpty(myCarNameStr)) {
            ToastUtil.showToast("请输入车辆名");
            return;
        }
        if (TextUtils.isEmpty(deviceTypeNumber)) {
            ToastUtil.showToast("请输入设备号");
            return;
        }
        if (deviceTypeNumber.length() < 13) {
            ToastUtil.showToast("设备号为13位");
            return;
        }

        hashMap.put("battery", batteryStr);
        if (null != mCarEntity) {
            hashMap.put("id", String.valueOf(mCarEntity.getCId()));
            hashMap.put("method", "upCar");
        } else {
            hashMap.put("method", "register");
        }
        hashMap.put("hardCode", deviceTypeNumber);
        hashMap.put("mark", carNameStr);
        hashMap.put("name", myCarNameStr);
        UserEntity userEntity = mDeviceAPI.getUserInfo();
        if (userEntity != null) {
            UserEntity.UserBean userBean = userEntity.getUser();
            hashMap.put("phone", userBean.getUPhone());
        }


        hashMap.put("uname", mUserName.getText().toString());
        String uidCard = mVertifyNumber.getText().toString();
        if (!TextUtils.isEmpty(uidCard) && !RegexUtils.checkIdCard(uidCard)) {
            ToastUtil.showToast("请输入正确的身份证号");
            return;
        }
        hashMap.put("uidCard", mVertifyNumber.getText().toString());
        hashMap.put("modelNum", mCarModeNumber.getText().toString());
        String carFrameNumber = mCarFrameNumer.getText().toString();//车架号
        if (!TextUtils.isEmpty(carFrameNumber) && carFrameNumber.length() == 17) {
            ToastUtil.showToast("车架号为17位");
            return;
        }
        hashMap.put("frameNum", carFrameNumber);
        String carAcount = mNumberPlateDt.getText().toString();//车牌号
        if (!TextUtils.isEmpty(carAcount) && carAcount.length() == 7) {
            ToastUtil.showToast("车牌号为7位");
            return;
        }
        hashMap.put("licenceNum", carAcount);

        registerCar(hashMap);
    }

    private void registerCar(final HashMap<String, String> hashMap) {
        if (null != hashMap) {
            mDeviceAPI.newsCarRegister(hashMap, mShouNewApplication.new ShouNewHttpCallBackLisener() {
                @Override
                protected Object parseData(String result) {
                    return null;
                }

                @Override
                protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                    closeLoadingDialog();
                    mCommit.setEnabled(true);
                    if (null == exception) {
                        if (null != mCarEntity) {
                            ToastUtil.showToast("修改车辆成功");
                            finish();
                        }
                        //result：-3=硬件被注册    -2=车牌号已被注册   -1=提供的手机号用户不存在    0=车辆名称重名    1=注册成功）
                        if (json.has("data")) {
                            try {
                                JSONObject jsonObject = json.getJSONObject("data");
                                if (jsonObject.has("result")) {
                                    int result = jsonObject.getInt("result");
                                    String msg = "";
                                    switch (result) {
                                        case 1:
                                            msg = "注册成功";
                                            if (!Preference.getBoolean(NewCarRegisterActivity.this, Preference.IS_LOGIN, false)) {
                                                mShouNewApplication.jumpLoginActivity(NewCarRegisterActivity.this);
                                            }
                                            finish();
                                            break;
                                        case 0:
                                            msg = "车辆名称重名";
                                            break;
                                        case -1:
                                            msg = "提供的手机号用户不存在";
                                            break;
                                        case -2:
                                            msg = "车牌号已被注册";
                                            break;
                                        case -3:
                                            msg = "硬件被注册";
                                            break;
                                        //result：-5=硬件和品牌不匹配     -4=硬件不存在
                                        case -4:
                                            msg = "硬件不存在";
                                            break;
                                        case -5:
                                            msg = "硬件和品牌不匹配";
                                            break;
                                    }
                                    ToastUtil.showToast(msg);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    } else {
                        if (null != mCarEntity) {
                            ToastUtil.showToast("修改车辆失败");
                        } else {
                            ToastUtil.showToast("注册失败");
                        }

                        mCommit.setEnabled(true);
                    }
                }

                @Override
                protected void onLoading() {
                    super.onLoading();
                    createLoadingDialog();
                    mCommit.setEnabled(false);
                }
            });
        }

    }


    private void ActionSheetDialog(ArrayList<String> data) {
        if (data == null)
            data = new ArrayList<>();
        data.add("48");
        data.add("60");
        data.add("72");
        data.add("96");
        data.add("108");
        new SelectBatteryDialog(this, "电池型号(v)", data).setSelectBatteryLisener(new SelectBatteryDialog.SelectBatteryLisener() {
            @Override
            public void sure(String values) {
                mBatteryType.setText(values);
            }
        }).setCancelable(true).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            String qrCode = data.getStringExtra("QRCode");
            if (com.wp.zxing.config.Config.decode_time_out.equals(qrCode)) {
                ToastUtil.showToast("二维码扫描超时！请手动输入设备号！");
            } else if (!com.wp.zxing.config.Config.jump_saoma.equals(qrCode)) {
                if (qrCode.contains(Config.SN)) {
                    int length = 13 + Config.SN.length();
                    mDeviceType_ed.setText(qrCode.substring(Config.SN.length(), length));
                    mCarNameEt.setText(qrCode.substring(length, qrCode.length()));
                } else {
                    ToastUtil.showToast("不是首牛硬件二维码");
                }
            }
        }

    }

    /**
     * 获取车辆类型
     */
    private void getCarType() {
        mDeviceAPI.getCarType(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("carTypeList")) {
                                JSONArray carTypeList = jsonData.getJSONArray("carTypeList");
                                mCarTypeListData = new ArrayList<String>();
                                int length = carTypeList.length();
                                for (int i = 0; i < length; i++) {
                                    mCarTypeListData.add(carTypeList.getString(i));
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
