package com.viditkhandelwal.repertoire.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="Repertoire.db";
    private static final int DB_VERSION=1;

    public static final String TABLE_RECIPE="Recipe";
    public static final String COL_RECIPE_ID="id";
    public static final String COL_NAME="name";
    public static final String COL_TIME="time";
    public static final String COL_SERVES="serves";
    public static final String COL_IS_FAVORITE="is_favorite";
    public static final String COL_INGREDIENTS="ingredients";
    public static final String COL_PROCEDURE="procedure";
//    public static final String COL_IMAGE="image";

    public static DBHelper myInstance;


    private DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE "+TABLE_RECIPE+" ("+
                COL_RECIPE_ID+" INTEGER,"+
                COL_NAME+" TEXT NOT NULL,"+
                COL_TIME+" INTEGER NOT NULL,"+
                COL_SERVES+" INTEGER NOT NULL,"+
                COL_IS_FAVORITE+" INTEGER,"+
                COL_INGREDIENTS+" TEXT NOT NULL,"+
                COL_PROCEDURE+" TEXT NOT NULL,"+
//                COL_IMAGE+" BLOB,"+
                "PRIMARY KEY("+COL_RECIPE_ID+"AUTOINCREMENT)"+
                ")";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static DBHelper getInstance(Context context)
    {
        if(myInstance == null)
        {
            myInstance = new DBHelper(context);
        }
        return myInstance;
    }

    public List<Recipe> getAllRecipes()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM "+TABLE_RECIPE;
        Cursor cursor = db.rawQuery(sql, null);

        int idx_id = cursor.getColumnIndex(COL_RECIPE_ID);
        int idx_name = cursor.getColumnIndex(COL_NAME);
        int idx_time = cursor.getColumnIndex(COL_TIME);
        int idx_serves = cursor.getColumnIndex(COL_SERVES);
        int idx_is_favorite = cursor.getColumnIndex(COL_IS_FAVORITE);
        int idx_ingredients = cursor.getColumnIndex(COL_INGREDIENTS);
        int idx_procedure = cursor.getColumnIndex(COL_PROCEDURE);
//        int idx_image = cursor.getColumnIndex(COL_IMAGE);

        List<Recipe> recipes = new ArrayList<Recipe>();

        if(cursor.moveToFirst())
        {
            do {

                int id = cursor.getInt(idx_id);
                String name = cursor.getString(idx_name);
                int time = cursor.getInt(idx_time);
                int serves = cursor.getInt(idx_serves);
                int isFavorite = cursor.getInt(idx_is_favorite);
                String ingredients = cursor.getString(idx_ingredients);
                String procedure = cursor.getString(idx_procedure);

                recipes.add(new Recipe(id, name, time, serves,
                        Recipe.parseFavorite(isFavorite), ingredients, procedure));


//                byte[] photo  = cursor.getBlob(idx_image);
//                Drawable image = new BitmapDrawable(getResources(),
//                        BitmapFactory.decodeByteArray(b, 0, b.length));


            }while(cursor.moveToNext());
        }
        return recipes;

    }

    public long insertRecipe(Recipe recipe)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, recipe.getName());
        cv.put(COL_TIME, recipe.getTime());
        cv.put(COL_SERVES, recipe.getServes());
        cv.put(COL_IS_FAVORITE, Recipe.parseFavorite(recipe.isFavorite()));
        cv.put(COL_INGREDIENTS, recipe.getIngredients());
        cv.put(COL_PROCEDURE, recipe.getProcedure());

        long result = db.insert(TABLE_RECIPE, null, cv);

        db.close();
        return result;
    }

    public int deleteRecipe(Recipe recipe)
    {
        SQLiteDatabase db = getReadableDatabase();
        String whereClause = String.format("%s = %o", COL_RECIPE_ID, recipe.getId());
        int result = db.delete(TABLE_RECIPE, whereClause, null);
        return result;
    }
}
