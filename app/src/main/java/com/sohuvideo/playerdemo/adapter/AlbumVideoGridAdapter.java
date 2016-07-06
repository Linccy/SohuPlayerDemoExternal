package com.sohuvideo.playerdemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.sohuvideo.playerdemo.PlayVideoHelper;
import com.sohuvideo.playerdemo.R;
import com.sohuvideo.playerdemo.entity.Video;
import com.sohuvideo.playerdemo.utils.CollectionUtil;

public class AlbumVideoGridAdapter extends BaseAdapter {

	private List<Video> mVideos;
	private Context mContext;

	public AlbumVideoGridAdapter(Context context, List<Video> videoList) {
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
		return CollectionUtil.isGetValid(mVideos, position) ? mVideos
				.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(),
					R.layout.grid_item_videobtn, null);
			holder = new ViewHolder();
			holder.mVideoBtn = (Button)convertView.findViewById(R.id.btn_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!CollectionUtil.isGetValid(mVideos, position)) {
			return convertView;
		}
		final Video video = mVideos.get(position);
		if (video != null) {
			holder.mVideoBtn.setText("第" + video.getVideo_order() + "集");
			holder.mVideoBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlayVideoHelper.playSohuOnlineVideo(mContext,
							video.getAid(), video.getVid(), video.getSite(), 0);
				}
			});
		}
		return convertView;
	}

	private class ViewHolder {
		private Button mVideoBtn;
	}

	public void addAll(List<Video> videoList) {
		mVideos.addAll(videoList);
	}

	public void clear() {
		mVideos.clear();
	}
}
