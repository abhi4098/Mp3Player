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


public class GenresFragment extends Fragment {
    public static ArrayList<TypeModel> genreList;
    String TAG = "abhi";
    private GridView genreView;
    private String songImage="";
    RoundImage roundedImage;
    public static LinearLayout llAllSongs;
    int songIndex;

    public GenresFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.type_list_fragment, container, false);
        genreView = (GridView) rootView.findViewById(R.id.common_type_list);
      //  llAllSongs = (LinearLayout) rootView.findViewById(R.id.llAllSongs);
        genreList = new ArrayList<>();
        getSongListGenre();
        TypeAdapter typeAdapter = new TypeAdapter(getActivity(), genreList);
        genreView.setAdapter(typeAdapter);
        genreView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Fragment typeSongFrag= new TypeSongFragment();
                Bundle args = new Bundle();
                args.putString("typeId", String.valueOf(genreView.getAdapter().getItemId(position)));
                args.putString("type","Genres");
                typeSongFrag.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, typeSongFrag).commit();
            }

        });

        return rootView;
    }

    private void getSongListGenre() {
        ContentResolver musicContentResolver = getActivity().getContentResolver();
        Uri genreUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        Cursor genreCursor = musicContentResolver.query(genreUri, null, null, null, null);
        if (genreCursor != null && genreCursor.moveToFirst()) {

            int titleColumn = genreCursor.getColumnIndex
                    (MediaStore.Audio.Genres.NAME);
            int idColumn = genreCursor.getColumnIndex
                    (MediaStore.Audio.Genres._ID);


            do {
                long thisId = genreCursor.getLong(idColumn);
                String thisTitle = genreCursor.getString(titleColumn);
                Log.e(TAG, "getSongListGenre: " +thisTitle + "  " + thisId );
                genreList.add(new TypeModel(thisId, thisTitle));
            }
            while (genreCursor.moveToNext());
        }
        genreCursor.close();
        Collections.sort(genreList, new Comparator<TypeModel>() {
            public int compare(TypeModel a, TypeModel b) {
                return a.getName().compareTo(b.getName());
            }
        });
    }



}
