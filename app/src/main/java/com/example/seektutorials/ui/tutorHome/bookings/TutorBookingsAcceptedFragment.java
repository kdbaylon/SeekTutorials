package com.example.seektutorials.ui.tutorHome.bookings;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.seektutorials.R;
import com.example.seektutorials.ui.tuteeHome.bookings.Session;
import com.example.seektutorials.ui.tuteeHome.bookings.TuteeBookingsAcceptedFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Tutor bookings accepted fragment
 */
public class TutorBookingsAcceptedFragment extends Fragment{
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userUid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutor_bookings_accepted, null);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        userUid = mAuth.getCurrentUser().getUid();
        //get recyclerview
        RecyclerView bookingsRecyclerView = view.findViewById(R.id.bookings);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookingsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Query query = db.collection("users").document(userUid).collection("bookings").whereEqualTo("status", "accepted");
        //show pending bookings
        FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>().setQuery(query, Session.class).build();
        final FirestoreRecyclerAdapter<Session, TutorBookingsAcceptedFragment.BookingsViewHolder> adapter = new FirestoreRecyclerAdapter<Session, TutorBookingsAcceptedFragment.BookingsViewHolder>(options) {
            @Override
            public void onBindViewHolder(TutorBookingsAcceptedFragment.BookingsViewHolder holder, int position, final Session model) {
                holder.setFname(model.getUid());
                holder.setLname(model.getUid());
                holder.setSubject(model.getSubject());
                holder.setSched(model.getSched());
                holder.setFee(model.getFee());
                holder.setProfilepic(model.getUid());
                holder.markAsFinishedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Map<String, Object> update =new HashMap<>();
                        update.put("status","finished");
                        update.put("reviewed",false);
                        //update status from pending to accepted
                        db.collection("users").document(userUid).collection("bookings").document(model.getBookingUUID())
                                .update(update)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("users").document(model.getUid()).collection("bookings").document(model.getBookingUUID())
                                                .update(update)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(),"Session marked as finished",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity(),"Fail to mark as finished",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(),"Fail to mark as finished",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

            }

            @Override
            public TutorBookingsAcceptedFragment.BookingsViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.booking_tutor_accepted, group, false);
                return new TutorBookingsAcceptedFragment.BookingsViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
            }

        };
        //make adapter listen so it updates
        adapter.startListening();
        bookingsRecyclerView.setAdapter(adapter);

        return view;
    }

    public class BookingsViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, fname, lname, sched, fee;
        public Button markAsFinishedButton;
        public CircleImageView profilepic;

        public BookingsViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            fname = itemView.findViewById(R.id.fname);
            lname = itemView.findViewById(R.id.lname);
            sched = itemView.findViewById(R.id.sched);
            fee = itemView.findViewById(R.id.fee);
            profilepic = itemView.findViewById(R.id.profilepic);
            markAsFinishedButton = itemView.findViewById(R.id.markAsFinishedButton);
        }

        public void setFname(String string) {
            DocumentReference reference = db.collection("users").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            //get text
                            String fnamee = document.getString("fname");
                            fname.setText(fnamee);
                        } else {
                            Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void setLname(String string) {
            DocumentReference reference = db.collection("users").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            //get text
                            String lnamee = document.getString("lname");
                            lname.setText(lnamee);
                        } else {
                            Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void setSubject(String string) {
            DocumentReference reference = db.collection("subjects").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            //get text
                            String subj = document.getString("name");
                            subject.setText(subj);
                        } else {
                            Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void setSched(String string) {
            sched.setText(string);
        }

        public void setFee(String string) {
            fee.setText(string);
        }

        public void setProfilepic(String string) {
            // get the Firebase  storage reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + string);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //load img using glide
                    Glide.with(getActivity()).load(uri.toString()).placeholder(R.drawable.round_account_circle_24).dontAnimate().into(profilepic);
                }
            });

        }
    }

}

