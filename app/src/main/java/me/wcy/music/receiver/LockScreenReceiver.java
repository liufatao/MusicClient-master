package me.wcy.music.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


import me.wcy.music.activity.LockScreenActivity;

/**
 * 监听锁屏广播
 * @author Administr
 */
public class LockScreenReceiver extends BroadcastReceiver {
    private static final String TAG="LockScreenReceiver";
    private static Handler handler=new Handler();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
            Log.d(TAG,"收到了锁屏广播");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(context,LockScreenActivity.class);
                }
            },1*1000);

        }
    }

    private void startActivity(Context context,Class<?> cls){
        Intent intent=new Intent(context,cls);
        //标志位FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS，是为了避免在最近使用程序列表出现Service所启动的Activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }
}
