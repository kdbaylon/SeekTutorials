package com.example.seektutorials.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Tutor sign up fragment
 */
public class TutorSignUpFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextInputEditText fnameEditText, lnameEditText, emailEditText, passwordEditText, confirmPasswordEditText, schoolEditText, courseEditText, locationEditText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tutor_sign_up,null);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //getting input
        Button register = view.findViewById(R.id.registerButton);
        fnameEditText=view.findViewById(R.id.first_name);
        lnameEditText=view.findViewById(R.id.last_name);
        emailEditText=view.findViewById(R.id.email);
        passwordEditText=view.findViewById(R.id.password);
        confirmPasswordEditText=view.findViewById(R.id.confirm_password);
        schoolEditText=view.findViewById(R.id.school);
        courseEditText=view.findViewById(R.id.course);
        locationEditText=view.findViewById(R.id.location);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTutor(view);
            }
        });
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!passwordEditText.getText().toString().equals(editable.toString())){
                    confirmPasswordEditText.setError("Passwords do not match!");
                }
            }
        });

        return view;
    }
    public boolean isEmail(TextInputEditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public void registerTutor(View view){
        // Take the values
        final String fname, lname, email, password, confirmPassword, school, course, location;
        fname = fnameEditText.getText().toString();
        lname = lnameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        confirmPassword = confirmPasswordEditText.getText().toString();
        school = schoolEditText.getText().toString();
        course = courseEditText.getText().toString();
        location = locationEditText.getText().toString();
        //errors
        if (TextUtils.isEmpty(fname)) {
            fnameEditText.setError("This is required");
            return;
        }
        if (TextUtils.isEmpty(lname)) {
            lnameEditText.setError("This is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("This is required");
            return;
        }
        if (!isEmail(emailEditText)) {
            emailEditText.setError("Enter valid email!");
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("This is required");
            return;
        }
        if ((password.length()<8)) {
            passwordEditText.setError("Password must be 8 characters long");
            return;
        }
        if (TextUtils.isEmpty(school)) {
            schoolEditText.setError("This is required");
            return;
        }
        if (TextUtils.isEmpty(course)) {
            courseEditText.setError("This is required");
            return;
        }
        if (TextUtils.isEmpty(location)) {
            locationEditText.setError("This is required");
            return;
        }
        final Map<String, Object> user = new HashMap<>();
        user.put("userType","tutor");
        user.put("fname", fname);
        user.put("lname", lname);
        user.put("email", email);
        user.put("school", school);
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
                    startActivity(new Intent(getActivity(), LoginActivity.class));

                  } else {
                      // If sign in fails, display a message to the user.
                      Toast.makeText(getActivity(), "Registration failed.", Toast.LENGTH_SHORT).show();
                  }
              }
           });

    }
}