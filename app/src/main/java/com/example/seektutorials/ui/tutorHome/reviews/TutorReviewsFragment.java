package com.example.seektutorials.ui.tutorHome.reviews;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.example.seektutorials.ui.tutorHome.subjects.Subject;
import com.example.seektutorials.ui.tutorHome.subjects.TutorSubjectsFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Tutor reviews fragment
 */
public class TutorReviewsFragment extends Fragment {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutor_reviews, null);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //get layout addresses
        final TextView firstNameTextView = view.findViewById(R.id.fname);
        final TextView lastNameTextView = view.findViewById(R.id.lname);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
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
                        //set text
                        firstNameTextView.setText(fname);
                        lastNameTextView.setText(lname);
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //recyclerview for reviews
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Query query = db.collection("users").document(uid).collection("reviews").limit(10);
        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>().setQuery(query, Review.class).build();

        final FirestoreRecyclerAdapter<Review, TutorReviewsFragment.TutorReviewCardViewHolder> adapter = new FirestoreRecyclerAdapter<Review, TutorReviewsFragment.TutorReviewCardViewHolder>(options) {
            @Override
            public void onBindViewHolder(TutorReviewsFragment.TutorReviewCardViewHolder holder, int position, Review model) {
                holder.setFname(model.getFname());
                holder.setLname(model.getLname());
                holder.setComment(model.getComment());
                holder.setSubject(model.getSubject());
                holder.setRate(model.getRate());
                holder.setProfilepic(model.getTuteeUID());
            }

            @Override
            public TutorReviewsFragment.TutorReviewCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutor_review_card, group, false);
                return new TutorReviewsFragment.TutorReviewCardViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
            }

        };
        adapter.startListening();
        //Final step, where "mRecyclerView" is defined in your xml layout as
        //the recyclerview
        mRecyclerView.setAdapter(adapter);
        return view;
    }
    public class TutorReviewCardViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView profilepic;
        public TextView fname, lname, comment, subject;
        public RatingBar ratingBar;
        public TutorReviewCardViewHolder(View itemView) {
            super(itemView);
            fname = itemView.findViewById(R.id.fname);
            lname = itemView.findViewById(R.id.lname);
            comment = itemView.findViewById(R.id.comment);
            subject = itemView.findViewById(R.id.subject);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            profilepic = itemView.findViewById(R.id.profilepic);

        }
        public void setFname(String string) {
            fname.setText(string);
        }
        public void setLname(String string) {lname.setText(string);}
        public void setComment(String string) { comment.setText(string); }
        public void setSubject(String string) { subject.setText(string); }
        public void setRate(Float f) {
            ratingBar.setRating(f);
        }
        public void setProfilepic(String string) {
            // get the Firebase  storage reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/"+string);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri){
                    //load img using glide
                    Glide.with(getActivity()).load(uri.toString()).placeholder(R.drawable.round_account_circle_24).dontAnimate().into(profilepic);
                }
            });

        }
    }

}