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
import me.wcy.music.model.FolderInfo;
import me.wcy.music.storage.db.DBManager;

/**
 * 文件夹路径数据适配器
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private static final String TAG = FolderAdapter.class.getName();
    private List<FolderInfo> folderInfoList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public FolderAdapter(Context context, List<FolderInfo> folderInfoList) {
        this.context = context;
        this.folderInfoList = folderInfoList;

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout contentLl;
        ImageView folderIv;
        TextView folderName;
        TextView count;

        public ViewHolder(View itemView) {
            super(itemView);
            this.contentLl = (LinearLayout) itemView.findViewById(R.id.ly_music_content);
            this.folderIv = (ImageView) itemView.findViewById(R.id.iv_folder);
            this.folderName = (TextView) itemView.findViewById(R.id.tv_folderName);
            this.count = (TextView) itemView.findViewById(R.id.tv_music_count);
        }

    }

    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fold_rv_list, parent, false);
        FolderAdapter.ViewHolder viewHolder = new FolderAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FolderAdapter.ViewHolder holder, final int position) {
        Log.d("aaaa", "onBindViewHolder: position = " + position);
        FolderInfo folder = folderInfoList.get(position);
        holder.folderIv.setImageResource(R.drawable.folder);
        holder.folderName.setText(folder.getName());
        holder.count.setText("" + folder.getCount()+"首"+folder.getPath());
        holder.contentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(holder.contentLl, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return folderInfoList.size();
    }

    public void update(List<FolderInfo> folderInfoList) {
        this.folderInfoList.clear();
        this.folderInfoList.addAll(folderInfoList);
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
