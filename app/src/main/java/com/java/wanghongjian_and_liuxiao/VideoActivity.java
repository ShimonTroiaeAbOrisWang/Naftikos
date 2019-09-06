package com.java.wanghongjian_and_liuxiao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.record.PlayRecordManager;
import com.kk.taurus.playerbase.widget.BaseVideoView;

public class VideoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        //TextView textView = findViewById(R.id.urlTextView);
        //textView.setText(videoURL);

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

        BaseVideoView mVideoView = findViewById(R.id.videoView);
        /*
        mVideoView.setOnPlayerEventListener(this);
        mVideoView.setOnReceiverEventListener(this);

        ReceiverGroup receiverGroup = new ReceiverGroup();
        receiverGroup.addReceiver(KEY_LOADING_COVER, new LoadingCover(context));
        receiverGroup.addReceiver(KEY_CONTROLLER_COVER, new ControllerCover(context));
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, new CompleteCover(context));
        receiverGroup.addReceiver(KEY_ERROR_COVER, new ErrorCover(context));
        mVideoView.setReceiverGroup(receiverGroup);
*/

        mVideoView.setEventHandler(new OnVideoViewEventHandler());

        mVideoView.setDataSource(new DataSource(intent.getStringExtra(MainActivity.EXTRA_VIDEO_URL)));
        mVideoView.start();
    }
}
