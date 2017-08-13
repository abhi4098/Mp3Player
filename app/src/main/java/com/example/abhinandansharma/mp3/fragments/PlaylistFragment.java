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
 * Created by Abhinandan on 7/6/17.
 */
public class PlaylistFragment extends Fragment {
    public static ArrayList<TypeModel> playlistList;
    String TAG = "abhi";
    private GridView playlistView;
    private String songImage="";
    RoundImage roundedImage;
    public static LinearLayout llAllSongs;
    int songIndex;

    public PlaylistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.type_list_fragment, container, false);
        playlistView = (GridView) rootView.findViewById(R.id.common_type_list);
        //  llAllSongs = (LinearLayout) rootView.findViewById(R.id.llAllSongs);
        playlistList = new ArrayList<>();
        getPlaylistList();
        TypeAdapter typeAdapter = new TypeAdapter(getActivity(), playlistList);
        playlistView.setAdapter(typeAdapter);
        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Fragment typeSongFrag= new TypeSongFragment();
                Bundle args = new Bundle();
                args.putString("typeId", String.valueOf(playlistView.getAdapter().getItemId(position)));
                args.putString("type","PlayLists");
                typeSongFrag.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, typeSongFrag).commit();
            }

        });

        return rootView;
    }

    private void getPlaylistList() {
        ContentResolver musicContentResolver = getActivity().getContentResolver();
        Uri playlistUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor playlistCursor = musicContentResolver.query(playlistUri, null, null, null, null);
        if (playlistCursor != null && playlistCursor.moveToFirst()) {
            //get columns
            int titleColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Playlists.NAME);
            int idColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Playlists._ID);
           /* int numOfTracksColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Genres._COUNT);*/

            do {
                long thisId = playlistCursor.getLong(idColumn);
                String thisTitle = playlistCursor.getString(titleColumn);
                // String thisNumOfTracks = playlistCursor.getString(numOfTracksColumn);
                Log.e(TAG, "getSongListGenre: " +thisTitle + "  " + thisId );
                playlistList.add(new TypeModel(thisId, thisTitle));
            }
            while (playlistCursor.moveToNext());
        }
        playlistCursor.close();
        Collections.sort(playlistList, new Comparator<TypeModel>() {
            public int compare(TypeModel a, TypeModel b) {
                return a.getName().compareTo(b.getName());
            }
        });
    }


}
