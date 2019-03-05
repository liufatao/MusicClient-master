package me.wcy.music.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.LatestAdapter;
import me.wcy.music.adapter.MyLoveAdapter;
import me.wcy.music.constants.Constant;
import me.wcy.music.constants.Extras;
import me.wcy.music.executor.ControlPanel;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.listener.OnItemClickListener;
import me.wcy.music.listener.OnMoreClickListener;
import me.wcy.music.listener.OnMusicClickListener;
import me.wcy.music.model.LatestMusic;
import me.wcy.music.model.LoveMusic;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.utils.PopWindowUtils;
import me.wcy.music.widget.MusicPopMenuWindow;

public class MyLoveActivity extends BaseActivity implements OnItemClickListener,OnMoreClickListener,OnPlayerEventListener,View.OnClickListener,OnMusicClickListener {
    private Toolbar toolbar;
    private RecyclerView loveMusic_recycler_view;
    private MyLoveAdapter myLoveAdapter;
    private List<LoveMusic> loveMusics=new ArrayList<>();
    private MusicDaoUtils musicDaoUtils;
    private ControlPanel controlPanel;
    private FrameLayout flPlayBar;
    private PlayFragment mPlayFragment;
    private boolean isPlayFragmentShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_love);
        initView();
        initData();
    }
    private void initView(){
        musicDaoUtils=MusicDaoUtils.getInstance();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Constant.LABEL_MYLOVE);
        }
        loveMusic_recycler_view=findViewById(R.id.loveMusic_recycler_view);
        flPlayBar = findViewById(R.id.fl_play_bar);
        flPlayBar.setOnClickListener(this);
        AudioPlayer.get().addOnPlayEventListener(this);
    }

    private void initData(){
        myLoveAdapter = new MyLoveAdapter(this, loveMusics);
        loveMusic_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        myLoveAdapter.setOnItemClickListener(this);
        myLoveAdapter.setOnMoreClickListener(this);
        loveMusic_recycler_view.setAdapter(myLoveAdapter);
    }

    /**
     * 更新列表数据
     */
    private void updataMusic(){
        loveMusics.clear();
        loveMusics.addAll(musicDaoUtils.queryAllLoveMusic());
        myLoveAdapter.notifyDataSetChanged();
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


    @Override
    protected void onServiceBound() {
        super.onServiceBound();
        controlPanel = new ControlPanel(flPlayBar, this);
        AudioPlayer.get().addOnPlayEventListener(controlPanel);
        parseIntent();
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
    protected void onResume() {
        super.onResume();
        updataMusic();

    }

    @Override
    public void onItemClick(View view, int postion) {
        AudioPlayer.get().addAndPlay(loveMusics.get(postion));
        Toast.makeText(this,loveMusics.get(postion).getTitle(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMoreClick(int position) {
        PopWindowUtils.getInstace().showPopFormBottom(MyLoveActivity.this,loveMusics.get(position),findViewById(R.id.activity_mylove));
    }

    @Override
    public void onChange(Music music) {
        updataMusic();
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
    public void onClick(View view) {

    }

    @Override
    public void onLoveMusic() {
        updataMusic();
    }
}
