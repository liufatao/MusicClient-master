package me.wcy.music.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import me.wcy.lrcview.LrcView;
import me.wcy.music.R;
import me.wcy.music.constants.OnGetCurrentDateTimeListener;
import me.wcy.music.executor.SearchLrc;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.swipebacklayout.SwipeBackLayout;
import me.wcy.music.swipebacklayout.activity.SwipeBackActivity;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.DateTimeUtil;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.TimeThreadUtil;
import me.wcy.music.widget.LinearTextView;

/**
 * 锁屏界面
 */
public class LockScreenActivity extends SwipeBackActivity implements OnGetCurrentDateTimeListener, View.OnClickListener, OnPlayerEventListener {
    private final static String TAG = "LockScreenActivity";
    private TextView tvTime;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvSinger;
    private LinearTextView tvUnlock;
    private DateTimeUtil dateTimeUtil;
    private TimeThreadUtil timeThreadUtil;
    private ImageView ivPrev;
    private ImageView ivPlay;
    private ImageView ivNext;
    private LrcView mLrcViewSingle;
    private SwipeBackLayout mSwipeBackLayout;
    private ImageView ivLockScreeBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        setContentView(R.layout.activity_lock_screen);
        initView();
        initData(AudioPlayer.get().getPlayMusic());
    }

    private void initView() {
        ivLockScreeBg = (ImageView) findViewById(R.id.iv_lock_scree_bg);
        ivPrev = (ImageView) findViewById(R.id.iv_prev);
        ivPrev.setOnClickListener(this);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivPlay.setOnClickListener(this);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        ivNext.setOnClickListener(this);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvSinger = (TextView) findViewById(R.id.tvSinger);
        mLrcViewSingle = (LrcView) findViewById(R.id.lrc_view_single);
        tvUnlock = (LinearTextView) findViewById(R.id.tvUnlock);
        if (timeThreadUtil == null) {
            timeThreadUtil = new TimeThreadUtil(this);
        }
        timeThreadUtil.start();
        AudioPlayer.get().addOnPlayEventListener(this);
        mSwipeBackLayout = getSwipeBackLayout();
        //设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(200);//滑动删除的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法

    }

    private void initData(Music music) {
        Log.d(TAG, "initData()");
        tvTitle.setText(music.getTitle());
        tvSinger.setText(music.getArtist());
        ivLockScreeBg.setImageBitmap(CoverLoader.get().loadBlur(music));


        if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) {
            ivPlay.setSelected(true);
        } else {
            ivPlay.setSelected(false);
        }
        setLrc(music);
    }

    private void setLrc(final Music music) {
        if (music.getType() == Music.Type.LOCAL) {
            String lrcPath = FileUtils.getLrcFilePath(music);
            if (!TextUtils.isEmpty(lrcPath)) {
                loadLrc(lrcPath);
            } else {
                new SearchLrc(music.getArtist(), music.getTitle()) {
                    @Override
                    public void onPrepare() {
                        loadLrc("");
                        setLrcLabel("正在搜索歌词");
                    }

                    @Override
                    public void onExecuteSuccess(@NonNull String lrcPath) {

                        loadLrc(lrcPath);
                        setLrcLabel("暂无歌词");
                    }

                    @Override
                    public void onExecuteFail(Exception e) {

                        setLrcLabel("暂无歌词");
                    }
                }.execute();
            }
        } else {
            String lrcPath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
            loadLrc(lrcPath);
        }
    }

    /**
     * 载入歌词
     *
     * @param path
     */
    private void loadLrc(String path) {
        File file = new File(path);
        mLrcViewSingle.loadLrc(file);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideBottomUIMenu();
    }

    /**
     * 设置label
     *
     * @param label
     */
    private void setLrcLabel(String label) {
        mLrcViewSingle.setLabel(label);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void hideBottomUIMenu() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setSystemUI(window.getDecorView());
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }

    private void setSystemUI(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //返回按键

    }

    @Override
    public void onGetDateTime() {
        dateTimeUtil = DateTimeUtil.getInstance();
        tvTime.setText(dateTimeUtil.getCurrentTimeHHMM());
        tvDate.setText(dateTimeUtil.getCurrentDateWeekDay());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play:
                //暂停播放
                AudioPlayer.get().playPause();
                break;
            case R.id.iv_next:
                //下一曲
                AudioPlayer.get().next();
                break;
            case R.id.iv_prev:
                //上一曲
                AudioPlayer.get().prev();
                break;
        }
    }

    @Override
    public void onChange(Music music) {
        initData(music);
    }

    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {
        if (mLrcViewSingle.hasLrc()) {
            mLrcViewSingle.updateTime(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }
}
