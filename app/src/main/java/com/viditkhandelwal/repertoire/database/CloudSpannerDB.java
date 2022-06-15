package com.viditkhandelwal.repertoire.database;

import static com.viditkhandelwal.repertoire.Utils.LOG_TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.viditkhandelwal.repertoire.FirebaseStorageCallback;
import com.viditkhandelwal.repertoire.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CloudSpannerDB {

    private static final String SPANNER_INSTANCE_ID = "repertoire-spanner";
    private static final String SPANNER_DATABASE_ID = "repertoire";

    public static final String TABLE_RECIPE="Recipe";
    public static final String COL_RECIPE_ID="id";
    public static final String COL_NAME="name";
    public static final String COL_TIME="time";
    public static final String COL_SERVES="serves";
    public static final String COL_IS_FAVORITE="is_favorite";
    public static final String COL_INGREDIENTS="ingredients";
    public static final String COL_PROCEDURE="procedure";

    public static final int IDX_ID = 0;
    public static final int IDX_NAME = 1;
    public static final int IDX_TIME = 2;
    public static final int IDX_SERVES = 3;
    public static final int IDX_IS_FAVORITE = 4;
    public static final int IDX_INGREDIENTS = 5;
    public static final int IDX_PROCEDURE = 6;

    public static final String IMAGE_FILE_TYPE= "jpg";
    public static final String IMAGE_FILE_EXTENSION= ".jpg";

    public static CloudSpannerDB myInstance;
    private DatabaseClient dbClient;
    private static FirebaseStorage firebaseStorage;

    public static CloudSpannerDB getInstance()
    {
        if(myInstance == null)
        {
            myInstance = new CloudSpannerDB();
            return myInstance;
        }
        return myInstance;
    }

    public CloudSpannerDB()
    {
        dbClient = createDatabaseClient();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    private static DatabaseClient createDatabaseClient()
    {
        SpannerOptions options = SpannerOptions.newBuilder().build();
        Spanner spanner = options.getService();
        DatabaseId db = DatabaseId.of(options.getProjectId(), SPANNER_INSTANCE_ID, SPANNER_DATABASE_ID);
        DatabaseClient dbClient = spanner.getDatabaseClient(db);
        return dbClient;
    }

    public long addRecipe(Recipe recipe)
    {
        //querying database to get current row count
        String getCountQuery="SELECT COUNT(*) FROM "+TABLE_RECIPE;
        ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(getCountQuery));
        long numRows = resultSet.getLong(0);

        //Creating a mutation object with new row
        Mutation mutation = Mutation.newInsertBuilder("Recipe")
                .set(COL_RECIPE_ID).to(++numRows)
                .set(COL_NAME).to(recipe.getName())
                .set(COL_TIME).to(recipe.getTime())
                .set(COL_SERVES).to(recipe.getServes())
                .set(COL_INGREDIENTS).to(recipe.getIngredients())
                .set(COL_PROCEDURE).to(recipe.getProcedure())
                .set(COL_IS_FAVORITE).to(recipe.isFavorite()).build();

        //Adding the Mutation object to a list, since the dbClient requires mutations to be passed as a list
        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(mutation);
        dbClient.write(mutations);

        //Getting a reference to the Firebase Storage Location and storing the Image (Drawable) there
        StorageReference storageRef = firebaseStorage.getReference();
        String newRecipeImageFileName = String.valueOf(numRows)+"_"+recipe.getName();
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
        return numRows;
    }

    public List<Recipe> getAllRecipes(FirebaseStorageCallback callback)
    {
        List<Recipe> recipes = new ArrayList<Recipe>();

        String getAllRecipesQuery="SELECT * FROM "+TABLE_RECIPE;
        ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(getAllRecipesQuery));
        while(resultSet.next())
        {
            int id = (int) resultSet.getLong(IDX_ID);
            String name = resultSet.getString(IDX_NAME);
            int time = (int) resultSet.getLong(IDX_TIME);
            int serves = (int) resultSet.getLong(IDX_SERVES);
            boolean isFavorite = resultSet.getBoolean(IDX_IS_FAVORITE);
            String ingredients = resultSet.getString(IDX_INGREDIENTS);
            String procedure = resultSet.getString(IDX_PROCEDURE);

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
                                isFavorite, ingredients, procedure, recipeImageDrawable[0]);
                        recipes.add(newRecipe);
                        Log.d(LOG_TAG, "Recipe added to ArrayList in DBHelper at position"+String.valueOf(recipes.size()));
                        Log.d(LOG_TAG, "Drawable has been created");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(LOG_TAG, "File creation failed");
                        callback.onFailure();
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
        }
        return recipes;
    }

    public DatabaseClient getDbClient() {
        return dbClient;
    }


}
