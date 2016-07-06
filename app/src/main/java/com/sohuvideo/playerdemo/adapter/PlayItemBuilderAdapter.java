/*
 * Copyright (c) 2013 Sohu TV. All rights reserved.
 */
package com.sohuvideo.playerdemo.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.sohuvideo.api.SohuPlayerItemBuilder;
import com.sohuvideo.playerdemo.R;
import com.sohuvideo.playerdemo.utils.ImageUtils;

/**
 * 通用的adapter TODO last selected?
 * 
 * @author shileiHao 2013-9-6
 */
public class PlayItemBuilderAdapter extends
		AbstractAdapter<SohuPlayerItemBuilder> {
	private boolean bIndexAsTitle = false;
	private int listType;
	private int activeIndex;
	private boolean hasMore;
	private boolean canDownload;
	private SohuPlayerItemBuilder activeItem;

	public void setActiveItem(SohuPlayerItemBuilder item) {
		boolean result = activeItem == null && item != null
				|| (activeItem != null && item == null)
				|| !activeItem.equals(item);
		if (result) {
			this.activeItem = item;
			notifyDataSetChanged();
		}
	}

	public int getListType() {
		return listType;
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

	public void setMore(boolean more) {
		this.hasMore = more;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setCanDownload(boolean canDownload) {
		this.canDownload = canDownload;
	}

	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param ctx
	 */
	public PlayItemBuilderAdapter(Context ctx) {
		super(ctx);
	}

	@Override
	protected int getLayoutId(int position) {
		return R.layout.item_video;
	}

	@Override
	protected ViewHolder<SohuPlayerItemBuilder> createViewHolder() {
		return new BuilderViewHolder();
	}

	public void setTitleMode(boolean useIndex) {
		this.bIndexAsTitle = useIndex;
	}

	@Override
	public void updateList(List<SohuPlayerItemBuilder> newList, boolean notify) {
		super.updateList(newList, notify);
		activeIndex = -1;
	}

	private class BuilderViewHolder implements ViewHolder<SohuPlayerItemBuilder> {
		private ImageView ivCover;
		private TextView tvTitle;
		private TextView tvDesc;
		private ImageView tvDownload;

		@Override
		public void initView(View convertView) {
			ivCover = (ImageView) convertView.findViewById(R.id.iv_cover);
			tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
			tvDownload = (ImageView) convertView
					.findViewById(R.id.tv_button_download);
		}

		public String int2String(int position) {
			return position >= 10 ? String.valueOf(position) : "0" + position;
		}

		@Override
		public void updateView(final SohuPlayerItemBuilder item, int position,
				int count) {
			ImageUtils.download(item.poster, ivCover);
			if (bIndexAsTitle) {
				tvTitle.setText(mContext.getString(R.string.txt_episode_title,
						int2String(position + 1)));
			} else {
				tvTitle.setText(item.getTitle());
			}
			tvDesc.setText(item.summary);
			if (item.equals(activeItem)) {
				tvTitle.setTextColor(mContext.getResources().getColor(
						R.color.red));
				activeIndex = position;
			} else {
				tvTitle.setTextColor(mContext.getResources().getColor(
						R.color.white));
			}
			if (canDownload) {
				tvDownload.setVisibility(View.VISIBLE);
				tvDownload.setOnClickListener(mListener);
			} else {
				tvDownload.setVisibility(View.GONE);
				tvDownload.setOnClickListener(null);
			}
			tvDownload.setTag(item);
		}
	}

	private void download(final Context context, SohuPlayerItemBuilder item) {

	}


	private final OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			download(v.getContext(), (SohuPlayerItemBuilder) v.getTag());
		}
	};
}
