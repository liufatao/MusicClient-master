package me.wcy.music.application;

import android.app.Application;
import android.content.Intent;

import me.wcy.music.service.PlayService;
import me.wcy.music.storage.db.DBManager;

/**
 * 自定义Application
 *
 * @author lft
 * @date 2018/11/27
 */
public class MusicApplication extends Application {
   public static MusicApplication musicApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        AppCache.get().init(this);
        ForegroundObserver.init(this);
        //启动媒体播放服务
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
        musicApplication=this;
    }
    public static MusicApplication getMusicAppContext(){
        return musicApplication;
    }
}
