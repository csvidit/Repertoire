package com.viditkhandelwal.repertoire.database;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private int id;
    private String name;
    private int time;
    private int serves;
    private boolean isFavorite;
    private String ingredients;
    private String procedure;
//    private byte[] image;
    private Drawable image;

    public Recipe(int id, String name, int time, int serves, boolean isFavorite, String ingredients, String procedure) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.serves = serves;
        this.isFavorite = isFavorite;
        this.ingredients = ingredients;
        this.procedure = procedure;
    }

    public Recipe(String name, int time, int serves, boolean isFavorite, String ingredients, String procedure) {
        this.name = name;
        this.time = time;
        this.serves = serves;
        this.isFavorite = isFavorite;
        this.ingredients = ingredients;
        this.procedure = procedure;
    }

    public Recipe(String name, int time, int serves, boolean isFavorite, String ingredients, String procedure, Drawable image) {
        this.name = name;
        this.time = time;
        this.serves = serves;
        this.isFavorite = isFavorite;
        this.ingredients = ingredients;
        this.procedure = procedure;
        this.image = image;
    }

    public static List<String> parse(String s)
    {
        String[] lines = s.split(",");
        List<String> result = new ArrayList<String>();
        for(int i = 0; i<lines.length; i++)
        {
            String line = String.valueOf(i+1)+" "+lines[i];
            line = line.trim();
            result.add(line);
        }
        return result;
    }

    public static String parseTime(int time)
    {
        String result;
        if(time<=60)
        {
            return String.valueOf(time);
        }
        else
        {
            int h = time/60;
            int m = time%60;
            if(h>1)
            {
                result = String.valueOf(h)+" hours ";
            }
            else
            {
               result = String.valueOf(h)+" hour ";
            }
            if(m>0)
            {
                result += "and "+String.valueOf(m);
                if(m==1)
                {
                    result+=" minute";
                }
                else
                {
                    result+=" minutes";
                }
            }
        }
        return result.trim();
    }

    public static int parseTime(String time)
    {
        String[] tokens = time.split(" ");
        int result=0;
        result += Integer.valueOf(tokens[0])*60;
        if(tokens.length>2)
        {
            result += Integer.valueOf(tokens[3]);
        }
        return result;
    }

    public static boolean parseFavorite(int isFavorite)
    {
        if(isFavorite == 1)
        {
            return true;
        }
        return false;
    }

    public static int parseFavorite(boolean isFavorite)
    {
        if(isFavorite)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getServes() {
        return serves;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getProcedure() {
        return procedure;
    }

//    public byte[] getImage() {
//        return image;
//    }


    public Drawable getImage() {
        return image;
    }
}
