package com.example.seektutorials.ui.tuteeHome.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.seektutorials.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Tutor profile fragment
 */
public class TuteeProfileFragment extends Fragment {
    //Firebase
    //storage
    private StorageReference storageReference;
    private FirebaseStorage storage;
    //auth
    private FirebaseAuth mAuth;
    //firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    CircleImageView imageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutee_profile, null);
        //get layout addresses
        imageView = view.findViewById(R.id.profilepic);
        final TextView fnameTextView = view.findViewById(R.id.fname);
        final TextView lnameTextView = view.findViewById(R.id.lname);
        final TextView locationTextView = view.findViewById(R.id.loc);
        final TextView courseTextView = view.findViewById(R.id.course);
        final TextView emailTextView = view.findViewById(R.id.email);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("images/"+uid);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri){
                //load img using glide
                Glide.with(getActivity()).load(uri.toString()).placeholder(R.drawable.round_account_circle_24).dontAnimate().into(imageView);
            }
        });

        DocumentReference docRef = db.collection("users").document(uid);
        //get info from firestore document
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //get text
                        String fname =document.getString("fname");
                        String lname =document.getString("lname");
                        String email =document.getString("email");
                        String course =document.getString("course");
                        String location =document.getString("location");
                        //set text
                        fnameTextView.setText(fname);
                        lnameTextView.setText(lname);
                        emailTextView.setText(email);
                        courseTextView.setText(course);
                        locationTextView.setText(location);
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}