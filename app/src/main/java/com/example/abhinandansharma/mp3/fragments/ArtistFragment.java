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
public class ArtistFragment extends Fragment {



    public static ArrayList<TypeModel> artistList;
    String TAG = "abhi";
    private GridView artistView;
    private String songImage="";
    RoundImage roundedImage;
    public static LinearLayout llAllSongs;
    int songIndex;

    public ArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.type_list_fragment, container, false);
        artistView = (GridView) rootView.findViewById(R.id.common_type_list);
        //  llAllSongs = (LinearLayout) rootView.findViewById(R.id.llAllSongs);
        artistList = new ArrayList<>();
        getArtistList();
        TypeAdapter typeAdapter = new TypeAdapter(getActivity(), artistList);
        artistView.setAdapter(typeAdapter);
        artistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Fragment typeSongFrag= new TypeSongFragment();
                Bundle args = new Bundle();
                args.putString("typeId", String.valueOf(artistView.getAdapter().getItemId(position)));
                args.putString("type","Artists");
                typeSongFrag.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, typeSongFrag).commit();
            }

        });

        return rootView;
    }

    private void getArtistList() {
        ContentResolver musicContentResolver = getActivity().getContentResolver();
        Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor artistCursor = musicContentResolver.query(artistUri, null, null, null, null);
        if (artistCursor != null && artistCursor.moveToFirst()) {
            //get columns
            int titleColumn = artistCursor.getColumnIndex
                    (MediaStore.Audio.Artists.ARTIST);
            int idColumn = artistCursor.getColumnIndex
                    (MediaStore.Audio.Artists._ID);
           /* int numOfTracksColumn = artistCursor.getColumnIndex
                    (MediaStore.Audio.Genres._COUNT);*/

            do {
                long thisId = artistCursor.getLong(idColumn);
                String thisTitle = artistCursor.getString(titleColumn);
                // String thisNumOfTracks = artistCursor.getString(numOfTracksColumn);
                Log.e(TAG, "getSongListGenre: " +thisTitle + "  " + thisId );
                artistList.add(new TypeModel(thisId, thisTitle));
            }
            while (artistCursor.moveToNext());
        }
        artistCursor.close();
        Collections.sort(artistList, new Comparator<TypeModel>() {
            public int compare(TypeModel a, TypeModel b) {
                return a.getName().compareTo(b.getName());
            }
        });
    }



}
