package com.practice.songplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.icu.util.TimeUnit;
import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.media.PlaybackParams;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements Runnable {

    // Declaration of variables
    NumberPicker songPicker ;
    SeekBar positioner ;
    MediaPlayer songPlayer ;
    TextView timeHint ;
    FloatingActionButton playButton ;
    int position, numberOfSongs=4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisation of objects
        positioner = findViewById(R.id.positioner) ;
        songPicker = findViewById(R.id.songPicker) ;
        timeHint = findViewById(R.id.textTime) ;
        playButton = findViewById(R.id.playbutton) ;

        switch (songPicker.getValue())
        {
            case 0: songPlayer = MediaPlayer.create(getApplicationContext(),R.raw.numb);  break ;
            case 1: songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.newdivide);  break ;
            case 2: songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.intheend);  break ;
            case 3: songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.givenup);  break ;
        }

        String[] songNames = { "Numb", "New Divide", "In The End", "Given Up" } ;
        songPicker.setDisplayedValues(songNames);
        songPicker.setMinValue(0);
        songPicker.setMaxValue(numberOfSongs-1);
        positioner.setMax(songPlayer.getDuration());

        //When a new song is picked from the NumberPicker
        songPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                songPlayer.stop();
                switch (newVal)
                {
                    case 0: songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.numb);  break ;
                    case 1: songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.newdivide);  break ;
                    case 2: songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.intheend);  break ;
                    case 3: songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.givenup);  break ;
                }
                positioner.setMax(songPlayer.getDuration());
                positioner.setProgress(0);
            }
        });

        //When SeekBar's value is changed either by the user or by the program itself
        positioner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( fromUser )  songPlayer.seekTo(progress);    //Changes the position in the song if from the user
                position = progress;

                int minutes = position/60000 ;
                int seconds = position/1000 ;
                seconds = seconds%60 ;
                if( seconds<10 )    timeHint.setText(minutes+":0"+seconds);
                else    timeHint.setText(minutes+":"+seconds);
                timeHint.setX(seekBar.getLeft()+position*(seekBar.getRight()-seekBar.getLeft()-70)/(float)seekBar.getMax()); //setting the timeHint TextView position
                timeHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void playSong( View view )
    {
        if( songPlayer.isPlaying() )    //If already playing, then needs to pause
        {
            songPlayer.pause();
            playButton.setImageDrawable(ContextCompat.getDrawable(this,android.R.drawable.ic_media_play));      //Changes the Pause icon to play icon
        }
        else                            // Start the Song
        {
            songPlayer.seekTo(position);
            songPlayer.start();
            playButton.setImageDrawable(ContextCompat.getDrawable(this,android.R.drawable.ic_media_pause));     //Changes the Play icon to Pause icon
            new Thread(this).start();       //New Thread is started to keep the Slider of the seekBar and timeHin updated
        }
    }

    public void run()           //This function starts as the new Thread starts
    {
        while (songPlayer != null && songPlayer.isPlaying())
        {
            positioner.setProgress(songPlayer.getCurrentPosition());    //updating the SeekBar
        }
    }
}
