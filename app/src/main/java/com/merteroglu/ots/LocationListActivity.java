package com.merteroglu.ots;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.merteroglu.ots.Adapter.LocationListViewAdapter;
import com.merteroglu.ots.Model.Location;
import com.merteroglu.ots.Model.Student;

import javax.annotation.Nullable;

public class LocationListActivity extends AppCompatActivity {

    private ListView listView;
    private LocationListViewAdapter locationListViewAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        listView = findViewById(R.id.locationsListView);
        locationListViewAdapter = new LocationListViewAdapter(LocationListActivity.this,R.layout.locationlistview_item);
        listView.setAdapter(locationListViewAdapter);

        firestore = FirebaseFirestore.getInstance();


        final String studentID = getIntent().getStringExtra("StudentID");
        DocumentReference studentRef = firestore.collection("student").document(studentID);
        studentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Student student;
                    student = document.toObject(Student.class);
                    student.setId(document.getId());
                    for(Location lo : student.getLocations()){
                        locationListViewAdapter.add(lo);
                    }
                    listView.setAdapter(locationListViewAdapter);
                    listView.deferNotifyDataSetChanged();
                }
            }
        });

        studentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d("Location List Activity", "onEvent: " + e.getMessage());
                    return;
                }

                if(documentSnapshot != null && documentSnapshot.exists()){
                    Student student;
                    student = documentSnapshot.toObject(Student.class);
                    student.setId(documentSnapshot.getId());
                    locationListViewAdapter.clearAll();
                    for(Location lo : student.getLocations()){
                        locationListViewAdapter.add(lo);
                    }
                    listView.setAdapter(locationListViewAdapter);
                    listView.deferNotifyDataSetChanged();
                }
            }
        });

    }
}
