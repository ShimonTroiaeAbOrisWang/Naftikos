package com.java.wanghongjian_and_liuxiao;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.record.PlayRecordManager;
import com.kk.taurus.playerbase.widget.BaseVideoView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

public class VideoActivity extends AppCompatActivity {

    private SlidrConfig mConfig;
    private BaseVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .velocityThreshold(2400)
                .scrimStartAlpha(0).scrimEndAlpha(0)
                .build();

        // Attach the Slidr Mechanism to this activity
        Slidr.attach(this, mConfig);

        Intent intent = getIntent();

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        /* hide bar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        /* init video lib */
        PlayerConfig.setUseDefaultNetworkEventProducer(true);
        PlayerLibrary.init(this);

        PlayerConfig.playRecord(true);
        PlayRecordManager.setRecordConfig(
                new PlayRecordManager.RecordConfig.Builder()
                        .setMaxRecordCount(100)
                        //.setRecordKeyProvider()
                        //.setOnRecordCallBack()
                        .build());

        mVideoView = findViewById(R.id.videoView);



        mVideoView.setEventHandler(new OnVideoViewEventHandler());

        mVideoView.setDataSource(new DataSource(intent.getStringExtra(MainActivity.EXTRA_VIDEO_URL)));
        mVideoView.start();
    }

    public void pause (View view) {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.resume();
        }
    }

    @Override
    public void finish() {
        mVideoView.stopPlayback();
        super.finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
    }
}
