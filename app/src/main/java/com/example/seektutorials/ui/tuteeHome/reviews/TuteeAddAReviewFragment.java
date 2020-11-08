package com.example.seektutorials.ui.tuteeHome.reviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.seektutorials.R;
import com.example.seektutorials.ui.tuteeHome.bookings.TuteeBookSessionFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TuteeAddAReviewFragment extends DialogFragment  {
    String subjUUID,tutorUID,bookingUUID;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userUid;
    private TextView fnameTextView, lnameTextView, subjTextView;
    private EditText commentEditText;
    private RatingBar ratingBar;
    public static TuteeAddAReviewFragment newInstance(String string, String string2, String string3) {
        Bundle bundle = new Bundle();
        bundle.putString("subjUUID", string);
        bundle.putString("tutorUID", string2);
        bundle.putString("bookingUUID",string3);
        TuteeAddAReviewFragment fragment = new TuteeAddAReviewFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get bundle
        Bundle bundle = getArguments();
        if(bundle!=null){
            subjUUID = bundle.getString("subjUUID");
            tutorUID = bundle.getString("tutorUID");
            bookingUUID = bundle.getString("bookingUUID");
        }
        LayoutInflater i = getActivity().getLayoutInflater();
        View view = i.inflate(R.layout.tutee_review_tutor, null);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        userUid = mAuth.getCurrentUser().getUid();
        //get layout
        fnameTextView = view.findViewById(R.id.fname);
        lnameTextView = view.findViewById(R.id.lname);
        subjTextView = view.findViewById(R.id.subject_layout);
        commentEditText = view.findViewById(R.id.comment);
        ratingBar = view.findViewById(R.id.rating_bar);

        AlertDialog.Builder b=  new  AlertDialog.Builder(getActivity())
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int whichButton) {
                                //errors
                                if (commentEditText.equals("") ) {
                                    Toast.makeText(getActivity(), "Write a review", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Float rate = ratingBar.getRating();
                                String ratingUUID= UUID.randomUUID().toString();
                                //map to a hashmap
                                final Map<String, Object> rating =new HashMap<>();
                                rating.put("ratingUUID",ratingUUID);
                                rating.put("tuteeUID",userUid);
                                rating.put("subject",subjTextView.getText().toString());
                                rating.put("rate",rate);
                                rating.put("comment",commentEditText.getText().toString());

                                //add to ratings of the tutor
                                db.collection("users").document(tutorUID).collection("reviews").document(ratingUUID)
                                        .set(rating)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Document written failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                //update status of tutoring session to reviewed
                                final Map<String,Object> update=new HashMap<>();
                                update.put("reviewed",true);
                                db.collection("users").document(tutorUID).collection("bookings").document(bookingUUID)
                                        .update(update)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Update failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                db.collection("users").document(userUid).collection("bookings").document(bookingUUID)
                                        .update(update)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Updated.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Update failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        //populate textviews
        DocumentReference ref =db.collection("subjects").document(subjUUID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //get text
                        String subject = document.getString("name");
                        //set text
                        subjTextView.setText(subject);
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DocumentReference ref2 =db.collection("users").document(tutorUID);
        ref2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //get text
                        String fname =document.getString("fname");
                        String lname =document.getString("lname");

                        //set text
                        fnameTextView.setText(fname);
                        lnameTextView.setText(lname);

                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b.setView(view);
        return b.create();
    }
    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03989E"));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03989E"));
    }

}
