package me.wcy.music.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.FragmentAdapter;
import me.wcy.music.adapter.PlaylistAdapter;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.Extras;
import me.wcy.music.constants.Keys;
import me.wcy.music.executor.ControlPanel;
import me.wcy.music.executor.NaviMenuExecutor;
import me.wcy.music.executor.WeatherExecutor;
import me.wcy.music.fragment.LocalMusicFragment;
import me.wcy.music.fragment.MusicFragment;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.fragment.SheetListFragment;
import me.wcy.music.loader.MusicLoaderCallback;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.QuitTimer;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.ChineseToEnglish;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.ParseUtils;
import me.wcy.music.utils.PermissionReq;
import me.wcy.music.utils.SharedPreferencesUtil;
import me.wcy.music.utils.SystemUtils;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

/**
 * The type Music activity.
 */
public class MusicActivity extends BaseActivity implements View.OnClickListener, QuitTimer.OnTimerListener,
        NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    @Bind(R.id.drawer_layout)
    private DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    private NavigationView navigationView;
    @Bind(R.id.iv_menu)
    private ImageView ivMenu;
    @Bind(R.id.iv_search)
    private ImageView ivSearch;
    @Bind(R.id.tv_local_music)
    private TextView tvLocalMusic;
    @Bind(R.id.tv_online_music)
    private TextView tvOnlineMusic;
    @Bind(R.id.viewpager)
    private ViewPager mViewPager;
    @Bind(R.id.fl_play_bar)
    private FrameLayout flPlayBar;
    private static final String tag = "MusicActivity";
    private View vNavigationHeader;
    //    private LocalMusicFragment mLocalMusicFragment;
    private SheetListFragment mSheetListFragment;
    private MusicFragment musicFragment;
    private PlayFragment mPlayFragment;
    private ControlPanel controlPanel;
    private NaviMenuExecutor naviMenuExecutor;
    private MenuItem timerItem;
    private boolean isPlayFragmentShow;
    private MusicDaoUtils musicDaoUtils;
    private Loader<Cursor> loader;
    private static Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        refreshNightModeTitle();

    }

    @Override
    protected void onServiceBound() {
        Log.d(tag, "onServiceBound()");
        setupView();
        naviMenuExecutor = new NaviMenuExecutor(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                controlPanel = new ControlPanel(flPlayBar, MusicActivity.this);
                AudioPlayer.get().addOnPlayEventListener(controlPanel);
            }
        },2*1000);

        QuitTimer.get().setOnTimerListener(this);
        parseIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(tag, "onNewIntent()");
        setIntent(intent);
        parseIntent();
        if (intent != null) {
            // 是否退出App的标识
            boolean isExitApp = intent.getBooleanExtra("reset", false);
            if (isExitApp) {
                recreate();
            }
        }
    }



    private void setupView() {
        // add navigation header
        vNavigationHeader = LayoutInflater.from(this).inflate(R.layout.navigation_header, navigationView, false);
        navigationView.addHeaderView(vNavigationHeader);
        // setup view pager
//        mLocalMusicFragment = new LocalMusicFragment();
        mSheetListFragment = new SheetListFragment();
        musicFragment = new MusicFragment();
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(musicFragment);
        adapter.addFragment(mSheetListFragment);
        mViewPager.setAdapter(adapter);
        tvLocalMusic.setSelected(true);

        ivMenu.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        tvOnlineMusic.setOnClickListener(this);
        flPlayBar.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void loadMusic() {
        musicDaoUtils = MusicDaoUtils.getInstance();
        Log.d(tag, "loadMusic()" + musicDaoUtils.queryAllMusic().size());
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        initLoader();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_storage);
                    }
                })
                .request();
    }

    /**
     * 加载本地音乐
     */
    public void initLoader() {
        Log.d(tag, "initLoader()");
        loader = getLoaderManager().initLoader(0, null, new MusicLoaderCallback(getApplicationContext(), value -> {
            Log.d(tag, "initLoader()" + value.size());
            if (value.size() > 0) {
                AppCache.get().getLocalMusicList().clear();
                AppCache.get().getLocalMusicList().addAll(value);
                musicDaoUtils.deleteAll();
                //将本地音乐所有数据加入数据库
                musicDaoUtils.insertMusicList(AppCache.get().getLocalMusicList());
                AudioPlayer.get().init(getApplicationContext());
            } else {
                initMusicData();
            }
        }));
    }

    List<Music> musicList;

    private void initMusicData() {
        musicList = musicDaoUtils.queryFilterTimeMusic();
        if (musicList.size() > 0) {
            AppCache.get().getLocalMusicList().addAll(musicList);
            musicDaoUtils.deleteAll();
            //将本地音乐所有数据加入数据库
            musicDaoUtils.insertMusicList(AppCache.get().getLocalMusicList());
        }
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    @Override
    public void onTimer(long remain) {
        if (timerItem == null) {
            timerItem = navigationView.getMenu().findItem(R.id.action_timer);
        }
        String title = getString(R.string.menu_timer);
        timerItem.setTitle(remain == 0 ? title : SystemUtils.formatTime(title + "(mm:ss)", remain));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_search:
                startActivity(new Intent(this, SearchMusicActivity.class));
                break;
            case R.id.tv_local_music:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_online_music:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag, "onResume()");
        loadMusic();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(tag, "onNavigationItemSelected()");
        drawerLayout.closeDrawers();
        handler.postDelayed(() -> item.setChecked(false), 500);
        return naviMenuExecutor.onNavigationItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(tag, "onPageScrolled()");
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    public void refreshNightModeTitle() {
        Log.d(tag, "refreshNightModeTitle" + MusicUtils.getNightMode(this));
        if (MusicUtils.getNightMode(this)) {
            navigationView.getMenu().findItem(R.id.action_night).setTitle("日间模式");
        } else {
            navigationView.getMenu().findItem(R.id.action_night).setTitle("夜间模式");
        }


    }

    @Override
    public void onBackPressed() {
        Log.d(tag, "onBackPressed()");
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(tag, "onSaveInstanceState()");
        outState.putInt(Keys.VIEW_PAGER_INDEX, mViewPager.getCurrentItem());
        musicFragment.onSaveInstanceState(outState);
        mSheetListFragment.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        Log.d(tag, "onRestoreInstanceState()");
        mViewPager.post(() -> {
            mViewPager.setCurrentItem(savedInstanceState.getInt(Keys.VIEW_PAGER_INDEX), false);
            mSheetListFragment.onRestoreInstanceState(savedInstanceState);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(controlPanel);
        QuitTimer.get().setOnTimerListener(null);

        super.onDestroy();
    }
}
