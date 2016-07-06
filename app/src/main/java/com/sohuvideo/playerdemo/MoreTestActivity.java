package com.sohuvideo.playerdemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MoreTestActivity extends BaseActivity {

    private EditText tvidEdt;
    private EditText liveUrlEdit;
    private EditText localUrlEdit;
    private Button playLiveBtn;
    private Button playLocalBtn;
    private EditText vidEdit;
    private EditText siteEdit;
    private EditText aidEdit;
    private EditText secondEdit;
    private Button playSohuBtn;
    private int tvid;
    private long vid;
    private int site;
    private long aid;
    private int startposition;
    private String liveUrl;
    private String localUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moretest);
        setTitle(getResources().getString(R.string.my_moretest));
        setupView();
    }

    private void setupView() {
        tvidEdt = (EditText) findViewById(R.id.tvid);
        liveUrlEdit = (EditText) findViewById(R.id.liveurl);
        localUrlEdit = (EditText) findViewById(R.id.localurl);
        vidEdit = (EditText) findViewById(R.id.vid);
        siteEdit = (EditText) findViewById(R.id.site);
        aidEdit = (EditText) findViewById(R.id.aid);
        secondEdit = (EditText) findViewById(R.id.second);
        playSohuBtn = (Button) findViewById(R.id.playsohuvideo);
        playLiveBtn = (Button) findViewById(R.id.playlivevideo);
        playLocalBtn = (Button) findViewById(R.id.playlocalvideo);
        playSohuBtn.setOnClickListener(listener);
        playLiveBtn.setOnClickListener(listener);
        playLocalBtn.setOnClickListener(listener);
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            tvid = Integer.parseInt(tvidEdt.getEditableText().toString().trim());
            liveUrl = liveUrlEdit.getEditableText().toString().trim();
            localUrl = localUrlEdit.getEditableText().toString().trim();
            vid = Long.parseLong(vidEdit.getEditableText().toString().trim());
            site = Integer.parseInt(siteEdit.getEditableText().toString().trim());
            aid = Long.parseLong(aidEdit.getEditableText().toString().trim());
            startposition = Integer.parseInt(secondEdit.getEditableText().toString().trim()) * 1000;
            switch (v.getId()) {
                case R.id.playlivevideo:
                    if (tvid == 0 && TextUtils.isEmpty(liveUrl)) {
                        Toast.makeText(MoreTestActivity.this, "please input one live infomation", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    PlayVideoHelper.playSohuLiveVideo(MoreTestActivity.this, tvid, liveUrl, 0);
                    break;
                case R.id.playlocalvideo:
                    if (TextUtils.isEmpty(localUrl)) {
                        Toast.makeText(MoreTestActivity.this, "please input one local infomation", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    PlayVideoHelper.playLocalVideo(MoreTestActivity.this, localUrl, 0);
                    break;
                case R.id.playsohuvideo:
                    if (vid == 0 || site <= 0) {
                        Toast.makeText(MoreTestActivity.this, "please input sohuvideo infomation", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    PlayVideoHelper.playSohuOnlineVideo(MoreTestActivity.this, aid, vid, site, startposition);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void initMenu(MenuInflater inflater, Menu menu) {

    }
}
