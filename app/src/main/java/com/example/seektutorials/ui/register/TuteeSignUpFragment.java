package com.example.seektutorials.ui.register;

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
import com.example.seektutorials.TuteeHome;
import com.example.seektutorials.TutorHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Tutee sign up fragment
 */
public class TuteeSignUpFragment extends Fragment {
    private FirebaseAuth mAuth;
    private TextInputEditText fnameEditText, lnameEditText, emailEditText, passwordEditText, confirmPasswordEditText, courseEditText, locationEditText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tutee_sign_up, null);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        //getting input
        Button register = view.findViewById(R.id.registerButton);
        fnameEditText=view.findViewById(R.id.first_name);
        lnameEditText=view.findViewById(R.id.last_name);
        emailEditText=view.findViewById(R.id.email);
        passwordEditText=view.findViewById(R.id.password);
        confirmPasswordEditText=view.findViewById(R.id.confirm_password);
        courseEditText=view.findViewById(R.id.course);
        locationEditText=view.findViewById(R.id.location);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTutee(view);
            }
        });
        return view;
    }
    public void registerTutee(View view){
        // Take the values
        String fname, lname, email, password, confirmPassword, course, location;
        fname = fnameEditText.getText().toString();
        lname = lnameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        confirmPassword = confirmPasswordEditText.getText().toString();
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
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!(password.equals(confirmPassword))){
            Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
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
        user.put("userType","tutee");
        user.put("fname", fname);
        user.put("lname", lname);
        user.put("email", email);
        user.put("course", course);
        user.put("location", location);


        //register
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
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
                            startActivity(new Intent(getActivity(), TuteeHome.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });



    }
}