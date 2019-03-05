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
import me.wcy.music.model.LatestMusic;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.utils.CoverLoader;

public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.ViewHolder> {
    private List<LatestMusic> musicList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnMoreClickListener onMoreClickListener;
    public LatestAdapter(Context context, List<LatestMusic> musicList){
        this.context=context;
        this.musicList=musicList;
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
     */
    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener){
        this.onMoreClickListener=onMoreClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LatestAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_latest_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.vPlaying.setVisibility((AudioPlayer.get().getPlaySongID().equals(musicList.get(position).getSongId()) ) ? View.VISIBLE : View.INVISIBLE);
        LatestMusic music = musicList.get(position);
        Bitmap cover = CoverLoader.get().loadThumb(music);
        holder.ivAlbum.setImageBitmap(cover);
        holder.tvSongname.setText(musicList.get(position).getTitle());
        holder.tvSinger.setText(musicList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvSongname;
        private TextView tvSinger;
        private ImageView ivMore;
        private ImageView ivAlbum;
        private View vPlaying;
        private RelativeLayout rlMusicListItem;
        public ViewHolder(View itemView) {
            super(itemView);
            ivAlbum = itemView.findViewById(R.id.ivAlbum);
            vPlaying=itemView.findViewById(R.id.vPlaying);
            tvSongname = itemView.findViewById(R.id.tvSongname);
            tvSinger = itemView.findViewById(R.id.tvSinger);
            ivMore = itemView.findViewById(R.id.ivMore);
            ivMore.setOnClickListener(this);
            rlMusicListItem=itemView.findViewById(R.id.rlMusicListItem);
            rlMusicListItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.rlMusicListItem:
                    onItemClickListener.onItemClick(view,getAdapterPosition());
                    break;
                case R.id.ivMore:
                    onMoreClickListener.onMoreClick(getAdapterPosition());
                    break;
            }

        }
    }
}
