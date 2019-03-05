package me.wcy.music.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import me.wcy.music.R;
import me.wcy.music.listener.OnItemClickListener;
import me.wcy.music.model.AlbumInfo;

/**
 *专辑适配器
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{
    
    private static final String TAG = AlbumAdapter.class.getName();
    private List<AlbumInfo> albumInfoList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AlbumAdapter(Context context, List<AlbumInfo> albumInfoList) {
        this.context = context;
        this.albumInfoList = albumInfoList;

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout Lycontent;
        ImageView albumIv;
        TextView albumName;
        TextView count;

        public ViewHolder(View itemView) {
            super(itemView);
            this.Lycontent = (LinearLayout) itemView.findViewById(R.id.ly_music_content);
            this.albumIv = (ImageView) itemView.findViewById(R.id.iv_album);
            this.albumName = (TextView) itemView.findViewById(R.id.tv_albumName);
            this.count = (TextView) itemView.findViewById(R.id.tv_music_count);
        }

    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album_rv_list,parent,false);
        AlbumAdapter.ViewHolder viewHolder = new AlbumAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AlbumAdapter.ViewHolder holder, final int position) {
        Log.d("aaaa", "onBindViewHolder: position = "+position);
        AlbumInfo album = albumInfoList.get(position);
        holder.albumIv.setImageResource(R.drawable.album);
        holder.albumName.setText(album.getName());
        holder.count.setText(album.getCount()+"首 "+album.getSinger());
        holder.Lycontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(holder.Lycontent,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return albumInfoList.size();
    }

    public void update(List<AlbumInfo> albumInfoList) {
        this.albumInfoList.clear();
        this.albumInfoList.addAll(albumInfoList);
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
}
