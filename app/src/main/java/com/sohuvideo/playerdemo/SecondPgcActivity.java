package com.sohuvideo.playerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sohuvideo.playerdemo.adapter.VideoGridAdapter;
import com.sohuvideo.playerdemo.entity.Pgc;
import com.sohuvideo.playerdemo.entity.PgcChannel;
import com.sohuvideo.playerdemo.entity.VideoList;
import com.sohuvideo.playerdemo.net.PgcListProtocol;
import com.sohuvideo.playerdemo.net.VideoListProtocol;

import java.util.List;

/**
 * pgc二级页面
 *
 * @author tinghaoma
 */
public class SecondPgcActivity extends BaseActivity {
    private LinearLayout titleLinear;
    private VideoGridAdapter mGridAdapter;
    private TextView mLoadingText;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_pgc);
        setUpViews();
        handleIntent();
    }

    private void setUpViews() {
        titleLinear = (LinearLayout) findViewById(R.id.titlelinear);
        mColumnGridView = (GridView) findViewById(R.id.mid);
        mLoadingLayout = findViewById(R.id.loading);
        mLoadingText = (TextView) findViewById(R.id.loading_text);
    }

    private void handleIntent() {
        mIntent = getIntent();
        final Pgc mPgc = (Pgc) mIntent.getSerializableExtra("pgc");
        setTitle(mPgc.cate_name);
        if (mPgc != null) {
            showLoadingDialog();
            new Thread() {
                @Override
                public void run() {
                    PgcListProtocol mColumnListProtocol = new PgcListProtocol(SecondPgcActivity.this, mPgc,
                            PlayVideoHelper.APIKEY);
                    final Pgc mNewPgc = mColumnListProtocol.request();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mNewPgc != null) {
                                initTitle(mNewPgc.getmChannelList());
                                if (mNewPgc.getmChannelList().size() > 0) {
                                    showContent(mNewPgc.getmChannelList().get(0));
                                } else {
                                    mLoadingText.setText("该栏目没有数据,请切换频道");
                                }
                            } else {
                                mLoadingText.setText("该栏目没有数据,请切换频道");
                            }
                        }
                    });
                };
            }.start();
        }
    }

    private void showContent(final PgcChannel mPgcChannel) {
        showLoadingDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoListProtocol videoListProtocol = new VideoListProtocol(SecondPgcActivity.this, mPgcChannel,
                        PlayVideoHelper.APIKEY);
                final VideoList videoList = videoListProtocol.request();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (videoList != null & videoList.getCount() > 0) {
                            mGridAdapter = new VideoGridAdapter(SecondPgcActivity.this, videoList.getVideos());
                            mColumnGridView.setAdapter(mGridAdapter);
                            dismissLoadingDialog();
                        } else {
                            mLoadingText.setText("该栏目没有数据,请切换频道");
                        }
                    }
                });
            }
        }).start();
    }

    private void initTitle(List<PgcChannel> getmChannelList) {
        for (int i = 0; i < getmChannelList.size(); i++) {
            Button btn = new Button(SecondPgcActivity.this);
            btn.setBackground(getResources().getDrawable(R.drawable.button_background));
            btn.setTextSize(14);
            final PgcChannel mPgcChannel = getmChannelList.get(i);
            btn.setText(mPgcChannel.second_cate_name);
            btn.setId(i);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    showContent(mPgcChannel);
                }
            });
            titleLinear.addView(btn, i);
        }
    }

    @Override
    public void initMenu(MenuInflater inflater, Menu menu) {

    }

}
