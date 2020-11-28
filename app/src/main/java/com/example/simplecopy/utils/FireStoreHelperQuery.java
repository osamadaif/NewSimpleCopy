package com.example.simplecopy.utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.simplecopy.data.model.Numbers;
import com.example.simplecopy.ui.fragment.MainNumbers.MainRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

import static com.example.simplecopy.utils.Constants.FAVORITE;
import static com.example.simplecopy.utils.Constants.NOTE;
import static com.example.simplecopy.utils.Constants.NUMBER;
import static com.example.simplecopy.utils.Constants.TITLE;
import static com.example.simplecopy.utils.Constants.UID;

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

    public static void fsDelete(CollectionReference collectionReference, String rowIdStr){
        collectionReference
                .document (rowIdStr)
                .delete ();
    }

//    public static void getDataFromFireStor(CollectionReference collectionReference, String collectionName, List<Numbers> dataList) {
//        collectionReference(collectionName)
//                .addSnapshotListener(new EventListener<QuerySnapshot> () {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots)
//                        {
////                            keys.add(snapshot.getId());
//                            dataList.add(new Numbers (Integer.parseInt(String.valueOf(snapshot.get(UID))),
//                                    snapshot.getString(TITLE),Integer.parseInt(String.valueOf(snapshot.get((NUMBER)))),
//                                    snapshot.getString(NOTE),Integer.parseInt(String.valueOf(snapshot.get(FAVORITE))),
//                                    Integer.parseInt(String.valueOf(snapshot.get(DONE))),
//                                    Integer.parseInt(String.valueOf(snapshot.get(DAILY)))));
//                        }
//                        MainRepository.insert(dataList);
//                    }
//                });
//    }
}
