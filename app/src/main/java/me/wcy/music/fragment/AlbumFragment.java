package me.wcy.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.wcy.music.R;
import me.wcy.music.activity.ModelActivity;
import me.wcy.music.adapter.AlbumAdapter;
import me.wcy.music.listener.OnItemClickListener;
import me.wcy.music.model.AlbumInfo;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.utils.MusicUtils;

/**
 * 专辑列表
 */

public class AlbumFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = "AlbumFragment";
    private RecyclerView recyclerView;
    private Context mContext;
    private ArrayList<AlbumInfo> albumInfoList = new ArrayList<>();
    private AlbumAdapter albumAdapter;
    private MusicDaoUtils musicDaoUtils;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        musicDaoUtils = MusicDaoUtils.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        albumInfoList.clear();
        albumInfoList.addAll(MusicUtils.groupByAlbum(musicDaoUtils.queryAllMusic()));
        Log.d(TAG, "onResume: albumInfoList.size() = " + albumInfoList.size());
        albumAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_singer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.singer_recycler_view);
        albumAdapter = new AlbumAdapter(getContext(), albumInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(albumAdapter);
        albumAdapter.setOnItemClickListener(this);
    }
    //recyclerView item点击事件
    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(mContext,ModelActivity.class);
        intent.putExtra(ModelActivity.KEY_TITLE,albumInfoList.get(postion).getName());
        intent.putExtra(ModelActivity.KEY_TYPE, ModelActivity.ALBUM_TYPE);
        startActivity(intent);
    }
}