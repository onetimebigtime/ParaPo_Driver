package com.example.parapo_driver.ui.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parapo_driver.MainActivity;
import com.example.parapo_driver.R;
import com.example.parapo_driver.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    //INITIALIZING COMPONENTS
    private TextView cancelTextView;
    private EditText signupFullNameText, signupEmailAddressText, signupPlateNumberText,signupPasswordText, signupConfirmText;
    private int seat_1, seat_2, seat_3, seat_4, seat_5, seat_6, seat_7, seat_8, seat_9, seat_10;
    private double latitude, longitude;
    private String full_name, email_address, plate_number, password, confirm_password;
    private Boolean isOnline;
    private ProgressBar signupProgressBar;
    private static final String TAG = "SignUpFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //SET SIGN UP TITLE BAR
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign Up");

        Toast.makeText(SignUpActivity.this, "Sign Up now to avail our services!", Toast.LENGTH_SHORT).show();

        //SET UP TEXT BOXES
        signupFullNameText = findViewById(R.id.signup_fullname_text);
        signupEmailAddressText = findViewById(R.id.signup_email_text);
        signupPlateNumberText = findViewById(R.id.signup_plate_number_text);
        signupPasswordText = findViewById(R.id.signup_password_text);
        signupConfirmText = findViewById(R.id.confirmpass_text);

        cancelTextView = findViewById(R.id.signup_cancel_link);

        //SET UP PROGRESS BAR
        signupProgressBar = findViewById(R.id.signup_progressbar);

        //SET UP BUTTON
        Button signupButton = findViewById(R.id.signup_button);

        //-----------------------------SIGN UP BUTTON ON CLICK FUNCTION SECTION------------------------------------------
        signupButton.setOnClickListener(v -> {
            //SETTING UP SEAT,LAT, LONG VARIABLES VALUE TO 0
            seat_1 = seat_2 = seat_3 = seat_4 = seat_5 = seat_6 = seat_7 = seat_8 = seat_9 = seat_10 = 0;
            latitude = longitude = 0d;
            isOnline = false;

            //SET STRING VARIABLE VALUES ACCORDING TO TEXT BOXES INPUT
            full_name =signupFullNameText.getText().toString().trim();
            email_address =signupEmailAddressText.getText().toString().trim();
            plate_number =signupPlateNumberText.getText().toString().trim();
            password =signupPasswordText.getText().toString().trim();
            confirm_password =signupConfirmText.getText().toString().trim();

            //CHECK IF TEXT BOXES ARE EMPTY
            if(TextUtils.isEmpty(full_name)) {
                Toast.makeText(SignUpActivity.this, "Please provide a full name", Toast.LENGTH_SHORT).show();
                signupFullNameText.setError("Enter a full name!");
                signupFullNameText.requestFocus();
            }
            else if (TextUtils.isEmpty(email_address) || !Patterns.EMAIL_ADDRESS.matcher(email_address).matches()) {
                Toast.makeText(SignUpActivity.this, "Please provide an email address", Toast.LENGTH_SHORT).show();
                signupEmailAddressText.setError("Enter a valid email address!");
                signupEmailAddressText.requestFocus();
            } else if (TextUtils.isEmpty(plate_number)) {
                Toast.makeText(SignUpActivity.this, "Please provide a plate number", Toast.LENGTH_SHORT).show();
                signupPlateNumberText.setError("Enter a valid vehicle plate number");
                signupPlateNumberText.requestFocus();
            }
            else if(TextUtils.isEmpty(password) || password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Please provide a password", Toast.LENGTH_SHORT).show();
                signupPasswordText.setError("Enter a more than 6 character password!");
                signupPasswordText.requestFocus();
            }
            else if(TextUtils.isEmpty(confirm_password)) {
                Toast.makeText(SignUpActivity.this, "Please provide a password", Toast.LENGTH_SHORT).show();
                signupConfirmText.setError("Enter  a password to confirm!");
                signupConfirmText.requestFocus();
            }
            //CHECK IF PASSWORD IS THE SAME AS CONFIRM PASSWORD
            else if(!password.equals(confirm_password)) {
                Toast.makeText(SignUpActivity.this, "Please confirm pasword", Toast.LENGTH_SHORT).show();
                signupConfirmText.setError("Enter password for verification!");
                signupConfirmText.requestFocus();
                //ERASING TEXT IN THE CONFIRM TEXT BOX
                signupConfirmText.clearComposingText();
            }
            //NO ERROR PROCEED
            else {
                signupProgressBar.setVisibility(View.VISIBLE);
                //1--SIGNING UP DRIVER
                signInUser(full_name, email_address, plate_number, password,seat_1, seat_2, seat_3, seat_4, seat_5, seat_6, seat_7, seat_8, seat_9, seat_10, latitude, longitude, isOnline);
            }
        });
        //-----------------------------SIGN UP BUTTON ON CLICK FUNCTION SECTION------------------------------------------

        //-----------------------------CANCEL BUTTON ON CLICK FUNCTION SECTION------------------------------------------
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });
        //-----------------------------CANCEL BUTTON ON CLICK FUNCTION SECTION------------------------------------------
    }
    //------------------------SIGNING UP DRIVER FUNCTION SECTION----------------------------------------------------------
    private void signInUser(String full_name, String email_address, String plate_number, String password, int seat_1, int seat_2, int seat_3, int seat_4, int seat_5, int seat_6, int seat_7, int seat_8, int seat_9, int seat_10, double latitude, double longitude, Boolean isOnline) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //FUNCTION TO CREATE A USER USING EMAIL AND PASSWORD
        firebaseAuth.createUserWithEmailAndPassword(email_address, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //IF THE CREATION IS SUCCESSFUL AND COMPLETE
                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "Congrats! You are now a ParaPo Driver", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();//GET THE CURRENT USER
                    String userId = firebaseUser.getUid();

                    //SET USER DATA
                    UserData setUserData = new UserData(userId ,full_name, plate_number, seat_1, seat_2, seat_3, seat_4, seat_5, seat_6, seat_7, seat_8, seat_9, seat_10, latitude, longitude, isOnline);
                    //GETTING DRIVERS REFERENCE IN FIREBASE
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Drivers");
                    databaseReference.child(userId).setValue(setUserData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //firebaseUser.sendEmailVerification(); //SEND EMAIL VERIFICATION
                                //CREATING AN INTENT TO OPEN MAIN ACTIVITY
                                Intent intent =new Intent(SignUpActivity.this, MainActivity.class );

                                //Prevent user from returning back to register
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //END SIGNUP ACTIVITY
                            }
                            else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                //Set progressbar visibility TO DISAPPEAR
                                signupProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
                else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthUserCollisionException e) {
                        signupEmailAddressText.setError("A Traveler exist with this email!");
                        signupEmailAddressText.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    ///Set progressbar visibility TO DISAPPEAR
                    signupProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    //------------------------SIGNING UP DRIVER FUNCTION SECTION----------------------------------------------------------
}