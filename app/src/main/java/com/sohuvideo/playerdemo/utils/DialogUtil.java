package com.sohuvideo.playerdemo.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sohuvideo.playerdemo.R;
import com.sohuvideo.playerdemo.adapter.OptionAdapter;
import com.sohuvideo.playerdemo.adapter.OptionAdapter.OptionItem;

public class DialogUtil {
	private static final String TAG = "DialogUtil";

	public interface OnOptionsListener {
		void onOptions(int[] selecteds);
	}

	public interface OnOptionItemClickListener {
		void onOption(int position, boolean selected);
	}

	private static class OptionsDialogListener implements
			DialogInterface.OnClickListener {
		private Context mContext;
		private OnOptionsListener mOnOptionsListener;
		private OnOptionItemClickListener mOnOptionItemClickListener;
		private OptionAdapter mAdapter;

		public OptionsDialogListener(Context context,
				OnOptionsListener onOptionsListener,
				OnOptionItemClickListener onOptionItemClickListener,
				OptionAdapter adapter, ListView view) {
			this.mContext = context;
			this.mOnOptionsListener = onOptionsListener;
			mAdapter = adapter;
			view.setAdapter(mAdapter);
			view.setOnItemClickListener(mOnItemClickListener);
			this.mOnOptionItemClickListener = onOptionItemClickListener;
		}

		public OptionsDialogListener(Context context,
				OnOptionsListener onOptionsListener, List<OptionItem> items,
				boolean multi, ListView view) {
			this.mContext = context;
			this.mOnOptionsListener = onOptionsListener;
			init(items, multi, view);
		}

		public OptionsDialogListener(Context context,
				OnOptionsListener onOptionsListener, String[] options,
				int[] indexs, boolean multi, ListView view) {
			this.mContext = context;
			this.mOnOptionsListener = onOptionsListener;
			init(options, indexs, multi, view);
		}

		private void init(List<OptionItem> items, boolean multi, ListView view) {
			mAdapter = new OptionAdapter(mContext, multi);
			view.setAdapter(mAdapter);
			view.setOnItemClickListener(mOnItemClickListener);
			mAdapter.setItems(items);
		}

		private void init(String[] options, int[] indexs, boolean multi,
				ListView view) {
			mAdapter = new OptionAdapter(mContext, multi);
			view.setAdapter(mAdapter);
			view.setOnItemClickListener(mOnItemClickListener);
			ArrayList<OptionItem> items = new ArrayList<OptionAdapter.OptionItem>();
			OptionItem item = null;
			for (int i = 0; i < options.length; i++) {
				boolean selected = false;
				for (int index : indexs) {
					if (index == i) {
						selected = true;
						break;
					}
				}
				item = new OptionItem(options[i], selected);
				items.add(item);
			}
			mAdapter.setItems(items);
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which != DialogInterface.BUTTON_POSITIVE) {
				return;
			}

			if (mOnOptionsListener != null) {
				mOnOptionsListener.onOptions(ArrayUtil.intValues(mAdapter
						.getSelecteds()));
			}
		}

		private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mAdapter == null) {
					return;
				}
				mAdapter.update(position);
				if (mOnOptionItemClickListener != null && mAdapter != null) {
					mOnOptionItemClickListener.onOption(position,
							mAdapter.isSelected(position));
				}
			}
		};
	}

	public static void showOptionsDialog(Context context, int titleid,
			String[] options, int[] indexs, boolean multi,
			OnOptionsListener onOptionsListener) {
		String title = context.getString(titleid);
		showOptionDialog(context, title, options, indexs, multi,
				onOptionsListener);
	}

	public static void showOptionDialog(Context context, String title,
			String[] options, int[] indexs, boolean multi,
			OnOptionsListener onOptionsListener) {
		ListView view = (ListView) View.inflate(context, R.layout.option_list,
				null);

		OptionsDialogListener listener = new OptionsDialogListener(context,
				onOptionsListener, options, indexs, multi, view);

		UIUtil.showDialog(context, listener, title, R.string.btn_confirm,
				R.string.btn_cancel, -1, view);
	}

	public static void showOptionDialog(Context context, String title,
			List<OptionItem> items, boolean multi,
			OnOptionsListener onOptionsListener) {
		ListView view = (ListView) View.inflate(context, R.layout.option_list,
				null);

		OptionsDialogListener listener = new OptionsDialogListener(context,
				onOptionsListener, items, multi, view);

		// UIUtil.showDialog(context, listener, title, R.string.btn_confirm,
		// R.string.btn_cancel, -1, view);
		UIUtil.showDialog(context, listener, title, -1, -1, -1, view);
	}

	public static void showOptionDialog(Context context, String title,
			OptionAdapter adapter, OnOptionsListener onOptionsListener,
			OnOptionItemClickListener onOptionItemClickListener) {
		ListView view = (ListView) View.inflate(context, R.layout.option_list,
				null);
		OptionsDialogListener listener = new OptionsDialogListener(context,
				onOptionsListener, onOptionItemClickListener, adapter, view);
		UIUtil.showDialog(context, listener, title, -1, -1, -1, view);
	}
}
