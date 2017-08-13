package com.example.abhinandansharma.mp3;




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
import com.example.abhinandansharma.mp3.fragments.AlbumFragment;
import com.example.abhinandansharma.mp3.fragments.GenresFragment;
import com.example.abhinandansharma.mp3.fragments.PlaylistFragment;
import com.example.abhinandansharma.mp3.model.DrawerModel;
import com.example.abhinandansharma.mp3.model.Song;
import com.example.abhinandansharma.mp3.model.TypeModel;
import com.example.abhinandansharma.mp3.utility.RoundImage;

import static com.example.abhinandansharma.mp3.fragments.AllSongsFragment.songsList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener ,SeekBar.OnSeekBarChangeListener {
    public static ArrayList<Song> songList;
    public static ArrayList<TypeModel> albumList ;
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
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    public  static MediaPlayer mp;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    public static LinearLayout llMediaControls;
    int playingSongIndex;
    public static ImageButton imPlaySong;
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
        albumList = new ArrayList<>();
        getAlbumList();


        setUpToolbar();
        setUpDrawer();
        if (getFragmentManager().findFragmentById(R.id.fragment_frame) == null) {
            selectDrawerItem(0);
        }


        songList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            }
        } else
            getSongList();
     //   getSongListGenre();


    }

    private void getAlbumList() {


        ContentResolver musicContentResolver = getContentResolver();
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = musicContentResolver.query(albumUri, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) {
            //get columns
            int albumColumn = albumCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM_ART);
            int idColumn = albumCursor.getColumnIndex
                    (MediaStore.Audio.Albums._ID);
            int numOfTracksColumn = albumCursor.getColumnIndex
                    (MediaStore.Audio.Albums.NUMBER_OF_SONGS);

            do {
                long id = albumCursor.getLong(idColumn);
                String title = albumCursor.getString(albumColumn);
                String numOfTracks = albumCursor.getString(numOfTracksColumn);
                Log.e(TAG, "getSongListGenre: " +title + "  " + id  + "  " + numOfTracks );
              albumList.add(new TypeModel(id, title,numOfTracks));
            }
            while (albumCursor.moveToNext());
        }
        albumCursor.close();
        /*Collections.sort(albumList, new Comparator<TypeModel>() {
            public int compare(TypeModel a, TypeModel b) {
                return a.getName().compareTo(b.getName());
            }
        });*/
    }


    private void setUpDrawer() {
        DrawerModel[] drawerItem = new DrawerModel[5];

        drawerItem[0] = new DrawerModel(R.drawable.ic_play_arrow_black_24dp, "All Songs");
        drawerItem[1] = new DrawerModel(R.drawable.ic_queue_music_black_24dp, "Artist");
        drawerItem[2] = new DrawerModel(R.drawable.ic_favorite_border_black_24dp, "Album");
        drawerItem[3] = new DrawerModel(R.drawable.ic_favorite_border_black_24dp, "Genres");
        drawerItem[4] = new DrawerModel(R.drawable.ic_favorite_border_black_24dp, "Playlist");

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






    public void setMp3Player(int songIndex)
    {
        playingSongIndex = songIndex;

        if (mp != null){
            try {
                mp.release();
                mp = null;

            } catch (Exception e) {

            }

            mp = new MediaPlayer();
            playSong(songIndex);
        }
        else

        {
            mp = new MediaPlayer();
            playSong(songIndex);
        }

    }




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void playSong(int songIndex) {

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
        else
        {
            ivSongImage.setImageDrawable(getDrawable(R.drawable.end));
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

    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable() {

        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            sbMusic.setProgress(progress);

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
        Cursor musicCursor = musicContentResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst() ) {

            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int   songImageIdColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM_ID);

             while (musicCursor.moveToNext())
             {
                 long id = musicCursor.getLong(idColumn);
                 String title = musicCursor.getString(titleColumn);
                 String artist = musicCursor.getString(artistColumn);
                 int songImageId =musicCursor.getInt(songImageIdColumn);
                 for (int i=0; i<albumList.size(); i++)
                 {
                      long albumId = albumList.get(i).getID();
                     if (songImageId == albumId)
                     {
                         String songImage = albumList.get(i).getName();
                         Log.e(TAG, "getSongList: " + id + " " + songImageId);
                         songList.add(new Song(id, title, artist, songImage));
                     }
                 }





             }

        }
        musicCursor.close();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });


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
        switch (v.getId()) {

            case R.id.ic_activity_play:
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        imPlaySong.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                    }
                } else {
                    if (mp != null) {
                        mp.start();
                        imPlaySong.setImageResource(R.drawable.ic_pause_black_36dp);
                    }
                }
                break;

            case R.id.ll_media_controls:
                 seekbarProgress = sbMusic.getProgress();
                 Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
                 intent.putExtra("Song_number", playingSongIndex);
                 intent.putExtra("media_player_state", "true");
                 intent.putExtra("seekbar_progress", seekbarProgress);
                 //setResult(100, intent);
                startActivityForResult(intent, 1);
                break;


        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){

                if (mp.isPlaying()) {
                    imPlaySong.setImageResource(R.drawable.ic_pause_black_36dp);
                } else {
                    imPlaySong.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                }


                int currentSongIndex = data.getIntExtra("currentSongIndex",0);
                playingSongIndex =currentSongIndex;
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
          /*  if (resultCode == RESULT_CANCELED) {
                //Do nothing?
            }*/
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
        mp.seekTo(currentPosition);
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
                fragment = new AlbumFragment();
                break;
            case 3:
                fragment = new GenresFragment();
                break;
            case 4:
                fragment = new PlaylistFragment();
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
