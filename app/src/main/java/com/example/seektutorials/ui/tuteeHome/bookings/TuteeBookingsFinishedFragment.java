package com.example.seektutorials.ui.tuteeHome.bookings;

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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.seektutorials.R;
import com.example.seektutorials.ui.tuteeHome.reviews.TuteeAddAReviewFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Tutee bookings finished fragment
 */
public class TuteeBookingsFinishedFragment extends Fragment{
    public final int REVIEWED = 0;
    public final int NOT_REVIEWED = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userUid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutee_bookings_finished, null);
        // taking FirebaseAuth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userUid = mAuth.getCurrentUser().getUid();
        //get recyclerview
        RecyclerView bookingsRecyclerView = view.findViewById(R.id.bookings);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookingsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Query query = db.collection("users").document(userUid).collection("bookings").whereEqualTo("status", "finished");
        //show finished bookings
        FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>().setQuery(query, Session.class).build();
        final FirestoreRecyclerAdapter<Session, TuteeBookingsFinishedFragment.BookingsViewHolder> adapter = new FirestoreRecyclerAdapter<Session, TuteeBookingsFinishedFragment.BookingsViewHolder>(options) {
            @Override
            public void onBindViewHolder(TuteeBookingsFinishedFragment.BookingsViewHolder holder, int position, Session model) {
                holder.setFname(model.getUid());
                holder.setLname(model.getUid());
                holder.setSubject(model.getSubject());
                holder.setSched(model.getSched());
                holder.setFee(model.getFee());
                holder.setProfilepic(model.getUid());
                final String subjUUID = model.getSubject();
                final String tutorUID = model.getUid();
                final String bookingUUID = model.getBookingUUID();
                if(!model.isReviewed()){
                    holder.rateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DialogFragment reviewFragment= TuteeAddAReviewFragment.newInstance(subjUUID,tutorUID,bookingUUID);
                            reviewFragment.show(getActivity().getSupportFragmentManager(),"Review");
                        }
                    });
                }
            }

            @Override
            public TuteeBookingsFinishedFragment.BookingsViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
                if(viewType == REVIEWED){
                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.booking_no_button, group, false);
                    return new TuteeBookingsFinishedFragment.BookingsViewHolder(view);
                }else{
                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.booking_tutee_finished, group, false);
                    return new TuteeBookingsFinishedFragment.BookingsViewHolder(view);
                }
            }
            @Override
            public int getItemViewType(int position) {
                if(getItem(position).isReviewed())
                    return REVIEWED;
                else
                    return NOT_REVIEWED;
            }



        };
        //make adapter listen so it updates
        adapter.startListening();
        bookingsRecyclerView.setAdapter(adapter);

        return view;
    }

    public class BookingsViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, fname, lname, sched, fee;
        public Button rateButton;
        public CircleImageView profilepic;

        public BookingsViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            fname = itemView.findViewById(R.id.fname);
            lname = itemView.findViewById(R.id.lname);
            sched = itemView.findViewById(R.id.sched);
            fee = itemView.findViewById(R.id.fee);
            profilepic = itemView.findViewById(R.id.profilepic);
            rateButton = itemView.findViewById(R.id.rateButton);
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

