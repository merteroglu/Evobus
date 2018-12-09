package com.merteroglu.ots;

import android.Manifest;
import android.content.Intent;
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


import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.recognition.packets.ConfigurableDevice;
import com.estimote.coresdk.recognition.packets.Nearable;
import com.estimote.coresdk.service.BeaconManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.merteroglu.ots.Model.Driver;
import com.merteroglu.ots.Model.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DriverActivity";
    private GoogleMap mMap;
    private String permissions[] = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_PRIVILEGED,Manifest.permission.CALL_PHONE};
    private FirebaseFirestore firestore;
    private Driver driver;
    private List<Student> studentList;
    private double driverCurrentLocationLatitude;
    private double driverCurrentLocationLongitude;
    private BeaconManager beaconManager;

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
                    driverCurrentLocationLatitude = driver.getVehicleLocation().getLatitude();
                    driverCurrentLocationLongitude = driver.getVehicleLocation().getLongitude();
                    getStudentList();
                }
            }
        });


        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateDriverVehicleLocation(location.getLatitude(),location.getLongitude());
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

        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(2000,2000);

        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(BeaconRegion beaconRegion, List<Beacon> beacons) {
                Toast.makeText(DriverActivity.this, "Entered Region : " + beaconRegion.getIdentifier(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onEnteredRegion: " + beaconRegion.getIdentifier());
                for(Student s : studentList){
                    if(s.getName().equals(beaconRegion.getIdentifier())){
                        updateStudentBusSituation(s,true);
                    }
                }
            }

            @Override
            public void onExitedRegion(BeaconRegion beaconRegion) {
                Toast.makeText(DriverActivity.this, "Exited Region : " + beaconRegion.getIdentifier(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onExitedRegion: " + beaconRegion.getIdentifier());
                for(Student s : studentList){
                    if(s.getName().equals(beaconRegion.getIdentifier())){
                        updateStudentBusSituation(s,false);
                    }
                }
            }
        });

        beaconManager.setBackgroundScanPeriod(2000,2000);


    }

    private void startMonitoring(){
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                if(studentList != null){
                    for (Student s : studentList){
                        beaconManager.startMonitoring(new BeaconRegion(s.getName(),UUID.fromString(s.getBid()),null,null));
                        Log.d(TAG, "onServiceReady: " + s.getName() + " -> " + s.getBid());
                    }
                }

                beaconManager.setConfigurableDevicesListener(new BeaconManager.ConfigurableDevicesListener() {
                    @Override
                    public void onConfigurableDevicesFound(List<ConfigurableDevice> configurableDevices) {
                        for (ConfigurableDevice device : configurableDevices){
                            Log.d(TAG, "onConfigurableDevicesFound: " + device.getUniqueKey() + "  ==> " + device.macAddress);
                        }
                    }
                });
                beaconManager.startConfigurableDevicesDiscovery();

                beaconManager.setNearableListener(new BeaconManager.NearableListener() {
                    @Override
                    public void onNearablesDiscovered(List<Nearable> nearables) {
                        for (Nearable nearable : nearables)
                            Log.d(TAG, "onNearablesDiscovered: " + nearable.identifier);
                    }
                });

                beaconManager.startNearableDiscovery();

            }
        });
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
                            }
                            addHomeMarkers();
                        }
                    }
                });
        studentRef.whereEqualTo("vehicle",driver.getVehicle())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, "onEvent: " + e.getMessage());
                            return;
                        }

                        if(queryDocumentSnapshots != null){
                            if(queryDocumentSnapshots.size() != 0){
                                studentList.clear();
                                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                                    Student student = ds.toObject(Student.class);
                                    student.setId(ds.getId());
                                    studentList.add(student);
                                }
                                addHomeMarkers();
                            }
                        }
                    }
                });
        startMonitoring();
    }

    private void addHomeMarkers() {
        if(mMap != null){
            mMap.clear();
            for(Student s : studentList){
                float v = s.isInVehicle() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;
                if(s.getAddress() != null)
                mMap.addMarker(new MarkerOptions()
                .position(new LatLng(s.getAddress().getLatitude(),s.getAddress().getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(v)));
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
                Intent intent = new Intent(getApplicationContext(),StudentListActivity.class);
                intent.putExtra("Vehicle",driver.getVehicle());
                startActivity(intent);
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

    public void updateDriverVehicleLocation(double latitude,double longitude){
        driverCurrentLocationLatitude = latitude;
        driverCurrentLocationLongitude = longitude;
        if(driver != null) {
            firestore.collection("driver").document(driver.getId())
                    .update(
                            "vehicleLocation", new GeoPoint(latitude, longitude)
                    );
            updateStudentsCurrentLocation(latitude, longitude);
        }
    }

    public void updateStudentsCurrentLocation(double latitude,double longitude){
        for(Student s : studentList){
            if(s.isInVehicle()){
                firestore.collection("student").document(s.getId())
                        .update(
                                "currentLocation",new GeoPoint(latitude,longitude)
                        );
            }
        }
    }

   public void updateStudentBusSituation(Student s, boolean isOnBus){
       Timestamp time = new Timestamp(Calendar.getInstance().getTime());
       GeoPoint location = new GeoPoint(driverCurrentLocationLatitude,driverCurrentLocationLongitude);
        if(isOnBus){
            firestore.collection("student").document(s.getId())
                    .update(
                            "OnBusLocation",location,
                            "OnBusTime",time,
                            "inVehicle",true
                    );
            firestore.collection("student").document(s.getId()).collection("locations")
                    .add(new com.merteroglu.ots.Model.Location(time,location,true));

        }else{
            firestore.collection("student").document(s.getId())
                    .update(
                            "OffBusLocation",location,
                            "OffBusTime",time,
                            "inVehicle",false
                    );
            firestore.collection("student").document(s.getId()).collection("locations")
                    .add(new com.merteroglu.ots.Model.Location(time,location,false));
        }
   }



}
