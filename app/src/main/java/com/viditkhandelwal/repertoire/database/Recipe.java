package com.viditkhandelwal.repertoire.database;

import java.util.List;

public class Recipe {

    private int id;
    private String name;
    private String time;
    private int serves;
    private boolean isFavorite;
    private List<String> ingredients;
    private List<String> procedure;
    private byte[] image;

    public Recipe(int id, String name, String time, int serves, boolean isFavorite, List<String> ingredients, List<String> procedure, byte[] image) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.serves = serves;
        this.isFavorite = isFavorite;
        this.ingredients = ingredients;
        this.procedure = procedure;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getServes() {
        return serves;
    }

    public void setServes(int serves) {
        this.serves = serves;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getProcedure() {
        return procedure;
    }

    public void setProcedure(List<String> procedure) {
        this.procedure = procedure;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
