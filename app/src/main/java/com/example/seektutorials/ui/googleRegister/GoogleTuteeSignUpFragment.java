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
import com.example.seektutorials.ui.tuteeHome.TuteeHome;
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
public class GoogleTuteeSignUpFragment extends Fragment {
    private String emailAdd, name;
    private FirebaseAuth mAuth;
    private TextInputEditText fnameEditText, lnameEditText, courseEditText, locationEditText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    public static GoogleTuteeSignUpFragment newInstance(String string, String string2) {
        Bundle bundle = new Bundle();
        bundle.putString("emailAdd", string);
        bundle.putString("name",string2);
        GoogleTuteeSignUpFragment fragment = new GoogleTuteeSignUpFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.google_tutee_sign_up, null);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        //getting input
        Button register = view.findViewById(R.id.registerButton);
        fnameEditText=view.findViewById(R.id.first_name);
        lnameEditText=view.findViewById(R.id.last_name);
        courseEditText=view.findViewById(R.id.course);
        locationEditText=view.findViewById(R.id.location);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTutee(view);
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
    public void registerTutee(View view){
        // Take the values
        String fname, lname, email, course, location;
        fname = fnameEditText.getText().toString();
        lname = lnameEditText.getText().toString();
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
        user.put("email", emailAdd);
        user.put("course", course);
        user.put("location", location);
        //register
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



    }
}