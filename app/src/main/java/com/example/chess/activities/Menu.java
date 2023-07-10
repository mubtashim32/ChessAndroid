package com.example.chess.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.chess.R;
import com.example.chess.menu_fragments.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Menu extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Play play = new Play();
    Ranks ranks = new Ranks();
    Watch watch = new Watch();
    Profile profile = new Profile();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    public static boolean watchGameRunning = false, watchGameFinished = false;
    public static boolean watchProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(Menu.this, Login.class));
        }
        setContentView(R.layout._menu);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, play).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.play:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, play).commit();
                        return true;
                    case R.id.ranks:
                        watchProfile = true;
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, ranks).commit();
                        return true;
                    case R.id.watch:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, watch).commit();
                        return true;
                    case R.id.profile:
                        watchProfile = false;
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profile).commit();
                        return true;
                }
                return false;
            }
        });
    }
}