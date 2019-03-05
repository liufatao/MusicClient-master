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
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.activity.ModelActivity;
import me.wcy.music.adapter.SingerAdapter;
import me.wcy.music.model.SingerInfo;
import me.wcy.music.storage.db.DBManager;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.utils.MusicUtils;

/**
 *歌手列表
 */

public class SingerFragment extends Fragment {

    private static final String TAG = "SingerFragment";
    private RecyclerView recyclerView;
    private Context mContext;
    private SingerAdapter adapter;
    private List<SingerInfo> singerInfoList = new ArrayList<>();
    private MusicDaoUtils musicDaoUtils;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        musicDaoUtils=MusicDaoUtils.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        singerInfoList.clear();
        singerInfoList.addAll(MusicUtils.groupBySinger(musicDaoUtils.queryAllMusic() ));
        Log.d(TAG, "onResume: singerInfoList.size() = "+singerInfoList.size());
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d("aaaa", "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_singer,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData(){
        recyclerView = (RecyclerView)getView().findViewById(R.id.singer_recycler_view);
        Log.e(TAG, "SingerFragment: singerInfoList.size() ="+ singerInfoList.size());
        adapter = new SingerAdapter(getContext(),singerInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SingerAdapter.OnItemClickListener() {
            @Override
            public void onDeleteMenuClick(View content, int position) {
                Log.d(TAG, "onDeleteMenuClick: ");
            }

            @Override
            public void onContentClick(View content, int position) {
                Log.d(TAG, "onContentClick: ");
                Intent intent = new Intent(mContext,ModelActivity.class);
                intent.putExtra(ModelActivity.KEY_TITLE,singerInfoList.get(position).getName());
                intent.putExtra(ModelActivity.KEY_TYPE, ModelActivity.SINGER_TYPE);
                mContext.startActivity(intent);
            }
        });
    }
}
