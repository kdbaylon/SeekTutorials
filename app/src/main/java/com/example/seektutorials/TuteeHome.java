package com.example.seektutorials;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import com.example.seektutorials.ui.login.LoginActivity;
import com.example.seektutorials.ui.tuteeHome.TuteeMessagesFragment;
import com.example.seektutorials.ui.tuteeHome.TuteeProfileFragment;
import com.example.seektutorials.ui.tuteeHome.TuteeBookingsFragment;
import com.example.seektutorials.ui.tuteeHome.search.TuteeSearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class TuteeHome extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutee_home);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        openFragment(new TuteeSearchFragment());
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.search_navigation:
                        openFragment(new TuteeSearchFragment());
                        return true;
                    case R.id.profile_navigation:
                        openFragment(new TuteeProfileFragment());
                        return true;
                    case R.id.requests_navigation:
                        openFragment(new TuteeBookingsFragment());
                        return true;
                    case R.id.messages_navigation:
                        openFragment(new TuteeMessagesFragment());
                        return true;
                }
                return false;
            }
        });

    }

    void openFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void logOut(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(TuteeHome.this, LoginActivity.class);
        startActivity(intent);
        TuteeHome.this.finish();
    }
}