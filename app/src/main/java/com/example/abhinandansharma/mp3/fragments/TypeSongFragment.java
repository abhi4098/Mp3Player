package com.example.abhinandansharma.mp3.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.abhinandansharma.mp3.MainActivity;
import com.example.abhinandansharma.mp3.R;
import com.example.abhinandansharma.mp3.adapters.SongAdapter;
import com.example.abhinandansharma.mp3.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Abhinandan on 6/6/17.
 */
public class TypeSongFragment extends Fragment {
    public static ArrayList<Song> typeSongList;
    public static ArrayList<Song> songsList;
    String TAG = "abhi";
    private ListView typeSongView;
    long typeId;
    String type;
    int songIndex;


    public TypeSongFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.song_list_fragment, container, false);
        typeSongView = (ListView) rootView.findViewById(R.id.common_song_list);
        typeId = Long.parseLong(getArguments().getString("typeId"));
        type = getArguments().getString("type");
        Log.e(TAG, "onCreateView: " +typeId );
        //llAllSongs = (LinearLayout) rootView.findViewById(R.id.llAllSongs);
        typeSongList = new ArrayList<>();


        if (type.equals("Genres"))
            getGenreSongList();
        else if (type.equals("Albums"))
            getAlbumSongList();
        else if (type.equals("Artists"))
            getArtistSongList();
        else
        getPlaylistSongList();

       SongAdapter songAdt = new SongAdapter(getActivity(), typeSongList);
        typeSongView.setAdapter(songAdt);
       typeSongView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                songIndex = (int) typeSongView.getAdapter().getItemId(position);
                Log.e("abhi", "onItemClick:playlist " +songIndex );
                MainActivity mainactivity = (MainActivity) getActivity();
                songsList =MainActivity.songList;


                for (int i=0; i<songsList.size(); i++)
                {
                    long posInSongList = songsList.get(i).getID();

                    if (songIndex ==posInSongList )
                    {
                        songIndex = i;
                    }
                }
                mainactivity.setMp3Player(songIndex);
               // Song playSong = typeSongList.get(songIndex);
               // songImage = playSong.getImage();
               /* if (songImage !=null) {
                    Bitmap bm = BitmapFactory.decodeFile(songImage);
                    llAllSongs.setBackground(Drawable.createFromPath(songImage));
                    llMediaControls.setOnClickListener((View.OnClickListener) getActivity());
                    imPlaySong.setOnClickListener((View.OnClickListener) getActivity());
                }*/

            }

        });


        return rootView;
    }

    private void getPlaylistSongList() {

        ContentResolver musicContentResolver = getActivity().getContentResolver();
        Uri PlaylistSongUri = MediaStore.Audio.Playlists.Members.getContentUri("external",typeId);
        Cursor playlistCursor = musicContentResolver.query(PlaylistSongUri, null, null, null, null);
        if (playlistCursor != null && playlistCursor.moveToFirst()) {
            int idColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Playlists.Members.AUDIO_ID);
            int titleColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int artistColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int artistIdColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE_KEY);

            do {
                long id = playlistCursor.getLong(idColumn);
                String title = playlistCursor.getString(titleColumn);
                String artist = playlistCursor.getString(artistColumn);
                String artistId = playlistCursor.getString(artistIdColumn);
                    Log.e(TAG, "getSongListGenre:-------------- " + id + "  " + artist + "  " +title);
                    typeSongList.add(new Song(id, title, artist, artistId));

            }
            while (playlistCursor.moveToNext());
        }
        playlistCursor.close();
        Collections.sort(typeSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });


    }

    private void getArtistSongList() {

        ContentResolver musicContentResolver = getActivity().getContentResolver();
        Uri artistSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor artistCursor = musicContentResolver.query(artistSongUri, null, null, null, null);
        if (artistCursor != null && artistCursor.moveToFirst()) {
            int idColumn = artistCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int titleColumn = artistCursor.getColumnIndex
                    (MediaStore.Audio.Media.DISPLAY_NAME);
            int artistColumn = artistCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int artistIdColumn = artistCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST_ID);

            do {
                long id = artistCursor.getLong(idColumn);
                String title = artistCursor.getString(titleColumn);
                String artist = artistCursor.getString(artistColumn);
                String artistId = artistCursor.getString(artistIdColumn);
                if (String.valueOf(typeId).equals(artistId)) {
                    Log.e(TAG, "getSongListGenre: playlist " + title + "  " + artist + "  " + id);
                    typeSongList.add(new Song(id, title, artist, artistId));
                }
            }
            while (artistCursor.moveToNext());
        }
        artistCursor.close();
        Collections.sort(typeSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }




    private void getAlbumSongList() {
        ContentResolver musicContentResolver = getActivity().getContentResolver();
        Uri albumSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = musicContentResolver.query(albumSongUri, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) {
            int idColumn = albumCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int titleColumn = albumCursor.getColumnIndex
                    (MediaStore.Audio.Media.DISPLAY_NAME);
            int artistColumn = albumCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumIdColumn = albumCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);

            do {
                long id = albumCursor.getLong(idColumn);
                String title = albumCursor.getString(titleColumn);
                String artist = albumCursor.getString(artistColumn);
                String albumId = albumCursor.getString(albumIdColumn);
                if (String.valueOf(typeId).equals(albumId)) {
                    Log.e(TAG, "getSongListGenre: " + title + "  " + artist + "  " + albumId);
                    typeSongList.add(new Song(id, title, artist, albumId));
                }
            }
            while (albumCursor.moveToNext());
        }
        albumCursor.close();
        Collections.sort(typeSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }




    private void getGenreSongList() {

        ContentResolver musicContentResolver = getActivity().getContentResolver();
        Uri genreSongUri = MediaStore.Audio.Genres.Members.getContentUri("external",typeId);
        Cursor genreCursor = musicContentResolver.query(genreSongUri, null, null, null, null);
        if (genreCursor != null && genreCursor.moveToFirst()) {
            int idColumn = genreCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int titleColumn = genreCursor.getColumnIndex
                    (MediaStore.Audio.Media.DISPLAY_NAME);
            int artistColumn = genreCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int imageColumn = genreCursor.getColumnIndex
                    (MediaStore.Audio.Media.TRACK);

            do {
                long id = genreCursor.getLong(idColumn);
                String title = genreCursor.getString(titleColumn);
                String artist = genreCursor.getString(artistColumn);
                String image = genreCursor.getString(imageColumn);
                Log.e(TAG, "getSongListGenre: " +id + "  " + artist + "  " + image );
                typeSongList.add(new Song(id, title,artist,image));
            }
            while (genreCursor.moveToNext());
        }
        genreCursor.close();
        Collections.sort(typeSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }
}
