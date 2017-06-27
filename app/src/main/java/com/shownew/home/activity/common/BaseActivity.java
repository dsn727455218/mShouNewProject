package com.shownew.home.activity.common;

import android.os.Bundle;

import com.shownew.home.ShouNewApplication;
import com.wp.baselib.common.ImmersiveActivity;

public class BaseActivity extends ImmersiveActivity {
    protected ShouNewApplication mShouNewApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShouNewApplication = ShouNewApplication.getInstance();
    }
}
