package com.rollingdice.deft.android.tab.model;

/**
 * Created by sudarshan on 11/06/2016.
 */

public class Song {

    private int id;
    private Boolean play;
    private String genre;
    private String title;
    private String path;
    private Integer toggle;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getPlay() {
        return play;
    }

    public void setPlay(Boolean play) {
        this.play = play;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getToggle() {
        return toggle;
    }

    public void setToggle(Integer toggle) {
        this.toggle = toggle;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }



    /*public String getPath(){return path;}
    public String getTitle(){return title;}
    public Boolean getPlay(){return play;}
    public int getId(){return Id;}
    public String getGenre(){return genre;}
    public Boolean getToggle(){return toggle;}

    public void setPath(String path)
    {
        this.path = path;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setPlay(Boolean play)
    {
        this.play = play;
    }
    public void setId(int Id)
    {
        this.Id = Id;
    }
    public void setGenre(String genre)
    {
        this.genre = genre;
    }
    public void setToggle(Boolean toggle)
    {
        this.toggle = toggle;
    }*/


}
