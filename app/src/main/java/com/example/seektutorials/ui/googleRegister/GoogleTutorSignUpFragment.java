package com.example.seektutorials.ui.googleRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seektutorials.R;
import com.example.seektutorials.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Tutor sign up fragment
 */
public class GoogleTutorSignUpFragment extends Fragment {
    private String emailAdd, name;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextInputEditText fnameEditText, lnameEditText, schoolEditText, courseEditText, locationEditText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    public static GoogleTutorSignUpFragment newInstance(String string, String string2) {
        Bundle bundle = new Bundle();
        bundle.putString("emailAdd", string);
        bundle.putString("name",string2);
        GoogleTutorSignUpFragment fragment = new GoogleTutorSignUpFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.google_tutor_sign_up,null);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //getting input
        Button register = view.findViewById(R.id.registerButton);
        fnameEditText=view.findViewById(R.id.first_name);
        lnameEditText=view.findViewById(R.id.last_name);
        schoolEditText=view.findViewById(R.id.school);
        courseEditText=view.findViewById(R.id.course);
        locationEditText=view.findViewById(R.id.location);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTutor(view);
            }
        });
        //get bundle
        Bundle bundle = getArguments();
        if(bundle!=null){
            emailAdd = bundle.getString("emailAdd");
            name = bundle.getString("name");
        }
        fnameEditText.setText(name);
        return view;
    }
    public void registerTutor(View view){
        // Take the values
        final String fname, lname, school, course, location;
        fname = fnameEditText.getText().toString();
        lname = lnameEditText.getText().toString();
        school = schoolEditText.getText().toString();
        course = courseEditText.getText().toString();
        location = locationEditText.getText().toString();
        //errors
        if (TextUtils.isEmpty(fname)) {
            Toast.makeText(getActivity(), "Enter first name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(lname)) {
            Toast.makeText(getActivity(), "Enter last name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(school)) {
            Toast.makeText(getActivity(), "Enter school!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(course)) {
            Toast.makeText(getActivity(), "Enter course!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(getActivity(), "Enter city!", Toast.LENGTH_SHORT).show();
            return;
        }
        final Map<String, Object> user = new HashMap<>();
        user.put("userType","tutor");
        user.put("fname", fname);
        user.put("lname", lname);
        user.put("email", emailAdd);
        user.put("school", school);
        user.put("course", course);
        user.put("location", location);

        uid=mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Document written.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Document written failed.", Toast.LENGTH_SHORT).show();
                    }
                });
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }
}