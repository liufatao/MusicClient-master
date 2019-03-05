package me.wcy.music.executor;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import me.wcy.music.R;
import me.wcy.music.activity.AboutActivity;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.activity.SettingActivity;
import me.wcy.music.activity.ThemeActivity;
import me.wcy.music.constants.Actions;
import me.wcy.music.service.PlayService;
import me.wcy.music.service.QuitTimer;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.ToastUtils;

/**
 * 导航菜单执行器
 *
 */
public class NaviMenuExecutor {
    private MusicActivity activity;

    public NaviMenuExecutor(MusicActivity activity) {
        this.activity = activity;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                MusicUtils.startActivity(SettingActivity.class);
                return true;
            case R.id.action_night:
                nightMode();
                break;
            case R.id.action_theme:
                MusicUtils.startActivity(ThemeActivity.class);
                break;
            case R.id.action_timer:
                timerDialog();
                return true;
            case R.id.action_exit:
                activity.finish();
                PlayService.startCommand(activity, Actions.ACTION_STOP);
                return true;
            case R.id.action_about:
                MusicUtils.startActivity(AboutActivity.class);
                return true;
        }
        return false;
    }


    private void nightMode() {
        int preTheme = 0;
        if(MusicUtils.getNightMode(activity)){
            //当前为夜间模式，则恢复之前的主题
            MusicUtils.setNightMode(activity,false);
            preTheme = MusicUtils.getPreTheme(activity);
            MusicUtils.setTheme(activity,preTheme);
        }else {
            //当前为白天模式，则切换到夜间模式
            MusicUtils.setNightMode(activity,true);
            MusicUtils.setTheme(activity,ThemeActivity.THEME_SIZE-1);
        }
        activity.recreate();

    }

    private void timerDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.menu_timer)
                .setItems(activity.getResources().getStringArray(R.array.timer_text), (dialog, which) -> {
                    int[] times = activity.getResources().getIntArray(R.array.timer_int);
                    startTimer(times[which]);
                })
                .show();
    }

    private void startTimer(int minute) {
        QuitTimer.get().start(minute * 60 * 1000);
        if (minute > 0) {
            ToastUtils.show(activity.getString(R.string.timer_set, String.valueOf(minute)));
        } else {
            ToastUtils.show(R.string.timer_cancel);
        }
    }
}
