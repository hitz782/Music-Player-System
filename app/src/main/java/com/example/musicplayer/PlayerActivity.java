package com.example.musicplayer;// PlayerActivity.java

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PlayerActivity extends AppCompatActivity {

    private Button playButton;
    private Button pauseButton;
    private Button previousButton;
    private Button nextButton;
    private TextView songNameTextView;
    private TextView lyricsTextView;
    private SeekBar seekBar;

    private String selectedSong;
    private MediaPlayer mediaPlayer;

    private String[] songs = {"Aathma-raama","Bones", "Dandelions", "Fake-love",
            "How-far-i'll-go","How-you-like-that","Little-do-you-know", "Love-is-gone",
            "On-my-way", "Perfect", "Undo", "Way-too-easy", "Wishlist"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        songNameTextView = findViewById(R.id.songNameTextView);
        lyricsTextView = findViewById(R.id.lyricsTextView);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        if (intent != null) {
            selectedSong = intent.getStringExtra("song");
            String lyrics = loadLyrics(selectedSong);
            lyricsTextView.setText(lyrics);
            songNameTextView.setText(selectedSong);
        }

        mediaPlayer = loadSong(selectedSong);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement previous button logic
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                int currentIndex = getIndex(selectedSong);
                int previousIndex = (currentIndex - 1) % songs.length;
                if (previousIndex < 0) {
                    previousIndex = songs.length - 1;
                }
                String previousSong = songs[previousIndex];
                String lyrics = loadLyrics(previousSong);
                lyricsTextView.setText(lyrics);
                selectedSong = previousSong;
                songNameTextView.setText(selectedSong);
                mediaPlayer = loadSong(selectedSong);
                mediaPlayer.start();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement next button logic
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                int currentIndex = getIndex(selectedSong);
                int nextIndex = (currentIndex + 1) % songs.length;
                String nextSong = songs[nextIndex];
                String lyrics = loadLyrics(nextSong);
                lyricsTextView.setText(lyrics);
                selectedSong = nextSong;
                songNameTextView.setText(selectedSong);
                mediaPlayer = loadSong(selectedSong);
                mediaPlayer.start();
            }
        });

        // SeekBar functionality
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });

        // Update SeekBar progress
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = mediaPlayer.getDuration();
                seekBar.setMax(duration);
            }
        });

        // Update SeekBar position
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentPosition);
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private int getIndex(String song) {
        for (int i = 0; i < songs.length; i++) {
            if (songs[i].equals(song)) {
                return i;
            }
        }
        return -1;
    }

    private MediaPlayer loadSong(String song) {
        AssetManager assetManager = getAssets();
        try {
            String[] files = assetManager.list("songs");
            for (String file : files) {
                if (file.equals(song + ".mp3")) {
                    AssetFileDescriptor assetFileDescriptor = assetManager.openFd("songs/" + file);
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                    mediaPlayer.prepare();
                    return mediaPlayer;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String loadLyrics(String song) {
        String lyrics = "";
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("lyrics/" + song + ".txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lyrics += line + "\n";
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lyrics;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
