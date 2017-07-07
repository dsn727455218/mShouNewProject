package com.shownew.home.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.AddressEntity;
import com.shownew.home.module.entity.CityEntity;
import com.shownew.home.utils.dialog.CitySelectDialog;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.Response;

public class AddressEditActivity extends BaseActivity implements View.OnClickListener {

    private List<CityEntity> mCityEntities;
    private TextView mSelectAddress;
    private EditText mReceiptPersonName;
    private EditText mReceiptPhone;
    private EditText mReceiptDetailAddress;
    private UserAPI mUserAPI;
    private TextView mComplete;
    private AddressEntity mAddressEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("地址编辑");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);

        mReceiptPersonName = (EditText) findViewById(R.id.receipt_person_name);
        mReceiptPhone = (EditText) findViewById(R.id.receipt_phone_tv);
        mReceiptDetailAddress = (EditText) findViewById(R.id.receipt_detail_address);
        mSelectAddress = (TextView) findViewById(R.id.select_address);
        mSelectAddress.setOnClickListener(this);
        mComplete = (TextView) findViewById(R.id.complete);
        mComplete.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCityData();
        getAddress();
    }

    private void getAddress() {
        mUserAPI.getDefaultAddress(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("location")) {
                                mAddressEntity = JsonUtils.fromJson(jsonData.getString("location"), AddressEntity.class);
                                if (null != mAddressEntity) {
                                    mReceiptPersonName.setText(mAddressEntity.getLName());
                                    mReceiptPhone.setText(mAddressEntity.getLPhone());
                                    mReceiptDetailAddress.setText(mAddressEntity.getLAddress());
                                    mSelectAddress.setText(mAddressEntity.getLCity());
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

    private void getCityData() {
        new AsyncTask<Void, Void, List<CityEntity>>() {
            @Override
            protected List<CityEntity> doInBackground(Void... params) {
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    InputStream stream = getAssets().open("city.json");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String len;
                    while ((len = reader.readLine()) != null) {
                        stringBuilder.append(len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return JsonUtils.fromJson(stringBuilder.toString(), new TypeToken<List<CityEntity>>() {
                }.getType());
            }


            @Override
            protected void onPostExecute(List<CityEntity> cityEntities) {
                mCityEntities = cityEntities;
            }
        }.execute();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complete:
                commitAddress();
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.select_address:
                if (null != mCityEntities) {
                    new CitySelectDialog(AddressEditActivity.this, mCityEntities).setCitySelectLisener(new CitySelectDialog.CitySelectLisener() {
                        @Override
                        public void sure(String address) {
                            mSelectAddress.setText(address);
                        }
                    }).setCancelable(true).show();
                } else {
                    ToastUtil.showToast("获取数据中...");
                    getCityData();
                }
                break;
        }
    }

    private void commitAddress() {
        String name = mReceiptPersonName.getText().toString();
        String phone = mReceiptPhone.getText().toString();
        String address = mReceiptDetailAddress.getText().toString();
        String selectAddress = mSelectAddress.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToast("请输入收货人名字");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast("请输入收货人电话号码");
            return;
        }
        if (!StringUtil.isMobileNo(phone)) {
            ToastUtil.showToast("请输入正确的电话号码");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtil.showToast("请输入详细地址");
            return;
        }
        if (TextUtils.isEmpty(selectAddress)) {
            ToastUtil.showToast("请选择地址");
            return;
        }
        String lId = "";
        if (mAddressEntity != null) {
            lId = mAddressEntity.getLId();
        }
        commitAddressToService(lId, name, phone, address, selectAddress);
    }

    private void commitAddressToService(String lId, String name, String phone, String address, String selectAddress) {
        mUserAPI.modifyAddress(lId, selectAddress, address, phone, name, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (null == exception) {
                    ToastUtil.showToast("设置成功");
                    finish();
                } else {
                    ToastUtil.showToast("设置失败");
                    mComplete.setEnabled(true);
                }
            }

            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }
        });
    }

}
