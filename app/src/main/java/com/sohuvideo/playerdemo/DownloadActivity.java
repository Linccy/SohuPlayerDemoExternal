package com.sohuvideo.playerdemo;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sohuvideo.api.DownloadInfo;
import com.sohuvideo.api.SohuDownloadManager;
import com.sohuvideo.api.SohuPlayerSDK;
import com.sohuvideo.playerdemo.adapter.DownloadListAdapter;

public class DownloadActivity extends BaseActivity implements OnItemClickListener {
    private static final String TAG = "DownloadActivity";

    private SohuDownloadManager mSohuDownloadManager;

    private ListView lv_download_list;// 列表

    private DownloadListAdapter mDownloadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.download_title));
        SohuPlayerSDK.init(this);
        mDownloadListAdapter = new DownloadListAdapter(this);
        mSohuDownloadManager = SohuDownloadManager.getInstance();
        mSohuDownloadManager.registerDownloadObserver(mDownloadListAdapter);
        List<DownloadInfo> allTask = mSohuDownloadManager.getAllTasks();
        mDownloadListAdapter.sortTask(allTask);
        mDownloadListAdapter.setData(allTask);
        initLayout();
    }

    @Override
    protected void onDestroy() {
        mSohuDownloadManager.unregisterDownloadObserver(mDownloadListAdapter);
        super.onDestroy();
    }

    /**
     * 初始化界面
     */
    private void initLayout() {
        setContentView(R.layout.download_list);
        lv_download_list = (ListView) findViewById(R.id.lv_download_list);
        lv_download_list.setAdapter(mDownloadListAdapter);
        lv_download_list.setOnItemClickListener(this);
    }

    /**
     * 添加测试下载任务
     */
    private void addDownloadMovieTask() {
        DownloadInfo movieInfo = new DownloadInfo(2164533, 0, 1, 2);
        movieInfo.setTitle("搜狐视频 Movie");
        mSohuDownloadManager.addTask(movieInfo);
        DownloadInfo pgcInfo = new DownloadInfo(81432070, 0, 2, 2);
        pgcInfo.setTitle("PGC Movie");
        mSohuDownloadManager.addTask(pgcInfo);
    }

    /**
     * 每个列表item点击触发的事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final DownloadInfo info = (DownloadInfo) mDownloadListAdapter.getItem(position);
        if (info == null) {
            return;
        }

        CharSequence[] actions;
        if (info.isCompleted()) {
            actions = new String[] { "Delete", "Play" };
        } else {
            actions = new String[] { "Delete" };
        }
        AlertDialog itemDialog = new AlertDialog.Builder(this).setItems(actions, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 删除任务
                        mSohuDownloadManager.removeTask(info.getTaskId());
                        break;
                    case 1:// 播放
                        PlayVideoHelper.playDownloadVideo(DownloadActivity.this, info.getTaskId(), 0);
                        break;
                }

            }
        }).create();

        itemDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return super.onOptionsItemSelected(item);
            case R.id.add_downloadtask:
                addDownloadMovieTask();// 添加买测试下载
                return true;
        }
        return false;
    }

    @Override
    public void initMenu(MenuInflater inflater, Menu menu) {
        inflater.inflate(R.menu.menu_download, menu);
    }
}
