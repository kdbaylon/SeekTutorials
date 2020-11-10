package com.example.seektutorials.ui.tuteeHome.search;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.seektutorials.ui.chat.ChatRoomFragment;
import com.example.seektutorials.ui.tutorHome.reviews.Review;
import com.example.seektutorials.ui.tutorHome.reviews.TutorReviewsFragment;
import com.example.seektutorials.ui.tutorHome.subjects.Subject;
import com.example.seektutorials.ui.tutorHome.subjects.TutorEditSubjectFragment;
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
 * Tutor profile fragment
 */
public class ViewTutorProfileFragment extends Fragment {
    public float average;
    public float counter;
    //Firebase
    //storage
    private StorageReference storageReference;
    private FirebaseStorage storage;
    //auth
    private FirebaseAuth mAuth;
    String tutorUID;
    //firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    CircleImageView imageView;
    public static ViewTutorProfileFragment newInstance(String string) {
        Bundle bundle = new Bundle();
        bundle.putString("tutorUID", string);
        ViewTutorProfileFragment fragment = new ViewTutorProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutee_view_tutor, null);
        //get layout addresses
        Button messageButton = view.findViewById(R.id.messageButton);
        //profile info
        imageView = view.findViewById(R.id.profilepic);
        final TextView fnameTextView = view.findViewById(R.id.fname);
        final TextView lnameTextView = view.findViewById(R.id.lname);
        final TextView descTextView = view.findViewById(R.id.desc);
        final TextView locationTextView = view.findViewById(R.id.loc);
        final TextView schoolTextView = view.findViewById(R.id.school);
        final TextView courseTextView = view.findViewById(R.id.course);
        final TextView emailTextView = view.findViewById(R.id.email);
        final RatingBar ratingBarAve = view.findViewById(R.id.rating_bar_ave);
        //subjects and reviews recyclerviews
        RecyclerView subjects = (RecyclerView) view.findViewById(R.id.subjects_card);
        RecyclerView reviews = (RecyclerView) view.findViewById(R.id.reviews_card);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        //get tutorUID from bundle
        Bundle bundle = getArguments();
        if(bundle!=null){
            tutorUID = bundle.getString("tutorUID");
        }
        //message button
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment nextFrag= ChatRoomFragment.newInstance(tutorUID);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("images/"+tutorUID);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri){
                //load img using glide
                Glide.with(getActivity()).load(uri.toString()).placeholder(R.drawable.round_account_circle_24).dontAnimate().into(imageView);
            }
        });

        DocumentReference docRef = db.collection("users").document(tutorUID);
        //get info from firestore document
        //profile
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //get text
                        String fname =document.getString("fname");
                        String lname =document.getString("lname");
                        String desc =document.getString("desc");
                        String email =document.getString("email");
                        String course =document.getString("course");
                        String school =document.getString("school");
                        String location =document.getString("location");
                        //set text
                        fnameTextView.setText(fname);
                        lnameTextView.setText(lname);
                        descTextView.setText(desc);
                        emailTextView.setText(email);
                        courseTextView.setText(course);
                        schoolTextView.setText(school);
                        locationTextView.setText(location);
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //recyclerview for subjects
        subjects.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        subjects.setItemAnimator(new DefaultItemAnimator());
        Query subject_query = db.collection("users").document(tutorUID).collection("subjects");
        FirestoreRecyclerOptions<Subject> subject_options = new FirestoreRecyclerOptions.Builder<Subject>().setQuery(subject_query, Subject.class).build();

        final FirestoreRecyclerAdapter<Subject, ViewTutorProfileFragment.TutorSubjectCardViewHolder> subject_adapter = new FirestoreRecyclerAdapter<Subject, ViewTutorProfileFragment.TutorSubjectCardViewHolder>(subject_options) {
            @Override
            public void onBindViewHolder(ViewTutorProfileFragment.TutorSubjectCardViewHolder holder, int position, Subject model) {
                holder.setName(model.getName());
                holder.setDescription(model.getDescription());
                holder.setWeekly_sched(model.getWeekly_sched());
                holder.setTime(model.getTime());
                holder.setFee(model.getFee());
            }

            @Override
            public ViewTutorProfileFragment.TutorSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.profile_subject_card, group, false);
                return new TutorSubjectCardViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
            }

        };
        //recyclerview for reviews
        reviews.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        reviews.setItemAnimator(new DefaultItemAnimator());
        Query review_query = db.collection("users").document(tutorUID).collection("reviews");
        FirestoreRecyclerOptions<Review> review_options = new FirestoreRecyclerOptions.Builder<Review>().setQuery(review_query, Review.class).build();

        final FirestoreRecyclerAdapter<Review, ViewTutorProfileFragment.TutorReviewCardViewHolder> review_adapter = new FirestoreRecyclerAdapter<Review, ViewTutorProfileFragment.TutorReviewCardViewHolder>(review_options) {
            @Override
            public void onBindViewHolder(ViewTutorProfileFragment.TutorReviewCardViewHolder holder, int position, Review model) {
                holder.setFname(model.getTuteeUID());
                holder.setLname(model.getTuteeUID());
                holder.setComment(model.getComment());
                holder.setSubject(model.getSubject());
                holder.setRate(model.getRate());
                holder.setProfilepic(model.getTuteeUID());
                counter +=1;
                average +=model.getRate();
                Float aveFinal= average/counter;
                ratingBarAve.setRating(aveFinal);
            }

            @Override
            public ViewTutorProfileFragment.TutorReviewCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutor_review_card, group, false);
                return new TutorReviewCardViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
            }

        };
        subject_adapter.startListening();
        review_adapter.startListening();
        subjects.setAdapter(subject_adapter);
        reviews.setAdapter(review_adapter);
        return view;
    }
    //subject viewholder
    public class TutorSubjectCardViewHolder extends RecyclerView.ViewHolder{
        public TextView name, description, weekly_sched, time, fee;
        public TutorSubjectCardViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            weekly_sched = itemView.findViewById(R.id.weekly_sched);
            time = itemView.findViewById(R.id.time);
            fee = itemView.findViewById(R.id.fee);
        }
        public void setName(String string) {
            name.setText(string);
        }
        public void setDescription(String string) {
            description.setText(string);
        }
        public void setWeekly_sched(String string) {
            weekly_sched.setText(string);
        }
        public void setTime(String string) {
            time.setText(string);
        }
        public void setFee(String string) {
            fee.setText(string);
        }

    }
    //review viewholder
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
            DocumentReference reference = db.collection("users").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document != null && document.exists()){
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
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document != null && document.exists()){
                            //get text
                            String lnamee = document.getString("lname");
                            lname.setText(lnamee);
                        }else {
                            Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });}
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