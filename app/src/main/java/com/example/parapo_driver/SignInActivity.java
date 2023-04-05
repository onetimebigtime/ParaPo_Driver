package com.example.parapo_driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parapo_driver.ui.signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private TextView signinShowHideLabel;
    private EditText signinEmailText, signinPasswordText;
    private ProgressBar signinProgressBar;
    private FirebaseAuth firebaseAuth;

    private PopUpAlert popUpAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //TITLE BAR
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign In");

        //FIREBASE OBJECT
        firebaseAuth = FirebaseAuth.getInstance();

        //SETTING UP TEXT BOXES
        signinEmailText = findViewById(R.id.signin_email_text);
        signinPasswordText = findViewById(R.id.signin_password_text);
        signinShowHideLabel = findViewById(R.id.signin_showpass_label);

        //PROGRESS BAR
        signinProgressBar = findViewById(R.id.signin_progressbar);

        //BUTTONS
        Button signinButton = findViewById(R.id.signin_button);
        Button toSignupButton = findViewById(R.id.to_signup_button);

        //------------------SHOW HIDE PASSWORD FUNCTION SECTION------------------
        signinShowHideLabel.setText(R.string.show_pass);
        signinShowHideLabel.setOnClickListener(v -> {
            if (signinPasswordText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                signinPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                signinShowHideLabel.setText(R.string.show_pass);
            }
            else {
                signinPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                signinShowHideLabel.setText(R.string.hide_pass);
            }
        });
        //------------------SHOW HIDE PASSWORD FUNCTION SECTION------------------
        //-----------------TO SIGN UP BUTTON ON CLICK FUNCTION SECTION------------
        toSignupButton.setOnClickListener(v -> {
            try {
                //CREATING AN INTENT TO OPEN SIGN UP ACTIVITY
                Intent intent =new Intent(SignInActivity.this, SignUpActivity.class );

                //PREVENT USER FROM GOING BACK TO THE SIGN IN ACTIVITY
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); //END SIGN IN ACTIVITY
            } catch (Exception e){
                String title = "Something Went Wrong!";
                String message = "Something went wrong while we are loading sign up page. Please check your internet connection and try again.";
                popUpAlert = new PopUpAlert(title, message, this);
            }
        });
        //-----------------TO SIGN UP BUTTON ON CLICK FUNCTION SECTION------------
        //--------------SIGN IN BUTTON ON CLICK FUNCTION SECTION------------------
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signInEmail = signinEmailText.getText().toString();
                String signInPassword = signinPasswordText.getText().toString();
                //CHECKING IF TEXT BOXES HAVE ERROR VALUES
                if (TextUtils.isEmpty(signInEmail) || !Patterns.EMAIL_ADDRESS.matcher(signInEmail).matches()) {
                    Toast.makeText(SignInActivity.this, "Please provide your email", Toast.LENGTH_SHORT).show();
                    signinEmailText.setError("Enter a valid email address");
                    signinEmailText.requestFocus();
                }
                else if (TextUtils.isEmpty(signInPassword)) {
                    Toast.makeText(SignInActivity.this, "Please provide your correct password", Toast.LENGTH_SHORT).show();
                    signinPasswordText.setError("Enter a correct password");
                    signinPasswordText.requestFocus();
                }
                else {
                    signinProgressBar.setVisibility(View.VISIBLE);
                    //1--SIGNING IN USER FUNCTION
                    signInUser(signInEmail, signInPassword);
                }
            }
        });
        //--------------SIGN IN BUTTON ON CLICK FUNCTION SECTION------------------
    }

    //---------------SIGNING IN USER FUNCTION SECTION------------------------
    private void signInUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                }
                else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    }
                    catch (FirebaseAuthInvalidUserException e) {
                        String title = "Unknown Traveler";
                        String message = "Can't find Traveler account! Make sure that you have a valid account to use ParaPo.";
                        popUpAlert = new PopUpAlert(title, message, SignInActivity.this);
                        signinEmailText.setError("Traveler doesn't exist!");
                        signinEmailText.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        signinEmailText.setError("Invalid log in credential");
                        signinPasswordText.setError("Invalid log in credential");
                        signinEmailText.requestFocus();
                        signinPasswordText.requestFocus();
                    }
                    catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                signinProgressBar.setVisibility(View.GONE);
            }
        });
    }
    //---------------SIGNING IN USER FUNCTION SECTION------------------------
    //---------------ON START FUNCTION SECTION-----------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            Toast.makeText(SignInActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
        else {
            Toast.makeText(SignInActivity.this, "Login Now!", Toast.LENGTH_SHORT).show();
        }
    }

    //---------------ON START FUNCTION SECTION-----------------------------------------
}