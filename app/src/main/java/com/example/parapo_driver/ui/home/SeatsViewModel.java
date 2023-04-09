package com.example.parapo_driver.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.parapo_driver.ui.signup.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SeatsViewModel extends ViewModel {
    private int seat1, seat2, seat3, seat4, seat5, seat6, seat7, seat8, seat9, seat10;
    private FirebaseUser firebaseUser;
    private UserData setUserData;
    String message;

    public void checkFirebaseUser(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            message = "User can't be found!";
        }
        else {
            getUserData();
        }
    }

    private void getUserData() {
        String userId = firebaseUser.getUid(); //GET UNIQUE USER ID IN FIREBASE

        //GET THE DRIVERS REFERENCE
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Drivers");
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setUserData = snapshot.getValue(UserData.class);
                if (setUserData != null) {
                    seat1 = setUserData.seat_1;
                    seat2 = setUserData.seat_2;
                    seat3 = setUserData.seat_3;
                    seat4 = setUserData.seat_4;
                    seat5 = setUserData.seat_5;
                    seat6 = setUserData.seat_6;
                    seat7 = setUserData.seat_7;
                    seat8 = setUserData.seat_8;
                    seat9 = setUserData.seat_9;
                    seat10 = setUserData.seat_10;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public SeatsViewModel() {
        checkFirebaseUser();
    }

    public int getSeat1() {
        return seat1;
    }

    public int getSeat2() {
        return seat2;
    }

    public int getSeat3() {
        return seat3;
    }

    public int getSeat4() {
        return seat4;
    }

    public int getSeat5() {
        return seat5;
    }

    public int getSeat6() {
        return seat6;
    }

    public int getSeat7() {
        return seat7;
    }

    public int getSeat8() {
        return seat8;
    }

    public int getSeat9() {
        return seat9;
    }

    public int getSeat10() {
        return seat10;
    }
}