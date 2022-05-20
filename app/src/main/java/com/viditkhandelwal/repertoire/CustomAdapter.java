package com.viditkhandelwal.repertoire;

import static com.viditkhandelwal.repertoire.Utils.LOG_TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.viditkhandelwal.repertoire.database.Recipe;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private List<Recipe> recipes;
    private Context context;

    public CustomAdapter(List<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_recipes_row, parent, false);
        }

        TextView recipeName = convertView.findViewById(R.id.textview_list_recipe_name);
        TextView timeTaken = convertView.findViewById(R.id.textview_list_time_taken);
        ImageView image = convertView.findViewById(R.id.imageview_list_recipe_image);
        RatingBar isFavorite = convertView.findViewById(R.id.ratingbar_list_recipe_rating);

        Recipe currRecipe = recipes.get(position);

        recipeName.setText(currRecipe.getName());
        timeTaken.setText(Recipe.parseTime(currRecipe.getTime()));
        image.setImageDrawable(currRecipe.getImage());

        Log.d(LOG_TAG, "CustomAdapter has set views in convertView");

        return convertView;
    }
}
