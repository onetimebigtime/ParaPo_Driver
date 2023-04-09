package com.example.parapo_driver.ui.notifications;

import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.parapo_driver.ui.signup.UserData;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelfViewModel extends ViewModel {
    private final MutableLiveData<String> fullNameTitle, fullName, emailAddress, plateNumber;

    private FirebaseUser firebaseUser;
    private UserData setUserData;
    private String message;
    private boolean isFinished;
    private boolean isThere;

    public void checkFirebaseUser(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            message = "User can't be found!";
            isThere = false;
        }
        else {
            isThere = true;
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
                    fullNameTitle.setValue(setUserData.full_name);
                    fullName.setValue(setUserData.full_name);
                    emailAddress.setValue(firebaseUser.getEmail());
                    plateNumber.setValue(setUserData.plate_number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        isFinished = true;
    }

    public SelfViewModel() {
        fullNameTitle = new MutableLiveData<>();
        fullName = new MutableLiveData<>();
        emailAddress = new MutableLiveData<>();
        plateNumber = new MutableLiveData<>();
        isFinished = false;
        checkFirebaseUser();
    }

    public LiveData<String> getFullNameTitle() {
        return fullNameTitle;
    }

    public LiveData<String> getFullName() {
        return fullName;
    }

    public LiveData<String> getEmailAddress() {
        return emailAddress;
    }

    public LiveData<String> getPlateNumber() {
        return plateNumber;
    }
    public String getMessage() {return message;}
    public boolean isFinished() {return isFinished;}
    public boolean isThere() {return isThere;}
}