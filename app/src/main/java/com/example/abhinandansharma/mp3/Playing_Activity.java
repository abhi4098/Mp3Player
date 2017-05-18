package com.example.abhinandansharma.mp3;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.MediaController.MediaPlayerControl;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.media.MediaPlayer;


/**
 * Created by abhinandan.sharma on 10/6/2015.
 */
public class Playing_Activity extends Activity implements MediaPlayerControl {
    public MusicController controller;
    private boolean paused=false, playbackPaused=false;
    private int Songposition;
    MusicService musicService;
    boolean mBound = false;
    private Intent intent;
    public MediaPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("abhi", "onCreate_playing_activity");
        setContentView(R.layout.playing_activity);
        intent = getIntent();
        if(intent != null) {
            Songposition = intent.getIntExtra("Song_number", 0);
        }


    }
    @Override
    protected void onStart() {
        Log.d("abhi", "onStart_playing_activity");

        super.onStart();
        Intent StartSongService = new Intent(this, MusicService.class);
        bindService(StartSongService, mConnection, Context.BIND_AUTO_CREATE);

       // startService(StartSongService);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("abhi", "onResume_playing_activity");



        if(paused) {

            paused = false;
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("abhi", "onStop_playing_activity");
        controller.hide();
    }

    private MusicService.MusicBinder musicBinder;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("abhi","onServiceConnected");
            musicBinder = (MusicService.MusicBinder) service;
            musicService = musicBinder.getService();
            musicService.setList(MainActivity.songList);
            musicService.setSong(Songposition);
            setController();
            musicService.playSong();



            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("abhi","onServiceDisconnected");
            mBound = false;
        }
    };

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
        Log.d("abhi", "onPause_playing_activity");




    }

    @Override
    protected void onDestroy() {
        Log.d("abhi", "onDestroy_playing_activity");


        if(player != null) {
            player.release();
            // Set the MediaPlayer to null to avoid IlLegalStateException
            // when call mp.reset() after launching the app again
            player = null;
        }
       // stopService(intent);



        try {
        if (mBound) {
            unbindService(mConnection);
        }
        } catch (Exception e) {

        }
        super.onDestroy();
    }




    private void playNext(){
        musicService.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicService.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }
    private void setController(){
        controller = new MusicController(Playing_Activity.this);
        Log.e("abhi", "setController");
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(Playing_Activity.this);
        controller.setAnchorView(findViewById(R.id.playing_activity));
        controller.setEnabled(true);
        controller.show();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("abhi","onOptionsItemSelected_playing_activity");

        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicService.setShuffle();
                break;
            case R.id.action_end:
                stopService(intent);
                musicService=null;
                System.exit(0);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("abhi","onCreateOptionsMenu_playing_activity");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void start() {
        musicService.go();

    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicService !=null && mBound && musicService.isPng())
            return musicService.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService!=null && mBound && musicService.isPng())
            return musicService.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicService!=null && mBound)
            return musicService.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }






}
