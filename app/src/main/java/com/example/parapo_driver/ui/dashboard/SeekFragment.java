package com.example.parapo_driver.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SeekFragment extends Fragment {

    private FragmentSeekBinding binding;
    public static final String TAG = "SeekFragment";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch driveButton;
    private TextView latTextView, longTextView, passengerCountView;
    private EditText seekRouteText;
    private FusedLocationProviderClient fusedLocationClient; //GIVE LOCATION
    private final static int REQUEST_CODE = 100;
    private ArrayList<UserData> list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SeekViewModel seekViewModel =
                new ViewModelProvider(this).get(SeekViewModel.class);

        binding = FragmentSeekBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        //-----------------SETTING UP THE COMPONENTS----------------------
        //ADD MAP HERE

        passengerCountView = root.findViewById(R.id.seek_passenger_view);
        driveButton = root.findViewById(R.id.drive_button);
        latTextView = root.findViewById(R.id.lat_textView);
        longTextView = root.findViewById(R.id.long_textView);
        seekRouteText = root.findViewById(R.id.seek_route_text);
        FloatingActionButton selfLocateButton = root.findViewById(R.id.floatingActionButton2);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity());

        //-----------------SETTING UP THE COMPONENTS----------------------

        getPassengerCount();

        //-----------------SELF LOCATE BUTTON ON CLICK FUNCTION SECTION----------------------
        selfLocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasLocationPermissions()){
                    getCurrentLocation();
                }
                else{
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
            }
        });
        //-----------------SELF LOCATE BUTTON ON CLICK FUNCTION SECTION----------------------

        //--------------------DRIVE BUTTON ON CLICK FUNCTION SECTION------------------------
        driveButton.setOnClickListener(v -> {
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
                    double latitude = 0;
                    double longitude = 0;
                    boolean isOnline = false;
                    route = "Unknown";

                    latTextView.setText(String.valueOf(latitude));
                    longTextView.setText(String.valueOf(longitude));

                    updateUserData(latitude, longitude, isOnline, route);
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        });
        //--------------------DRIVE BUTTON ON CLICK FUNCTION SECTION------------------------

        /*final TextView textView = binding.textDashboard;
        seekViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

        return root;
    }
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
                latTextView.setText(String.valueOf(location.getLatitude()));
                longTextView.setText(String.valueOf(location.getLongitude()));
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
    }
    //-----------GET CURRENT LOCATION------------------------

    //--------LOCATION CALL BACK----------------------------
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                String route = seekRouteText.getText().toString().trim();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                boolean isOnline = true;

                latTextView.setText(String.valueOf(latitude));
                longTextView.setText(String.valueOf(longitude));

                //UPDATING USER DATA
                updateUserData(latitude, longitude, isOnline, route);
            }

        }
    };
    //--------LOCATION CALL BACK----------------------------

    @SuppressWarnings("unchecked")
    private void updateUserData(double latitude, double longitude, boolean isOnline, String route) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this.requireActivity(), "Unable to find user!", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap userData = new HashMap();
            userData.put("latitude", latitude);
            userData.put("longitude", longitude);
            userData.put("is_online", isOnline);
            userData.put("route", route);
            String userId = firebaseUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Drivers");

            databaseReference.child(userId).updateChildren(userData).addOnCompleteListener((OnCompleteListener<Void>) task -> {
                if(task.isSuccessful()){

                } else {
                    Toast.makeText(requireActivity(), "Can't Complete the task!", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}