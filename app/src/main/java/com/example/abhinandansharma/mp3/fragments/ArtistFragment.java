package com.example.abhinandansharma.mp3.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhinandansharma.mp3.R;

/**
 * Created by Abhinandan on 10/12/15.
 */
public class ArtistFragment extends Fragment {

    public ArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        return rootView;
    }

}
