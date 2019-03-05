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
import me.wcy.music.adapter.FolderAdapter;
import me.wcy.music.listener.OnItemClickListener;
import me.wcy.music.model.FolderInfo;
import me.wcy.music.storage.db.DBManager;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.utils.MusicUtils;

/**
 * 文件夹列表
 */

public class FolderFragment extends Fragment implements OnItemClickListener {
    
    private static final String TAG = "FolderFragment";
    private RecyclerView recyclerView;
    private Context mContext;
    private FolderAdapter folderAdapter;
    private List<FolderInfo> folderInfoList = new ArrayList<>();
    private MusicDaoUtils musicDaoUtils;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        musicDaoUtils = MusicDaoUtils.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_singer,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData(){
        recyclerView = (RecyclerView)getView().findViewById(R.id.singer_recycler_view);
        folderAdapter = new FolderAdapter(getContext(),folderInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(folderAdapter);
        folderAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        folderInfoList.clear();
        folderInfoList.addAll(MusicUtils.groupByFolder(musicDaoUtils.queryAllMusic()));
        Log.d(TAG, "onResume: folderInfoList.size() = "+folderInfoList.size());
        folderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(mContext,ModelActivity.class);
        intent.putExtra(ModelActivity.KEY_TITLE,folderInfoList.get(postion).getName());
        intent.putExtra(ModelActivity.KEY_TYPE, ModelActivity.FOLDER_TYPE);
        intent.putExtra(ModelActivity.KEY_PATH,folderInfoList.get(postion).getPath());
        startActivity(intent);
    }
}
