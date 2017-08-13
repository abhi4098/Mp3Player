package com.example.abhinandansharma.mp3;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinandansharma.mp3.model.Song;
import com.example.abhinandansharma.mp3.utility.RoundImage;
import com.triggertrap.seekarc.SeekArc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.abhinandansharma.mp3.MainActivity.mp;

/**
 * Created by Abhinandan on 18/5/17.
 */

public class MusicPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,View.OnClickListener,SeekArc.OnSeekArcChangeListener{
    private ImageButton btnPlay;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton ivSongImage;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private TextView songTitleLabel;
    private TextView songArtistlabel;
    private LinearLayout llMain;
    private com.triggertrap.seekarc.SeekArc songProgressBar;


    //private static  MediaPlayer mp;
    private Intent intent;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private MainActivity songManager;
    private Utilities utils;
    private ArrayList<Song> songsList;
    //current position
    private int songPosn;
    private String songTitle="";
    private String songArtist="";
    private String songImage="";
    RoundImage roundedImage;

    private static final int NOTIFY_ID=1;
    private boolean shuffle=false;
    private Random rand;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    String playerState;
    int seekbarProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);

        intent = getIntent();
        if(intent != null) {
            currentSongIndex = intent.getIntExtra("Song_number", 0);
            playerState = intent.getStringExtra("media_player_state");
            seekbarProgress= getIntent().getIntExtra("seekbar_progress",0);
        }


        btnPlay = (ImageButton) findViewById(R.id.ic_play);
        btnNext = (ImageButton) findViewById(R.id.next_song);
        btnPrevious = (ImageButton) findViewById(R.id.previous_song);
        btnRepeat = (ImageButton) findViewById(R.id.ic_repeat_one);
        btnShuffle = (ImageButton) findViewById(R.id.ic_shuffle);
        songTitleLabel = (TextView) findViewById(R.id.song_name);
        songArtistlabel = (TextView) findViewById(R.id.song_artist);
        songProgressBar = (SeekArc) findViewById(R.id.seekArc);
        ivSongImage = (ImageButton) findViewById(R.id.song_image);
        llMain = (LinearLayout) findViewById(R.id.playing_activity);
        songManager = new MainActivity();
        utils = new Utilities();

        songProgressBar.setOnSeekArcChangeListener(this);
        songsList = MainActivity.songList;


        playSong(currentSongIndex);

        mp.setOnCompletionListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);




    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ic_play:
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        btnPlay.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                    }
                }else{
                    if(mp!=null){
                        mp.start();
                        btnPlay.setImageResource(R.drawable.ic_pause_black_36dp);
                    }
                }
                break;



            case R.id.next_song:

                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                    Log.e("abhi", "onClick:current song " +currentSongIndex );
                }else{
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                    Log.e("abhi", "onClick: first song "  );
                }
                break;



            case R.id.previous_song:

                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }
                break;


            case R.id.ic_repeat_one:
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat OFF", Toast.LENGTH_SHORT).show();
                   // btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    /*btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);*/
                }
                break;



            case R.id.ic_shuffle:
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle OFF", Toast.LENGTH_SHORT).show();
                  //  btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    /*btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);*/
                }
                break;
        }
    }


    public void  playSong(int songIndex) {

            Song playSong = songsList.get(songIndex);
            songTitle = playSong.getTitle();
            songArtist = playSong.getArtist();
            songImage = playSong.getImage();
            songArtistlabel.setText(songArtist);
            songTitleLabel.setText(songTitle);

           checkMp3PlayerStatus(playSong);

        if (songImage !=null) {
            Bitmap bm = BitmapFactory.decodeFile(songImage);
            Bitmap resizedImage = Bitmap.createScaledBitmap(bm, 710, 710, true);
            roundedImage = new RoundImage(resizedImage);
            ivSongImage.setImageDrawable(roundedImage);
            llMain.setBackground(Drawable.createFromPath(songImage));
        }



        if (mp.isPlaying()) {
            btnPlay.setImageResource(R.drawable.ic_pause_black_36dp);
        } else {
            btnPlay.setImageResource(R.drawable.ic_play_arrow_black_36dp);
        }





        }

    private void checkMp3PlayerStatus(Song playSong) {


        if (playerState.equals("true")){

            playerState = "false";

            try {
                songProgressBar.setProgress(seekbarProgress);
                updateProgressBar();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        }
        else

        {
            mp.reset();

        long currSong = playSong.getID();
            Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
            try {
                mp.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC ", "Error setting data source", e);
            }

        try {
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

            try {
                songProgressBar.setProgress(0);
                updateProgressBar();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }



    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable() {

        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            //songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            //songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }

    @Override
    public void onBackPressed() {


        Log.e("abhi", "onBackPressed: " +currentSongIndex );
        Intent returnMainActivity = new Intent();
        returnMainActivity.putExtra("currentSongIndex",currentSongIndex);

        setResult(RESULT_OK,returnMainActivity);
        finish();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }

    }



    @Override
    public void onProgressChanged(SeekArc seekArc, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekArc seekArc) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekArc seekArc) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekArc.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
