package me.wcy.music.fragment;



import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.LocalMusicRecyclerviewAdapter;
import me.wcy.music.listener.OnItemClickListener;
import me.wcy.music.listener.OnMoreClickListener;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.utils.PopWindowUtils;
import me.wcy.music.widget.SideBar;

/**
 *本地音乐列表
 */

public class SingleFragment extends Fragment implements OnPlayerEventListener,OnMoreClickListener {
    private static final String TAG = "SingleFragment";
    private RecyclerView recyclerView;
    private SideBar sideBar;
    private TextView sideBarPreTv;
    private Context context;
    private MusicDaoUtils musicDaoUtils;
    private List<Music> musicInfoList = new ArrayList<>();
    private LocalMusicRecyclerviewAdapter localMusicRecyclerviewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " );
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView: " );

        return  inflater.inflate(R.layout.fragment_single,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }



    private void initView(){
        musicDaoUtils=MusicDaoUtils.getInstance();
        recyclerView =getView().findViewById(R.id.local_recycler_view);
        sideBarPreTv = getView().findViewById(R.id.local_music_siderbar_pre_tv);
        sideBar = getView().findViewById(R.id.local_music_siderbar);
        sideBar.setTextView(sideBarPreTv);
    }

    private void initData(){
        musicInfoList=musicDaoUtils.queryOrderAscMusic();
        Log.d(TAG,"initData()"+musicDaoUtils.queryOrderAscMusic().size());
        localMusicRecyclerviewAdapter=new LocalMusicRecyclerviewAdapter(context,musicInfoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(localMusicRecyclerviewAdapter);
        AudioPlayer.get().addOnPlayEventListener(this);
        localMusicRecyclerviewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                AudioPlayer.get().play(postion);
            }
        });
        localMusicRecyclerviewAdapter.setOnMoreClickListener(this);

        sideBar.setOnListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String letter) {
                //该字母首次出现的位置
                for (int i = 0; i < localMusicRecyclerviewAdapter.getItemCount(); i++) {
                    if (musicInfoList.get(i).getFirstLetter().charAt(0) == letter.charAt(0)) {
//                        recyclerView.smoothScrollToPosition(i);
                        recyclerView.scrollToPosition(i);
                        return;
                    }
                }


            }
        });
    }

    @Override
    public void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(this);
        super.onDestroy();

    }


    @Override
    public void onChange(Music music) {
        localMusicRecyclerviewAdapter.notifyDataSetChanged();
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
    public void onMoreClick(int position) {
        PopWindowUtils.getInstace().showPopFormBottom(getActivity(),musicInfoList.get(position),getView().findViewById(R.id.fragement_single));
    }
}
