package com.viditkhandelwal.repertoire;

import com.viditkhandelwal.repertoire.database.Recipe;

import java.util.List;

public interface FirebaseStorageCallback {

    public void onSuccess(List<Recipe> recipes);

}
