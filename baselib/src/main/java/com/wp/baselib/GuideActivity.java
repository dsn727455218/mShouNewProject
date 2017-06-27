package com.wp.baselib;


import com.wp.baselib.utils.FileUtil;

import java.io.IOException;

/**
 * <pre>
 * 新手指引处理类
 * 如果需要用到新手指引集成该类
 * 在firstPage里处理遮罩
 * </pre>
 * @description
 * @author summer
 * @date 2014年8月26日 上午10:36:12
 */
public abstract class GuideActivity extends AndroidActivity {

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			String firstName = getClass().getSimpleName();
			if (!FileUtil.checkFileIsExists(this, firstName)) {
				firstPage();
				try {
					FileUtil.writeLocalFile(this, firstName, "isFrist".getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 第一次进入该页面
	 */
	public abstract void firstPage();
	
}
