package com.viditkhandelwal.repertoire;

import static com.viditkhandelwal.repertoire.Utils.LOG_TAG;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.viditkhandelwal.repertoire.database.DBHelper;
import com.viditkhandelwal.repertoire.database.Recipe;
import com.viditkhandelwal.repertoire.databinding.ActivityAddRecipeBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    private ActivityAddRecipeBinding binding;
    private EditText currentIngredient;
    private EditText currentProcedure;
    private List<EditText> ingredients;
    private List<EditText> procedureSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ingredients = new ArrayList<EditText>();
        procedureSteps = new ArrayList<EditText>();
        ingredients.add(binding.edittextIngredients);
        procedureSteps.add(binding.edittextProcedure);
        currentIngredient = binding.edittextIngredients;
        currentProcedure = binding.edittextProcedure;
        binding.buttonSubmitRecipe.setOnClickListener(button_submit_recipe_clickListener);
        binding.buttonAddIngredient.setOnClickListener(button_add_ingredient_clickListener);
        binding.buttonAddProcedureStep.setOnClickListener(button_add_procedure_step_clickListener);
        Log.d(LOG_TAG, "Current Ingredient EditText ID: "+currentIngredient.getId());
        Log.d(LOG_TAG, "Current Procedure Step EditText ID: "+currentProcedure.getId());
    }

    private View.OnClickListener button_add_ingredient_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup parent = binding.getRoot();
            EditText newIngredientEditText = generateIngredientView();
            ingredients.add(newIngredientEditText);
            currentIngredient = newIngredientEditText;
            Log.d(LOG_TAG, "New Ingredient EditText ID: "+currentIngredient.getId());
            LinearLayout layout = binding.linearlayoutAddIngredients;
            layout.addView(newIngredientEditText);
        }
    };

    private EditText generateIngredientView()
    {
        EditText editText = new EditText(this);
        editText.setTextColor(getResources().getColor(R.color.white));
        editText.setLinkTextColor(getResources().getColor(R.color.white));
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.white));
        editText.setBackgroundTintList(colorStateList);
        ViewGroup.LayoutParams newParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(newParams);
        editText.setId(View.generateViewId());
        return editText;
    }

    private View.OnClickListener button_add_procedure_step_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup parent = binding.getRoot();
            EditText newProcedureStepEditText = generateProcedureView();
            procedureSteps.add(newProcedureStepEditText);
            currentProcedure = newProcedureStepEditText;
            Log.d(LOG_TAG, "New Procedure Step EditText ID: "+currentProcedure.getId());
            LinearLayout layout = binding.linearlayoutAddProcedure;
            layout.addView(newProcedureStepEditText);
        }
    };

    private EditText generateProcedureView()
    {
        EditText editText = new EditText(this);
        editText.setTextColor(getResources().getColor(R.color.white));
        editText.setLinkTextColor(getResources().getColor(R.color.white));
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.white));
        editText.setBackgroundTintList(colorStateList);
        ViewGroup.LayoutParams newParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(newParams);
        editText.setId(View.generateViewId());
        return editText;
    }

    public EditText getCurrentIngredient() {
        return currentIngredient;
    }

    public void setCurrentIngredient(EditText currentIngredient) {
        this.currentIngredient = currentIngredient;
    }

    private String parseList(List<EditText> list)
    {
        String returnString="";
        Iterator<EditText> iter = list.iterator();
        while(iter.hasNext())
        {
            returnString += iter.next().getText().toString()+"<br>";
        }
        returnString.substring(0, returnString.length()-5);
        return returnString;
    }

    private View.OnClickListener button_submit_recipe_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DBHelper helper = DBHelper.getInstance(AddRecipeActivity.this);
            String name = binding.edittextRecipeName.getText().toString();
            int timeTaken = Integer.valueOf(binding.edittextTimeTaken.getText().toString());
            int serves = Integer.valueOf(binding.edittextServes.getText().toString());
            String ingredientString = parseList(ingredients);
            String procedureString = parseList(procedureSteps);
            boolean isFavorite = binding.checkboxIsFavorite.isChecked();
            helper.addRecipe(new Recipe(name, timeTaken, serves, isFavorite, ingredientString, procedureString));
            Toast.makeText(AddRecipeActivity.this, "Recipe successfully added", Toast.LENGTH_SHORT).show();
            finish();
        }
    };
}