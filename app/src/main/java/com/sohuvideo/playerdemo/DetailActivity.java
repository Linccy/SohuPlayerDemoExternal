package com.sohuvideo.playerdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sohuvideo.api.DownloadInfo;
import com.sohuvideo.api.SohuDownloadManager;
import com.sohuvideo.api.SohuPlayerAPI;
import com.sohuvideo.api.SohuPlayerDefinition;
import com.sohuvideo.playerdemo.adapter.AlbumVideoGridAdapter;
import com.sohuvideo.playerdemo.adapter.DownloadVideoGridAdapter;
import com.sohuvideo.playerdemo.entity.Video;
import com.sohuvideo.playerdemo.entity.VideoList;
import com.sohuvideo.playerdemo.net.AlbumListProtocol;
import com.sohuvideo.playerdemo.utils.ImageUtils;

import java.util.ArrayList;

public class DetailActivity extends Activity {

    private SohuDownloadManager mDownloadManager;

    private ImageView videImage;

    private TextView videoNameTv;
    private TextView videoYear;
    private TextView videoArea;
    private TextView videoDirector;
    private TextView introductionTv;
    private TextView albumTv;
    private TextView introduction;
    private ScrollView introductionSV;
    private GridView albumGridView;
    private Button playBtn;
    private Button downloadBtn;
    private AlertDialog downloadDialog; // 下载对话框
    private Video video;
    private AlbumVideoGridAdapter adapter;
    private DownloadVideoGridAdapter downloadAdapter;
    private int[] definitions;
    private int definition = 1;
    private VideoList albumVideoList = new VideoList();
    private ArrayList<Video> downloadVideos = new ArrayList<Video>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            video = (Video) savedInstanceState.getSerializable("video");
        }
        setContentView(R.layout.activity_detail);
        setTitle(getResources().getString(R.string.video_detail));
        setupView();
        initData();
        startRequstAlbumData();
        mDownloadManager = SohuDownloadManager.getInstance();
        // 注册下载任务监听
        mDownloadManager.registerDownloadObserver(DownloadObserver.getInstance(this));
    }

    @Override
    protected void onDestroy() {
        // 注册下载任务监听
        mDownloadManager.unregisterDownloadObserver(DownloadObserver.getInstance(this));
        super.onDestroy();
    }

    private void setupView() {
        videImage = (ImageView) findViewById(R.id.pic);
        videoNameTv = (TextView) findViewById(R.id.videoname);
        videoYear = (TextView) findViewById(R.id.videotime);
        videoDirector = (TextView) findViewById(R.id.videodirector);
        videoArea = (TextView) findViewById(R.id.videoarea);
        playBtn = (Button) findViewById(R.id.play);
        downloadBtn = (Button) findViewById(R.id.download);
        introductionTv = (TextView) findViewById(R.id.introductiontv);
        introduction = (TextView) findViewById(R.id.introduction);
        introductionSV = (ScrollView) findViewById(R.id.scrollview_introduction);
        albumTv = (TextView) findViewById(R.id.albumtv);
        albumGridView = (GridView) findViewById(R.id.albumgv);
        introductionTv.setTextColor(getResources().getColor(R.color.red));
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.albumtv:
                    introductionTv.setTextColor(getResources().getColor(R.color.black));
                    albumTv.setTextColor(getResources().getColor(R.color.red));
                    albumGridView.setVisibility(View.VISIBLE);
                    introductionSV.setVisibility(View.GONE);
                    break;
                case R.id.introductiontv:
                    introductionTv.setTextColor(getResources().getColor(R.color.red));
                    albumTv.setTextColor(getResources().getColor(R.color.black));
                    albumGridView.setVisibility(View.GONE);
                    introductionSV.setVisibility(View.VISIBLE);
                    break;
                case R.id.download:
                    if (video == null || video.getIs_download() == 0 || definitions == null || definitions.length == 0) {
                        Toast.makeText(DetailActivity.this, "该视频不支持下载", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showDownloadDialog();
                    break;
                case R.id.play:
                    PlayVideoHelper.playSohuOnlineVideo(DetailActivity.this, video.getAid(), video.getVid(),
                            video.getSite(), 0);
                    break;
                default:
                    break;
            }

        }
    };

    private void initData() {
        Intent intent = getIntent();
        video = (Video) intent.getSerializableExtra("video");
        if (video != null) {
            ImageUtils.download(video.getPic(), videImage);
            String videoName = video.getVideo_name();
            if (videoName.contains("《")) {
                if (!videoName.startsWith("《")) {
                    videoName = "《" + videoName;
                }
                if (!videoName.endsWith("》")) {
                    videoName = videoName + "》";
                }
            }
            videoNameTv.setText(videoName);
            videoYear.setText(video.getYear() + "");
            videoDirector.setText(video.getDirector());
            videoArea.setText(video.getArea());
            introduction.setText("        " + video.getVideo_desc());
            downloadBtn.setClickable(false);
            playBtn.setOnClickListener(listener);
            introductionTv.setOnClickListener(listener);
            albumTv.setOnClickListener(listener);
            if (video.getIs_download() == 0) {
                downloadBtn.setEnabled(false);
                downloadBtn.setTextColor(getResources().getColor(R.color.gray1));
                downloadBtn.setAlpha(0.6f);
            } else {
                downloadBtn.setEnabled(true);
                downloadBtn.setTextColor(getResources().getColor(R.color.black));
                downloadBtn.setAlpha(1f);
            }
            downloadBtn.setOnClickListener(listener);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SohuPlayerAPI.SohuOpenVideoInfo info = SohuPlayerAPI.fetchOpenVideoInfo(video.getVid(),
                            video.getSite());
                    if (info != null) {
                        definitions = info.getSupportDefinitions();
                        downloadBtn.setClickable(true);
                    }
                }
            }).start();
        }
    }

    private void startRequstAlbumData() {
        if (video.getAid() == 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int totalSize = video.getTotal_video_count();
                int page = 1;
                while (totalSize > 0) {
                    AlbumListProtocol albumListProtocol = new AlbumListProtocol(DetailActivity.this, video.getAid(),
                            30, PlayVideoHelper.APIKEY, page);
                    if (albumListProtocol.request() == null || albumListProtocol.request().getVideos() == null) {
                        return;
                    }
                    if (albumVideoList == null) {
                        albumVideoList = new VideoList();
                    }
                    albumVideoList.addVideos(albumListProtocol.request().getVideos());
                    totalSize = totalSize - 30;
                    page++;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (albumVideoList != null && albumVideoList.getVideos().size() > 0) {
                            adapter = new AlbumVideoGridAdapter(DetailActivity.this, albumVideoList.getVideos());
                            albumGridView.setAdapter(adapter);
                        }
                    }
                });
            }
        }).start();
    }

    private void download(Video video, int definition) {
        DownloadInfo info = new DownloadInfo(video.getVid(), video.getAid(), video.getSite(), definition);
        info.setTitle(video.getVideo_name());
        mDownloadManager.addTask(info);
    }

    private void showDownloadDialog() {
        if (definitions == null || definitions.length <= 0) {
            Toast.makeText(DetailActivity.this, "未获取到该视频的清晰度~", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.dialog_download, null);
        GridView videosGV = (GridView) layout.findViewById(R.id.albumdownload);
        RadioGroup radiogroup = (RadioGroup) layout.findViewById(R.id.radiogroup);
        RadioButton fluencyRb = (RadioButton) layout.findViewById(R.id.definition_fluency);
        RadioButton highRb = (RadioButton) layout.findViewById(R.id.definition_high);
        RadioButton superRb = (RadioButton) layout.findViewById(R.id.definition_super);
        RadioButton originalRb = (RadioButton) layout.findViewById(R.id.definition_original);
        for (int i = 0; i < definitions.length; i++) {
            switch (definitions[i]) {
                case SohuPlayerDefinition.PE_DEFINITION_FLUENCY:
                    fluencyRb.setVisibility(View.VISIBLE);
                    break;
                case SohuPlayerDefinition.PE_DEFINITION_HIGH:
                    highRb.setVisibility(View.VISIBLE);
                    break;
                case SohuPlayerDefinition.PE_DEFINITION_SUPER:
                    superRb.setVisibility(View.VISIBLE);
                    break;
                case SohuPlayerDefinition.PE_DEFINITION_ORIGINAL:
                    originalRb.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.definition_fluency:
                        definition = SohuPlayerDefinition.PE_DEFINITION_FLUENCY;
                        break;
                    case R.id.definition_high:
                        definition = SohuPlayerDefinition.PE_DEFINITION_HIGH;
                        break;
                    case R.id.definition_super:
                        definition = SohuPlayerDefinition.PE_DEFINITION_SUPER;
                        break;
                    case R.id.definition_original:
                        definition = SohuPlayerDefinition.PE_DEFINITION_ORIGINAL;
                        break;
                    default:
                        break;
                }
            }
        });
        if (albumVideoList != null && albumVideoList.getVideos().size() > 0) {
            downloadAdapter = new DownloadVideoGridAdapter(albumVideoList.getVideos());
            videosGV.setAdapter(downloadAdapter);
        }
        builder.setView(layout);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (definition == 0) {
                    return;
                }
                if (downloadAdapter == null) {
                    downloadVideos.add(video);
                } else {
                    downloadVideos = downloadAdapter.getCheckVideo();
                }
                if (downloadVideos == null || downloadVideos.size() <= 0) {
                    return;
                }
                for (Video video : downloadVideos) {
                    download(video, definition);
                }
                if (downloadDialog != null) {
                    downloadDialog.dismiss();
                }
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("video", video);
    }
}
