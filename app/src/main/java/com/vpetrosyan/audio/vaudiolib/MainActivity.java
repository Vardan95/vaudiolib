package com.vpetrosyan.audio.vaudiolib;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.TextView;

import com.vpetrosyan.audio.file.AudioData;
import com.vpetrosyan.audio.file.AudioDataExtractor;
import com.vpetrosyan.audio.file.PCMAudioExtractor;
import com.vpetrosyan.audio.file.WAVAudioDataExtractor;
import com.vpetrosyan.audio.formatter.AudioTimeFormatter;
import com.vpetrosyan.audio.formatter.LongTimeFormatter;
import com.vpetrosyan.audio.vwaveview.VWaveView;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView timeTextView;
    private VWaveView waveView;

    private FloatingActionButton mPlayBtn;

    private MediaPlayer mediaPlayer;

    private AudioTimeFormatter formatter = new LongTimeFormatter();

    private Handler mHandler = new Handler();

    private CheckBox checkBox;

    private Runnable waveUpdater = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                waveView.seekTo(mediaPlayer.getCurrentPosition());
            }
            mHandler.postDelayed(this, 24);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTextView = findViewById(R.id.time);
        waveView = findViewById(R.id.wave);
        waveView.setVisibility(View.GONE);

        checkBox = findViewById(R.id.checkbox);

        waveView.setListener(new VWaveView.SeekListener() {
            @Override
            public void onSeek(long time, boolean isFromUser) {
                timeTextView.setText(formatter.formatTime(time));

                if (isFromUser) {
                    if(!checkBox.isChecked()) {
                        pauseAudio();
                    }
                    mediaPlayer.seekTo((int) time);
                }
            }
        });

        mPlayBtn = findViewById(R.id.playFab);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.test3);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            //AudioDataExtractor extractor = new PCMAudioExtractor();
                            AudioDataExtractor extractor = new WAVAudioDataExtractor();
                            //waveView.setAudio(extractor.extractData(MainActivity.this, R.raw.jinglebells));
                            waveView.setAudio(extractor.extractData(MainActivity.this, R.raw.test3));
                            timeTextView.setText(formatter.formatTime(0));

                            waveView.setVisibility(View.VISIBLE);

                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mPlayBtn.setImageResource(android.R.drawable.ic_media_play);
                            mediaPlayer.seekTo(0);
                        }
                    });
                }

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    pauseAudio();
                } else {
                    runOnUiThread(waveUpdater);
                    mPlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                    mediaPlayer.start();
                }
            }
        });

        runOnUiThread(waveUpdater);
    }

    private void pauseAudio() {
        mPlayBtn.setImageResource(android.R.drawable.ic_media_play);
        mediaPlayer.pause();
    }
}
