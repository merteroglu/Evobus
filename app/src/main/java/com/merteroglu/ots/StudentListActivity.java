package com.merteroglu.ots;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.merteroglu.ots.Adapter.StudentListViewAdapter;
import com.merteroglu.ots.Model.Student;

import javax.annotation.Nullable;

public class StudentListActivity extends AppCompatActivity {

    private ListView listView;
    private StudentListViewAdapter studentListViewAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        listView = findViewById(R.id.studentsListView);
        studentListViewAdapter = new StudentListViewAdapter(StudentListActivity.this,R.layout.studentlistview_item);
        listView.setAdapter(studentListViewAdapter);

        firestore = FirebaseFirestore.getInstance();

        final String vehicle = getIntent().getStringExtra("Vehicle");

        CollectionReference studentRef = firestore.collection("student");
        studentRef.whereEqualTo("vehicle",vehicle)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() != 0){
                            studentListViewAdapter.clearAll();
                            for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                                Student student = ds.toObject(Student.class);
                                student.setId(ds.getId());
                                studentListViewAdapter.add(student);
                            }
                            listView.setAdapter(studentListViewAdapter);
                            listView.deferNotifyDataSetChanged();
                        }
                    }
                });

        firestore.collection("student")
                .whereEqualTo("vehicle",vehicle)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d("Student List Activity", "onEvent: " + e.getMessage());
                            return;
                        }

                        studentListViewAdapter.clearAll();
                        if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0){
                            for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                                Student student = ds.toObject(Student.class);
                                student.setId(ds.getId());
                                studentListViewAdapter.add(student);
                            }
                            listView.setAdapter(studentListViewAdapter);
                            listView.deferNotifyDataSetChanged();
                        }
                    }
                });
    }
}
