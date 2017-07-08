package com.shownew.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shownew.home.Config;


/**消息 广播监听
 * @author Jason
 * @version 1.0
 * @date 2017/7/8 0008
 */

public class MsgBroadCasetRecivier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            String action = intent.getAction();
            if(Config.BROADCASEREVEIVER_MGS_ACTION.equals(action)){
                if(mMsgLisener!=null){
                    mMsgLisener.callback();
                }
            }
        }

    }

    private  MsgLisener mMsgLisener;

    public void setMsgLisener(MsgLisener msgLisener) {
        mMsgLisener = msgLisener;
    }

    public interface MsgLisener{
        void callback();
    }
}
