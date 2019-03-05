package me.wcy.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.wcy.music.R;
import me.wcy.music.activity.LatestActivity;
import me.wcy.music.activity.LocalMusicActivity;
import me.wcy.music.activity.MyLoveActivity;
import me.wcy.music.storage.db.MusicDaoUtils;
import me.wcy.music.utils.MusicUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicFragment} interface
 * to handle interaction events.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends BaseFragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RelativeLayout lyLocaMusic;
    private TextView tvLocalMusicCount;
    private RelativeLayout lyRecentMusic;
    private TextView tvRecentMusicCount;
    private RelativeLayout lyMyLoveMusic;
    private TextView tvMyLoveCount;
    private MusicDaoUtils musicDaoUtils;

    public MusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        lyLocaMusic = getView().findViewById(R.id.lyLocaMusic);
        lyLocaMusic.setOnClickListener(this);
        tvLocalMusicCount = getView().findViewById(R.id.tvLocalMusicCount);

        lyRecentMusic = getView().findViewById(R.id.lyRecentMusic);
        lyRecentMusic.setOnClickListener(this);

        tvRecentMusicCount = getView().findViewById(R.id.tvRecentMusicCount);
        lyMyLoveMusic = getView().findViewById(R.id.lyMyLoveMusic);
        lyMyLoveMusic.setOnClickListener(this);
        tvMyLoveCount = getView().findViewById(R.id.tvMyLoveCount);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lyLocaMusic:
                //本地音乐
                MusicUtils.startActivity(LocalMusicActivity.class);
                break;
            case R.id.lyMyLoveMusic:
                //我的喜爱
                MusicUtils.startActivity(MyLoveActivity.class);
                break;
            case R.id.lyRecentMusic:
                MusicUtils.startActivity(LatestActivity.class);
                break;
        }
    }


}

