package com.shownew.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.fragment.CarServiceFragment;
import com.shownew.home.fragment.HomeFragment;
import com.shownew.home.fragment.MyFragment;
import com.wp.baselib.common.TabFragmentActivity;
import com.wp.baselib.utils.ExitUtil;

public class MainActivity extends TabFragmentActivity {
    private ShouNewApplication mShouNewApplication;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShouNewApplication = ShouNewApplication.getInstance();
        //记录每个打开的Activity
        ExitUtil.getInstance().addInstance(this);
        setBottomMenuAndIntent(new String[]{"1", "2", "3"}, new int[]{R.drawable.menu_home, R.drawable.menu_service, R.drawable.menu_user}, new int[]{R.string.tab_bottom_home, R.string.tab_bottom_carservice, R.string.tab_bottom_my}, getIntentArr(), R.color.tab_color, 0);


        //检查是否有更新


        //设置背景
        setTabBackground(R.drawable.bg_bottom_tab);
        //特定跳转
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String menu_id = bundle.getString("menu_id");
            startIabIntent(menu_id);
        }
    }






    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /**
     * 准备tab的内容Intent
     */
    private Class[] getIntentArr() {
        return new Class[]{HomeFragment.class, CarServiceFragment.class, MyFragment.class};
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //退出每个打开的Activity
            ExitUtil.getInstance().exitShowToast(this);
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment f = getSupportFragmentManager().findFragmentByTag("1");
        f.onActivityResult(requestCode, resultCode, data);
    }
}
