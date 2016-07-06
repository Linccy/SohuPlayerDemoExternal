package com.sohuvideo.playerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.sohuvideo.playerdemo.adapter.PgcGridAdapter;
import com.sohuvideo.playerdemo.entity.PgcList;
import com.sohuvideo.playerdemo.utils.JsonUtils;

public class MainActivity extends BaseActivity {
    private static final String FILENAME = "pgc.json";
    private static final String TAG = "MainActivity";
    private PgcGridAdapter mPgcAdapter;
    private GridView mPgcGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        loadData();
    }

    private void setupViews() {
        mColumnGridView = (GridView) findViewById(R.id.mid);
        mPgcGridView = (GridView) findViewById(R.id.pgc_gridview);
        mLoadingLayout = findViewById(R.id.loading);
    }

    private void loadData() {
        showLoadingDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final PgcList mPgcList = getAssetsJsonResource();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO 显示PGC内容
                        if (mPgcList != null) {
                            mPgcAdapter = new PgcGridAdapter(mPgcList.getPgcs(), MainActivity.this);
                            mPgcGridView.setAdapter(mPgcAdapter);
                        }
                        dismissLoadingDialog();
                    }
                });
            }
        }).start();
    }

    /**
     * 读取assets下面的pgc.json
     */
    private PgcList getAssetsJsonResource() {
        String jsonStr = JsonUtils.getJson(MainActivity.this, FILENAME);
        PgcList pgcList = JsonUtils.getPgcList(jsonStr);
        return pgcList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_more_test:
                Intent moreTestIntent = new Intent();
                moreTestIntent.setClass(MainActivity.this, MoreTestActivity.class);
                startActivity(moreTestIntent);
                return true;
            case R.id.menu_download:
                Intent downloadIntent = new Intent();
                downloadIntent.setClass(MainActivity.this, DownloadActivity.class);
                startActivity(downloadIntent);
                return true;
        }
        return false;
    }

    @Override
    public void initMenu(MenuInflater inflater, Menu menu) {
        inflater.inflate(R.menu.menu_main, menu);
    }
}
