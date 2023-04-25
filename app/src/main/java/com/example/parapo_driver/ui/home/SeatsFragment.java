package com.example.parapo_driver.ui.home;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parapo_driver.R;
import com.example.parapo_driver.databinding.FragmentSeatsBinding;
import com.example.parapo_driver.ui.signup.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SeatsFragment extends Fragment {

    private FragmentSeatsBinding binding;
    public static final String TAG = "SeatFragment";

    private TextView seat1, seat2, seat3, seat4, seat5, seat6, seat7, seat8, seat9, seat10;

    private EditText routeText;
    private FirebaseUser firebaseUser;
    private UserData setUserData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SeatsViewModel seatsViewModel =
                new ViewModelProvider(this).get(SeatsViewModel.class);

        binding = FragmentSeatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button goButton = root.findViewById(R.id.go_button);
        routeText = root.findViewById(R.id.seat_route_text);

        seat1 = binding.seat1Label;
        seat2 = binding.seat2Label;
        seat3 = binding.seat3Label;
        seat4 = binding.seat4Label;
        seat5 = binding.seat5Label;
        seat6 = binding.seat6Label;
        seat7 = binding.seat7Label;
        seat8 = binding.seat8Label;
        seat9 = binding.seat9Label;
        seat10 = binding.seat10Label;
        checkFirebaseUser();

        goButton.setOnClickListener(v -> {
            String route = routeText.getText().toString().trim();
            updateRoute(route);
        });

        /*final TextView textView = binding.textHome;
        seatsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    //-------------------CHECK IF USER IS EMPTY IN THE FIREBASE DATABASE---------------------
    public void checkFirebaseUser(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this.requireActivity(), "User data is missing!", Toast.LENGTH_SHORT).show();
        }
        else {
            getUserData();
        }
    }
    //-------------------CHECK IF USER IS EMPTY IN THE FIREBASE DATABASE---------------------

    //-------------------GET USER DATA IN THE FIREBASE DATABASE---------------------
    private void getUserData() {
        String userId = firebaseUser.getUid(); //GET UNIQUE USER ID IN FIREBASE

        //GET THE DRIVERS REFERENCE
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Drivers");
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setUserData = snapshot.getValue(UserData.class);
                if (setUserData != null) {
                    int color_0 = ContextCompat.getColor(SeatsFragment.this.requireActivity(),R.color.seat_color_0);
                    int color_1 = ContextCompat.getColor(SeatsFragment.this.requireActivity(),R.color.seat_color_1);
                    seatOne(setUserData.seat_1, color_0, color_1);
                    seatTwo(setUserData.seat_2, color_0, color_1);
                    seatThree(setUserData.seat_3, color_0, color_1);
                    seatFour(setUserData.seat_4, color_0, color_1);
                    seatFive(setUserData.seat_5, color_0, color_1);
                    seatSix(setUserData.seat_6, color_0, color_1);
                    seatSeven(setUserData.seat_7, color_0, color_1);
                    seatEight(setUserData.seat_8, color_0, color_1);
                    seatNine(setUserData.seat_9, color_0, color_1);
                    seatTen(setUserData.seat_10, color_0, color_1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //-------------------GET USER DATA IN THE FIREBASE DATABASE---------------------

    //-------------------CONTROL SEAT COLORS AND HANDLE UPDATES---------------------
    private void seatOne(int seat_1, int color_0, int color_1) {
        if (seat_1 == 1){
            seat1.setBackgroundColor(color_1);
        }
        else {
            seat1.setBackgroundColor(color_0);
        }
    }
    private void seatTwo(int seat_2, int color_0, int color_1) {
        if (seat_2 == 1){
            seat2.setBackgroundColor(color_1);
        }
        else {
            seat2.setBackgroundColor(color_0);
        }
    }
    private void seatThree(int seat_3, int color_0, int color_1) {
        if (seat_3 == 1){
            seat3.setBackgroundColor(color_1);
        }
        else {
            seat3.setBackgroundColor(color_0);
        }
    }

    private void seatFour(int seat_4, int color_0, int color_1) {
        if (seat_4 == 1){
            seat4.setBackgroundColor(color_1);
        }
        else {
            seat4.setBackgroundColor(color_0);
        }
    }

    private void seatFive(int seat_5, int color_0, int color_1) {
        if (seat_5 == 1){
            seat5.setBackgroundColor(color_1);
        }
        else {
            seat5.setBackgroundColor(color_0);
        }
    }

    private void seatSix(int seat_6, int color_0, int color_1) {
        if (seat_6 == 1){
            seat6.setBackgroundColor(color_1);
        }
        else {
            seat6.setBackgroundColor(color_0);
        }
    }

    private void seatSeven(int seat_7, int color_0, int color_1) {
        if (seat_7 == 1){
            seat7.setBackgroundColor(color_1);
        }
        else {
            seat7.setBackgroundColor(color_0);
        }
    }

    private void seatEight(int seat_8, int color_0, int color_1) {
        if (seat_8 == 1){
            seat8.setBackgroundColor(color_1);
        }
        else {
            seat8.setBackgroundColor(color_0);
        }
    }

    private void seatNine(int seat_9, int color_0, int color_1) {
        if (seat_9 == 1){
            seat9.setBackgroundColor(color_1);
        }
        else {
            seat9.setBackgroundColor(color_0);
        }
    }

    private void seatTen(int seat_10, int color_0, int color_1) {
        if (seat_10 == 1){
            seat10.setBackgroundColor(color_1);
        }
        else {
            seat10.setBackgroundColor(color_0);
        }
    }

    public void updateRoute(String route){
        try {
            if (TextUtils.isEmpty(route)){
                routeText.setError("Please enter your route!");
                routeText.requestFocus();
            }

            else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    return;
                }
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Drivers").child(user.getUid());
                ref.child("route").setValue(route);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to update online status", e);
            Toast.makeText(requireActivity(), "Failed to update online status!", Toast.LENGTH_SHORT).show();
        }

    }
    //-------------------CONTROL SEAT COLORS AND HANDLE UPDATES---------------------
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}