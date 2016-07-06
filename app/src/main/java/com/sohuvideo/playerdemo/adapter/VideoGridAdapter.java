package com.sohuvideo.playerdemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sohuvideo.playerdemo.DetailActivity;
import com.sohuvideo.playerdemo.R;
import com.sohuvideo.playerdemo.entity.Video;
import com.sohuvideo.playerdemo.utils.CollectionUtil;
import com.sohuvideo.playerdemo.utils.ImageUtils;

public class VideoGridAdapter extends BaseAdapter {

    private List<Video> mVideos = null;
    private Context mContext;

    public VideoGridAdapter(Context context, List<Video> videoList) {
        mVideos = new ArrayList<Video>();
        mContext = context;
        mVideos.addAll(videoList);
    }

    @Override
    public int getCount() {
        return mVideos == null ? 0 : mVideos.size();
    }

    @Override
    public Object getItem(int position) {
        return CollectionUtil.isGetValid(mVideos, position) ? mVideos.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.grid_item_video, null);
            holder = new ViewHolder();
            holder.mImageView = convertView.findViewById(R.id.img_icon);
            ((ImageView) holder.mImageView).setImageResource(R.drawable.pic_defaultposter);
            holder.mTextView = convertView.findViewById(R.id.txt_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!CollectionUtil.isGetValid(mVideos, position)) {
            return convertView;
        }
        final Video video = mVideos.get(position);
        if (video != null) {
            String picUrl = video.getPic();
            ImageUtils.download(picUrl, (ImageView) holder.mImageView);
            String videoName = video.getVideo_name();
            if (!videoName.contains("《")) {
                if (!videoName.startsWith("《")) {
                    videoName = "《" + videoName;
                }
                if (!videoName.endsWith("》")) {
                    videoName = videoName + "》";
                }
            }
            ((TextView) holder.mTextView).setText(videoName);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("video", video);
                    intent.putExtras(bundle);
                    intent.setClass(mContext, DetailActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {

        private View mImageView;
        private View mTextView;
    }

    public void addAll(List<Video> videoList) {
        mVideos.addAll(videoList);
    }

    public void clear() {
        mVideos.clear();
    }
}
