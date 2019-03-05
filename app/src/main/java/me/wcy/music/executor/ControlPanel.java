package me.wcy.music.executor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.wcy.music.R;
import me.wcy.music.activity.PlaylistActivity;
import me.wcy.music.application.AppCache;
import me.wcy.music.model.LatestMusic;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.DateTimeUtil;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.utils.binding.ViewBinder;
import me.wcy.music.widget.PlayingPopWindow;

/**
 *音乐播放控制台
 */
public class ControlPanel implements View.OnClickListener, OnPlayerEventListener {
    @Bind(R.id.iv_play_bar_cover)
    private ImageView ivPlayBarCover;
    @Bind(R.id.tv_play_bar_title)
    private TextView tvPlayBarTitle;
    @Bind(R.id.tv_play_bar_artist)
    private TextView tvPlayBarArtist;
    @Bind(R.id.iv_play_bar_play)
    private ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    private ImageView ivPlayBarNext;
    @Bind(R.id.v_play_bar_playlist)
    private ImageView vPlayBarPlaylist;//播放列表
    @Bind(R.id.pb_play_bar)
    private ProgressBar mProgressBar;
    private static ControlPanel controlPanel;
    private View view;
    private Activity activity;
    private MusicDaoUtils musicDaoUtils;
    public ControlPanel(View view,Activity activity) {
        this.view=view;
        this.activity=activity;
        ViewBinder.bind(this, view);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        vPlayBarPlaylist.setOnClickListener(this);
        onChange(AudioPlayer.get().getPlayMusic());
        musicDaoUtils=MusicDaoUtils.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_bar_play:
                AudioPlayer.get().playPause();
                break;
            case R.id.iv_play_bar_next:
                AudioPlayer.get().next();
                break;
            case R.id.v_play_bar_playlist:
//                Context context = vPlayBarPlaylist.getContext();
//                Intent intent = new Intent(context, PlaylistActivity.class);
//                context.startActivity(intent);
                showPopFormBottom();
                break;
        }
    }
    public void showPopFormBottom() {
        PlayingPopWindow playingPopWindow = new PlayingPopWindow(activity);
//      设置Popupwindow显示位置（从底部弹出）
        playingPopWindow.showAtLocation(view, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha=0.7f;
       activity.getWindow().setAttributes(params);

        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        playingPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                params.alpha=1f;
                activity.getWindow().setAttributes(params);
            }
        });

    }

    private void insertLatestMusic(Music music){
        LatestMusic latestMusic=new LatestMusic();
        latestMusic.setType(music.getType());
        latestMusic.setSongId(music.getSongId());
        latestMusic.setTitle(music.getTitle());
        latestMusic.setArtist(music.getArtist());
        latestMusic.setAlbum(music.getAlbum());
        latestMusic.setAlbumId(music.getAlbumId());
        latestMusic.setCoverPath(music.getCoverPath());
        latestMusic.setDuration(music.getDuration());
        latestMusic.setPath(music.getPath());
        latestMusic.setParentPath(music.getParentPath());
        latestMusic.setFileName(music.getFileName());
        latestMusic.setFileSize(music.getFileSize());
        latestMusic.setDate(DateTimeUtil.getInstance().getStringDate());
        if (musicDaoUtils!=null) {
            musicDaoUtils.insertLatestMusic(latestMusic);
        }
    }
    @Override
    public void onChange(Music music) {
        if (music == null) {
            return;
        }
        Bitmap cover = CoverLoader.get().loadThumb(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) AudioPlayer.get().getAudioPosition());
        insertLatestMusic(music);
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }
}
