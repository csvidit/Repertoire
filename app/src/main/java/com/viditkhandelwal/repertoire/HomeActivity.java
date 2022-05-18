package com.viditkhandelwal.repertoire;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.viditkhandelwal.repertoire.database.DBHelper;
import com.viditkhandelwal.repertoire.database.Recipe;
import com.viditkhandelwal.repertoire.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityHomeBinding binding;
    private List<Recipe> recipes;
    CustomAdapter adapter;

    public static final int FROM_ADD_RECIPE_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRecipeList();
        binding.buttonAddRecipe.setOnClickListener(this);
    }

    private void setRecipeList()
    {
        DBHelper helper = DBHelper.getInstance(this);
        recipes = helper.getAllRecipes();
        adapter = new CustomAdapter(recipes, this);
        binding.listviewRecipes.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.button_add_recipe:
                Intent toAddRecipe = new Intent(this, AddRecipeActivity.class);
                startActivityForResult(toAddRecipe, FROM_ADD_RECIPE_ACTIVITY);
                setRecipeList();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FROM_ADD_RECIPE_ACTIVITY && resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(this, "Recipe successfully added", Toast.LENGTH_SHORT).show();
        }
    }
}