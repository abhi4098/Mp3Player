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
public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        return rootView;
    }

}
