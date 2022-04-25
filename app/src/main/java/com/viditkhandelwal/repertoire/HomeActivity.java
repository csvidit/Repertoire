package com.viditkhandelwal.repertoire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.viditkhandelwal.repertoire.database.Recipe;
import com.viditkhandelwal.repertoire.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipes = new ArrayList<Recipe>();

        setRecipeList();

        CustomAdapter adapter = new CustomAdapter(recipes, this);
        binding.listviewRecipes.setAdapter(adapter);


        binding.

    }

    private void setRecipeList()
    {

    }
}