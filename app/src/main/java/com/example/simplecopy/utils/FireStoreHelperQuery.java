package com.example.simplecopy.utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FireStoreHelperQuery {
    private static final String TAG = "FireStoreHelperQuery";
    private FirebaseFirestore fdb;
    private CollectionReference numberDocRef;

    public static void fsInsert(CollectionReference collectionReference, String rowIdStr, Map<String, Object> map){
        collectionReference
                .document (rowIdStr)
                .set (map)
                .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d (TAG, "onSuccess: done");
                    }
                })
                .addOnFailureListener (new OnFailureListener ( ) {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d (TAG, "onFailure: " + e.toString ( ));
                    }
                });
    }

    public static void fsUpdate(CollectionReference collectionReference, String rowIdStr, Map<String, Object> map){
        collectionReference
                .document (rowIdStr)
                .update (map)
                .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d (TAG, "onSuccess: done");
                    }
                })
                .addOnFailureListener (new OnFailureListener ( ) {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d (TAG, "onFailure: " + e.toString ( ));
                    }
                });
    }
}
