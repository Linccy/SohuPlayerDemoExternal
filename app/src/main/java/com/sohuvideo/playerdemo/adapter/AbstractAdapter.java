/*
 * Copyright (c) 2013 Sohu TV. All rights reserved.
 */
package com.sohuvideo.playerdemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author shileiHao 2013-4-17
 */
public abstract class AbstractAdapter<T> extends BaseAdapter {
	protected LayoutInflater mInflater;
	protected final List<T> mLocalData = new ArrayList<T>();
	protected Context mContext;

	public AbstractAdapter(Context ctx) {
		mContext = ctx;
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void udpateList(List<T> newList) {
		updateList(newList, true);
	}

	public void updateList(List<T> newList, boolean notify) {
		mLocalData.clear();
		if (newList != null) {
			mLocalData.addAll(newList);
		}
		if (notify) {
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mLocalData.size();
	}

	@Override
	public T getItem(int position) {
		if (position < 0 || position >= getCount()) {
			return null;
		}
		return mLocalData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder<T> holder = null;
		if (convertView != null) {
			holder = (ViewHolder<T>) convertView.getTag();
		} else {
			convertView = mInflater.inflate(getLayoutId(position), null);
			holder = createViewHolder();
			holder.initView(convertView);
			convertView.setTag(holder);
		}
		holder.updateView(getItem(position), position, getCount());
		return convertView;
	}

	protected abstract int getLayoutId(int position);

	protected abstract ViewHolder<T> createViewHolder();

	public static interface ViewHolder<T> {
		public void initView(View convertView);

		public void updateView(T item, int position, int count);
	}
}
