package com.example.seektutorials.ui.googleRegister;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.seektutorials.R;
import com.example.seektutorials.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SignUpGoogle extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        final String emailAdd = getIntent().getExtras().getString("emailAdd");
        final String name = getIntent().getExtras().getString("name");
        Fragment nextFrag= GoogleTutorSignUpFragment.newInstance(emailAdd,name);
        openFragment(nextFrag);
        //bottom nav
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tutor_navigation:
                        Fragment nextFrag= GoogleTutorSignUpFragment.newInstance(emailAdd,name);
                        openFragment(nextFrag);
                        return true;
                    case R.id.tutee_navigation:
                        Fragment nextFrag2= GoogleTuteeSignUpFragment.newInstance(emailAdd,name);
                        openFragment(nextFrag2);
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
        Intent intent = new Intent(SignUpGoogle.this, LoginActivity.class);
        startActivity(intent);
    }



}