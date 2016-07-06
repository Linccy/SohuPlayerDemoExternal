package com.sohuvideo.playerdemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sohuvideo.playerdemo.R;

public class OptionAdapter extends BaseAdapter {
	private Context mContext;
	private boolean isMulti;

	private List<OptionItem> mItems;

	public OptionAdapter(Context context, boolean multi) {
		this.mContext = context;
		this.isMulti = multi;
	}

	public void setItems(List<OptionItem> items) {
		add(items, false);
	}

	public void appendItems(List<OptionItem> items) {
		add(items, true);
	}

	public void addItem(OptionItem item) {
		List<OptionItem> items = new ArrayList<OptionAdapter.OptionItem>();
		items.add(item);
		add(items, true);
	}

	private void add(List<OptionItem> items, boolean append) {
		if (mItems == null) {
			mItems = new ArrayList<OptionAdapter.OptionItem>();
		} else {
			if (!append) {
				mItems.clear();
			}
		}
		if (items != null) {
			mItems.addAll(items);
		}
		notifyDataSetChanged();
	}

	public Integer[] getSelecteds() {
		ArrayList<Integer> selecteds = new ArrayList<Integer>();
		for (int i = 0; i < mItems.size(); i++) {
			if (mItems.get(i).seleted) {
				selecteds.add(i);
			}
		}
		return selecteds.toArray(new Integer[0]);
	}

	public boolean isSelected(int index) {
		if (index < 0 || index >= getCount()) {
			return false;
		}
		OptionItem item = (OptionItem) getItem(index);
		return item.seleted;
	}

	public void update(int index) {
		if (index < 0 || index >= getCount()) {
			return;
		}
		OptionItem item = (OptionItem) getItem(index);
		if (isMulti) {
			item.seleted = !item.seleted;
		} else {
			clearSelected();
			item.seleted = true;
		}
		notifyDataSetChanged();
	}

	private void clearSelected() {
		for (OptionItem item : mItems) {
			item.seleted = false;
		}
	}

	@Override
	public int getCount() {
		return mItems == null ? 0 : mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems == null ? null : mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.option_item, null);
			holder = new ViewHolder();
			holder.mOption = (TextView) convertView.findViewById(R.id.option);
			holder.mSelected = (ImageView) convertView
					.findViewById(R.id.selected);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		OptionItem item = mItems.get(position);

		if (item == null) {
			return null;
		}

		holder.mOption.setText(item.option);
		int imgId = -1;
		if (isMulti) {
			imgId = item.seleted ? R.drawable.checkbox_selected
					: R.drawable.checkbox_normal;
		} else {
			imgId = item.seleted ? R.drawable.radio_btn_selected
					: R.drawable.radio_btn_normal;
		}
		holder.mSelected.setImageResource(imgId);
		return convertView;
	}

	private class ViewHolder {
		TextView mOption;
		ImageView mSelected;
	}

	public static class OptionItem {
		String option;
		boolean seleted;

		public OptionItem(String option, boolean selected) {
			this.option = option;
			this.seleted = selected;
		}

		public void setSelected(boolean selected) {
			this.seleted = selected;
		}
	}
}
