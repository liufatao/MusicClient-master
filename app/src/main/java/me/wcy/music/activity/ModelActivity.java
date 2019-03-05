package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.ModelAdapter;
import me.wcy.music.constants.Extras;
import me.wcy.music.executor.ControlPanel;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.listener.OnDeleteUpdateListener;
import me.wcy.music.listener.OnItemClickListener;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.Constant;
import me.wcy.music.listener.OnMoreClickListener;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.utils.PopWindowUtils;
import me.wcy.music.widget.MusicPopMenuWindow;
import me.wcy.music.widget.SideBar;

public class ModelActivity extends BaseActivity implements OnItemClickListener, OnMoreClickListener, View.OnClickListener,OnPlayerEventListener,OnDeleteUpdateListener {

    private static final String TAG = "ModelActivity";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_TYPE = "key_type";
    public static final String KEY_PATH = "key_path";
    public static final String SINGER_TYPE = "singer_type";
    public static final String ALBUM_TYPE = "album_type";
    public static final String FOLDER_TYPE = "folder_type";
    private Toolbar toolbar;
    private String type;
    private String title;
    private RecyclerView recyclerView;
    private ModelAdapter modelAdapter;
    private SideBar sideBar;
    private List<Music> musicList;
    private MusicDaoUtils musicDaoUtils;
    private ControlPanel controlPanel;
    private FrameLayout flPlayBar;
    private PlayFragment mPlayFragment;
    private boolean isPlayFragmentShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);
        initView();
        initData();

    }

    private void initView() {
        title = getIntent().getStringExtra(KEY_TITLE);
        type = getIntent().getStringExtra(KEY_TYPE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        flPlayBar = (FrameLayout) findViewById(R.id.fl_play_bar);
        flPlayBar.setOnClickListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
        sideBar = (SideBar) findViewById(R.id.model_music_siderbar);
        recyclerView = (RecyclerView) findViewById(R.id.model_recycler_view);
        musicList = new ArrayList<>();
        musicDaoUtils = MusicDaoUtils.getInstance();
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

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    protected void onServiceBound() {
        super.onServiceBound();
        controlPanel = new ControlPanel(flPlayBar, this);
        AudioPlayer.get().addOnPlayEventListener(controlPanel);
        parseIntent();
    }

    private void initData() {
        AudioPlayer.get().addOnPlayEventListener(this);
        modelAdapter = new ModelAdapter(this, musicList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        modelAdapter.setOnItemClickListener(this);
        modelAdapter.setOnMoreClickListener(this);
        recyclerView.setAdapter(modelAdapter);
        sideBar.setOnListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String letter) {
                //该字母首次出现的位置
                for (int i = 0; i < modelAdapter.getItemCount(); i++) {
                    if (musicList.get(i).getFirstLetter().charAt(0) == letter.charAt(0)) {
                        recyclerView.scrollToPosition(i);
                        return;
                    }
                }


            }
        });

    }



    public void updateView() {
        musicList.clear();
        if (type.equals(SINGER_TYPE)) {
            musicList.addAll(musicDaoUtils.getMusicListByArtist(title));
        } else if (type.equals(ALBUM_TYPE)) {
            musicList.addAll(musicDaoUtils.getMusicListByAlbum(title));
        } else if (type.equals(FOLDER_TYPE)) {
            musicList.addAll(musicDaoUtils.getMusicListByFolder(getIntent().getStringExtra(KEY_PATH)));
        }
        modelAdapter.notifyDataSetChanged();
        if (musicList.size() == 0) {
            sideBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            sideBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.get().removeOnPlayEventListener(this);
    }


    @Override
    public void onItemClick(View view, int postion) {
        AudioPlayer.get().addAndPlay(musicList.get(postion));
    }

    @Override
    public void onMoreClick(int position) {
        PopWindowUtils.getInstace().showPopFormBottom(ModelActivity.this,musicList.get(position),findViewById(R.id.activity_model));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onChange(Music music) {
        modelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerStart() {

    }

    @Override
    public void onPlayerPause() {

    }

    @Override
    public void onPublish(int progress) {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onDeleteUpdate() {
        updateView();
    }
}
