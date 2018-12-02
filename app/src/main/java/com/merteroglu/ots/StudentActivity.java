package com.merteroglu.ots;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.merteroglu.ots.Model.Driver;
import com.merteroglu.ots.Model.Student;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

public class StudentActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "StudentActivity";
    private GoogleMap mMap;
    private FirebaseFirestore firestore;
    private ImageView imgPp,imgDurum;
    private TextView txtStudentName,txtStudentPhone,txtStudentOnBus,txtStudentOffBus,txtDriverName,txtDriverPhone;
    private Student student;
    private Driver driver;
    private Marker busMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firestore = FirebaseFirestore.getInstance();

        final String studentID = getIntent().getStringExtra("StudentID");

        init();

        DocumentReference studentRef = firestore.collection("student").document(studentID);
        studentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    student = document.toObject(Student.class);
                    student.setId(document.getId());
                    getDriverInfo();
                }
            }
        });

    }

    private void fillFields() {
        if(student.isInVehicle())
            imgDurum.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.circle_green));
        else
            imgDurum.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.circle_red));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        txtStudentName.setText(student.getName());
        txtStudentPhone.setText(student.getPhone());
        txtStudentOnBus.setText("Biniş Saati : " + sdf.format(student.getOnBusTime().toDate()));
        txtStudentOffBus.setText("İniş Saati : " + sdf.format(student.getOffBusTime().toDate()));
        txtDriverName.setText(driver.getName());
        txtDriverPhone.setText(driver.getPhone());
    }

    private void init() {
        imgPp = findViewById(R.id.imgPp);
        imgDurum = findViewById(R.id.imgDurum);
        txtStudentName = findViewById(R.id.txtStudentName);
        txtStudentPhone = findViewById(R.id.txtStudentPhone);
        txtStudentOnBus = findViewById(R.id.txtStudentOnBus);
        txtStudentOffBus = findViewById(R.id.txtStudentOffBus);
        txtDriverName = findViewById(R.id.txtDriverName);
        txtDriverPhone = findViewById(R.id.txtDriverPhone);
    }

    private void getDriverInfo(){
        CollectionReference driverRef = firestore.collection("driver");
        driverRef.whereEqualTo("vehicle",student.getVehicle())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() != 0) {
                            driver = queryDocumentSnapshots.getDocuments().get(0).toObject(Driver.class);
                            driver.setId(queryDocumentSnapshots.getDocuments().get(0).getId());
                            student.setDriverId(driver.getId());
                            fillFields();
                            setListeners(student.getId(), driver.getId());
                            if (mMap != null) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(driver.getVehicleLocation().getLatitude(), driver.getVehicleLocation().getLongitude()));
                                busMarker = mMap.addMarker(markerOptions);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setListeners(String studentID,String driverID){
        DocumentReference studentListener = firestore.collection("student").document(studentID);
        studentListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "onEvent: " + e.getMessage());
                    return;
                }

                if(documentSnapshot != null && documentSnapshot.exists()){
                    student = documentSnapshot.toObject(Student.class);
                    student.setId(documentSnapshot.getId());
                    fillFields();
                }
            }
        });

        DocumentReference driverListener = firestore.collection("driver").document(driverID);
        driverListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "onEvent: " + e.getMessage());
                    return;
                }

                if(documentSnapshot != null && documentSnapshot.exists()){
                    driver = documentSnapshot.toObject(Driver.class);
                    driver.setId(documentSnapshot.getId());
                    if(busMarker != null)
                    busMarker.setPosition(new LatLng(driver.getVehicleLocation().getLatitude(),driver.getVehicleLocation().getLongitude()));
                }
            }
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       /* MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(driver.getVehicleLocation().getLatitude(),driver.getVehicleLocation().getLongitude()));
        busMarker = mMap.addMarker(markerOptions); */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.location_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_studentlocations:
                Toast.makeText(this, "Menu tıklandı", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
