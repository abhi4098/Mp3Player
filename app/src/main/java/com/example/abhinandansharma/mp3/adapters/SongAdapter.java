package com.example.abhinandansharma.mp3.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abhinandansharma.mp3.R;
import com.example.abhinandansharma.mp3.model.Song;
import com.example.abhinandansharma.mp3.utility.RoundImage;

/**
 * Created by abhinandan.sharma on 9/30/2015.
 */
public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    private String songImage="";
    RoundImage roundedImage;

    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }
    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        Log.d("abhi", "getView");

        LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.song, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        ImageView songImageBtn = (ImageView) songLay.findViewById(R.id.song_image_small);

        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
       /* songImage =currSong.getImage();
        if (songImage !=null) {
            Bitmap bm = BitmapFactory.decodeFile(songImage);
            Bitmap resizedImage = Bitmap.createScaledBitmap(bm, 165, 165, true);
            roundedImage = new RoundImage(resizedImage);
            songImageBtn.setImageDrawable(roundedImage);

        }*/
       /* else
        {
            Bitmap bm = BitmapFactory.decodeFile(R.drawable.end);
            Bitmap resizedImage = Bitmap.createScaledBitmap(bm, 165, 165, true);
            roundedImage = new RoundImage(resizedImage);
            songImageBtn.setImageDrawable(roundedImage);

        }*/
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
