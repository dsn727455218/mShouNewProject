package com.shownew.home.activity.msg;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.PublicApi;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * 消息
 */
public class AllMsgActivity extends BaseActivity implements View.OnClickListener {
    private PublicApi mPublicApi;
    private TextView mCarMsg;
    private TextView mCarMsgTime;
    private TextView mActionMsg;
    private TextView mActionMsgTime;
    private TextView mSysMsg;
    private TextView mSysMsgTime;
    private TextView mMsg_count_sys;
    private TextView mMsg_count_car;
    private TextView mMsg_count_action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_msg);
        mPublicApi = new PublicApi(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("消息中心");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);


        mCarMsg = (TextView) findViewById(R.id.car_msg_content);
        findViewById(R.id.msg_car).setOnClickListener(this);
        findViewById(R.id.msg_sys).setOnClickListener(this);
        findViewById(R.id.msg_action).setOnClickListener(this);
        mCarMsgTime = (TextView) findViewById(R.id.car_msg_time);

        mActionMsg = (TextView) findViewById(R.id.action_msg_content);
        mActionMsgTime = (TextView) findViewById(R.id.action_msg_time);

        mSysMsg = (TextView) findViewById(R.id.sys_msg_content);
        mSysMsgTime = (TextView) findViewById(R.id.sys_msg_time);


        mMsg_count_sys = (TextView) findViewById(R.id.msg_count_sys);
        mMsg_count_car = (TextView) findViewById(R.id.msg_count_car);
        mMsg_count_action = (TextView) findViewById(R.id.msg_count_action);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getNewMsg();
    }

    private void getNewMsg() {
        mPublicApi.getMsgAllNew(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONArray jsonObject = json.getJSONArray("data");
                            int length = jsonObject.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject object = (JSONObject) jsonObject.get(i);
                                int count = object.getInt("count");
                                int from = object.getInt("from");
                                switch (from) {
                                    case 0:
                                        mCarMsg.setText(object.getString("text"));
                                        if (count == 0) {
                                            mMsg_count_car.setVisibility(View.INVISIBLE);
                                        } else {
                                            mMsg_count_car.setVisibility(View.VISIBLE);
                                        }
                                        mMsg_count_car.setText(count > 20 ? "20+" : String.valueOf(count));
                                        mCarMsgTime.setText(object.getString("date"));
                                        break;
                                    case 1:
                                        mSysMsg.setText(object.getString("text"));
                                        if (count == 0) {
                                            mMsg_count_sys.setVisibility(View.INVISIBLE);
                                        } else {
                                            mMsg_count_sys.setVisibility(View.VISIBLE);
                                        }
                                        mMsg_count_sys.setText(count > 20 ? "20+" : String.valueOf(count));
                                        mSysMsgTime.setText(object.getString("date"));
                                        break;
                                    case 2:
                                        mActionMsg.setText(object.getString("text"));
                                        if (count == 0) {
                                            mMsg_count_action.setVisibility(View.INVISIBLE);
                                        } else {
                                            mMsg_count_action.setVisibility(View.VISIBLE);
                                        }
                                        mMsg_count_action.setText(count > 20 ? "20+" : String.valueOf(count));
                                        mActionMsgTime.setText(object.getString("date"));
                                        break;
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
    public void onClick(View v) {
        if (!Preference.getBoolean(this, Preference.IS_LOGIN, false)) {
            mShouNewApplication.jumpLoginActivity(this);
            return;
        }
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                overridePendingTransition(R.anim.left_out, R.anim.right_in);
                break;
            case R.id.msg_action:
                mainApplication.redirect(MessageActionActivity.class);
                break;
            case R.id.msg_car:
                bundle.putInt("msgType", 0);
                mainApplication.redirectAndPrameter(MessageCenterActivity.class, bundle);
                break;
            case R.id.msg_sys:
                bundle.putInt("msgType", 1);
                mainApplication.redirectAndPrameter(MessageCenterActivity.class, bundle);
                break;
        }
    }
}
