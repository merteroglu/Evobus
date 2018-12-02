package com.merteroglu.ots;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.merteroglu.ots.Model.Driver;
import com.merteroglu.ots.Model.Student;

import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DriverActivity";
    private GoogleMap mMap;
    private String permissions[] = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_PRIVILEGED};
    private FirebaseFirestore firestore;
    private Driver driver;
    private List<Student> studentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestAppPermissions(permissions, R.string.app_name, 10);

        firestore = FirebaseFirestore.getInstance();
        studentList = new ArrayList<>();

        final String driverID = getIntent().getStringExtra("DriverID");

        DocumentReference driverRef = firestore.collection("driver").document(driverID);
        driverRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    driver = document.toObject(Driver.class);
                    driver.setId(document.getId());
                    getStudentList();
                }
            }
        });


        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                driverVehicleLocation(location.getLatitude(),location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 20, mLocationListener);
        }

    }

    private void getStudentList() {
        CollectionReference studentRef = firestore.collection("student");
        studentRef.whereEqualTo("vehicle",driver.getVehicle())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() != 0){
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                                Student student = ds.toObject(Student.class);
                                student.setId(ds.getId());
                                studentList.add(student);
                                addHomeMarkers();
                            }
                        }
                    }
                });
    }

    private void addHomeMarkers() {
        if(mMap != null){
            for(Student s : studentList){
                if(s.getAddress() != null)
                mMap.addMarker(new MarkerOptions()
                .position(new LatLng(s.getAddress().getLatitude(),s.getAddress().getLongitude())));
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.driver_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_studentlist:
                Toast.makeText(this, "Menu tıklandı", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void requestAppPermissions(final String[]requestedPermissions, final int stringId, final int requestCode) {


        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean showRequestPermissions = false;
        for(String permission: requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
            showRequestPermissions = showRequestPermissions || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }

        if (permissionCheck!=PackageManager.PERMISSION_GRANTED) {
            if(showRequestPermissions) {
                ActivityCompat.requestPermissions(DriverActivity.this, requestedPermissions, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, requestedPermissions, requestCode);
            }
        } else {

        }
    }

    public void driverVehicleLocation(double latitude,double longitude){
        firestore.collection("driver").document(driver.getId())
                .update(
                        "vehicleLocation",new GeoPoint(latitude,longitude)
                );
    }

}
