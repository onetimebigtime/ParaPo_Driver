package com.example.parapo_driver.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parapo_driver.BuildConfig;
import com.example.parapo_driver.R;
import  com.example.parapo_driver.databinding.FragmentSeekBinding;
import com.example.parapo_driver.ui.signup.UserData;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SeekFragment extends Fragment {

    private FragmentSeekBinding binding;
    public static final String TAG = "SeekFragment";

    private ToggleButton driveButton;
    private TextView passengerCountView;
    private EditText seekRouteText;
    private FusedLocationProviderClient fusedLocationClient; //GIVE LOCATION
    private final static int REQUEST_CODE = 100;
    private ArrayList<UserData> list;

    private static final double defaultLocationVal = 0;

    private Drawable seekIcon;
    private Map<String, Marker> markers; //FOR MARKING LOCATION
    private IMapController mapController; //CONTROL THE MAP

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<GeoPoint> geoPointList; //LIST OF LOCATION POINTS
    GeoPoint startPoint;    //STARTING POINT FOR DEFAULT VIEW
    private MapView mapView; //DISPLAY THE MAP
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private MyLocationNewOverlay myLocationNewOverlay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SeekViewModel seekViewModel =
                new ViewModelProvider(this).get(SeekViewModel.class);

        binding = FragmentSeekBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity());

        //--------------------DRIVE BUTTON ON CLICK FUNCTION SECTION------------------------
        /*driveButton.setOnClickListener(v -> {
            String route = seekRouteText.getText().toString().trim();
            if (TextUtils.isEmpty(route)) {
                seekRouteText.setError("Please enter your route!");
                seekRouteText.requestFocus();
                driveButton.setChecked(false);
            } else {
                if (driveButton.isChecked()) {
                    if (hasLocationPermissions()) {
                        getRealtimeLocation();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //DIALOG BOX STRINGS
                            String title = "Location Permission Request";
                            String message = "ParaPo needs your location to use our services";
                            String posTitle = "Enable";
                            String negTitle = "Cancel";
                            showAlertDialog(title, message, posTitle, (dialog, which) -> multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}), negTitle);
                        } else {
                            multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION});
                        }
                    }
                } else {
                    //INPUT LOCATION TO ZERO

                }
            }
        });*/
        //--------------------DRIVE BUTTON ON CLICK FUNCTION SECTION------------------------

        /*final TextView textView = binding.textDashboard;
        seekViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Configuration.getInstance().setUserAgentValue(this.requireActivity().getPackageName());

        //----------INITIALIZE ARRAY LISTS---------------------------------------

        //----------INITIALIZE ARRAY LISTS---------------------------------------

        //----------SETTING UP COMPONENTS----------------------------------------
        FloatingActionButton selfLocateButton = view.findViewById(R.id.floatingActionButton2);
        driveButton = view.findViewById(R.id.drive_button);
        passengerCountView = view.findViewById(R.id.seek_passenger_view);
        seekRouteText = view.findViewById(R.id.seek_route_text);
        seekIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_passenger);
        mapView = view.findViewById(R.id.map);
        //----------SETTING UP COMPONENTS----------------------------------------

        //----------SETTING UP MAP----------------------------------------
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setClickable(true);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        setDefaultView(mapController);
        //----------SETTING UP MAP----------------------------------------
        geoPointList = new ArrayList<>();
        markers = new HashMap<>();

        //----------CALLING FUNCTIONS----------------------------------------
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this.requireActivity()), mapView);

        getPassengerCount();
        getRealtimeMarker();
        //----------CALLING FUNCTIONS----------------------------------------

        //-----------------SELF LOCATE BUTTON ON CLICK FUNCTION SECTION----------------------
        selfLocateButton.setOnClickListener(v -> {
            if (hasLocationPermissions()) {
                getMyLocation();
                getRealtimeLocation();
            }
            getLocationPermission();
        });
        //-----------------SELF LOCATE BUTTON ON CLICK FUNCTION SECTION----------------------

        //-----------------DRIVER BUTTON ON CLICK FUNCTION SECTION----------------------
        driveButton.setOnClickListener(v -> {
            //String route = seekRouteText.getText().toString().trim();
            if (driveButton.isChecked() && hasLocationPermissions()) {
                getRealtimeLocation();
                getMyLocation();
                updateUserData(true, "Dakota");
                Toast.makeText(requireActivity(), "You are now visible! Drive safely!", Toast.LENGTH_SHORT).show();
            } else {
                getLocationPermission();
                //seekRouteText.setError("Please enter your route!");
                //seekRouteText.requestFocus();
                //driveButton.setChecked(false);
                myLocationNewOverlay.disableMyLocation();

                updateLocationData(defaultLocationVal, defaultLocationVal);
                updateUserData(false, "Unknown");
                fusedLocationClient.removeLocationUpdates(locationCallback);

                Toast.makeText(requireActivity(), "You are now offline!", Toast.LENGTH_SHORT).show();
            }
        });
        //-----------------DRIVER BUTTON ON CLICK FUNCTION SECTION----------------------


    }
    //-----------------LOCATION PERMISSION FUNCTION SECTION----------------------
    private void getLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
            //DIALOG BOX STRINGS
            String title = "Location Permission Request";
            String message = "ParaPo needs your location to use our services";
            String posTitle = "Enable";
            String negTitle = "Cancel";
            showAlertDialog(title, message, posTitle, (dialog, which) -> multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}), negTitle);
        }
        else {
            multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }
    //-----------------LOCATION PERMISSION FUNCTION SECTION----------------------

    //-----------------GET USER OWN LOCATION FUNCTION SECTION----------------------
    private void getMyLocation() {
        myLocationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationNewOverlay);
        myLocationNewOverlay.enableFollowLocation();
    }
    //-----------------GET USER OWN LOCATION FUNCTION SECTION----------------------


    //-----------------SET DEFAULT VIEW OF MAP------------------------------------------
    public void setDefaultView(IMapController mapController){
        if (hasLocationPermissions()){
            mapController.setZoom(10.0);
            GeoPoint startPoint = new GeoPoint(14.3194, 120.9190); // ADAMSON
            mapController.setCenter(startPoint);
        } else if (!hasLocationPermissions()) {
            mapController.setZoom(10.0);
            GeoPoint startPoint = new GeoPoint(14.3194, 120.9190); // ADAMSON
            mapController.setCenter(startPoint);
        }
    }
    //-----------------SET DEFAULT VIEW OF MAP------------------------------------------

    //-----------------GET TRAVELER MARKER REALTIME LOCATION--------------------------------------
    private void getRealtimeMarker() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Travelers");
        valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                geoPointList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    String userId = dataSnapshot.getKey();
                    assert userData != null;
                    boolean isOnline = userData.is_online;

                    if (isOnline) {
                        double latitude = userData.latitude;
                        double longitude = userData.longitude;

                        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                        geoPointList.add(geoPoint);

                        //UPDATE OR ADD MARKER
                        Marker marker = markers.get(userId);

                        if (marker == null) {
                            marker = new Marker(mapView);
                            marker.setIcon(seekIcon);
                            mapView.getOverlays().add(marker);
                            markers.put(userId, marker);
                        }

                        marker.setPosition(geoPoint);
                        marker.setTitle(userId);
                    }else {
                        Marker marker = markers.get(userId);
                        if (marker != null) {
                            mapView.getOverlays().remove(marker);
                            markers.remove(userId);
                        }
                    }
                }
                mapView.invalidate();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError" +error.getMessage());
            }
        };
    }
    //-----------------GET TRAVELER MARKER REALTIME LOCATION--------------------------------------

    //-----------------GET USERS REALTIME LOCATION--------------------------------------
    @SuppressLint("MissingPermission")
    private  void getRealtimeLocation(){

        LocationRequest locationRequest = new LocationRequest.Builder(5000)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(10)
                .build();

        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this.requireActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            } else {
                if (task.getException() instanceof ResolvableApiException) {

                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) task.getException();
                        resolvableApiException.startResolutionForResult(requireActivity(),REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
    //-----------------GET USERS REALTIME LOCATION--------------------------------------
    //--------LOCATION CALL BACK----------------------------
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                mapController.setZoom(20.0);
                startPoint = new GeoPoint(latitude, longitude);
                mapController.setCenter(startPoint);
                mapController.animateTo(startPoint);

                //UPDATING USER DATA
                updateLocationData(latitude, longitude);
            }

        }
    };
    //--------LOCATION CALL BACK----------------------------

    //---------------------SEE IF USER HAS LOCATION PERMISSION ENABLED-----------------------

    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this.requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    //---------------------SEE IF USER HAS LOCATION PERMISSION ENABLED-----------------------

    //---------------------SHOW ALERT DIALOG BOX SECTION-----------------------
    void showAlertDialog(String title, String message,
                         String positiveTitle, DialogInterface.OnClickListener positiveListener,
                         String negativeTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireActivity());
        builder.setTitle(title).setMessage(message).setPositiveButton(positiveTitle, positiveListener)
                .setNegativeButton(negativeTitle, null);
        builder.create().show();
    }
    //---------------------SHOW ALERT DIALOG BOX SECTION-----------------------

    //--------------------LAUNCH PERMISSION IF LOCATION PERMISSION IS STILL NOT GRANTED-------------------------------
    private final ActivityResultLauncher<String[]> multiplePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        boolean fineLocationPermission;
        if (result.get(Manifest.permission.ACCESS_FINE_LOCATION)!= null) {
            fineLocationPermission = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
            if(fineLocationPermission) {
                getRealtimeLocation();
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showAlertDialog("Permission Request", "ParaPo needs your location to use our services", "Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:"+ BuildConfig.APPLICATION_ID));
                        startActivity(intent);
                    },"Cancel");
                }
            }
        }
    });
    //--------------------LAUNCH PERMISSION IF LOCATION PERMISSION IS STILL NOT GRANTED-------------------------------

    //-----------GET CURRENT LOCATION------------------------

    /*
    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder()
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setDurationMillis(5000)
                .setMaxUpdateAgeMillis(0)
                .build();

        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(currentLocationRequest, cancellationTokenSource.getToken()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Location location = task.getResult();
            }
            else {
                //TRY CATCH INSERT
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(this.requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }*/
    //-----------GET CURRENT LOCATION------------------------

    private void updateUserData(Boolean isOnline, String route) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this.requireActivity(), "Unable to find user!", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("is_online", isOnline);
            userData.put("route", route);
            String userId = firebaseUser.getUid();

            updateData(userId, userData);
        }
    }

    private void updateLocationData(double latitude, double longitude) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this.requireActivity(), "Unable to find user!", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("latitude", latitude);
            userData.put("longitude", longitude);
            String userId = firebaseUser.getUid();

            updateData(userId, userData);
        }
    }

    private void updateData(String userId, HashMap<String, Object> userData){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Drivers");

        databaseReference.child(userId).updateChildren(userData).addOnCompleteListener((OnCompleteListener<Void>) task -> {
            if(task.isSuccessful()){

            } else {
                Toast.makeText(requireActivity(), "Can't Complete the task!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //--------LOCATION CALL BACK----------------------------

    //--------PASSENGER AVAILABLE----------------------------
    public void getPassengerCount() {
        list = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Travelers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    assert userData != null;
                    boolean isOnline = userData.is_online;
                    if (isOnline) {
                        list.add(userData);
                        int countPassenger = list.size();
                        passengerCountView.setText(String.valueOf(countPassenger));

                    } else if (list.size()==0) {
                        passengerCountView.setText(String.valueOf(list.size()));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //--------PASSENGER AVAILABLE----------------------------



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        myLocationNewOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        myLocationNewOverlay.disableMyLocation();
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(valueEventListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        driveButton.setOnCheckedChangeListener(null);
        binding = null;
    }
}