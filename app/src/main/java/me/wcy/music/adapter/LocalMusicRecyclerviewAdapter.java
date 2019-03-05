package me.wcy.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.listener.OnItemClickListener;
import me.wcy.music.listener.OnMoreClickListener;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.utils.CoverLoader;

/**
 * 本地音乐数据适配器
 */
public class LocalMusicRecyclerviewAdapter extends RecyclerView.Adapter<LocalMusicRecyclerviewAdapter.ViewHolder> {
    private static final String TAG = "LocalMusicRecyclerviewAdapter";
    private List<Music> musicList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnMoreClickListener onMoreClickListener;

    public LocalMusicRecyclerviewAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    /**
     * Item 点击事件
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    /**
     * 更多点击事件
     * @param onMoreClickListener
     */
    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener){
        this.onMoreClickListener=onMoreClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_local_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.vPlaying.setVisibility((position == AudioPlayer.get().getPlayPosition()) ? View.VISIBLE : View.INVISIBLE);
        Music music = musicList.get(position);
        Bitmap cover = CoverLoader.get().loadThumb(music);
        holder.ivAlbum.setImageBitmap(cover);
        holder.tvSongname.setText(musicList.get(position).getTitle());
        holder.tvSinger.setText(musicList.get(position).getArtist());
        holder.tvLetter.setVisibility(View.VISIBLE);
        char frist_letter = 0;
        if (musicList.get(position).getFirstLetter()!=null){
            frist_letter=musicList.get(position).getFirstLetter().charAt(0);
        }
       holder.tvLetter.setText(String.valueOf(frist_letter));
        //将相同字母开头的合并在一起
        if (position == 0) {
            //第一个是一定显示的
            holder.tvLetter.setVisibility(View.VISIBLE);
        } else {
            //后一个与前一个对比,判断首字母是否相同，相同则隐藏
            char headerWord = 0;
            if (!musicList.get(position - 1).getFirstLetter().isEmpty()){
                headerWord=musicList.get(position - 1).getFirstLetter().charAt(0);
            }

            if (frist_letter==headerWord) {
                holder.tvLetter.setVisibility(View.GONE);
            } else {
                holder.tvLetter.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvLetter;
        private TextView tvSongname;
        private TextView tvSinger;
        private ImageView ivMore;
        private ImageView ivAlbum;
        private View vPlaying;
        private RelativeLayout rlLocalMusicListItem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            ivAlbum = itemView.findViewById(R.id.ivAlbum);
            vPlaying=itemView.findViewById(R.id.vPlaying);
            tvSongname = itemView.findViewById(R.id.tvSongname);
            tvSinger = itemView.findViewById(R.id.tvSinger);
            ivMore = itemView.findViewById(R.id.ivMore);
            ivMore.setOnClickListener(this);
            rlLocalMusicListItem=itemView.findViewById(R.id.rlLocalMusicListItem);
            rlLocalMusicListItem.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
           switch (view.getId()){
               case R.id.rlLocalMusicListItem:
                   onItemClickListener.onItemClick(view,getAdapterPosition());
                   break;
               case R.id.ivMore:
                   onMoreClickListener.onMoreClick(getAdapterPosition());
                   break;
           }


        }
    }

}
