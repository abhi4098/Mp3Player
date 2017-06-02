package com.example.abhinandansharma.mp3.model;

/**
 * Created by abhinandan.sharma on 9/30/2015.
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private String image;

    public Song(long songID, String songTitle, String songArtist, String songImage) {

        id = songID;
        title = songTitle;
        artist = songArtist;
        image = songImage;

    }
        public long getID()
        {
            return id;
        }



        public String getTitle()
        {
              return title;
        }

        public String getImage()
        {
             return image;
       }

        public String getArtist()
        {
            return artist;
        }


    }

