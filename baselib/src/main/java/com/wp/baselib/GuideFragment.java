package com.wp.baselib;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wp.baselib.utils.FileUtil;

import java.io.IOException;

/**
 * <pre>
 * 新手指引Fragment子类的处理类 
 * 如果需要用到新手指引集成该类
 * 在firstPage里处理遮罩
 * </pre>
 * @description
 * @author summer
 * @date 2014年8月26日 上午10:36:12
 */
public abstract class GuideFragment extends Fragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		String firstName = getClass().getSimpleName();
		if (!FileUtil.checkFileIsExists(getActivity(), firstName)) {
			firstPage();
			try {
				FileUtil.writeLocalFile(getActivity(), firstName, "isFrist".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 第一次进入该页面
	 */
	public abstract void firstPage();
	
}
