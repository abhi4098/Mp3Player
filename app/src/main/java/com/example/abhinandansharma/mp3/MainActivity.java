package com.example.abhinandansharma.mp3;

/*
1. Bound service
2. Onstartcommand for mp3 in case the app is closed the song continue to play in background
3.
 */


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.abhinandansharma.mp3.adapters.DrawerItemCustomAdapter;
import com.example.abhinandansharma.mp3.fragments.AllSongsFragment;
import com.example.abhinandansharma.mp3.fragments.ArtistFragment;
import com.example.abhinandansharma.mp3.fragments.FavoritesFragment;
import com.example.abhinandansharma.mp3.model.DrawerModel;
import com.example.abhinandansharma.mp3.model.Song;
import com.example.abhinandansharma.mp3.utility.RoundImage;

import static com.example.abhinandansharma.mp3.R.id.llAllSongs;
import static com.example.abhinandansharma.mp3.fragments.AllSongsFragment.songsList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener ,SeekBar.OnSeekBarChangeListener {
    public static ArrayList<Song> songList;
    String TAG = "abhi";
    SeekBar sbMusic;
    int seekbarProgress;
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private Utilities utils;
    RoundImage roundedImage;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    public  static MediaPlayer mp;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    LinearLayout llMediaControls;
    int playingSongIndex;
    ImageButton imPlaySong;
    ImageView ivSongImage;
    TextView tvSongName;
    TextView tvSongArtist;
    private String songTitle="";
    private String songArtist="";
    private String songImage="";
    private Handler mHandler = new Handler();


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        llMediaControls = (LinearLayout) findViewById(R.id.ll_media_controls);
        imPlaySong = (ImageButton) findViewById(R.id.ic_activity_play);
        ivSongImage = (ImageView) findViewById(R.id.song_image_small);
        tvSongName = (TextView) findViewById(R.id.activity_song_name);
        tvSongArtist = (TextView) findViewById(R.id.activity_song_artist);
        sbMusic=(SeekBar)findViewById(R.id.seekBar1);
        utils = new Utilities();
        sbMusic.setOnSeekBarChangeListener(this);
        llMediaControls.setOnClickListener(this);
        setUpToolbar();
        setUpDrawer();
        if (getFragmentManager().findFragmentById(R.id.fragment_frame) == null) {
            selectDrawerItem(0);
        }
       /* songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songIndex = position;
                Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                intent.putExtra("Song_number", songIndex);
                setResult(100, intent);
                startActivity(intent);
                //finish();
            }

        });*/
        songList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //Log.e("abhi", "onCreate: not granted ");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                return;
            }
        } else
            getSongList();


    }


    private void setUpDrawer() {
        DrawerModel[] drawerItem = new DrawerModel[3];

        drawerItem[0] = new DrawerModel(R.drawable.ic_play_arrow_black_24dp, "All Songs");
        drawerItem[1] = new DrawerModel(R.drawable.ic_queue_music_black_24dp, "Artist");
        drawerItem[2] = new DrawerModel(R.drawable.ic_favorite_border_black_24dp, "Favorites");
        //drawerItem[3] = new DrawerModel(R.drawable.table, "Find My Phone");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter drawerAdapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();
    }


    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    void setupDrawerToggle() {
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }




    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle("Dr Mobo Smart Security");
    }



    public void setMp3Player(int songIndex)
    {
        playingSongIndex = songIndex;

        if (mp != null){
            try {
                mp.release();
                mp = null;
                Log.e("abhi", "mp not null " );

            } catch (Exception e) {

            }

            mp = new MediaPlayer();
            playSong(songIndex);
        }
        else

        {
            Log.e("abhi", "mp null " );
            mp = new MediaPlayer();
            playSong(songIndex);
        }

    }




    public void playSong(int songIndex) {

        Log.e(TAG, "onClick: song index from fragment------------- " + songIndex);
       /* MusicPlayerActivity musicPlayerActivity = new MusicPlayerActivity();
        musicPlayerActivity.playSong(songIndex);*/
        mp.reset();
        Song playSong = songsList.get(songIndex);
        songTitle = playSong.getTitle();
        songArtist = playSong.getArtist();
        songImage = playSong.getImage();
        tvSongName.setText(songTitle);
        tvSongArtist.setText(songArtist);



        if (songImage !=null) {
            Bitmap bm = BitmapFactory.decodeFile(songImage);
            Bitmap resizedImage = Bitmap.createScaledBitmap(bm, 710, 710, true);
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

        imPlaySong.setImageResource(R.drawable.ic_pause_black_36dp);



        try {
            sbMusic.setProgress(0);
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
            sbMusic.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    public void getSongList() {

        ContentResolver musicContentResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri imageUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicContentResolver.query(musicUri, null, null, null, null);
        Cursor imageCursor = musicContentResolver.query(imageUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst() && imageCursor != null && imageCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int songImage = imageCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM_ART);

            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisImage = imageCursor.getString(songImage);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisImage));
            }
            while (musicCursor.moveToNext() && imageCursor.moveToNext());
        }
        musicCursor.close();
        imageCursor.close();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

       /* SongAdapter songAdt = new SongAdapter(this, songList);
        Log.e("abhi", "onRequestPermissionsResult:songlist " + songList);
        songView.setAdapter(songAdt);*/

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abhi", "onRequestPermissionsResult: ");
                getSongList();

            } else {

            }
        }
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClick(View v) {
        seekbarProgress = sbMusic.getProgress();
        Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
        intent.putExtra("Song_number", playingSongIndex);
        intent.putExtra("media_player_state", "true");
        intent.putExtra("seekbar_progress" , seekbarProgress);
        //setResult(100, intent);
        startActivityForResult(intent,1);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                int currentSongIndex = data.getIntExtra("currentSongIndex",0);
                playingSongIndex =currentSongIndex;
                Log.e("abhi", "onActivityResult: "+currentSongIndex );
                Song playSong = songsList.get(currentSongIndex);
                songTitle = playSong.getTitle();
                songArtist = playSong.getArtist();
                songImage = playSong.getImage();
                tvSongName.setText(songTitle);
                tvSongArtist.setText(songArtist);
                if (songImage !=null) {
                    Bitmap bm = BitmapFactory.decodeFile(songImage);
                    Bitmap resizedImage = Bitmap.createScaledBitmap(bm, 710, 710, true);
                    roundedImage = new RoundImage(resizedImage);
                    ivSongImage.setImageDrawable(roundedImage);
                    AllSongsFragment.llAllSongs.setBackground(Drawable.createFromPath(songImage));
                }

            }
            if (resultCode == RESULT_CANCELED) {
                //Do nothing?
            }
        }
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }



    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectDrawerItem(position);
        }

    }


    private void selectDrawerItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new AllSongsFragment();
                break;
            case 1:
                fragment = new ArtistFragment();
                break;
            case 2:
                fragment = new FavoritesFragment();
                break;

            default:
                fragment = new AllSongsFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }

    }
}
