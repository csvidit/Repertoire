package com.viditkhandelwal.repertoire;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.viditkhandelwal.repertoire.database.DBHelper;
import com.viditkhandelwal.repertoire.database.Recipe;
import com.viditkhandelwal.repertoire.databinding.ActivityAddRecipeBinding;

public class AddRecipeActivity extends AppCompatActivity {

    private ActivityAddRecipeBinding binding;
    private EditText currentIngredient;
    private EditText currentProcedure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentIngredient = binding.edittextIngredients;
        currentProcedure = binding.edittextProcedure;
        binding.buttonSubmitRecipe.setOnClickListener(button_submit_recipe_clickListener);
        binding.buttonAddIngredient.setOnClickListener(button_add_ingredient_clickListener);
    }

    private View.OnClickListener button_add_ingredient_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup parent = binding.getRoot();
            ConstraintLayout layout = binding.constraintlayoutAddRecipe;
            EditText newIngredientEditText = generateIngredientView();
            layout.addView(newIngredientEditText);
            setContentView(binding.getRoot());
        }
    };

    private EditText generateIngredientView()
    {
        EditText editText = new EditText(this);
        editText.setTextColor(getResources().getColor(R.color.white));
        ConstraintSet newConstraints = new ConstraintSet();
        newConstraints.clone(binding.constraintlayoutAddRecipe);
        newConstraints.connect(editText.getId(), ConstraintSet.TOP, currentIngredient.getId(),
                ConstraintSet.BOTTOM, 20);
        newConstraints.connect(editText.getId(), ConstraintSet.START, binding.guideline12.getId(),
                ConstraintSet.START);
        newConstraints.applyTo(binding.constraintlayoutAddRecipe);
        currentIngredient = editText;
        newConstraints.connect(binding.buttonAddIngredient.getId(), ConstraintSet.TOP,
                currentIngredient.getId(), ConstraintSet.BOTTOM, 20);
        newConstraints.connect(binding.buttonAddIngredient.getId(), ConstraintSet.START,
                currentIngredient.getId(), ConstraintSet.START);
        newConstraints.connect(binding.buttonAddIngredient.getId(), ConstraintSet.END,
                currentIngredient.getId(), ConstraintSet.END);
        newConstraints.applyTo(binding.constraintlayoutAddRecipe);
        return editText;
    }

    public EditText getCurrentIngredient() {
        return currentIngredient;
    }

    public void setCurrentIngredient(EditText currentIngredient) {
        this.currentIngredient = currentIngredient;
    }

    private View.OnClickListener button_submit_recipe_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DBHelper helper = DBHelper.getInstance(AddRecipeActivity.this);
            String name = binding.edittextRecipeName.getText().toString();
            int timeTaken = Integer.valueOf(binding.edittextTimeTaken.getText().toString());
            int serves = Integer.valueOf(binding.edittextServes.getText().toString());
            String ingredients = binding.edittextIngredients.getText().toString();
            String procedure = binding.edittextProcedure.getText().toString();
            boolean isFavorite = binding.checkboxIsFavorite.isChecked();
            helper.addRecipe(new Recipe(name, timeTaken, serves, isFavorite, ingredients, procedure));
            Toast.makeText(AddRecipeActivity.this, "Recipe successfully added", Toast.LENGTH_SHORT).show();
            finish();
        }
    };
}