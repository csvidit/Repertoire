package com.viditkhandelwal.repertoire;

import static com.viditkhandelwal.repertoire.Utils.LOG_TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.cloud.spanner.DatabaseClient;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.viditkhandelwal.repertoire.database.CloudSpannerDB;
import com.viditkhandelwal.repertoire.database.DBHelper;
import com.viditkhandelwal.repertoire.database.Recipe;
import com.viditkhandelwal.repertoire.databinding.ActivityHomeBinding;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityHomeBinding binding;
    private List<Recipe> recipes;
    private CustomAdapter adapter;
    private FirebaseAuth mAuth;

    public static final int FROM_ADD_RECIPE_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d(LOG_TAG, "Anonymous FirebaseAuth Successful");
                    setRecipeList();
                    Log.d(LOG_TAG, "Set Recipe List called onCreate");
                }
                else
                {
                    Log.d(LOG_TAG, "Anonymous FirebaseAuth Unsuccessful");
                }
            }
        });
        binding.buttonAddRecipe.setOnClickListener(this);
        DatabaseClient dbClient = CloudSpannerDB.getInstance().getDbClient();

    }

    private void setRecipeList()
    {
        DBHelper helper = DBHelper.getInstance(this);
        helper.getAllRecipes(new FirebaseStorageCallback() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                Log.d(LOG_TAG, "Number of recipes: "+String.valueOf(recipes.size()));
                adapter = new CustomAdapter(recipes, HomeActivity.this);
                binding.listviewRecipes.setAdapter(adapter);
                Log.d(LOG_TAG, "Set Recipe List function call ended");
            }

            @Override
            public void onFailure() {
                Log.d(LOG_TAG, "Firebase Storage Retrieve Failed");
            }


        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.button_add_recipe:
                Intent toAddRecipe = new Intent(this, AddRecipeActivity.class);
                startActivityForResult(toAddRecipe, FROM_ADD_RECIPE_ACTIVITY);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FROM_ADD_RECIPE_ACTIVITY && resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(this, "Recipe successfully added", Toast.LENGTH_SHORT).show();
            setRecipeList();
            Log.d(LOG_TAG, "Set Recipe List called onActivityResult");
        }
    }
}