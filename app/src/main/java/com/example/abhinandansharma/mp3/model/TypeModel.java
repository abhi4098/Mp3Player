package com.example.abhinandansharma.mp3.model;

/**
 * Created by Abhinandan on 6/6/17.
 */

public class TypeModel {
    private long id;
    private String name;
    private String numOfTracks;


    public TypeModel(long id, String name) {

        this.id = id;
        this.name = name;

    }
    public TypeModel(long id, String name, String numOfTracks) {

        this.id = id;
        this.name = name;
        this.numOfTracks = numOfTracks;

    }
    public long getID()
    {
        return id;
    }

    public String getNumberOfTracks()
    {
        return numOfTracks;
    }



    public String getName()
    {
        return name;
    }

}
