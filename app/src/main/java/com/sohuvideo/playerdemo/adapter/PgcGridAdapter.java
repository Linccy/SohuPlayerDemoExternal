package com.sohuvideo.playerdemo.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sohuvideo.playerdemo.R;
import com.sohuvideo.playerdemo.SecondPgcActivity;
import com.sohuvideo.playerdemo.entity.Pgc;
import com.sohuvideo.playerdemo.utils.CollectionUtil;

public class PgcGridAdapter extends BaseAdapter {
    private List<Pgc> mPgcList;
    private Context mContext;

    public PgcGridAdapter(List<Pgc> mPgcList, Context mContext) {
        super();
        this.mPgcList = mPgcList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mPgcList == null ? 0 : mPgcList.size();
    }

    @Override
    public Object getItem(int position) {
        return CollectionUtil.isGetValid(mPgcList, position) ? mPgcList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.grid_item_column, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.txt_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!CollectionUtil.isGetValid(mPgcList, position)) {
            return convertView;
        }
        final Pgc mPgc = mPgcList.get(position);
        if (mPgc != null) {
            String mPgcName = mPgc.cate_name;
            holder.mTextView.setText(mPgcName);
        }
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("pgc", mPgc);
                intent.putExtras(bundle);
                intent.setClass(mContext, SecondPgcActivity.class);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private TextView mTextView;
    }

}
