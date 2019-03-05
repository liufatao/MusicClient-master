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
import me.wcy.music.model.SingerInfo;
import me.wcy.music.storage.db.DBManager;

/**
 * 歌手列表数据适配器
 */

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder>{

    private static final String TAG = SingerAdapter.class.getName();
    private List<SingerInfo> singerInfoList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public SingerAdapter(Context context, List<SingerInfo> singerInfoList) {
        this.context = context;
        this.singerInfoList = singerInfoList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout Lycontent;
        ImageView singerIv;
        TextView singelName;
        TextView count;

        public ViewHolder(View itemView) {
            super(itemView);
            this.Lycontent = (LinearLayout) itemView.findViewById(R.id.ly_music_content);
            this.singerIv = (ImageView) itemView.findViewById(R.id.iv_header);
            this.singelName = (TextView) itemView.findViewById(R.id.tv_music_name);
            this.count = (TextView) itemView.findViewById(R.id.tv_music_count);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_singer_rv_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d("aaaa", "onBindViewHolder: position = "+position);
        SingerInfo singer = singerInfoList.get(position);
        holder.singelName.setText(singer.getName());
        holder.singerIv.setImageResource(R.drawable.ic_user);
        holder.count.setText(singer.getCount()+"首");
        holder.Lycontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onContentClick(holder.Lycontent,position);
            }
        });

//        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onItemClickListener.onDeleteMenuClick(holder.swipeContent,position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return singerInfoList.size();
    }

    public void update(List<SingerInfo> singerInfoList) {
        this.singerInfoList.clear();
        this.singerInfoList.addAll(singerInfoList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onDeleteMenuClick(View content, int position);
        void onContentClick(View content, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
}
