package com.example.abhinandansharma.mp3.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.abhinandansharma.mp3.MainActivity;
import com.example.abhinandansharma.mp3.R;
import com.example.abhinandansharma.mp3.adapters.SongAdapter;
import com.example.abhinandansharma.mp3.model.Song;
import com.example.abhinandansharma.mp3.utility.RoundImage;

import java.util.ArrayList;

/**
 * Created by Abhinandan on 10/12/15.
 */
public class AllSongsFragment extends Fragment {

    public static ArrayList<Song> songsList;
    private ListView songView;
    private String songImage="";
    RoundImage roundedImage;
    public static LinearLayout llAllSongs;
    int songIndex;

    public AllSongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_allsongs, container, false);
        songView = (ListView) rootView.findViewById(R.id.song_list);
        llAllSongs = (LinearLayout) rootView.findViewById(R.id.llAllSongs);
        songsList = MainActivity.songList;
        SongAdapter songAdt = new SongAdapter(getActivity(), songsList);
        songView.setAdapter(songAdt);
          songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                songIndex = position;
                Log.e("abhi", "onItemClick: " +songIndex );
                MainActivity mainactivity = (MainActivity) getActivity();
                mainactivity.setMp3Player(songIndex);
                Song playSong = songsList.get(songIndex);
                songImage = playSong.getImage();
                if (songImage !=null) {
                    Bitmap bm = BitmapFactory.decodeFile(songImage);
                    llAllSongs.setBackground(Drawable.createFromPath(songImage));
                }
                /*Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
                intent.putExtra("Song_number", songIndex);
                //setResult(100, intent);
                startActivity(intent);
                //finish();*/
            }

        });

        return rootView;
    }

}
