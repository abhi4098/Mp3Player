package com.example.abhinandansharma.mp3;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.PowerManager;

import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;

import com.example.abhinandansharma.mp3.model.Song;

/**
 * Created by abhinandan.sharma on 9/30/2015.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener  {
    private final IBinder mBinder = new MusicBinder();
    public MusicController controller;

    //media player
    public MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private String songTitle="";
    private static final int NOTIFY_ID=1;
    private boolean shuffle=false;
    private Random rand;

    @Override
    public void onCreate() {
        Log.d("abhi","onCreate_service");

        super.onCreate();
        songPosn=0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        Log.d("abhi","initMusicPlayer");
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        Log.d("abhi","setList");

        songs = theSongs;
    }

    public void playSong(){
        Log.d("abhi","playSong");

        //play a song
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();
       //get id
        long currSong = playSong.getID();
       //set uri
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("abhi","onCompletion");


        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("abhi","onError");

        mp.reset();
        return false;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("abhi", "onPrepared");


        mp.start();
        Intent notIntent = new Intent(this, Playing_Activity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            not = builder.build();
        }


        startForeground(NOTIFY_ID, not);
    }
    public void setSong(int songIndex){
        Log.d("abhi","setSong");

        songPosn=songIndex;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            Log.d("abhi", "getService");

            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("abhi", "onBind");

        return mBinder;
    }

    /** method for clients */
    @Override
    public boolean onUnbind(Intent intent){
        Log.d("abhi", "onUnbind");
        player.stop();
        player.release();
        return false;
    }
    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){

        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }
    public void playPrev(){
        songPosn--;
        if(songPosn < 0) songPosn=songs.size()-1;
        playSong();
    }
    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else {
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        playSong();
    }
    @Override
    public void onDestroy() {
        Log.d("abhi", "onDestroy_service");


        stopForeground(true);

    }
    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }


}
