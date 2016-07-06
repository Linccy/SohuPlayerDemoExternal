package com.sohuvideo.playerdemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.sohuvideo.playerdemo.R;
import com.sohuvideo.playerdemo.entity.Video;
import com.sohuvideo.playerdemo.utils.CollectionUtil;

public class DownloadVideoGridAdapter extends BaseAdapter {

	private ArrayList<Video> mVideos;
	private SparseBooleanArray mCheckedCbx;

	public DownloadVideoGridAdapter(List<Video> videoList) {
		mVideos = new ArrayList<Video>();
		mCheckedCbx = new SparseBooleanArray();
		mVideos.addAll(videoList);
	}

	@Override
	public int getCount() {
		return mVideos == null ? 0 : mVideos.size();
	}

	@Override
	public Object getItem(int position) {
		return CollectionUtil.isGetValid(mVideos, position) ? mVideos
				.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(),
					R.layout.grid_item_video_download, null);
			holder = new ViewHolder();
			holder.mVideoCB = (CheckBox)convertView.findViewById(R.id.downloadcheck);
			final CheckBox cbx = holder.mVideoCB;
			cbx.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean check = cbx.isChecked();
					mCheckedCbx.put(position, check);
				}
			});
			convertView.setTag(holder);
			mCheckedCbx.put(position, false);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!CollectionUtil.isGetValid(mVideos, position)) {
			return convertView;
		}
		final Video video = mVideos.get(position);
		if (video != null) {
			holder.mVideoCB.setText("第" + video.getVideo_order() + "集");
			holder.mVideoCB.setChecked(mCheckedCbx.get(position));
			
		}
		return convertView;
	}

	private class ViewHolder {
		private CheckBox mVideoCB;
	}

	public void addAll(List<Video> videoList) {
		mVideos.addAll(videoList);
	}

	/**
	 * 得到勾选的视频列表
	 * @return
	 */
	public ArrayList<Video> getCheckVideo(){
		ArrayList<Video> list = new ArrayList<Video>();
		for(int i = 0; i < mCheckedCbx.size(); i++) {
			if(mCheckedCbx.get(i)) {
				list.add(mVideos.get(i));
			}
		}
		
		return list;
	}
	
	public void clear() {
		mVideos.clear();
	}
}
