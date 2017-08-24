package com.shownew.home.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;

import com.shownew.home.R;
import com.shownew.home.fragment.CarServiceFragment;
import com.shownew.home.fragment.HomeFragment;
import com.shownew.home.fragment.MyFragment;
import com.wp.baselib.common.TabFragmentActivity;
import com.wp.baselib.utils.ExitUtil;

public class MainActivity extends TabFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBottomMenuAndIntent(new String[]{"1", "2", "3"}, new int[]{R.drawable.menu_home, R.drawable.menu_service, R.drawable.menu_user}, new int[]{R.string.tab_bottom_home, R.string.tab_bottom_carservice, R.string.tab_bottom_my}, getIntentArr(), R.color.tab_color, 0);
        //设置背景
        setTabBackground(R.drawable.bg_bottom_tab);
        //特定跳转
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String menu_id = bundle.getString("menu_id");
            startIabIntent(menu_id);
        }
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment f = getSupportFragmentManager().findFragmentByTag("1");
        f.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission != PackageManager.PERMISSION_GRANTED) {//如果没有权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
}
