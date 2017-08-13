package com.example.abhinandansharma.mp3.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.abhinandansharma.mp3.R;
import com.example.abhinandansharma.mp3.adapters.TypeAdapter;
import com.example.abhinandansharma.mp3.model.TypeModel;
import com.example.abhinandansharma.mp3.utility.RoundImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Abhinandan on 10/12/15.
 */
public class AlbumFragment extends Fragment {

    public static ArrayList<TypeModel> albumList;
    String TAG = "abhi";
    private GridView albumView;
    public static LinearLayout llAllSongs;
    int songIndex;

    public AlbumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.type_list_fragment, container, false);
        albumView = (GridView) rootView.findViewById(R.id.common_type_list);
        //  llAllSongs = (LinearLayout) rootView.findViewById(R.id.llAllSongs);
        albumList = new ArrayList<>();
        getAlbumList();
        TypeAdapter typeAdapter = new TypeAdapter(getActivity(), albumList);
        albumView.setAdapter(typeAdapter);
        albumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Fragment typeSongFrag= new TypeSongFragment();
                Bundle args = new Bundle();
                args.putString("typeId", String.valueOf(albumView.getAdapter().getItemId(position)));
                args.putString("type","Albums");
                typeSongFrag.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, typeSongFrag).commit();
            }

        });

        return rootView;
    }

    private void getAlbumList() {
        ContentResolver musicContentResolver = getActivity().getContentResolver();
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = musicContentResolver.query(albumUri, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) {
            //get columns
            int albumColumn = albumCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM);
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
        Collections.sort(albumList, new Comparator<TypeModel>() {
            public int compare(TypeModel a, TypeModel b) {
                return a.getName().compareTo(b.getName());
            }
        });
    }


}
