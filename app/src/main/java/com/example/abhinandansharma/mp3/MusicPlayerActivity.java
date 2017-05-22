package com.example.abhinandansharma.mp3;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinandansharma.mp3.utility.RoundImage;
import com.triggertrap.seekarc.SeekArc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Abhinandan on 18/5/17.
 */

public class MusicPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,View.OnClickListener,SeekArc.OnSeekArcChangeListener{
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton ivSongImage;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private TextView songTitleLabel;
    private TextView songArtistlabel;
    private com.triggertrap.seekarc.SeekArc songProgressBar;

    /*private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;*/
    // Media Player
    private static  MediaPlayer mp;
    private int Songposition;
    MusicService musicService;
    boolean mBound = false;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);
        ((TextView) findViewById(R.id.main_toolbar_title)).setText(R.string.app_name);
        intent = getIntent();


        if(intent != null) {
            currentSongIndex = intent.getIntExtra("Song_number", 0);
        }


        // All player buttons
        btnPlay = (ImageButton) findViewById(R.id.ic_play);
       // btnForward = (ImageButton) findViewById(R.id.btnForward);
       // btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnNext = (ImageButton) findViewById(R.id.next_song);
        btnPrevious = (ImageButton) findViewById(R.id.previous_song);
        btnRepeat = (ImageButton) findViewById(R.id.ic_repeat_one);
        btnShuffle = (ImageButton) findViewById(R.id.ic_shuffle);
        songTitleLabel = (TextView) findViewById(R.id.song_name);
        songArtistlabel = (TextView) findViewById(R.id.song_artist);
        songProgressBar = (SeekArc) findViewById(R.id.seekArc);
        ivSongImage = (ImageButton) findViewById(R.id.song_image);
        songManager = new MainActivity();
        utils = new Utilities();

        songProgressBar.setOnSeekArcChangeListener(this);
        // Important

        // Getting all songs list
      songsList = MainActivity.songList;

        if (mp != null){
            try {
                mp.release();
                mp = null;
                Log.e("abhi", "mp not null " );
            } catch (Exception e) {

            }

            mp = new MediaPlayer();
            playSong(currentSongIndex);
        }
        else

        {
            Log.e("abhi", "mp null " );
            mp = new MediaPlayer();
            playSong(currentSongIndex);
        }
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

/*    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, 200, 200);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(50, 50, 50, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }*/

    public void  playSong(int songIndex) {


            mp.reset();
            Song playSong = songsList.get(songIndex);
            songTitle = playSong.getTitle();
            songArtist = playSong.getArtist();
            songImage = playSong.getImage();
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 1500.0f); // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)
        animation.setDuration(15000); // animation duration
        animation.setRepeatCount(4); // animation repeat count
       // animation.setRepeatMode(2); // repeat animation (left to right, right to left)
        animation.setFillAfter(true);
        //your_view for mine is imageView
            songArtistlabel.setText(songArtist);
            songTitleLabel.setText(songTitle);
            songTitleLabel .startAnimation(animation);
            songArtistlabel.startAnimation(animation);


        if (songImage !=null) {
            Bitmap bm = BitmapFactory.decodeFile(songImage);
            Bitmap resizedImage = Bitmap.createScaledBitmap(bm, 700, 700, true);
            roundedImage = new RoundImage(resizedImage);
            ivSongImage.setImageDrawable(roundedImage);
        }



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

            btnPlay.setImageResource(R.drawable.ic_pause_black_36dp);



        try {
            songProgressBar.setProgress(0);
            updateProgressBar();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
       // updateProgressBar();

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
