package com.example.seektutorials;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


import com.example.seektutorials.ui.login.LoginActivity;
import com.example.seektutorials.ui.register.TuteeSignUpFragment;
import com.example.seektutorials.ui.register.TutorSignUpFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;


public class SignUp extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        openFragment(new TutorSignUpFragment());


        //bottom nav
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tutor_navigation:
                        openFragment(new TutorSignUpFragment());
                        return true;
                    case R.id.tutee_navigation:
                        openFragment(new TuteeSignUpFragment());
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
    public void backButton(View view){
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
    }



}