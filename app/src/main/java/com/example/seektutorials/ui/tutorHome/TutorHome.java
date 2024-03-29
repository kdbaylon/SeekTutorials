package com.example.seektutorials.ui.tutorHome;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.seektutorials.R;
import com.example.seektutorials.ui.login.LoginActivity;
import com.example.seektutorials.ui.chat.MessagesFragment;
import com.example.seektutorials.ui.tutorHome.profile.EditTutorProfileFragment;
import com.example.seektutorials.ui.tutorHome.profile.TutorProfileFragment;
import com.example.seektutorials.ui.tutorHome.bookings.TutorBookingsFragment;
import com.example.seektutorials.ui.tutorHome.reviews.TutorReviewsFragment;
import com.example.seektutorials.ui.tutorHome.subjects.TutorAddSubjectFragment;
import com.example.seektutorials.ui.tutorHome.subjects.TutorSubjectsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;


public class TutorHome extends AppCompatActivity {
    String uid;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializing firebase auth object
        mAuth = FirebaseAuth.getInstance();
        //get user
        uid = mAuth.getCurrentUser().getUid();
        setContentView(R.layout.activity_tutor_home);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        openFragment(new TutorProfileFragment());
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile_navigation:
                        openFragment(new TutorProfileFragment());
                        return true;
                    case R.id.subjects_navigation:
                        openFragment(new TutorSubjectsFragment());
                        return true;
                    case R.id.reviews_navigation:
                        openFragment(new TutorReviewsFragment());
                        return true;
                    case R.id.requests_navigation:
                        openFragment(new TutorBookingsFragment());
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

    public void logOutTutor(MenuItem item) {
        Intent intent = new Intent(TutorHome.this, LoginActivity.class);
        startActivity(intent);
        TutorHome.this.finish();
        FirebaseAuth.getInstance().signOut();
    }
    public void editProfile(MenuItem item) {
        openFragment(new EditTutorProfileFragment());
    }
    public void backButton(View view) {
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }
    public void addSubject(View view) { openFragment(new TutorAddSubjectFragment());}
    public void setSupportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }
}