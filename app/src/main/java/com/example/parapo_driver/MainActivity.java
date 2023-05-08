package com.example.parapo_driver;

import android.os.Bundle;
import android.widget.Toast;

import com.example.parapo_driver.ui.signup.UserData;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.parapo_driver.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.parapo_driver.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        BadgeDrawable badgeDrawable = navView.getOrCreateBadge(R.id.navigation_seek);
        getPassengerCount(badgeDrawable);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_seats, R.id.navigation_seek, R.id.navigation_self)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void getPassengerCount(BadgeDrawable badgeDrawable) {
        List<UserData> list = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Travelers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int onlineCount = 0;
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    if (userData != null && userData.is_online) {
                        list.add(userData);
                        onlineCount++;
                    }
                }
                if (onlineCount == 0) {
                    badgeDrawable.setVisible(false);
                }
                else {
                    badgeDrawable.setVisible(true);
                    badgeDrawable.setNumber(onlineCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to get the passenger count!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}