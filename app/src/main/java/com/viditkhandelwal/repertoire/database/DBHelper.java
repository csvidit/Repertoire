package com.viditkhandelwal.repertoire.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
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
    public static final String COL_IMAGE="image";

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
                COL_IMAGE+" BLOB,"+
                "PRIMARY KEY("+COL_RECIPE_ID+"AUTOINCREMENT)"+
                ")";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public DBHelper getInstance(Context context)
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
        int idx_image = cursor.getColumnIndex(COL_IMAGE);

        if(cursor.moveToFirst())
        {
            do {

                int id = cursor.getInt(idx_id);

                byte[] photo  = cursor.getBlob(idx_image);
                ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
                Bitmap bitmap= BitmapFactory.decodeStream(imageStream);
                image.setImageBitmap(bitmap);


            }while(cursor.moveToNext());
        }

    }
}
