package com.example.seektutorials.ui.tuteeHome;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.seektutorials.R;
import com.example.seektutorials.ui.chat.MessagesFragment;
import com.example.seektutorials.ui.login.LoginActivity;
import com.example.seektutorials.ui.tuteeHome.profile.EditTuteeProfileFragment;
import com.example.seektutorials.ui.tuteeHome.profile.TuteeProfileFragment;
import com.example.seektutorials.ui.tuteeHome.bookings.TuteeBookingsFragment;
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
                        openFragment(new MessagesFragment());
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
    public void logOutTutee(MenuItem item) {
        Intent intent = new Intent(TuteeHome.this, LoginActivity.class);
        TuteeHome.this.finish();
        startActivity(intent);
        FirebaseAuth.getInstance().signOut();
    }
    public void editProfile(MenuItem item) {
        openFragment(new EditTuteeProfileFragment());
    }
    public void backButton(View view) {
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }


    public void setSupportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }
}