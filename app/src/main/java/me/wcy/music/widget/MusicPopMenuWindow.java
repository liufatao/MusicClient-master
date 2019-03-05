package me.wcy.music.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import me.wcy.music.R;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.Constant;
import me.wcy.music.listener.OnDeleteUpdateListener;
import me.wcy.music.listener.OnMusicClickListener;
import me.wcy.music.model.LatestMusic;
import me.wcy.music.model.LoveMusic;
import me.wcy.music.model.Music;
import me.wcy.music.model.PlayListInfo;
import me.wcy.music.storage.db.DBManager;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.storage.db.greendao.MusicDao;
import me.wcy.music.utils.DateTimeUtil;
import me.wcy.music.utils.MusicUtils;

public class MusicPopMenuWindow extends PopupWindow {
    private static final String TAG = "MusicPopMenuWindow";
    private View view;
    private Activity activity;
    private TextView nameTv;
    private LinearLayout loveLl;
    private LinearLayout deleteLl;
    private LinearLayout cancelLl;
    private Music musicInfo;
    private PlayListInfo playListInfo;
    private int witchActivity = Constant.ACTIVITY_LOCAL;
    private View parentView;
    private MusicDaoUtils musicDaoUtils;
    private ImageView popwin_love_iv;
    private TextView popwin_love_tv;
    private OnMusicClickListener onMusicClickListener;

    public MusicPopMenuWindow(Activity activity, Music musicInfo, View parentView, int witchActivity) {
        super(activity);
        this.activity = activity;
        this.musicInfo = musicInfo;
        this.parentView = parentView;
        this.witchActivity = witchActivity;
        initView();
        musicDaoUtils = MusicDaoUtils.getInstance();
    }

    public MusicPopMenuWindow(Activity activity, Music musicInfo, View parentView, int witchActivity, PlayListInfo playListInfo) {
        super(activity);
        this.activity = activity;
        this.musicInfo = musicInfo;
        this.parentView = parentView;
        this.witchActivity = witchActivity;
        this.playListInfo = playListInfo;
        initView();
    }

    /**
     * 收藏按钮监听
     *
     * @param onMusicClickListener
     */
    public void setOnMusicClickListener(OnMusicClickListener onMusicClickListener) {
        this.onMusicClickListener = onMusicClickListener;
    }

    @SuppressLint("StringFormatMatches")
    private void initView() {
        this.view = LayoutInflater.from(activity).inflate(R.layout.pop_window_menu, null);
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高,不设置显示不出来
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 设置外部可点击
        this.setOutsideTouchable(true);

        // 设置弹出窗体的背景
        this.setBackgroundDrawable(activity.getResources().getDrawable(R.color.colorWhite));

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.pop_window_animation);


        // 添加OnTouchListener监听判断获取触屏位置，如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        nameTv = (TextView) view.findViewById(R.id.popwin_name_tv);
        loveLl = (LinearLayout) view.findViewById(R.id.popwin_love_ll);
        deleteLl = (LinearLayout) view.findViewById(R.id.popwin_delete_ll);
        cancelLl = (LinearLayout) view.findViewById(R.id.popwin_cancel_ll);
        popwin_love_iv = view.findViewById(R.id.popwin_love_iv);
        popwin_love_tv = view.findViewById(R.id.popwin_love_tv);
        nameTv.setText(String.format(AppCache.get().getContext().getString(R.string.song), musicInfo.getTitle()));

        if (MusicDaoUtils.getInstance().queryLoveSongID(musicInfo.getSongId()) != null) {
            popwin_love_iv.setImageResource(R.drawable.ic_love_hover);
            popwin_love_tv.setText(R.string.my_loved);
        }

        loveLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLove();
            }
        });


        deleteLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除歌曲
                deleteOperate(musicInfo, activity);
                dismiss();
            }
        });

        cancelLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    private LoveMusic PackagIng(Music music) {
        LoveMusic loveMusic = new LoveMusic();
        loveMusic.setType(music.getType());
        loveMusic.setSongId(music.getSongId());
        loveMusic.setTitle(music.getTitle());
        loveMusic.setArtist(music.getArtist());
        loveMusic.setAlbum(music.getAlbum());
        loveMusic.setAlbumId(music.getAlbumId());
        loveMusic.setCoverPath(music.getCoverPath());
        loveMusic.setDuration(music.getDuration());
        loveMusic.setPath(music.getPath());
        loveMusic.setParentPath(music.getParentPath());
        loveMusic.setFileName(music.getFileName());
        loveMusic.setFileSize(music.getFileSize());

        return loveMusic;
    }

    /**
     * 添加取消收藏
     */
    private void addLove() {
        if (onMusicClickListener != null) {
            onMusicClickListener.onLoveMusic();
        }
        //添加收藏
        dismiss();
        MusicDaoUtils.getInstance().insertLoveMusic(PackagIng(musicInfo));
        View view = LayoutInflater.from(activity).inflate(R.layout.my_love_toast, null);
        ImageView iv_toast_love = view.findViewById(R.id.iv_toast_love);
        TextView tv_toast_content = view.findViewById(R.id.tv_toast_content);
        if (MusicDaoUtils.getInstance().queryLoveSongID(musicInfo.getSongId()) != null) {
            iv_toast_love.setImageResource(R.drawable.ic_love_hover);
            tv_toast_content.setText(R.string.add_love_succeed);
        } else {
            iv_toast_love.setImageResource(R.drawable.ic_love);
            tv_toast_content.setText(R.string.cancel_love_succeed);

        }
        Toast toast = new Toast(activity);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void deleteOperate(Music musicInfo, final Context context) {
        final long curId = musicInfo.getSongId();
        final int musicId = MusicUtils.getIntShared(Constant.KEY_ID);
        final String path = musicDaoUtils.getMusicPath(curId);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_delete_file, null);
        final CheckBox deleteFile = (CheckBox) view.findViewById(R.id.dialog_delete_cb);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();

        builder.setView(view);

        builder.setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (deleteFile.isChecked()) {
                    //同时删除文件
                    //删除的是当前播放的音乐
                    File file = new File(path);
                    if (file.exists()) {
                        deleteMediaDB(file, context);
                        boolean ret = file.delete();
                        Log.e(TAG, "onClick: ret = " + ret);
                        musicDaoUtils.deleteMusic(musicInfo);
                    }
//                    if (curId == musicId){
//                        Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
//                        intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
//                        context.sendBroadcast(intent);
//                        MyMusicUtil.setShared(Constant.KEY_ID,dbManager.getFirstId(Constant.LIST_ALLMUSIC));
//                    }
                } else {
                    //从列表移除
//                    if (witchActivity == Constant.ACTIVITY_MYLIST){
//                        dbManager.removeMusicFromPlaylist(curId,playListInfo.getId());
//                    }else {
//                        dbManager.removeMusic(curId,witchActivity);
//                    }
//
//                    if (curId == musicId) {
//                        //移除的是当前播放的音乐
//                        Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
//                        intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
//                        context.sendBroadcast(intent);
//                    }
                }
                if (onDeleteUpdateListener != null) {
                    onDeleteUpdateListener.onDeleteUpdate();
                }
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public static void deleteMediaDB(File file, Context context) {
        String filePath = file.getPath();
//        if(filePath.endsWith(".mp3")){
        int res = context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Media.DATA + "= \"" + filePath + "\"",
                null);
        Log.i(TAG, "deleteMediaDB: res = " + res);
//        }
    }

    private OnDeleteUpdateListener onDeleteUpdateListener;

    public void setOnDeleteUpdateListener(OnDeleteUpdateListener onDeleteUpdateListener) {
        this.onDeleteUpdateListener = onDeleteUpdateListener;
    }


}
