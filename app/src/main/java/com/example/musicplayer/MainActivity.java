package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String[] songs = {"Aathma-raama","Bones", "Dandelions", "Fake-love",
            "How-far-i'll-go","How-you-like-that","Little-do-you-know", "Love-is-gone",
            "On-my-way", "Perfect", "Undo", "Way-too-easy", "Wishlist"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView songListView = findViewById(R.id.songListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        songListView.setAdapter(adapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSong = songs[position];
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("song", selectedSong);
                startActivity(intent);
            }
        });
    }
}
