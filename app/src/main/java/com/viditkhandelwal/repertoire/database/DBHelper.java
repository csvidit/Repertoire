package com.viditkhandelwal.repertoire.database;

import static com.viditkhandelwal.repertoire.Utils.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.viditkhandelwal.repertoire.FirebaseStorageCallback;
import com.viditkhandelwal.repertoire.HomeActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static DBHelper myInstance;

    private static FirebaseStorage firebaseStorage;

    public static final String IMAGE_FILE_TYPE= "jpg";
    public static final String IMAGE_FILE_EXTENSION= ".jpg";


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
                "PRIMARY KEY("+COL_RECIPE_ID+" AUTOINCREMENT)"+
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
            firebaseStorage = FirebaseStorage.getInstance();
        }
        return myInstance;
    }

    public void getAllRecipes(FirebaseStorageCallback callback)
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

        List<Recipe> recipes = new ArrayList<Recipe>();

        if(cursor.moveToFirst())
        {
            do {

                Log.d(LOG_TAG, "Cursor Retrieve");

                int id = cursor.getInt(idx_id);
                String name = cursor.getString(idx_name);
                int time = cursor.getInt(idx_time);
                int serves = cursor.getInt(idx_serves);
                int isFavorite = cursor.getInt(idx_is_favorite);
                String ingredients = cursor.getString(idx_ingredients);
                String procedure = cursor.getString(idx_procedure);

                //Retrieve Recipe Picture from Firebase Storage
                StorageReference storageRef = firebaseStorage.getReference();
                String recipeImageName = String.valueOf(id)+"_"+name;
                String recipeImageFileName = recipeImageName+".jpg";
                StorageReference imageRef = storageRef.child(recipeImageFileName);
                final Drawable[] recipeImageDrawable = new Drawable[1];
                try {
                    File localFile = File.createTempFile(recipeImageName, IMAGE_FILE_TYPE);
                    imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d(LOG_TAG, "Local Image File has been created");
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                            recipeImageDrawable[0] = new BitmapDrawable(bitmap);
                            //Create a new Recipe object and add it to the Recipe ArrayList
                            Recipe newRecipe = new Recipe(id, name, time, serves,
                                    Recipe.parseFavorite(isFavorite), ingredients, procedure, recipeImageDrawable[0]);
                            recipes.add(newRecipe);
                            Log.d(LOG_TAG, "Recipe added to ArrayList in DBHelper at position"+String.valueOf(recipes.size()));
                            Log.d(LOG_TAG, "Drawable has been created");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(LOG_TAG, "File creation failed");
                        }
                    }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                            callback.onSuccess(recipes);

                        }
                    });
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }while(cursor.moveToNext());
        }
    }

    public long addRecipe(Recipe recipe)
    {
        //getting an instance of a writeable SQLite Database
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, recipe.getName());
        cv.put(COL_TIME, recipe.getTime());
        cv.put(COL_SERVES, recipe.getServes());
        cv.put(COL_IS_FAVORITE, Recipe.parseFavorite(recipe.isFavorite()));
        cv.put(COL_INGREDIENTS, recipe.getIngredients());
        cv.put(COL_PROCEDURE, recipe.getProcedure());

        //Adding everything except the image to the SQLite Database
        long result = db.insert(TABLE_RECIPE, null, cv);

        db.close();

        //Getting a reference to the Firebase Storage Location and storing the Image (Drawable) there
        StorageReference storageRef = firebaseStorage.getReference();
        String newRecipeImageFileName = String.valueOf(result)+"_"+recipe.getName();
        StorageReference newRecipeImageRef = storageRef.child(newRecipeImageFileName+".jpg");

        //Converting the Drawable to a ByteArray for transfer to Firebase Storage
        Bitmap bitmap = ((BitmapDrawable) recipe.getImage()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        UploadTask ut = newRecipeImageRef.putBytes(imageData);
        ut.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "Image Upload Failed");
                Log.d(LOG_TAG, e.getMessage());
                Log.d(LOG_TAG, e.getStackTrace().toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(LOG_TAG, "Image Upload Successful");
            }
        });

        //add a delay to make sure that HomeActivity does not try to retrieve image before it has been successfully uploaded.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

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
