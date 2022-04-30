package com.viditkhandelwal.repertoire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        DBHelper helper = DBHelper.getInstance(this);
//        recipes = helper.getAllRecipes();


//        CustomAdapter adapter = new CustomAdapter(recipes, this);
//        binding.listviewRecipes.setAdapter(adapter);
        binding.buttonAddRecipe.setOnClickListener(this);

    }

    private void setRecipeList()
    {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.button_add_recipe:
                Intent toAddRecipe = new Intent(this, AddRecipeActivity.class);
                startActivity(toAddRecipe);
                break;
        }

    }
}