package me.wcy.music.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import me.wcy.music.R;
import me.wcy.music.activity.ModelActivity;
import me.wcy.music.constants.Constant;
import me.wcy.music.listener.OnDeleteUpdateListener;
import me.wcy.music.model.Music;
import me.wcy.music.widget.MusicPopMenuWindow;

public class PopWindowUtils {
    private static PopWindowUtils popWindowUtils;
    private OnDeleteUpdateListener onDeleteUpdateListener;

    public void setOnDeleteUpdateListener(OnDeleteUpdateListener onDeleteUpdateListener) {
        this.onDeleteUpdateListener = onDeleteUpdateListener;
    }

    public static PopWindowUtils getInstace() {
        if (popWindowUtils == null) {
            popWindowUtils = new PopWindowUtils();
        }
        return popWindowUtils;
    }

    /**
     * 更多弹出框
     *
     * @param activity
     * @param musicInfo
     * @param parentView
     */
    public void showPopFormBottom(Activity activity, Music musicInfo, View parentView) {
        MusicPopMenuWindow menuPopupWindow = new MusicPopMenuWindow(activity, musicInfo, parentView, Constant.ACTIVITY_LOCAL);
//      设置Popupwindow显示位置（从底部弹出）
        menuPopupWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha = 0.7f;
        activity.getWindow().setAttributes(params);

        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        menuPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                params.alpha = 1f;
                activity.getWindow().setAttributes(params);
            }
        });
        menuPopupWindow.setOnDeleteUpdateListener(new OnDeleteUpdateListener() {
            @Override
            public void onDeleteUpdate() {
                onDeleteUpdateListener.onDeleteUpdate();
            }
        });

    }
}
