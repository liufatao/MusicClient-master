package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.constants.Constant;
import me.wcy.music.constants.Extras;
import me.wcy.music.executor.ControlPanel;
import me.wcy.music.fragment.AlbumFragment;
import me.wcy.music.fragment.FolderFragment;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.fragment.SingerFragment;
import me.wcy.music.fragment.SingleFragment;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.widget.MusicViewPager;

/**
 * 本地音乐歌曲列表
 */
public class LocalMusicActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private TabAdapter fragmentAdapter;
    private MusicViewPager musicViewPager;
    private List<String> titleList = new ArrayList<>(4);
    private List<Fragment> fragments = new ArrayList<>(4);
    private SingleFragment singleFragment;
    private SingerFragment singerFragment;
    private AlbumFragment albumFragment;
    private FolderFragment folderFragment;
    private PlayFragment mPlayFragment;
    private TextView nothingTv;
    @Bind(R.id.fl_play_bar)
    private FrameLayout flPlayBar;
    private ControlPanel controlPanel;
    private boolean isPlayFragmentShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Constant.LABEL_LOCAL);
        }
        initView();
    }

    private void initView(){
        addTapData();
        musicViewPager = findViewById(R.id.local_viewPager);
        tabLayout = (TabLayout)findViewById(R.id.local_tab);
        fragmentAdapter = new TabAdapter(getSupportFragmentManager());
        musicViewPager.setAdapter(fragmentAdapter);
        //预加载页面数
        musicViewPager.setOffscreenPageLimit(2);
        flPlayBar.setOnClickListener(this);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(musicViewPager);
        //本地扫描
        nothingTv = (TextView)findViewById(R.id.local_nothing_tv);
        nothingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LocalMusicActivity.this,ScanActivity.class);
//                startActivity(intent);
            }
        });

    }
    /**
     * 滑动布局
     */

    private void addTapData() {
        titleList.add(Constant.SINGLE);
        titleList.add(Constant.SONGSTER);
        titleList.add(Constant.ALBUM);
        titleList.add(Constant.FOLDER);

        if (singleFragment == null) {
            singleFragment = new SingleFragment();
            fragments.add(singleFragment);
        }
        if (singerFragment == null) {
            singerFragment = new SingerFragment();
            fragments.add(singerFragment);
        }
        if (albumFragment == null) {
            albumFragment = new AlbumFragment();
            fragments.add(albumFragment);
        }
        if (folderFragment == null) {
            folderFragment = new FolderFragment();
            fragments.add(folderFragment);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
        }
    }
    @Override
    protected void onServiceBound() {
        controlPanel = new ControlPanel(flPlayBar,this);
        AudioPlayer.get().addOnPlayEventListener(controlPanel);
        parseIntent();
    }
    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
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

    class TabAdapter extends FragmentPagerAdapter {

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * 用来显示tab上的名字
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.local_music_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.scan_local_menu){
//            Intent intent = new Intent(LocalMusicActivity.this,ScanActivity.class);
//            startActivity(intent);
        }else if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }

        super.onBackPressed();
    }

}
