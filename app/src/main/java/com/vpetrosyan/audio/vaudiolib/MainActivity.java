package com.vpetrosyan.audio.vaudiolib;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vpetrosyan.audio.file.AudioDataExtractor;
import com.vpetrosyan.audio.file.WAVAudioDataExtractor;
import com.vpetrosyan.audio.formatter.AudioTimeFormatter;
import com.vpetrosyan.audio.formatter.LongTimeFormatter;
import com.vpetrosyan.audio.vwaveview.VWaveView;

public class MainActivity extends AppCompatActivity {

    private TextView timeTextView;
    private VWaveView waveView;

    private FloatingActionButton mPlayBtn;

    private MediaPlayer mediaPlayer;

    private AudioTimeFormatter formatter = new LongTimeFormatter();

    private Handler mHandler = new Handler();

    private CheckBox checkBox;

    private boolean wasPlaying = false;

    private Runnable waveUpdater = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int time = mediaPlayer.getCurrentPosition();
                waveView.seekTo(time);
                timeTextView.setText(formatter.formatTime(time));
                mHandler.postDelayed(this, 24);
            }
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
            public void onSeek(long time) {
                mediaPlayer.seekTo((int) time);
                timeTextView.setText(formatter.formatTime(time));
            }

            @Override
            public void onSeekStarted() {
                pauseAudio();
            }

            @Override
            public void onSeekCompleted() {
                if(wasPlaying && checkBox.isChecked()) {
                    startAudio();
                }
            }
        });

        mPlayBtn = findViewById(R.id.playFab);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer == null) {
                    AudioDataExtractor extractor = new WAVAudioDataExtractor();
//                    AudioDataExtractor extractor = new PCMAudioExtractor();
                    waveView.setAudio(extractor.extractData(MainActivity.this, R.raw.test3));
                    timeTextView.setText(formatter.formatTime(0));

                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.test3);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
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
                    startAudio();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseAudio();
    }

    private void pauseAudio() {
        mPlayBtn.setImageResource(android.R.drawable.ic_media_play);

        if(mediaPlayer != null) {
            wasPlaying = mediaPlayer.isPlaying();
            mediaPlayer.pause();
        }
    }

    private void startAudio() {
        mPlayBtn.setImageResource(android.R.drawable.ic_media_pause);

        if(mediaPlayer != null) {
            mediaPlayer.start();
            runOnUiThread(waveUpdater);
        }
    }
}
